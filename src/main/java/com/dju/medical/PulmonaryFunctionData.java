package com.dju.medical;

import java.time.LocalDate;

public record PulmonaryFunctionData(String patientId, LocalDate birthDate, Sex sex,
                                    Measurement fev1, Measurement fvc, Measurement fev1Fvc) {
}
