package com.github.nanachi357.library.dto;

import jakarta.validation.constraints.Pattern;

public record PatchReaderRequest(
        @Pattern(regexp = "^(?=.*\\S)[^0-9]*$")
        String name
) {
}
