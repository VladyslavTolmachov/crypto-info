package com.crypto.info.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CryptoLimits {

    private String name;
    private CryptoData newest;
    private CryptoData oldest;
    private CryptoData min;
    private CryptoData max;

    public CryptoLimits(String name) {
        this.name = name;
    }

    public CryptoLimits(String name, CryptoData newest, CryptoData oldest, CryptoData min, CryptoData max) {
        this.name = name;
        this.newest = newest;
        this.oldest = oldest;
        this.min = min;
        this.max = max;
    }
}
