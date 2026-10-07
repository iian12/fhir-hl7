package com.dju.medical;

import java.util.List;

public record Hl7V2Message(String messageId, Hl7Patient patient, List<Hl7Observation> observations) {
}
