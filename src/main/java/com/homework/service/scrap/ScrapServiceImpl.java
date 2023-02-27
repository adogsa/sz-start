package com.homework.service.scrap;

import com.homework.domain.auth.Account;
import com.homework.domain.scrap.ScrapApiReqInterface;
import com.homework.domain.scrap.Tax;
import com.homework.domain.scrap.dto.RefundResDto;
import com.homework.domain.scrap.dto.ScrapApiRequestDto;
import com.homework.domain.scrap.dto.ScrapApiResDto;
import com.homework.repository.scrap.ScrapRepository;
import com.homework.service.auth.AccountService;
import com.homework.utils.CustomCallback;
import com.homework.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ScrapServiceImpl implements ScrapService {
    private final ScrapRepository scrapRepository;
    private final AccountService accountService;

    private ScrapApiReqInterface scrapAPI;

    @PostConstruct
    public void init() {
        scrapAPI = RequestUtil.createService(ScrapApiReqInterface.class);
    }

    @Transactional
    public String asyncScrapMyTax(String userId) {
        Account curAccount = this.accountService.findByUserId(userId);
        RequestUtil.requestAsync(scrapAPI.getMyTax(new ScrapApiRequestDto(curAccount.getName(), curAccount.getRegNo())),
                new CustomCallback<ScrapApiResDto>() {
                    @Override
                    public void onResponse(Call<ScrapApiResDto> call, Response<ScrapApiResDto> response) {
                        super.onResponse(call, response);
                        if (!response.isSuccessful() || !Objects.equals(response.body().getStatus(), "success")) {
                            throw new RuntimeException("스크랩 가능한 유저가 아니거나 scrap 통신에 문제가 생겼습니다.");
                        }
                        Optional<Tax> userTax = scrapRepository.findTopByUserId(userId);
                        saveMyTaxInfo(response.body(), userId);
                        userTax.ifPresent(scrapRepository::delete);
                    }

                    @Override
                    public void onFailure(Call<ScrapApiResDto> call, Throwable t) {
                        super.onFailure(call, t);
                        throw new RuntimeException(t.getMessage());
                    }
                });
        return "스크랩 요청이 완료되었습니다.";
    }

    @Transactional
    public ScrapApiResDto syncScrapMyTax(String userId) {
        Account curAccount = this.accountService.findByUserId(userId);
        Optional<ScrapApiResDto> myTaxInfo = RequestUtil.requestSync(scrapAPI.getMyTax(new ScrapApiRequestDto(curAccount.getName(), curAccount.getRegNo())));
        ScrapApiResDto data = myTaxInfo.orElseThrow();

        if (!Objects.equals(data.getStatus(), "success")) {
            throw new RuntimeException("스크랩 가능한 유저가 아니거나 scrap 통신에 문제가 생겼습니다.");
        }
        saveMyTaxInfo(data, userId);
        return data;
    }

    private void saveMyTaxInfo(ScrapApiResDto data, String userId) {
        JSONObject jsonList = new JSONObject(data.getData().getJsonList());
        JSONArray salaryArr = (JSONArray) jsonList.get("급여");
        long totalSalary = 0;
        for (int i = 0; i < salaryArr.length(); i++) {
            JSONObject one = salaryArr.getJSONObject(i);
            String oneSalary = one.getString("총지급액").replaceAll(",", "");

            totalSalary += Long.parseLong(oneSalary);
        }

        double calculatedTax = Double.parseDouble(((String) jsonList.get("산출세액")).replaceAll(",", ""));

        long insuranceAmount = 0;
        long educationAmount = 0;
        long donationAmount = 0;
        long medicalAmount = 0;
        double retirementAmount = 0;
        JSONArray jArray = (JSONArray) jsonList.get("소득공제");
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject one = jArray.getJSONObject(i);
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

        scrapRepository.save(entityData);
    }

    public RefundResDto refund(String userId) {
        Account curUser = this.accountService.findByUserId(userId);

        Tax data = this.scrapRepository.findTopByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("ScrapServiceImpl - refund : 사용자를 찾을 수 없습니다."));
//        Tax data = this.scrapRepository.findTaxByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("ScrapServiceImpl - refund : 사용자를 찾을 수 없습니다."));
        DecimalFormat format = new DecimalFormat("###,###.##");
        return RefundResDto.builder()
                .name(curUser.getName())
                .determinedTax(format.format(data.getEarnedIncomeDeduction()))
                .retirementAmount(format.format(data.getRetirementAmount())).build();
    }
}
