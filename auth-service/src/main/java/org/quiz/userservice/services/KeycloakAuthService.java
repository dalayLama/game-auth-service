package org.quiz.userservice.services;

import org.keycloak.representations.AccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "keycloakAuthService", url = "${quiz-keycloak.server-url}")
public interface KeycloakAuthService {

    @PostMapping(path = "/realms/{realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AccessTokenResponse getToken(@PathVariable("realm") String realm, Map<String, ?> form);

}
