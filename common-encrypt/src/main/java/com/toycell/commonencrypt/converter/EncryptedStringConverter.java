package com.toycell.commonencrypt.converter;

import com.toycell.commonencrypt.util.AesEncryptionUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JPA AttributeConverter for automatic encryption/decryption of String fields.
 * 
 * Usage in Entity:
 * @Convert(converter = EncryptedStringConverter.class)
 * private String sensitiveData;
 */
@Component
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private static AesEncryptionUtil encryptionUtil;

    @Autowired
    public void setEncryptionUtil(AesEncryptionUtil util) {
        EncryptedStringConverter.encryptionUtil = util;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        return encryptionUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return encryptionUtil.decrypt(dbData);
    }
}
