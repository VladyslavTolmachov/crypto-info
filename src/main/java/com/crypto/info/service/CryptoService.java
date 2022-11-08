package com.crypto.info.service;

import com.crypto.info.model.CryptoLimits;
import com.crypto.info.model.CryptoNormalizedRange;

import java.util.List;

public interface CryptoService {

    /**
     * Return limits for requested crypto for requested period of time
     *
     * @param cryptoName       - name of cryptocurrency
     * @param days,month,years - specific date. Count like now().minus days, month, years
     * @return - limits of specific cryptocurrency
     */
    CryptoLimits getCryptoLimits(String cryptoName, String days, String month, String years);

    /***
     * Return list of limits for all cryptocurrencies filtered by date
     * @param days,month,years - defines limit by date
     */
    List<CryptoLimits> getAllCryptosLimits(String days, String month, String years);

    /**
     * Collect and return normalized range for all cryptos
     *
     * @return list of crypto entities with calculated normalized range
     */
    List<CryptoNormalizedRange> getAllCryptoByNormalizedRange();

    /**
     * Calculates and return crypto data with highest normalized range on a specific date
     *
     * @param day,month,year - specific date
     * @return - crypto entity with normalized range value
     */
    CryptoNormalizedRange getCryptoByHighestNormalizedRange(String day, String month, String year);
}
