package com.dju.medical;

import java.math.BigDecimal;

public record Measurement(BigDecimal value, String unit) {
}
