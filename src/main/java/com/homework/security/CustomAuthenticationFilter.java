package com.homework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.domain.auth.dto.AccountRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {


        // request에 있는 username과 password를 파싱해서 자바 Object로 받기
        ObjectMapper om = new ObjectMapper();
        AccountRequestDto loginRequestDto;
        try {
            loginRequestDto = om.readValue(request.getInputStream(), AccountRequestDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("UserId가 입력에 없습니다.");
        }

        String userId = loginRequestDto.getUserId();
        if(userId == null) {
            throw new RuntimeException("UserId가 입력에 없습니다.");
        }
        String password = loginRequestDto.getPassword();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userId, password);
        return authenticationManager.authenticate(token);
    }
}
