package org.quiz.userservice.dto.request;

import jakarta.validation.constraints.NotNull;

public record TelegramAuthRequest(
        @NotNull(message = "can't be null")
        Integer telegramId
) {
}
