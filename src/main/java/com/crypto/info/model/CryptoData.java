package com.crypto.info.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CryptoData {

    private Instant date;
    private String name;
    private String price;

    public CryptoData() {
    }

    public CryptoData(Instant date, String name, String price) {
        this.date = date;
        this.name = name;
        this.price = price;
    }
}
