package com.sdet.sdet360.config.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Converter to handle String to UUID conversion for request parameters
 */
@Component
public class StringToUUIDConverter implements Converter<String, UUID> {
    
    private static final Logger logger = LoggerFactory.getLogger(StringToUUIDConverter.class);
    
    @Override
    public UUID convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            logger.warn("Received null or empty UUID string");
            return null;
        }
        
        try {
            return UUID.fromString(source);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format: {}", source);
            return null;
        }
    }
}
