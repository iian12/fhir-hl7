package com.dju.medical;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class HapiHl7V2Parser implements Hl7V2Parser {

    private final HapiContext context;
    private final Parser parser;

    public HapiHl7V2Parser() {
        this.context = new DefaultHapiContext();
        this.parser = context.getPipeParser();
    }

    @Override
    public Hl7V2Message parse(String rawMessage) {
        try {
            Message message = parser.parse(rawMessage);

            if (!(message instanceof ORU_R01 oru)) {
                throw new IllegalArgumentException(
                        "Unsupported HL7 message: " + message.getName()
                );
            }

            return convert(oru);

        } catch (HL7Exception e) {
            throw new IllegalArgumentException(
                    "Invalid HL7 v2 message",
                    e
            );
        }
    }

    private Hl7V2Message convert(ORU_R01 message)
            throws HL7Exception {

        String messageId = message
                .getMSH()
                .getMessageControlID()
                .getValue();

        PID pid = message
                .getPATIENT_RESULT()
                .getPATIENT()
                .getPID();

        String patientId = pid
                .getPatientIdentifierList(0)
                .getIDNumber()
                .getValue();

        String birthDateValue = pid
                .getDateTimeOfBirth()
                .getTime()
                .getValue();

        String sexValue = pid
                .getAdministrativeSex()
                .getValue();

        Hl7Patient patient = new Hl7Patient(
                patientId,
                parseBirthDate(birthDateValue),
                parseSex(sexValue)
        );

        List<Hl7Observation> observations =
                extractObservations(message);

        return new Hl7V2Message(
                messageId,
                patient,
                observations
        );
    }

    private List<Hl7Observation> extractObservations(
            ORU_R01 message
    ) throws HL7Exception {

        List<Hl7Observation> observations =
                new ArrayList<>();

        for (ORU_R01_ORDER_OBSERVATION order
                : message.getPATIENT_RESULT()
                .getORDER_OBSERVATIONAll()) {

            for (ORU_R01_OBSERVATION observation
                    : order.getOBSERVATIONAll()) {

                OBX obx = observation.getOBX();

                String code = obx
                        .getObservationIdentifier()
                        .getIdentifier()
                        .getValue();

                String display = obx
                        .getObservationIdentifier()
                        .getText()
                        .getValue();

                String codeSystem = obx
                        .getObservationIdentifier()
                        .getNameOfCodingSystem()
                        .getValue();

                String value = extractValue(obx);

                String unit = obx
                        .getUnits()
                        .getIdentifier()
                        .getValue();

                observations.add(
                        new Hl7Observation(
                                code,
                                codeSystem,
                                display,
                                new BigDecimal(value),
                                unit
                        )
                );
            }
        }

        return observations;
    }

    private String extractValue(OBX obx) throws HL7Exception {
        if (obx.getObservationValueReps() == 0) {
            throw new IllegalArgumentException(
                    "OBX-5 value is missing"
            );
        }

        Type data = obx
                .getObservationValue(0)
                .getData();

        return data.encode();
    }

    private LocalDate parseBirthDate(String value) {
        if (value == null || value.length() < 8) {
            return null;
        }

        return LocalDate.parse(
                value.substring(0, 8),
                DateTimeFormatter.BASIC_ISO_DATE
        );
    }

    private Sex parseSex(String value) {
        if (value == null) {
            return Sex.UNKNOWN;
        }

        return switch (value) {
            case "M" -> Sex.MALE;
            case "F" -> Sex.FEMALE;
            default -> Sex.UNKNOWN;
        };
    }
}