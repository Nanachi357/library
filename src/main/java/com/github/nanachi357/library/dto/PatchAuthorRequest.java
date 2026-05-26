package com.github.nanachi357.library.dto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record PatchAuthorRequest(
        @Pattern(regexp = "^(?=.*\\S)[^0-9]*$")
        String name,

        @PastOrPresent
        LocalDate birthDate,

        @Pattern(regexp = "^(?=.*\\S)[^0-9]*$")
        String country
) {
}
