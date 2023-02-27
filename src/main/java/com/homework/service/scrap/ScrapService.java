package com.homework.service.scrap;

import com.homework.domain.scrap.dto.RefundResDto;
import com.homework.domain.scrap.dto.ScrapApiResDto;

public interface ScrapService {
    String asyncScrapMyTax(String userId);

    ScrapApiResDto syncScrapMyTax(String userId);

    RefundResDto refund(String userId);

}
