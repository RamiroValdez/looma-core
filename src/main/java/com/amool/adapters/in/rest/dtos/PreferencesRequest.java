package com.amool.adapters.in.rest.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PreferencesRequest(
        @NotNull List<Long> genres
) {}
