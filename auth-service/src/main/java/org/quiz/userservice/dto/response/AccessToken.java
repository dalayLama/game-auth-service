package org.quiz.userservice.dto.response;

import lombok.Builder;
import org.keycloak.representations.AccessTokenResponse;

@Builder
public record AccessToken(
        String accessToken,
        long expiresIn,
        String refreshToken,
        long refreshExpiresIn
) {

    public AccessToken(AccessTokenResponse accessTokenResponse) {
        this(
                accessTokenResponse.getToken(),
                accessTokenResponse.getExpiresIn(),
                accessTokenResponse.getRefreshToken(),
                accessTokenResponse.getRefreshExpiresIn()
        );
    }
}
