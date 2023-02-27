package com.homework.domain.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScrapApiRequestDto {
    private String name;
    private String regNo;
}