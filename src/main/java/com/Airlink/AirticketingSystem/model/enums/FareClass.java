package com.Airlink.AirticketingSystem.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FareClass {
    ECONOMY,
    PREMIUM_ECONOMY,
    BUSINESS,
    FIRST,
    FIRST_CLASS(FIRST);

    private final FareClass primaryValue;

    FareClass() {
        this.primaryValue = this;
    }

    FareClass(FareClass primaryValue) {
        this.primaryValue = primaryValue;
    }

    @JsonValue
    public String toValue() {
        return this.primaryValue == this ? name() : primaryValue.name();
    }

    @JsonCreator
    public static FareClass fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return FareClass.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle special case for FIRST_CLASS
            if ("FIRST_CLASS".equalsIgnoreCase(value)) {
                return FIRST;
            }
            throw e;
        }
    }
}
