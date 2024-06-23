package org.quiz.userservice.controller;

import org.junit.jupiter.api.Test;
import org.quiz.userservice.dto.request.TelegramAuthRequest;
import org.quiz.userservice.dto.request.TelegramRegistrationRequest;
import org.quiz.userservice.dto.response.AccessToken;
import org.quiz.userservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static final TelegramAuthRequest TELEGRAM_AUTH_REQUEST = new TelegramAuthRequest(555);

    private static final TelegramRegistrationRequest TELEGRAM_REGISTRATION_REQUEST = new TelegramRegistrationRequest(
            555, "username");

    private static final AccessToken ACCESS_TOKEN = new AccessToken(
            "accessToken",
            300,
            "refreshToken",
            500
    );

    @Test
    void shouldReturnAccessToken() throws Exception {
        String request = TestFilesUtil.readResourceFile("requests/telegram-request.json");
        String response = TestFilesUtil.readResourceFile("responses/access-token.json");

        given(userService.authByTelegramId(TELEGRAM_AUTH_REQUEST)).willReturn(ACCESS_TOKEN);


        mockMvc.perform(post("/api/v1/login/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(response));

        then(userService).should(only()).authByTelegramId(TELEGRAM_AUTH_REQUEST);
    }

    @Test
    void shouldSignUp() throws Exception {
        String request = TestFilesUtil.readResourceFile("requests/sign-up-telegram-user-request.json");

        mockMvc.perform(post("/api/v1/sign-up/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk());

        then(userService).should(only()).signUp(TELEGRAM_REGISTRATION_REQUEST);
    }

    @Test
    void shouldReturn400ForNotValidAuthRequest() throws Exception {
        String request = TestFilesUtil.readResourceFile("requests/not-valid-telegram-request.json");

        mockMvc.perform(post("/api/v1/login/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        then(userService).should(never()).authByTelegramId(any());
    }

    @Test
    void shouldReturn400ForNullableAuthRequest() throws Exception {
        mockMvc.perform(post("/api/v1/login/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        then(userService).should(never()).authByTelegramId(any());
    }

    @Test
    void shouldReturn400ForNotValidRegistrationRequest() throws Exception {
        String request = TestFilesUtil.readResourceFile("requests/not-valid-sign-up-telegram-user-request.json");

        mockMvc.perform(post("/api/v1/sign-up/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        then(userService).should(never()).authByTelegramId(any());
    }

    @Test
    void shouldReturn400ForNullableRegistrationRequest() throws Exception {
        mockMvc.perform(post("/api/v1/sign-up/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        then(userService).should(never()).authByTelegramId(any());
    }

}