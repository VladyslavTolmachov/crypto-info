package com.crypto.info.service.impl;

import com.crypto.info.CryptoPriceValues;
import com.crypto.info.model.Crypto;
import com.crypto.info.model.CryptoData;
import com.crypto.info.model.CryptoLimits;
import com.crypto.info.model.CryptoNormalizedRange;
import com.crypto.info.service.CryptoService;
import com.crypto.info.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@PropertySource(value = "classpath:supported_cryptos.properties")
public class CryptoServiceImpl implements CryptoService {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoServiceImpl.class);

    @Value("${cryptos}")
    private List<String> cryptos;
    @Value("${prices.folder}")
    private String pricesFolder;

    /***
     * Reads all exist files for cryptos defined in properties
     */
    @PostConstruct
    private void cryptoValuesFirstLoad() {
        LOG.info("First crypto price values initialization is started");
        for (String cryptoName : cryptos) {
            CryptoPriceValues.getCryptoData(cryptoName, pricesFolder);
        }
    }

    @Override
    public List<CryptoLimits> getAllCryptosLimits(String days, String month, String years) {
        List<CryptoLimits> limits = new ArrayList<>();
        for (String cryptoName : cryptos) {
            Optional<Crypto> cryptoOptional = CryptoPriceValues.getCryptoData(cryptoName, pricesFolder);
            if (cryptoOptional.isEmpty()) {
                continue;
            }
            Crypto crypto = cryptoOptional.get();
            CryptoLimits cryptoLimits = createLimits(crypto, days, month, years);
            limits.add(cryptoLimits);
        }
        return limits;
    }

    @Override
    public CryptoLimits getCryptoLimits(String cryptoName, String days, String month, String years) {
        Optional<Crypto> cryptoOptional = CryptoPriceValues.getCryptoData(cryptoName, pricesFolder);
        if (cryptoOptional.isEmpty()) {
            return null;
        }
        Crypto crypto = cryptoOptional.get();
        return createLimits(crypto, days, month, years);
    }

    public boolean isSupportedCrypto(String cryptoName) {
        LOG.debug("Check crypto with name {}", cryptoName);
        return cryptos.contains(cryptoName);
    }

    public Crypto getCryptoData(String cryptoName) {
        Optional<Crypto> crypto = CryptoPriceValues.getCryptoData(cryptoName, pricesFolder);
        return crypto.orElse(new Crypto(cryptoName));
    }

    @Override
    public List<CryptoNormalizedRange> getAllCryptoByNormalizedRange() {
        LOG.debug("Start to create normalized range data");
        List<CryptoNormalizedRange> result = new ArrayList<>();
        for (String cryptoName : cryptos) {
            Optional<Crypto> cryptoOptional = CryptoPriceValues.getCryptoData(cryptoName, pricesFolder);
            if (cryptoOptional.isEmpty()) {
                continue;
            }
            CryptoNormalizedRange cryptoRange = new CryptoNormalizedRange();
            Crypto crypto = cryptoOptional.get();
            cryptoRange.setName(cryptoName);
            cryptoRange.setRange(calculateNormalizedRange(crypto.getData()));
            result.add(cryptoRange);
        }
        LOG.debug("size of result list: {}", result.size());
        return result.stream()
            .sorted((d1, d2) -> Double.compare(Double.parseDouble(d2.getRange()), Double.parseDouble(d1.getRange())))
            .collect(Collectors.toList());
    }

    @Override
    public CryptoNormalizedRange getCryptoByHighestNormalizedRange(String day, String month, String year) {
        LOG.debug("Start to find highest normalized range for specific day");
        List<CryptoNormalizedRange> rangeList = new ArrayList<>();
        for (String cryptoName : cryptos) {
            Optional<Crypto> cryptoOptional = CryptoPriceValues.getCryptoData(cryptoName, pricesFolder);
            if (cryptoOptional.isEmpty()) {
                continue;
            }
            Crypto crypto = cryptoOptional.get();
            List<CryptoData> data = DateUtil.filterDataBySpecificDay(crypto.getData(), day, month, year);
            if (data.isEmpty()) {
                continue;
            }
            String range = calculateNormalizedRange(data);
            rangeList.add(new CryptoNormalizedRange(cryptoName, range));
        }
        return rangeList.stream()
            .max(Comparator.comparingDouble(d -> Double.parseDouble(d.getRange()))).orElse(new CryptoNormalizedRange());
    }

    private String calculateNormalizedRange(List<CryptoData> data) {
        LOG.debug("Start to calculate, data size: {}", data.size());
        CryptoData minData = getMinCrypto(data);
        CryptoData maxData = getMaxCrypto(data);
        LOG.debug("Max: {} | Min: {}", maxData, minData);
        BigDecimal minPrice = new BigDecimal(minData.getPrice());
        BigDecimal maxPrice = new BigDecimal(maxData.getPrice());
        BigDecimal result = maxPrice.subtract(minPrice);
        result = result.divide(minPrice, 2, RoundingMode.HALF_EVEN);
        return result.toString();
    }

    private CryptoLimits createLimits(Crypto crypto, String days, String month, String years) {
        List<CryptoData> data = DateUtil.filterDataByTimePeriod(crypto.getData(), days, month, years);
        if (data.isEmpty()) {
            return new CryptoLimits(crypto.getName());
        }
        String cryptoName = crypto.getName();
        CryptoData max = getMaxCrypto(data);
        max.setName(cryptoName);
        CryptoData min = getMinCrypto(data);
        min.setName(cryptoName);
        CryptoData newest = getNewestCrypto(data);
        newest.setName(cryptoName);
        CryptoData oldest = getOldestCrypto(data);
        oldest.setName(cryptoName);
        LOG.debug("Find data: max: {}, min: {}, newest: {}, oldest: {}", max, min, newest, oldest);
        return new CryptoLimits(crypto.getName(), newest, oldest, min, max);
    }

    private CryptoData getMinCrypto(List<CryptoData> data) {
        return data.stream()
            .min(Comparator.comparingDouble(d -> Double.parseDouble(d.getPrice()))).orElse(null);
    }

    private CryptoData getMaxCrypto(List<CryptoData> data) {
        return data.stream()
            .max(Comparator.comparingDouble(d -> Double.parseDouble(d.getPrice()))).orElse(null);
    }

    private CryptoData getOldestCrypto(List<CryptoData> data) {
        return data.stream()
            .min(Comparator.comparingLong(d -> d.getDate().getEpochSecond())).orElse(null);
    }

    private CryptoData getNewestCrypto(List<CryptoData> data) {
        return data.stream()
            .max(Comparator.comparingLong(d -> d.getDate().getEpochSecond())).orElse(null);
    }
}
