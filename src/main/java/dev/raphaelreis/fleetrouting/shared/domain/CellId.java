package dev.raphaelreis.fleetrouting.shared.domain;

import java.util.regex.Pattern;

public record CellId(String value) {

    private static final Pattern FORMAT = Pattern.compile("[a-z0-9][a-z0-9-]{2,31}");

    public CellId {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("cellId must contain 3 to 32 lowercase letters, numbers, or hyphens");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

