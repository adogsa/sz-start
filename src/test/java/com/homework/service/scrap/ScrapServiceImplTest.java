package com.homework.service.scrap;

import com.homework.config.JasyptConfig;
import com.homework.domain.scrap.Tax;
import com.homework.domain.scrap.dto.ScrapApiResDto;
import com.homework.repository.auth.AccountRepository;
import com.homework.repository.scrap.ScrapRepository;
import com.homework.service.auth.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.Thread.sleep;
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
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    JasyptConfig jasyptConfig;

    String testUserId = "duly2";

    @DisplayName(value = "비동기적인 scrap rest api 호출 테스트")
    @Test
    void asyncScrapMyTax() throws InterruptedException {
        // when
        String result = scrapService.asyncScrapMyTax("be");

        // then
        assertThat(result).isNotNull();
        // 결과 확인하기 위해서 기다립니다.
        sleep(30000L);
    }

    @DisplayName(value = "동기적인 scrap rest api 호출 테스트")
    @Test
    void syncScrapMyTax() {
        // when
        ScrapApiResDto result = scrapService.syncScrapMyTax("be");

        // then
        assertThat(result.getData().getCompany()).isEqualTo("삼쩜삼");
        assertThat(result.getStatus()).isEqualTo("success");
    }

    @Test
    void refund() {
        // given
        Tax entityData = Tax.builder()
                .userId(testUserId)
                .totalSalary(30000000L) // 총급여
                .calculatedTax(600000.0) // 산출세액
                .insuranceAmount(100000L)   // 보험료
                .educationAmount(200000L)   // 교육료
                .donationAmount(150000L)    // 기부금
                .medicalAmount(700000L)     // 의료비
                .retirementAmount(1333333.333).build(); // 퇴직금

        // then
        assertThat(entityData.getEarnedIncomeDeduction()).isEqualTo(0);
    }

    @Test
    void jasypt() {
        String username = "username";
        String password = "password";

        System.out.println(jasyptConfig.stringEncryptor().encrypt(username));
        System.out.println(jasyptConfig.stringEncryptor().encrypt(password));
    }
}