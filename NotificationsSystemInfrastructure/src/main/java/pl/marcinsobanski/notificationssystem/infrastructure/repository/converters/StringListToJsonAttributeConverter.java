package pl.marcinsobanski.notificationssystem.infrastructure.repository.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Converter
public class StringListToJsonAttributeConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        final var list = Optional.ofNullable(attribute)
                .orElse(Collections.emptyList());
        return objectMapper.writeValueAsString(list);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData)
                .filter(Predicate.not(String::isBlank))
                .map(data -> objectMapper.readValue(data, ListStringTypeRefence.INSTANCE))
                .orElse(Collections.emptyList());
    }

    private static class ListStringTypeRefence extends TypeReference<List<String>> {
        private static final ListStringTypeRefence INSTANCE = new ListStringTypeRefence();
    }
}
