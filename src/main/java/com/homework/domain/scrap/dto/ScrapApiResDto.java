package com.homework.domain.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScrapApiResDto {
    private String status;
    private ScrapApiResData data;
}

//@Data
//@AllArgsConstructor
//class ScrapApiResJsonList {
//    @JsonProperty("급여")
//    private JSONArray salary;
//
//    @JsonProperty("산출세액")
//    private Long calculatedTax;
//
//    @JsonProperty("소득공제")
//    private JSONArray incomeDeduction;
//}