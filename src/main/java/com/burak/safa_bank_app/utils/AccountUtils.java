package com.burak.safa_bank_app.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "The user has already a created account";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account is successfully created!";

    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "Account number: %s is not exist!";

    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "Account is found successfully!";

    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "%s amount of credit is added successfully!";

    public static final String ACCOUNT_INSUFFICIENT_BALANCE_CODE = "006";
    public static final String ACCOUNT_INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance: %s amount is more than your balance!";

    public static final String ACCOUNT_WITHDRAW_SUCCESS_CODE = "007";
    public static final String ACCOUNT_WITHDRAW_SUCCESS_MESSAGE = "%s amount of credit is withdrawn successfully!";

    public static final String ACCOUNT_TRANSFER_SUCCESS_CODE = "008";
    public static final String ACCOUNT_TRANSFER_SUCCESS_MESSAGE = "%s amount of credit is transferred successfully!";


    public static String generateAccountNumber(){
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) +min);

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();
    }
}
