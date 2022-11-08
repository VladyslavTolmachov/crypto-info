package com.crypto.info.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CryptoNormalizedRange {

    private String name;
    private String range;

    public CryptoNormalizedRange() {
    }

    public CryptoNormalizedRange(String name, String range) {
        this.name = name;
        this.range = range;
    }
}
