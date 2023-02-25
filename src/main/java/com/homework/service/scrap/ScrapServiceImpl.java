package com.homework.service.scrap;

import com.homework.domain.auth.Account;
import com.homework.domain.scrap.Tax;
import com.homework.domain.scrap.dto.ScrapResDto;
import com.homework.repository.scrap.ScrapRepository;
import com.homework.service.auth.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ScrapServiceImpl implements ScrapService {
    private final AsyncScrapService asyncScrapService;
    private final ScrapRepository scrapRepository;
    private final AccountService accountService;

    public String requestScrapMyTax(String userId) {
        Account curAccount = this.accountService.findByUserId(userId);
        CompletableFuture<String> requestResult = this.asyncScrapService.scrapMyTax(curAccount.getName(), curAccount.getRegNo());

        requestResult.thenAccept(
                result -> {
                    JSONObject jObject = new JSONObject(result);
                    JSONObject data = jObject.getJSONObject("data");
                    JSONObject jsonList = data.getJSONObject("jsonList");

                    JSONArray salaryArr = jsonList.getJSONArray("급여");
                    long totalSalary = 0;
                    for (int i = 0; i < salaryArr.length(); i++) {
                        JSONObject one = salaryArr.getJSONObject(i);
                        String oneSalary = one.getString("총지급액").replaceAll(",", "");

                        totalSalary += Long.parseLong(oneSalary);
                    }

                    long calculatedTax = Long.parseLong(jsonList.getString("산출세액").replaceAll(",", ""));

                    long insuranceAmount = 0;
                    long educationAmount = 0;
                    long donationAmount = 0;
                    long medicalAmount = 0;
                    double retirementAmount = 0;
                    JSONArray jArray = jsonList.getJSONArray("소득공제");
                    log.info(String.valueOf(jArray));
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject one = jArray.getJSONObject(i);
                        log.info(String.valueOf(one));
                        String type = one.getString("소득구분");
                        if (Objects.equals(type, "보험료")) {
                            insuranceAmount = Long.parseLong(one.getString("금액").replaceAll(",", ""));
                        } else if (Objects.equals(type, "교육비")) {
                            educationAmount = Long.parseLong(one.getString("금액").replaceAll(",", ""));
                        } else if (Objects.equals(type, "기부금")) {
                            donationAmount = Long.parseLong(one.getString("금액").replaceAll(",", ""));
                        } else if (Objects.equals(type, "의료비")) {
                            medicalAmount = Long.parseLong(one.getString("금액").replaceAll(",", ""));
                        } else if (Objects.equals(type, "퇴직연금")) {
                            retirementAmount = Double.parseDouble(one.getString("총납임금액").replaceAll(",", ""));
                        }

                    }

                    Tax entityData = Tax.builder()
                            .userId(userId)
                            .totalSalary(totalSalary)
                            .calculatedTax(calculatedTax)
                            .insuranceAmount(insuranceAmount)
                            .educationAmount(educationAmount)
                            .donationAmount(donationAmount)
                            .medicalAmount(medicalAmount)
                            .retirementAmount(retirementAmount).build();

                    this.scrapRepository.save(entityData);
                }
        ).exceptionally(t -> {
            log.error(t.getMessage());
            t.printStackTrace();
            throw new CompletionException(t);
        });
        return "스크랩 요청이 완료되었습니다.";
    }

    public ScrapResDto refund(String userId) {
        Account curUser = this.accountService.findByUserId(userId);

        Tax data = this.scrapRepository.findTaxByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("ScrapServiceImpl - refund : 사용자를 찾을 수 없습니다."));
        DecimalFormat format = new DecimalFormat("###,###.##");
        return ScrapResDto.builder()
                .name(curUser.getName())
                .determinedTax(format.format(data.getEarnedIncomeDeduction()))
                .retirementAmount(format.format(data.getRetirementAmount())).build();
    }
}
