package com.homework.service;

import com.homework.domain.Account;
import com.homework.domain.dto.AccountRequestDto;

import java.util.Map;

public interface AccountService {
    Account findByUserId(String userId);
    Long saveAccount(AccountRequestDto dto);
    void updateRefreshToken(String userId, String refreshToken);

    Map<String, String> refresh(String refreshToken);
}
