package com.homework.service.scrap;

import com.homework.service.auth.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AsyncScrapService {
    @Autowired
    private AccountService accountService;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${external.my.tax.url}")
    private String externalMyTaxUrl;

    @Async
    public CompletableFuture<String> scrapMyTax(String name, String regNo) {
        // Header set
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Body set
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("name", name);
        body.add("regNo", regNo);

        // Message
        HttpEntity<?> requestMessage = new HttpEntity<>(body, httpHeaders);

        // Request
        ResponseEntity<String> response = this.restTemplate.postForEntity(externalMyTaxUrl, requestMessage, String.class);

        return CompletableFuture.completedFuture(response.getBody());
    }
}
