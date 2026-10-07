package com.dju.medical;

import java.math.BigDecimal;

public record Hl7Observation(String code, String codeSystem, String display, BigDecimal value, String unit) {
}
