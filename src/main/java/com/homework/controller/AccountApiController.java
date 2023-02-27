package com.homework.controller;

import com.homework.domain.auth.Account;
import com.homework.domain.auth.dto.AccountRequestDto;
import com.homework.domain.auth.dto.AccountResponseDto;
import com.homework.service.auth.AccountService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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

    @ApiOperation(value = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<Account> signup(@RequestBody AccountRequestDto requestDto) {
        return ResponseEntity.ok(accountService.saveAccount(requestDto));
    }

    @ApiOperation(value = "내정보 가져오기")
    @GetMapping("/me")
    public ResponseEntity<Account> me(@ApiIgnore Principal principal) {
        return ResponseEntity.ok(accountService.findByUserId(principal.getName()));
    }

    @ApiOperation(value = "jwt 토큰 다시 가져오기")
    @GetMapping("/refresh")
    public ResponseEntity<AccountResponseDto> refresh(HttpServletRequest request, HttpServletResponse response) {
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
        return ResponseEntity.ok(new AccountResponseDto(tokens.get(AT_HEADER), tokens.get(RT_HEADER)));
    }
}
