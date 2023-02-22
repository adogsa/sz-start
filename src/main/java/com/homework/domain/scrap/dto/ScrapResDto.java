package com.homework.domain.scrap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScrapResDto {
    @JsonProperty(value = "이름")
    private String name;
    @JsonProperty(value = "결정세액")
    private String determinedTax;
    @JsonProperty(value = "퇴직연금세액공제")
    private String retirementAmount;
}
