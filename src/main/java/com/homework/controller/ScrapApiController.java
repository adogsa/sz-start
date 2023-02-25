package com.homework.controller;

import com.homework.domain.scrap.dto.ScrapResDto;
import com.homework.service.scrap.ScrapService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/szs")
@RequiredArgsConstructor
@RestController
public class ScrapApiController {

    private final ScrapService scrapService;

    @ApiOperation(value = "세금 정보 scrap실행하기")
    @PostMapping("/scrap")
    public ResponseEntity<String> scrap(Principal principal) {
        return ResponseEntity.ok(scrapService.requestScrapMyTax(principal.getName()));
    }

    @ApiOperation(value = "내 결정세액 가져오기")
    @ResponseBody
    @GetMapping("/refund")
    public ResponseEntity<ScrapResDto> refund(Principal principal) {
        return ResponseEntity.ok(scrapService.refund(principal.getName()));
    }
}
