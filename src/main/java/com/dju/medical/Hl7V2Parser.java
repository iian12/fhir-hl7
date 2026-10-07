package com.dju.medical;

public interface Hl7V2Parser {
    Hl7V2Message parse(String rawMessage);
}
