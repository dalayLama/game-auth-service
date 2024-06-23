package org.quiz.userservice.controller;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
public record UserData(
        String username,
        Set<String> roles,
        Map<String, List<String>> attributes) {
}
