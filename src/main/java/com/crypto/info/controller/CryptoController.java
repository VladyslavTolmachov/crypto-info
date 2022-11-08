package com.crypto.info.controller;

import com.crypto.info.model.Crypto;
import com.crypto.info.model.CryptoLimits;
import com.crypto.info.model.CryptoNormalizedRange;
import com.crypto.info.service.impl.CryptoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/crypto")
public class CryptoController {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoController.class);

    private final CryptoServiceImpl cryptoService;

    public CryptoController(CryptoServiceImpl cryptoService) {
        this.cryptoService = cryptoService;
    }

    /**
     * Return cryptocurrency object with all data
     *
     * @param cryptoName - name of requested cryptocurrency
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{cryptoName}")
    public ResponseEntity<Crypto> getCryptocurrency(@PathVariable String cryptoName) {
        if (cryptoName.isEmpty() || !cryptoService.isSupportedCrypto(cryptoName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Crypto crypto = cryptoService.getCryptoData(cryptoName);
        if (Objects.isNull(crypto)) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(crypto, HttpStatus.OK);
    }

    /**
     * Return limits of all cryptocurrencies
     *
     * @param days,month,years - defines limit by date
     */
    @RequestMapping(method = RequestMethod.GET, value = "/limits")
    public ResponseEntity<List<CryptoLimits>> getAllCryptosLimits(@RequestParam(required = false, defaultValue = "0") String days,
                                                                  @RequestParam(required = false, defaultValue = "0") String month,
                                                                  @RequestParam(required = false, defaultValue = "0") String years) {
        LOG.info("Find values for days: {}, month: {}, years: {}", days, month, years);
        List<CryptoLimits> limits;
        try {
            limits = cryptoService.getAllCryptosLimits(days, month, years);
        } catch (RuntimeException e) {
            LOG.warn(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(limits, HttpStatus.OK);
    }

    /**
     * Return limits for a specific crypto
     *
     * @param days,month,years - defines limit by date
     */
    @RequestMapping(method = RequestMethod.GET, value = "/limits/{cryptoName}")
    public ResponseEntity<CryptoLimits> getCryptoLimits(@PathVariable String cryptoName,
                                                        @RequestParam(required = false, defaultValue = "0") String days,
                                                        @RequestParam(required = false, defaultValue = "0") String month,
                                                        @RequestParam(required = false, defaultValue = "0") String years) {
        LOG.info("Find values for days: {}, month: {}, years: {}", days, month, years);
        if (cryptoName.isEmpty() || !cryptoService.isSupportedCrypto(cryptoName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CryptoLimits limits;
        try {
            limits = cryptoService.getCryptoLimits(cryptoName, days, month, years);
        } catch (RuntimeException e) {
            LOG.warn(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(limits, HttpStatus.OK);
    }

    /**
     * @return sorted list of all cryptos, comparing the normalized range
     */
    @RequestMapping(method = RequestMethod.GET, value = "/normalizedRange")
    public ResponseEntity<List<CryptoNormalizedRange>> getAllCryptoByNormalizedRange() {
        List<CryptoNormalizedRange> cryptos = cryptoService.getAllCryptoByNormalizedRange();
        return new ResponseEntity<>(cryptos, HttpStatus.OK);
    }

    /**
     * @param day,month,year - specific date
     * @return crypto with the highest normalized range for a specific date
     */
    @RequestMapping(value = "/normalizedRange/highest")
    public ResponseEntity<CryptoNormalizedRange> getCryptoByNormalizedRange(@RequestParam(required = false) String day,
                                                                            @RequestParam(required = false) String month,
                                                                            @RequestParam(required = false) String year) {
        CryptoNormalizedRange cryptoRange;
        try {
            cryptoRange = cryptoService.getCryptoByHighestNormalizedRange(day, month, year);
        } catch (RuntimeException e) {
            LOG.warn(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(cryptoRange, HttpStatus.OK);
    }

}
