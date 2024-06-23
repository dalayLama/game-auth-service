package org.quiz.userservice.services;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.quiz.userservice.conf.properties.KeycloakProperties;
import org.quiz.userservice.controller.TestFilesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "quiz-keycloak.server-url=http://127.0.0.1:${wiremock.server.port}",
        "quiz-keycloak.realm=test-realm",
        "quiz-keycloak.client-id=id",
        "quiz-keycloak.client-secret=secret",
        "quiz-keycloak.auth.telegram-id-parameter-name=telegramId",
})
@AutoConfigureWireMock(port = 0)
class UserServiceKeycloakIntegrationTest {

    @Autowired
    private WireMockServer mockServer;

    @Autowired
    private UserServiceKeycloak keycloakService;

    @Autowired
    private KeycloakProperties properties;

    @Test
    void shouldAuthenticateUser() {
        AccessTokenResponse expected = new AccessTokenResponse();
        expected.setToken("access_token");
        expected.setExpiresIn(500);
        expected.setRefreshExpiresIn(300);
        expected.setRefreshToken("refresh_token");

        final Integer telegramId = 123;

        final String responseBody = TestFilesUtil.readResourceFile("responses/access-token-response.json");
        String url = "/realms/%s/protocol/openid-connect/token".formatted(properties.getRealm());
        mockServer.stubFor(WireMock.post(WireMock.urlEqualTo(url))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(responseBody)
                )
        );

        AccessTokenResponse accessTokenResponse = keycloakService.authByTelegramId(telegramId);
        assertThat(accessTokenResponse).usingRecursiveComparison().isEqualTo(expected);
    }

}