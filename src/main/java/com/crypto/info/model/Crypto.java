package com.crypto.info.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Crypto {

    private String name;
    private Instant lastUploadFileDate;
    private List<CryptoData> data;

    public Crypto() {
    }

    public Crypto(String name) {
        this.name = name;
    }
}
