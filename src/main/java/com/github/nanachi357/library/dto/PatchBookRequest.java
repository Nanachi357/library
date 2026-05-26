package com.github.nanachi357.library.dto;

import jakarta.validation.constraints.Pattern;

public record PatchBookRequest(
        @Pattern(regexp = ".*\\S.*")
        String title
) {
}
