package com.dju.medical;

import org.springframework.stereotype.Service;

@Service
public class Hl7MessageHandler {

    private final Hl7V2Parser parser;
    private final Hl7PulmonaryFunctionAdapter adapter;

    public Hl7MessageHandler(
            Hl7V2Parser parser,
            Hl7PulmonaryFunctionAdapter adapter
    ) {
        this.parser = parser;
        this.adapter = adapter;
    }

    public PulmonaryFunctionData handle(String rawMessage) {

        Hl7V2Message message =
                parser.parse(rawMessage);

        return adapter.adapt(message);
    }
}