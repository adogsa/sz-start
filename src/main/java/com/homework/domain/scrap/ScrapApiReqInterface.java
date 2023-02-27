package com.homework.domain.scrap;

import com.homework.domain.scrap.dto.ScrapApiRequestDto;
import com.homework.domain.scrap.dto.ScrapApiResDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ScrapApiReqInterface {

    @POST("v2/scrap")
    Call<ScrapApiResDto> getMyTax(@Body ScrapApiRequestDto scrapApiRequestDto);
}