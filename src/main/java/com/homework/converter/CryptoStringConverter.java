package com.homework.converter;

import com.homework.config.JasyptConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CryptoStringConverter implements AttributeConverter<String, String> {
    @Autowired
    JasyptConfig jasyptConfig;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return jasyptConfig.stringEncryptor().encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return jasyptConfig.stringEncryptor().decrypt(dbData);
    }
}
