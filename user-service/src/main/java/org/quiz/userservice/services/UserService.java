package org.quiz.userservice.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.quiz.userservice.dto.request.TelegramAuthRequest;
import org.quiz.userservice.dto.request.TelegramRegistrationRequest;
import org.quiz.userservice.dto.response.AccessToken;

public interface UserService {

    AccessToken authByTelegramId(@NotNull @Valid TelegramAuthRequest authRequest);

    void signUp(@NotNull @Valid TelegramRegistrationRequest registrationRequest);

    AccessToken refreshToken(@NotBlank String refreshToken);

    boolean telegramIdExists(@NotNull @Positive Integer telegramId);
}
