package com.homework.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountRequestDto {
    @NotBlank(message = "userId가 필요합니다.")
    private String userId;
    @NotBlank(message = "사용자 비번이 필요합니다.")
    private String password;
    @NotBlank(message = "사용자 이름이 필요합니다.")
    private String name;
    @NotBlank(message = "사용자 주민등록번호가 필요합니다.")
    private String regNo;

    @Id
    private Long id;
}
