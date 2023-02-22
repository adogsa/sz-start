package com.homework.controller;

import com.homework.domain.scrap.dto.ScrapResDto;
import com.homework.service.scrap.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/szs")
@RequiredArgsConstructor
@RestController
public class ScrapApiController {

    private final ScrapService scrapService;

    @PostMapping("/scrap")
    public ResponseEntity<String> scrap(Principal principal) {
        return ResponseEntity.ok(scrapService.requestScrapMyTax(principal.getName()));
    }

    @ResponseBody
    @GetMapping("/refund")
    public ResponseEntity<ScrapResDto> refund(Principal principal) {
        System.out.println("ddd" + principal.getName());
        return ResponseEntity.ok(scrapService.refund(principal.getName()));
    }
}
