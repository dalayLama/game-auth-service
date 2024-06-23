package org.quiz.userservice.conf.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "quiz-keycloak", ignoreUnknownFields = false)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakProperties {

    private String serverUrl;

    private String realm;

    private String authClientId;

    private String authClientSecret;

    private String adminClientId;

    private String adminClientSecret;

    private AuthProperties auth;

}
