package org.quiz.userservice.conf.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "quiz-keycloak.auth")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthProperties {

    private String telegramIdParameterName;

}
