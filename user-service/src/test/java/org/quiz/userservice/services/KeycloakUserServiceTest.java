package org.quiz.userservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quiz.userservice.conf.KeycloakUsersConsts;
import org.quiz.userservice.conf.properties.AuthProperties;
import org.quiz.userservice.conf.properties.KeycloakProperties;
import org.quiz.userservice.controller.UserData;
import org.quiz.userservice.dto.request.TelegramAuthRequest;
import org.quiz.userservice.dto.request.TelegramRegistrationRequest;
import org.quiz.userservice.dto.response.AccessToken;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

@ExtendWith(MockitoExtension.class)
class KeycloakUserServiceTest {

    @Mock
    private UserServiceKeycloak keycloakService;

    private final AuthProperties authProperties = new AuthProperties("paramName");

    @Spy
    private KeycloakProperties properties = KeycloakProperties.builder()
            .authClientId("clientId")
            .auth(authProperties)
            .build();

    @InjectMocks
    private KeycloakUserService userService;

    @Test
    void shouldAuthUser() {
        TelegramAuthRequest request = new TelegramAuthRequest(15);

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setToken("token");
        accessTokenResponse.setRefreshToken("refreshToken");
        accessTokenResponse.setExpiresIn(15);
        accessTokenResponse.setRefreshExpiresIn(20);
        AccessToken expectedResult = new AccessToken(accessTokenResponse);

        given(keycloakService.authByTelegramId(request.telegramId())).willReturn(accessTokenResponse);

        AccessToken result = userService.authByTelegramId(request);
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);

        then(keycloakService).should(only()).authByTelegramId(request.telegramId());
    }

    @Test
    void shouldRegisterUser() {
        TelegramRegistrationRequest request = new TelegramRegistrationRequest(55, "username");
        UserData expectedUserData = generateUserRepresentation(request);

        userService.signUp(request);

        then(keycloakService).should(only()).registerUser(expectedUserData);
    }

    private UserData generateUserRepresentation(TelegramRegistrationRequest registrationRequest) {
        return UserData.builder()
                .username(registrationRequest.username())
                .attributes(Map.of(KeycloakUsersConsts.TELEGRAM_ID_ATTRIBUTE_NAME,
                        List.of(registrationRequest.telegramId().toString())))
                .roles(Set.of(KeycloakUsersConsts.USER_ROLE))
                .build();
    }

}