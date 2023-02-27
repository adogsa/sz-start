package com.homework.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.homework.domain.auth.Account;
import com.homework.domain.auth.Role;
import com.homework.domain.auth.dto.AccountRequestDto;
import com.homework.repository.auth.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.homework.security.JwtConstants.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordRegNoEncoder;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService - loadUserByUsername : 사용자를 찾을 수 없습니다."));

        List<SimpleGrantedAuthority> authorities = account.getRoles()
                .stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        return new User(account.getUserId(), account.getPassword(), authorities);
    }

    @Override
    public Account findByUserId(String userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService - loadUserByUsername : 사용자를 찾을 수 없습니다."));
    }

    @Override
    public Account saveAccount(AccountRequestDto dto) {
        Account curAccount = validateUser(dto);
        curAccount = Account.builder()
                .id(curAccount.getId())
                .name(curAccount.getName())
                .regNo(curAccount.getRegNo())
                .password(passwordRegNoEncoder.encode(dto.getPassword()))
                .userId(dto.getUserId()).build();
        return accountRepository.save(curAccount);
    }

    private Account validateUser(AccountRequestDto dto) {

        if (accountRepository.existsByUserId(dto.getUserId())) {
            throw new RuntimeException("이미 존재하는 ID입니다.");
        }

        Account account = accountRepository.findByName(dto.getName()).orElseThrow(() -> new RuntimeException("사전 등록되어 있지 않은 사용자입니다. 관리자에게 문의해주세요."));

        if (!Objects.equals(dto.getRegNo(), account.getRegNo())) {
            throw new RuntimeException("사전 등록되어 있지 않은 사용자입니다. 관리자에게 문의해주세요.");
        }

        if (account.getUserId() != null && !account.getUserId().isBlank()) {
            throw new RuntimeException("이미 가입한 사용자입니다.");
        }

        return account;
    }

    // =============== TOKEN ============ //

    @Override
    public void updateRefreshToken(String userId, String refreshToken) {
        Account account = accountRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        account.updateRefreshToken(refreshToken);
        accountRepository.save(account);
    }

    @Override
    public Map<String, String> refresh(String refreshToken) {

        // === Refresh Token 유효성 검사 === //
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET)).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);

        // === Access Token 재발급 === //
        long now = System.currentTimeMillis();
        String username = decodedJWT.getSubject();
        Account account = accountRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        if (account.getRefreshToken() == null || !account.getRefreshToken().equals(refreshToken)) {
            throw new JWTVerificationException("유효하지 않은 Refresh Token 입니다.");
        }
        String accessToken = JWT.create()
                .withSubject(account.getUserId())
                .withExpiresAt(new Date(now + AT_EXP_TIME))
                .withClaim("roles", account.getRoles().stream().map(Role::getName)
                        .collect(Collectors.toList()))
                .sign(Algorithm.HMAC256(JWT_SECRET));
        Map<String, String> accessTokenResponseMap = new HashMap<>();

        // === 현재시간과 Refresh Token 만료날짜를 통해 남은 만료기간 계산 === //
        // === Refresh Token 만료시간 계산해 1개월 미만일 시 refresh token도 발급 === //
        long refreshExpireTime = decodedJWT.getClaim("exp").asLong() * 1000;
        long diffDays = (refreshExpireTime - now) / 1000 / (24 * 3600);
        long diffMin = (refreshExpireTime - now) / 1000 / 60;
        if (diffMin < 5) {
            String newRefreshToken = JWT.create()
                    .withSubject(account.getUserId())
                    .withExpiresAt(new Date(now + RT_EXP_TIME))
                    .sign(Algorithm.HMAC256(JWT_SECRET));
            accessTokenResponseMap.put(RT_HEADER, newRefreshToken);
            account.updateRefreshToken(newRefreshToken);
            accountRepository.save(account);
        }

        accessTokenResponseMap.put(AT_HEADER, accessToken);
        return accessTokenResponseMap;
    }
}
