package com.homework.service.scrap;

import com.homework.domain.scrap.dto.ScrapResDto;

public interface ScrapService {
    String requestScrapMyTax(String userId);

    ScrapResDto refund(String userId);

}
