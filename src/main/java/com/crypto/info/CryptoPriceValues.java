package com.crypto.info;

import com.crypto.info.model.Crypto;
import com.crypto.info.model.CryptoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/***
 * Class reads data from .csv files and collects it to the map.
 * If crypto data file is updated, requested crypto updated too.
 */
public class CryptoPriceValues {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoPriceValues.class);

    private static String pricesFolder;
    private static final String CRYPTO_FILE_NAME_SUFFIX = "_values.csv";
    private static final String COMA_DELIMITER = ",";
    private static final Lock lock = new ReentrantLock();

    private static ConcurrentMap<String, Crypto> cryptoMap = new ConcurrentHashMap<>();

    public static Optional<Crypto> getCryptoData(String cryptoName, String folderName) {
        try {
            pricesFolder = folderName;
            return Optional.ofNullable(readPricesFiles(cryptoName));
        } catch (IOException | NullPointerException | InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    private static Crypto readPricesFiles(String cryptoName) throws IOException, InterruptedException {
        if (!cryptoMap.containsKey(cryptoName)) {
            if (lock.tryLock()) {
                return loadData(cryptoName);
            }
            return null;
        }
        if (isDataUpToDate(cryptoName)) {
            return cryptoMap.get(cryptoName);
        }
        if (lock.tryLock()) {
            return loadData(cryptoName);
        }
        return cryptoMap.get(cryptoName);
    }

    private static Crypto loadData(String cryptoName) throws IOException {
        LOG.info("Loading data for {} crypto", cryptoName);
        Crypto crypto = new Crypto(cryptoName);
        List<CryptoData> cryptoData = new ArrayList<>();
        crypto.setData(cryptoData);
        try {
            String fileName = pricesFolder + File.separator + cryptoName + CRYPTO_FILE_NAME_SUFFIX;
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(COMA_DELIMITER);
                cryptoData.add(extractData(values));
            }
            crypto.setLastUploadFileDate(Instant.now());
            cryptoMap.put(cryptoName, crypto);
        } finally {
            LOG.info("{} crypto is loaded", cryptoName);
            lock.unlock();
        }
        return crypto;
    }

    private static CryptoData extractData(String[] values) {
        CryptoData data = new CryptoData();
        data.setDate(Instant.ofEpochMilli(Long.parseLong(values[0])));
        data.setPrice(values[2]);
        return data;
    }

    private static boolean isDataUpToDate(String cryptoName) throws IOException {
        Crypto crypto = cryptoMap.get(cryptoName);
        String fileName = pricesFolder + File.separator + cryptoName + CRYPTO_FILE_NAME_SUFFIX;
        Instant fileLastUpdate = lastUpdate(fileName).toInstant();
        Instant lastUpload = crypto.getLastUploadFileDate();
        LOG.debug("File last update: {}", fileLastUpdate);
        LOG.debug("File last upload: {}", lastUpload);
        return lastUpload.isAfter(fileLastUpdate);
    }

    private static FileTime lastUpdate(String fileName) throws IOException {
        Path file = Paths.get(fileName);
        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
        return attr.lastModifiedTime();
    }
}
