package com.github.zigcat.merchsite_microservice.main.services.converter;

import jakarta.persistence.AttributeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateToStringConverter implements AttributeConverter<LocalDate, String> {
    private DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String convertToDatabaseColumn(LocalDate localDate) {
        return localDate != null ? localDate.format(FORMATTER) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(String s) {
        return s.isEmpty() ? null : LocalDate.parse(s, FORMATTER);
    }
}
