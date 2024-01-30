package com.dbProject.service.impl;

import com.dbProject.model.ThpUserAccount;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;
@Service
public class ThpAccountManagerServiceImpl implements com.dbProject.service.ThpAccountManagerService {

    // JDBC connection parameters
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/finalprojectdb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "1234";

    @Override
    public ThpUserAccount createNewAccount(String userID, BigDecimal initialBalance) {
        ThpUserAccount newAccount = new ThpUserAccount();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            // Insert a new account with a unique card number and sheba number
            String insertQuery = "INSERT INTO ThpUserAccount (userID, balance, cardNumber, shebaNumber) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                String cadNumber = generateUniqueCardNumber();
                String sheba = generateUniqueShebaNumber();

                preparedStatement.setString(1, userID);
                preparedStatement.setBigDecimal(2, initialBalance);
                preparedStatement.setString(3, cadNumber);
                preparedStatement.setString(4, sheba);
                // Populate cardNumber and shebaNumber with unique values
                newAccount.setCardNumber(cadNumber);
                newAccount.setUserID(userID);
                newAccount.setShebaNumber(sheba);
                newAccount.setBalance(initialBalance);
                newAccount.setDailyLimit(BigDecimal.valueOf(10000000.00));
                newAccount.setLastResetDate(new Date());
                newAccount.setDailySatnaPayaLimit(BigDecimal.valueOf(40000000.00));
                preparedStatement.executeUpdate();

                }
            }



         catch (SQLException e) {
            e.printStackTrace();
        }

        return newAccount;
    }

    public static String generateUniqueShebaNumber() {
        String countryCode = "IR";
        String checkDigits = "00";
        String bankCode = "6274";
        String accountNumber = generateRandomNumericString(18);

        return countryCode + checkDigits + bankCode + accountNumber;
    }

    public static String generateUniqueCardNumber() {
        String issuerIdentificationNumber = "12345";
        String accountNumber = generateRandomNumericString(10);
        String checksum = "0";

        return issuerIdentificationNumber + accountNumber + checksum;
    }

    private static String generateRandomNumericString(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }

}
