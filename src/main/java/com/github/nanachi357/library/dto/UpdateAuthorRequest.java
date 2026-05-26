package com.github.nanachi357.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UpdateAuthorRequest(
        @NotBlank
        @Pattern(regexp = "^[^0-9]*$")
        String name,

        @NotNull
        @PastOrPresent
        LocalDate birthDate,

        @NotBlank
        @Pattern(regexp = "^[^0-9]*$")
        String country
) {
}
