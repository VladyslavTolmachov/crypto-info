package com.crypto.info.service.impl;

import com.crypto.info.CryptoInfoApplication;
import com.crypto.info.model.CryptoData;
import com.crypto.info.model.CryptoLimits;
import com.crypto.info.model.CryptoNormalizedRange;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = CryptoInfoApplication.class)
@TestPropertySource(locations = "classpath:test-cryptos.properties")
class CryptoServiceImplTest {

    @Autowired
    private CryptoServiceImpl cryptoService;

    @Test
    void isSupportedCrypto_shouldReturnCorrectResponse() {
        assertTrue(cryptoService.isSupportedCrypto("DOGE"));
        assertTrue(cryptoService.isSupportedCrypto("BTC"));
        assertFalse(cryptoService.isSupportedCrypto("XRP"));
    }

    @Test
    void getAllCryptoByNormalizedRange_pricesExist_shouldReturnExpectedList() {
        CryptoNormalizedRange range1 = new CryptoNormalizedRange("BTC", "0.01");
        CryptoNormalizedRange range2 = new CryptoNormalizedRange("DOGE", "0.01");
        List<CryptoNormalizedRange> expectedList = Arrays.asList(range1, range2);

        assertEquals(expectedList, cryptoService.getAllCryptoByNormalizedRange());
    }

    @Test
    void getAllCryptosLimits_dateValuesEmpty_shouldThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            cryptoService.getAllCryptosLimits("", "", "");
        });
    }

    @Test
    void getAllCryptosLimits_dateValuesNotEmpty_shouldReturnExpectedList() {
        List<CryptoLimits> expectedList = getExpectedLimitsList();

        assertEquals(expectedList, cryptoService.getAllCryptosLimits("0", "0", "11"));
    }

    @Test
    void getCryptoLimits_shouldReturnExpectedValue() {
        CryptoLimits expectedBTCLimits = getBTCLimits();

        assertEquals(expectedBTCLimits, cryptoService.getCryptoLimits("BTC", "0", "0", "11"));
    }

    @Test
    void getCryptoLimits_pricesFileNotExist_shouldReturnNull() {
        assertNull(cryptoService.getCryptoLimits("NotSupported", "0", "0", "11"));
    }

    private List<CryptoLimits> getExpectedLimitsList() {
        CryptoData DOGEOldest = new CryptoData(Instant.ofEpochMilli(Long.parseLong("1641013200000")), "DOGE", "0.1702");
        CryptoData DOGENewest = new CryptoData(Instant.ofEpochMilli(Long.parseLong("1664658000000")), "DOGE", "0.1727");
        CryptoData DOGEMin = new CryptoData(Instant.ofEpochMilli(Long.parseLong("1641013200000")), "DOGE", "0.1702");
        CryptoData DOGEMax = new CryptoData(Instant.ofEpochMilli(Long.parseLong("1664658000000")), "DOGE", "0.1727");
        CryptoLimits BTCLimits = getBTCLimits();
        CryptoLimits DOGELimits = new CryptoLimits("DOGE", DOGENewest, DOGEOldest, DOGEMin, DOGEMax);
        return Arrays.asList(BTCLimits, DOGELimits);
    }

    public CryptoLimits getBTCLimits() {
        CryptoData BTCOldest = new CryptoData(Instant.ofEpochMilli(Long.parseLong("1641009600000")), "BTC", "46813.21");
        CryptoData BTCNewest = new CryptoData(Instant.ofEpochMilli(Long.parseLong("1664658000000")), "BTC", "46979.61");
        CryptoData BTCMin = new CryptoData(Instant.ofEpochMilli(Long.parseLong("1641009600000")), "BTC", "46813.21");
        CryptoData BTCMax = new CryptoData(Instant.ofEpochMilli(Long.parseLong("1641031200000")), "BTC", "47143.98");
        return new CryptoLimits("BTC", BTCNewest, BTCOldest, BTCMin, BTCMax);
    }

}
