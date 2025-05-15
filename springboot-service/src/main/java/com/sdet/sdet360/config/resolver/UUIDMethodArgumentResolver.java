package com.sdet.sdet360.config.resolver;

import com.sdet.sdet360.config.annotation.SafeUUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.UUID;

/**
 * Custom method argument resolver for UUID path variables
 * Provides more graceful handling of null or invalid UUID strings
 */
@Component
public class UUIDMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger logger = LoggerFactory.getLogger(UUIDMethodArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UUID.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        // Check if parameter has SafeUUID annotation
        boolean hasSafeAnnotation = parameter.hasParameterAnnotation(SafeUUID.class);
        SafeUUID safeUUID = parameter.getParameterAnnotation(SafeUUID.class);
        
        // Get path variables
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        
        // Try to get value from path variables first, then from request parameters
        String paramName = parameter.getParameterName();
        String value = null;
        
        if (pathVariables != null && pathVariables.containsKey(paramName)) {
            value = pathVariables.get(paramName);
            logger.debug("Found path variable {} with value: {}", paramName, value);
        } else {
            value = webRequest.getParameter(paramName);
            logger.debug("Found request parameter {} with value: {}", paramName, value);
        }
        
        // Handle null or empty values
        if (value == null || value.trim().isEmpty()) {
            logger.debug("Null or empty value for UUID parameter: {}", paramName);
            
            // If SafeUUID annotation is present and has a default value, use it
            if (hasSafeAnnotation && safeUUID != null && !safeUUID.defaultValue().isEmpty()) {
                try {
                    return UUID.fromString(safeUUID.defaultValue());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid default UUID format in annotation: {}", safeUUID.defaultValue());
                }
            }
            
            // If SafeUUID annotation allows null, return null
            if (hasSafeAnnotation && safeUUID != null && safeUUID.allowNull()) {
                return null;
            }
            
            // Otherwise return null (default behavior)
            return null;
        }
        
        // Try to parse the UUID
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format for parameter {}: {}", paramName, value);
            
            // If SafeUUID annotation is present and has a default value, use it
            if (hasSafeAnnotation && safeUUID != null && !safeUUID.defaultValue().isEmpty()) {
                try {
                    return UUID.fromString(safeUUID.defaultValue());
                } catch (IllegalArgumentException ex) {
                    logger.warn("Invalid default UUID format in annotation: {}", safeUUID.defaultValue());
                }
            }
            
            // Otherwise return null
            return null;
        }
    }
}
