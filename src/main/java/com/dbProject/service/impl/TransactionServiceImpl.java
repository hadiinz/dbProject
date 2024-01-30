package com.dbProject.service.impl;

import com.dbProject.model.ThpTransaction;
import com.dbProject.model.ThpTransactionType;
import com.dbProject.model.ThpUserAccount;
import com.dbProject.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class TransactionServiceImpl implements TransactionService {

    // JDBC connection parameters
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/finalprojectdb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "1234";

    @Override
    public String handleTransaction(String sourceCard, String destinationCard, BigDecimal amount, ThpTransactionType transactionType) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Invalid amount for transaction.";
        }

        if (!isValidTransactionType(transactionType)) {
            return "Invalid transaction type.";
        }

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            connection.setAutoCommit(false);
            // Check if the source user exists
            ThpUserAccount sourceAccount = getUserAccount(connection, sourceCard);
            ThpUserAccount destinationAccount = getUserAccount(connection, destinationCard);

            if (sourceAccount.equals(null)) {
                return "Source user does not exist.";
            }

            // Check if the destination user exists
            if (destinationAccount.equals(null)) {
                return "Destination user does not exist.";
            }

            // Check if the source account has sufficient balance
            if (sourceAccount.getBalance().compareTo(amount) < 0) {
                return "Insufficient funds in the source account.";
            }
            // Check transaction limits based on the transaction type
            if (!checkTransactionLimits(connection, sourceCard, amount, transactionType)) {
                return "Transaction limit exceeded.";
            }
            // Update the balances
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
            destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

            // Create a transaction record
            ThpTransaction transaction = new ThpTransaction();
            transaction.setSourceCardNumber(sourceAccount.getCardNumber());
            transaction.setDestinationCardNumber(destinationAccount.getCardNumber());
            transaction.setAmount(amount);
            transaction.setSuccess(true);
            transaction.setDateTime(LocalDateTime.now());
            transaction.setTransactionType(transactionType);
            transaction.setTraceNumber(generateUniqueTraceNumber());

            // Save the transaction to the database
            saveTransactionToDatabase(connection, transaction);
            updateUserAccounts(sourceAccount, destinationAccount, connection);
            // Update the daily limit after a successful transaction
            updateDailyLimit(connection, sourceCard, amount, transactionType);

            connection.commit();

            return "Transaction successful. Trace Number: " + transaction.getTraceNumber();
        } catch (SQLException  e) {
            e.printStackTrace();
            return "Transaction failed.";
        }
    }

    private void updateUserAccounts(ThpUserAccount sourceAccount, ThpUserAccount destinationAccount, Connection connection) {
        String updateQuerySource = "UPDATE ThpUserAccount SET balance = ? where cardNumber = ? ";
        String updateQueryDest = "UPDATE ThpUserAccount SET balance = ? where cardNumber = ? ";

        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(updateQuerySource);
            PreparedStatement preparedStatement2 = connection.prepareStatement(updateQueryDest);

            preparedStatement1.setBigDecimal(1, sourceAccount.getBalance());
            preparedStatement1.setString(2, sourceAccount.getCardNumber());

            preparedStatement2.setBigDecimal(1, destinationAccount.getBalance());
            preparedStatement2.setString(2, destinationAccount.getCardNumber());


            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean checkTransactionLimits(Connection connection, String cardNumber, BigDecimal amount, ThpTransactionType transactionType) throws SQLException {
        ThpUserAccount userAccount = getUserAccount(connection, cardNumber);

        switch (transactionType) {
            case CartTOCart: {

                // Check if the daily limit is reached
                if (userAccount.getDailyLimit().compareTo(amount) < 0) {
                    return false;
                }

                // Check if it's a new day, and reset the daily limit if needed
                if (!isSameDayAsLastReset(connection, cardNumber)) {
                    resetDailyLimit(connection, cardNumber);
                    userAccount.setDailyLimit(BigDecimal.valueOf(10000000.00));
                }

                // Subtract the transaction amount from the daily limit
                userAccount.setDailyLimit(userAccount.getDailyLimit().subtract(amount));

                // Check if the new daily limit is still valid
                return userAccount.getDailyLimit().compareTo(BigDecimal.ZERO) >= 0;
            }
            case SATNA:
            case PAYA: {
                // Check dailySatnaPayaLimit
                if (userAccount.getDailySatnaPayaLimit().compareTo(amount) < 0) {
                    return false;
                }

                // Check if it's a new day, and reset the limit if needed
                if (!isSameDayAsLastReset(connection, cardNumber)) {
                    resetSatnaPayaLimit(connection, cardNumber);
                    userAccount.setDailySatnaPayaLimit(BigDecimal.valueOf(40000000.00));
                }

                // Subtract the transaction amount from the daily limit
                userAccount.setDailySatnaPayaLimit(userAccount.getDailySatnaPayaLimit().subtract(amount));

                // Check if the new daily limit is still valid
                return userAccount.getDailySatnaPayaLimit().compareTo(BigDecimal.ZERO) >= 0;
            }
            default:
                return false;

        }
    }
    private void resetSatnaPayaLimit(Connection connection, String cardNumber) throws SQLException {
        String updateQuery = "UPDATE ThpUserAccount SET dailySatnaPayaLimit = ?, lastResetDate = ? WHERE cardNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setBigDecimal(1, BigDecimal.valueOf(40000000.00));
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(3, cardNumber);

            preparedStatement.executeUpdate();
        }
    }

    private void resetDailyLimit(Connection connection, String cardNumber) throws SQLException {
        String updateQuery = "UPDATE ThpUserAccount SET dailyLimit = ?, lastResetDate = ? WHERE cardNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setBigDecimal(1, BigDecimal.valueOf(10000000.00));
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(3, cardNumber);

            preparedStatement.executeUpdate();
        }
    }
    private boolean isSameDayAsLastReset(Connection connection, String cardNumber) throws SQLException {
        String query = "SELECT lastResetDate FROM ThpUserAccount WHERE cardNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cardNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    LocalDate lastResetDate = resultSet.getDate("lastResetDate").toLocalDate();
                    return lastResetDate.equals(LocalDate.now());
                }
            }
        }

        // Return false if the last reset date is not found
        return false;
    }


    // retrieve the total amount of transactions for a specific transaction type on the current day
    private BigDecimal getDailyTransactionAmount(Connection connection, String userID, ThpTransactionType transactionType) throws SQLException {
        String query = "SELECT SUM(amount) AS totalAmount FROM ThpTransactions " +
                "WHERE userID = ? AND transactionType = ? AND DATE(dateTime) = CURDATE()";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userID);
            preparedStatement.setInt(2, transactionType.getType());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("totalAmount");
                }
            }
        }

        // Return a default value if the total amount is not found
        return BigDecimal.ZERO;
    }

    //update the daily limit after a successful transaction
    private void updateDailyLimit(Connection connection, String cardNumber, BigDecimal amount, ThpTransactionType transactionType) throws SQLException {
        String updateQuery = "UPDATE ThpUserAccount SET dailyLimit = dailyLimit - ?, lastResetDate = ? WHERE cardNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setBigDecimal(1, amount);
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(3, cardNumber);

            preparedStatement.executeUpdate();
        }
    }
    private ThpUserAccount getUserAccount(Connection connection, String cardNumber) throws SQLException {
        String query = "SELECT * FROM ThpUserAccount WHERE cardNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cardNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    ThpUserAccount userAccount = new ThpUserAccount();
                    userAccount.setAccountId(resultSet.getInt("accountID"));
                    userAccount.setUserID(resultSet.getString("userID"));
                    userAccount.setCardNumber(resultSet.getString("cardNumber"));
                    userAccount.setShebaNumber(resultSet.getString("shebaNumber"));
                    userAccount.setBalance(resultSet.getBigDecimal("balance"));
                    userAccount.setDailySatnaPayaLimit(resultSet.getBigDecimal("dailySatnaPayaLimit"));
                    userAccount.setDailyLimit(resultSet.getBigDecimal("dailyLimit"));
                    userAccount.setLastResetDate(resultSet.getDate("lastResetDate"));
                    return userAccount;
                }
            }
        }

        return null;
    }
    private void saveTransactionToDatabase(Connection connection, ThpTransaction transaction) throws SQLException {
        String insertQuery = "INSERT INTO ThpTransaction (sourceCardNumber, destinationCardNumber, amount, isSuccess, dateTime, traceNumber, transactionType) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, transaction.getSourceCardNumber());
            preparedStatement.setString(2, transaction.getDestinationCardNumber());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setBoolean(4, transaction.isSuccess());
            preparedStatement.setObject(5, transaction.getDateTime());
            preparedStatement.setString(6, transaction.getTraceNumber());
            preparedStatement.setInt(7, transaction.getTransactionType().getType());

            preparedStatement.executeUpdate();
        }
    }

    private boolean isValidTransactionType(ThpTransactionType transactionType) {
        return transactionType != null;
    }

    private String generateUniqueTraceNumber() {
        return "TRACE-" + System.currentTimeMillis();
    }


    @Override
    public List<ThpTransaction> getUserTransactions(String userID, int number) {
        List<ThpTransaction> transactions = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT * FROM ThpTransaction WHERE sourceCardNumber IN " +
                    "(SELECT cardNumber FROM ThpUserAccount WHERE userID = ?) " +
                    "OR destinationCardNumber IN " +
                    "(SELECT cardNumber FROM ThpUserAccount WHERE userID = ?) " +
                    "LIMIT ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userID);
                preparedStatement.setString(2, userID);
                preparedStatement.setInt(3, number);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        ThpTransaction transaction = new ThpTransaction();
                        transaction.setSourceCardNumber(resultSet.getString("sourceCardNumber"));
                        transaction.setDestinationCardNumber(resultSet.getString("destinationCardNumber"));
                        transaction.setAmount(resultSet.getBigDecimal("amount"));
                        transaction.setSuccess(resultSet.getBoolean("isSuccess"));
                        transaction.setDateTime(resultSet.getObject("dateTime", LocalDateTime.class));
                        transaction.setTraceNumber(resultSet.getString("traceNumber"));
                        transaction.setTransactionType( ThpTransactionType.fromType(Integer.valueOf(resultSet.getString("transactionType"))));

                        transactions.add(transaction);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    @Override
    public ThpTransaction traceTransaction(String traceNumber, String userID) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT * FROM ThpTransaction WHERE traceNumber = ? " +
                    "AND (sourceCardNumber IN " +
                    "(SELECT cardNumber FROM ThpUserAccount WHERE userID = ?) " +
                    "OR destinationCardNumber IN " +
                    "(SELECT cardNumber FROM ThpUserAccount WHERE userID = ?))";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, traceNumber);
                preparedStatement.setString(2, userID);
                preparedStatement.setString(3, userID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        ThpTransaction transaction = new ThpTransaction();
                        transaction.setSourceCardNumber(resultSet.getString("sourceCardNumber"));
                        transaction.setDestinationCardNumber(resultSet.getString("destinationCardNumber"));
                        transaction.setAmount(resultSet.getBigDecimal("amount"));
                        transaction.setSuccess(resultSet.getBoolean("isSuccess"));
                        transaction.setDateTime(resultSet.getObject("dateTime", LocalDateTime.class));
                        transaction.setTraceNumber(resultSet.getString("traceNumber"));
                        transaction.setTransactionType(ThpTransactionType.fromType(Integer.valueOf(resultSet.getString("transactionType"))));

                        return transaction;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}

