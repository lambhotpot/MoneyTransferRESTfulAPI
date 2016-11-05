package com.taskforce.moneyapp.utilities;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Utilities class to operate on money
 */
public enum MoneyUtil {
    INSTANCE;

    static Logger log = Logger.getLogger(MoneyUtil.class);

    //zero amount with scale 4 and financial rounding mode
    public static final BigDecimal zeroAmount = new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN);


    /**
     * @param inputCcyCode String Currency code to be validated
     * @return true if currency code is valid ISO code, false otherwise
     */
    public static boolean validateCcyCode(String inputCcyCode) {
        boolean result = false;
        try {
            Currency instance = Currency.getInstance(inputCcyCode);
            return instance.getSymbol().equals(inputCcyCode);
        } catch (Exception e) {
            log.warn("Cannot parse the input Currency Code, Validation Failed: ", e);
        }
        return result;
    }

}
