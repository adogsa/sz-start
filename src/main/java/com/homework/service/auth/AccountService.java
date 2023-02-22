package com.homework.service.auth;

import com.homework.domain.auth.Account;
import com.homework.domain.auth.dto.AccountRequestDto;

import java.util.Map;

public interface AccountService {
    Account findByUserId(String userId);
    Long saveAccount(AccountRequestDto dto);
    void updateRefreshToken(String userId, String refreshToken);

    Map<String, String> refresh(String refreshToken);
}
