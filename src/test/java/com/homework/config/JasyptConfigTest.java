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
//        System.out.println(enc);
        System.out.println(jasyptConfig.stringEncryptor().encrypt("860824-1655068"));
        System.out.println("dec:" + jasyptConfig.stringEncryptor().decrypt("Iv0nrojWtAjezFM4wBgJkqg5r4m7NKMz"));
        System.out.println("dec:" + jasyptConfig.stringEncryptor().decrypt("2efF9fvxc2YYVM1KgaR6yw6sfSLz8iAk"));

        System.out.println(jasyptConfig.stringEncryptor().encrypt("921108-1582816"));
        System.out.println(jasyptConfig.stringEncryptor().encrypt("880601-2455116"));
        System.out.println(jasyptConfig.stringEncryptor().encrypt("910411-1656116"));
        System.out.println(jasyptConfig.stringEncryptor().encrypt("820326-2715702"));
        assertThat(keyword).isEqualTo(des);
    }
}