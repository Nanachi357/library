package com.github.nanachi357.library.dto;

import java.time.LocalDate;

public record AuthorResponse(
        Long id,
        String name,
        LocalDate birthDate,
        String country
) {
}
