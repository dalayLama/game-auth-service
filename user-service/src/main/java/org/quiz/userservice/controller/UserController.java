package org.quiz.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.quiz.userservice.dto.request.TelegramAuthRequest;
import org.quiz.userservice.dto.request.TelegramRegistrationRequest;
import org.quiz.userservice.dto.response.AccessToken;
import org.quiz.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/login/telegram")
    public AccessToken auth(@RequestBody @NotNull @Valid TelegramAuthRequest authRequest) {
        return userService.authByTelegramId(authRequest);
    }

    @PostMapping("/sign-up/telegram")
    public ResponseEntity<Void> signUp(@RequestBody @NotNull @Valid TelegramRegistrationRequest registrationRequest) {
        userService.signUp(registrationRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("token/refresh")
    public AccessToken refreshToken(@RequestBody @NotBlank String refreshToken) {
        return userService.refreshToken(refreshToken);
    }

    @GetMapping("/users/telegram-id-{telegramId}/existence")
    public Boolean telegramIdExists(@PathVariable @Positive Integer telegramId) {
        return userService.telegramIdExists(telegramId);
    }

}
