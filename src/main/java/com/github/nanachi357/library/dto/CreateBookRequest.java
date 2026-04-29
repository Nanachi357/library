package com.github.nanachi357.library.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBookRequest(
        @NotBlank
        String title
) {
}
