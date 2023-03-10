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
                            throw new RuntimeException("????????? ????????? ????????? ???????????? scrap ????????? ????????? ???????????????.");
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
        return "????????? ????????? ?????????????????????.";
    }

    @Transactional
    public ScrapApiResDto syncScrapMyTax(String userId) {
        Account curAccount = this.accountService.findByUserId(userId);
        Optional<ScrapApiResDto> myTaxInfo = RequestUtil.requestSync(scrapAPI.getMyTax(new ScrapApiRequestDto(curAccount.getName(), curAccount.getRegNo())));
        ScrapApiResDto data = myTaxInfo.orElseThrow();

        if (!Objects.equals(data.getStatus(), "success")) {
            throw new RuntimeException("????????? ????????? ????????? ???????????? scrap ????????? ????????? ???????????????.");
        }
        saveMyTaxInfo(data, userId);
        return data;
    }

    private void saveMyTaxInfo(ScrapApiResDto data, String userId) {
        JSONObject jsonList = new JSONObject(data.getData().getJsonList());
        JSONArray salaryArr = (JSONArray) jsonList.get("??????");
        long totalSalary = 0;
        for (int i = 0; i < salaryArr.length(); i++) {
            JSONObject one = salaryArr.getJSONObject(i);
            String oneSalary = one.getString("????????????").replaceAll(",", "");

            totalSalary += Long.parseLong(oneSalary);
        }

        double calculatedTax = Double.parseDouble(((String) jsonList.get("????????????")).replaceAll(",", ""));

        long insuranceAmount = 0;
        long educationAmount = 0;
        long donationAmount = 0;
        long medicalAmount = 0;
        double retirementAmount = 0;
        JSONArray jArray = (JSONArray) jsonList.get("????????????");
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject one = jArray.getJSONObject(i);
            String type = one.getString("????????????");
            if (Objects.equals(type, "?????????")) {
                insuranceAmount = Long.parseLong(one.getString("??????").replaceAll(",", ""));
            } else if (Objects.equals(type, "?????????")) {
                educationAmount = Long.parseLong(one.getString("??????").replaceAll(",", ""));
            } else if (Objects.equals(type, "?????????")) {
                donationAmount = Long.parseLong(one.getString("??????").replaceAll(",", ""));
            } else if (Objects.equals(type, "?????????")) {
                medicalAmount = Long.parseLong(one.getString("??????").replaceAll(",", ""));
            } else if (Objects.equals(type, "????????????")) {
                retirementAmount = Double.parseDouble(one.getString("???????????????").replaceAll(",", ""));
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

        Tax data = this.scrapRepository.findTopByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("ScrapServiceImpl - refund : ???????????? ?????? ??? ????????????."));
//        Tax data = this.scrapRepository.findTaxByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("ScrapServiceImpl - refund : ???????????? ?????? ??? ????????????."));
        DecimalFormat format = new DecimalFormat("###,###.##");
        return RefundResDto.builder()
                .name(curUser.getName())
                .determinedTax(format.format(data.getEarnedIncomeDeduction()))
                .retirementAmount(format.format(data.getRetirementAmount())).build();
    }
}
