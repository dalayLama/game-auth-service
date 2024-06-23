package org.quiz.userservice.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.util.JsonSerialization;
import org.quiz.userservice.conf.KeycloakUsersConsts;
import org.quiz.userservice.conf.properties.KeycloakProperties;
import org.quiz.userservice.dto.request.TelegramAuthRequest;
import org.quiz.userservice.dto.request.TelegramRegistrationRequest;
import org.quiz.userservice.dto.response.AccessToken;
import org.quiz.userservice.exceptions.KeycloakException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Validated
public class UserServiceKeycloak implements UserService {

    private final Keycloak keycloak;

    private final KeycloakAuthService authService;

    private final KeycloakProperties properties;


    @Override
    public AccessToken authByTelegramId(@NotNull @Valid TelegramAuthRequest authRequest) {
        String telegramIdParameterName = properties.getAuth().getTelegramIdParameterName();
        Map<String, String> form = Map.of(
                OAuth2Constants.CLIENT_ID, properties.getAuthClientId(),
                OAuth2Constants.CLIENT_SECRET, properties.getAuthClientSecret(),
                OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD,
                telegramIdParameterName, authRequest.telegramId().toString()
        );
        String realm = properties.getRealm();
        AccessTokenResponse token = authService.getToken(realm, form);
        return new AccessToken(token);
    }

    @Override
    public void signUp(@NotNull @Valid TelegramRegistrationRequest registrationRequest) {
        UserRepresentation userRepresentation = toUserRepresentation(registrationRequest);
        String userId = createUser(userRepresentation);
        UserResource user = getUser(userId);
        List<RoleRepresentation> roles = getRoles(Collections.singleton(KeycloakUsersConsts.USER_ROLE));
        user.roles().realmLevel().add(roles);
    }

    @Override
    public AccessToken refreshToken(@NotBlank String refreshToken) {
        String realm = properties.getRealm();
        Map<String, String> form = Map.of(
                OAuth2Constants.CLIENT_ID, properties.getAuthClientId(),
                OAuth2Constants.CLIENT_SECRET, properties.getAuthClientSecret(),
                OAuth2Constants.GRANT_TYPE, OAuth2Constants.REFRESH_TOKEN,
                OAuth2Constants.REFRESH_TOKEN, refreshToken
        );
        AccessTokenResponse token = authService.getToken(realm, form);
        return new AccessToken(token);
    }

    @Override
    public boolean telegramIdExists(@NotNull @Positive Integer telegramId) {
        RealmResource realm = getRealm();
        String query = String.format("%s:%d", KeycloakUsersConsts.TELEGRAM_ID_ATTRIBUTE_NAME, telegramId);
        List<UserRepresentation> userRepresentations = realm.users().searchByAttributes(query);
        return !userRepresentations.isEmpty();
    }

    private List<RoleRepresentation> getRoles(Collection<String> rolesNames) {
        RealmResource realm = getRealm();
        String search = String.join(",", rolesNames);
        return realm.roles().list(search, false);
    }

    private UserResource getUser(String userId) {
        RealmResource realm = getRealm();
        UsersResource users = realm.users();
        return users.get(userId);
    }

    private String createUser(UserRepresentation userRepresentation) {
        RealmResource realm = getRealm();
        UsersResource users = realm.users();
        try (Response response = users.create(userRepresentation)) {
            assertError(response);
            return CreatedResponseUtil.getCreatedId(response);
        }
    }

    private RealmResource getRealm() {
        return keycloak.realm(properties.getRealm());
    }

    private UserRepresentation toUserRepresentation(TelegramRegistrationRequest request) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.username());
        userRepresentation.setEnabled(true);
        userRepresentation.setAttributes(Map.of(
                KeycloakUsersConsts.TELEGRAM_ID_ATTRIBUTE_NAME, List.of(request.telegramId().toString()))
        );
        return userRepresentation;
    }

    private void assertError(Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.getStatus());
        if (httpStatus == null) {
            String message = "Keycloak returned an unknown status - %s".formatted(response.getStatus());
            throw new KeycloakException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (httpStatus.isError()) {
            String error = response.hasEntity()? readError(response) : null;
            throw new KeycloakException(error, httpStatus);
        }
    }

    private String readError(Response response) {
        try {
            Map error = JsonSerialization.readValue((InputStream) response.getEntity(), Map.class);
            return error.containsKey("errorMessage")
                    ? (String) error.get("errorMessage")
                    : (String) error.get("error");
        } catch (IOException e) {
            throw new KeycloakException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
