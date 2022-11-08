package com.crypto.info.util;

import com.crypto.info.model.CryptoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class for working with timestamps
 */
public class DateUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    public DateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns filtered data by period of time
     *
     * @param data             - data to filter
     * @param days,month,years - period of time from now to past
     * @return filtered data
     */
    public static List<CryptoData> filterDataByTimePeriod(List<CryptoData> data, String days, String month, String years) {
        LOG.debug("Start filter data, list size: {}, date: years - {}, month - {}, days - {}", data.size(), years, month, days);
        List<CryptoData> result = data;
        try {
            if (Objects.nonNull(years) && Integer.parseInt(years) > 0) {
                result = filterDataByYears(result, Integer.parseInt(years));
            }
            if (Objects.nonNull(month) && Integer.parseInt(month) > 0) {
                result = filterDataByMonth(result, Integer.parseInt(month));
            }
            if (Objects.nonNull(days) && Integer.parseInt(days) > 0) {
                result = filterDataByDays(result, Integer.parseInt(days));
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    /***
     *  Filter incoming data by specific date
     * @param data - data with crypto information
     * @param day,month,year - date to filter
     * @return filtered data
     */
    public static List<CryptoData> filterDataBySpecificDay(List<CryptoData> data, String day, String month, String year) {
        List<CryptoData> result = data;
        LocalDate wantedDate = LocalDate.of(getValidYear(year), getValidMonth(month), getValidDay(day));
        LOG.debug("Filter data by specific date: {}, day: {}, month: {}, year: {}", wantedDate, day, month, year);
        try {
            if (!result.isEmpty()) {
                result = result.stream()
                    .filter(d -> wantedDate.isEqual(ChronoLocalDate.from(d.getDate().atZone(ZoneId.systemDefault()))))
                    .collect(Collectors.toList());
            }
        } catch (NumberFormatException | DateTimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    private static int getValidDay(String day) {
        if (Objects.isNull(day) || day.isEmpty()) {
            return LocalDate.now().getDayOfMonth();
        }
        return Integer.parseInt(day);
    }

    private static int getValidMonth(String month) {
        if (Objects.isNull(month) || month.isEmpty()) {
            return LocalDate.now().getMonth().getValue();
        }
        return Integer.parseInt(month);
    }

    private static int getValidYear(String year) {
        if (Objects.isNull(year) || year.isEmpty()) {
            return LocalDate.now().getYear();
        }
        return Integer.parseInt(year);
    }

    private static List<CryptoData> filterDataByYears(List<CryptoData> data, int years) {
        return data.stream()
            .filter(d -> d.getDate()
                .isAfter(LocalDate.now().minus(years, ChronoUnit.YEARS).atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .collect(Collectors.toList());
    }

    private static List<CryptoData> filterDataByMonth(List<CryptoData> data, int month) {
        return data.stream()
            .filter(d -> d.getDate()
                .isAfter(LocalDate.now().minus(month, ChronoUnit.MONTHS).atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .collect(Collectors.toList());
    }

    private static List<CryptoData> filterDataByDays(List<CryptoData> data, int days) {
        return data.stream()
            .filter(d -> d.getDate()
                .isAfter(LocalDate.now().minus(days, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .collect(Collectors.toList());
    }
}
