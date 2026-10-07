package com.dju.medical;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Hl7Patient(String patientId, LocalDate birthDate, Sex sex) {
}
