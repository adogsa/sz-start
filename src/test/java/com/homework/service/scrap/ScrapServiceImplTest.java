package com.homework.service.scrap;

import com.homework.domain.auth.Account;
import com.homework.domain.auth.dto.AccountRequestDto;
import com.homework.domain.scrap.Tax;
import com.homework.repository.auth.AccountRepository;
import com.homework.repository.scrap.ScrapRepository;
import com.homework.service.auth.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ScrapServiceImplTest {
    @Autowired
    ScrapRepository scrapRepository;
    @Autowired
    ScrapService scrapService;

    @Autowired
    AsyncScrapService asyncScrapService;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    Account testAccount;
    String testUserId;

    @DisplayName(value = "비동기적인 rest api 호출 테스트")
    @Test
    void requestScrapMyTax() {
        // given
        Account account = Account.builder().name("둘리2").regNo("111111-1111111").build();
        accountRepository.save(account);
        AccountRequestDto accountDto = new AccountRequestDto(testUserId, "password", "둘리2", "111111-1111111", 200L);
        accountService.saveAccount(accountDto); // 회원 가입

        // when
        String result = scrapService.requestScrapMyTax(testUserId);

        // then
        assertThat(result).isEqualTo("스크랩 요청이 완료되었습니다.");
    }

    @Test
    void refund() {
        // given
        Tax entityData = Tax.builder()
                .userId(testUserId)
                .totalSalary(30000000L) // 총급여
                .calculatedTax(600000L) // 산출세액
                .insuranceAmount(100000L)   // 보험료
                .educationAmount(200000L)   // 교육료
                .donationAmount(150000L)    // 기부금
                .medicalAmount(700000L)     // 의료비
                .retirementAmount(1333333.333).build(); // 퇴직금

        // then
        assertThat(entityData.getEarnedIncomeDeduction()).isEqualTo(0);
    }
}