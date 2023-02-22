package com.homework.converter;

import com.homework.encrypt.Seed;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CryptoStringConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return Seed.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Seed.decrypt(dbData);
    }
}
