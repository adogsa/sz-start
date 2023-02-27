package com.homework.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = JasyptConfig.class)
class JasyptConfigTest {
    @Autowired
    JasyptConfig jasyptConfig;

    @Test
    void encryptTest() {
        String keyword = "sa";
        String enc = jasyptConfig.stringEncryptor().encrypt(keyword);
        String des = jasyptConfig.stringEncryptor().decrypt(enc);
        assertThat(keyword).isEqualTo(des);
    }
}