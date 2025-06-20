package io.github.haeun.newsgptback.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.List;

@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return ObjectUtils.isEmpty(attribute)
                    ? null
                    : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            return ObjectUtils.isEmpty(dbData)
                    ? null
                    : objectMapper.readValue(dbData, new TypeReference<List<String>>(){});
        } catch (IOException e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }
}