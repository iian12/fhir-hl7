package com.dju.medical;

import org.springframework.stereotype.Component;

@Component
public class Hl7PulmonaryFunctionAdapter {

    private static final String FEV1 = "20150-9";
    private static final String FVC = "19870-5";
    private static final String FEV1_FVC = "19926-5";

    public PulmonaryFunctionData adapt(
            Hl7V2Message message
    ) {

        return new PulmonaryFunctionData(
                message.patient().patientId(),
                message.patient().birthDate(),
                message.patient().sex(),
                find(message, FEV1),
                find(message, FVC),
                find(message, FEV1_FVC)
        );
    }

    private Measurement find(
            Hl7V2Message message,
            String code
    ) {

        Hl7Observation observation = message
                .observations()
                .stream()
                .filter(it -> code.equals(it.code()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Required observation missing: " + code
                        )
                );

        return new Measurement(
                observation.value(),
                observation.unit()
        );
    }
}
