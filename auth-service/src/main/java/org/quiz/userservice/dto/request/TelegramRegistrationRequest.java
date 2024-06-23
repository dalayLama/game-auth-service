package org.quiz.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TelegramRegistrationRequest(
        @NotNull
        Integer telegramId,
        @NotBlank
        @Size(min = 3)
        String username
) {
}
