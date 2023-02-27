package com.homework.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountRequestDto {
    @NotBlank(message = "userId가 필요합니다.")
    @ApiModelProperty(notes = "사용자 아이디", example = "myUserId", required = true)
    private String userId;

    @ApiModelProperty(notes = "비번", example = "myPassword", required = true)
    @NotBlank(message = "사용자 비번이 필요합니다.")
    private String password;

    @ApiModelProperty(notes = "이름", example = "myName", required = true)
    @NotBlank(message = "사용자 이름이 필요합니다.")
    private String name;

    @ApiModelProperty(notes = "주민번호", example = "111111-1111111", required = true)
    @NotBlank(message = "사용자 주민등록번호가 필요합니다.")
    private String regNo;

    @JsonIgnore
    private Long id;
}
