package com.homework.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountResponseDto {
    @ApiModelProperty(notes = "사용자 JWT", example = "fdsafdsafdsafdsa")
    private String access_token;

    @ApiModelProperty(notes = "사용자 Refresh token", example = "fdsafdsafdsafdsa")
    private String refresh_token;
}
