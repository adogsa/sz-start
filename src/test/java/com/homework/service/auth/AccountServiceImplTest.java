package com.homework.service.auth;

import com.homework.domain.auth.Account;
import com.homework.domain.auth.dto.AccountRequestDto;
import com.homework.repository.auth.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
//@Transactional
@AutoConfigureMockMvc
class AccountServiceImplTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;

    Account testAccount;
    String testUserId = "duly2";
    String testUserName = "둘리2";
    String testUserReg = "111111-1111111";
    String testPassword = "password";
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        Account account = Account.builder().name(testUserName).regNo(testUserReg).build();
        testAccount = accountRepository.save(account);
    }

    @DisplayName("회원 가입")
    @Test
    void saveAccount() {
        // given
        AccountRequestDto accountDto = new AccountRequestDto(testUserId, "password", testUserName, testUserReg, 200L);

        // when
        long id = accountService.saveAccount(accountDto);

        // then
        assertThat(id).isEqualTo(testAccount.getId());
    }

    @DisplayName("사용자 정보 조회 테스트")
    @Test
    void findByUserId() {
        // given
        this.saveAccount(); // 회원 가입

        // when
        Account account = accountService.findByUserId(testUserId);  // 사용자 정보 조회

        // then
        assertThat(account.getName()).isEqualTo(testUserName);
        assertThat(account.getRegNo()).isEqualTo(testUserReg);
    }
}