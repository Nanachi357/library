package com.github.nanachi357.library.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBookRequest(
        @NotBlank
        String title
) {
}
