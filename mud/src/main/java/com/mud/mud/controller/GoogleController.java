package com.mud.mud.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mud.mud.entity.User;
import com.mud.mud.repository.UserRepository;
import com.mud.mud.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/oauth/google")
@RequiredArgsConstructor
public class GoogleController {
    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.redirect-uri}")
    private String googleRedirectUri;

    @GetMapping
    public void googleLogin(@RequestParam("code") String code,
                            HttpSession session,
                            HttpServletResponse response) throws IOException {
        try {
            String accessToken = getAccessToken(code);
            JsonNode userInfo = getUserInfo(accessToken);

            String googleId = userInfo.get("sub").asText();
            String email = userInfo.has("email") ? userInfo.get("email").asText() : null;
            String name = userInfo.has("name") ? userInfo.get("name").asText() : null;

            Optional<User> existingUser = userRepository.findByUserId(googleId);
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = new User();
                user.setUserId(googleId);
                user.setEmail(email);
                user.setUsername(name);
                user.setPassword(UUID.randomUUID().toString());
                userService.saveUser(user);
            }

            session.setAttribute("user", user);
            session.setAttribute("isLoggedIn", true);

            // 외부 URL로 리다이렉트
            response.sendRedirect("http://localhost:3000/main.html");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("http://localhost:3000/error.html");
        }
    }

    private String getAccessToken(String code) throws Exception {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        var headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        String body = "code=" + code
                + "&client_id=" + googleClientId
                + "&client_secret=" + googleClientSecret
                + "&redirect_uri=" + googleRedirectUri
                + "&grant_type=authorization_code";

        var request = new org.springframework.http.HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        var response = restTemplate.exchange(tokenUrl, org.springframework.http.HttpMethod.POST, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        return root.get("access_token").asText();
    }

    private JsonNode getUserInfo(String accessToken) throws Exception {
        String url = "https://openidconnect.googleapis.com/v1/userinfo";

        var headers = new org.springframework.http.HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        var entity = new org.springframework.http.HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        var response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.getBody());
    }
}
