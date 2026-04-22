package com.github.nanachi357.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateReaderRequest(
        @NotBlank
        @Pattern(regexp = "^[^0-9]*$")
        String name
) {
}
