package com.github.zigcat.merchsite_microservice.auth.services;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Converter(autoApply = true)
@Slf4j
public class LocalDateToStringConverter implements AttributeConverter<LocalDate, String> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String convertToDatabaseColumn(LocalDate localDate) {
        return localDate != null ? localDate.format(FORMATTER) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(String s) {
        return s.isEmpty() ? null : LocalDate.parse(s, FORMATTER);
    }
}
