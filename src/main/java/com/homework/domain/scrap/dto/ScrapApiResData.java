package com.homework.domain.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class ScrapApiResData {
    private HashMap<?, ?> jsonList;
    //    private JSONObject jsonList;
    private String company;
    private String errMsg;
}
