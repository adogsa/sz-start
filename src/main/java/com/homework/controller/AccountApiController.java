package com.homework.controller;

import com.homework.domain.Account;
import com.homework.domain.dto.AccountRequestDto;
import com.homework.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

import static com.homework.security.JwtConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequestMapping("/szs")
@RequiredArgsConstructor
@RestController
public class AccountApiController {

    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody AccountRequestDto dto) {
        return ResponseEntity.ok(accountService.saveAccount(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<Account> me(Principal principal) {
        return ResponseEntity.ok(accountService.findByUserId(principal.getName()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            throw new RuntimeException("JWT Token이 존재하지 않습니다.");
        }
        String refreshToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
        Map<String, String> tokens = accountService.refresh(refreshToken);
        response.setHeader(AT_HEADER, tokens.get(AT_HEADER));
        if (tokens.get(RT_HEADER) != null) {
            response.setHeader(RT_HEADER, tokens.get(RT_HEADER));
        }
        return ResponseEntity.ok(tokens);
    }
}
