package com.dbProject.service.impl;

import com.dbProject.model.ThpUser;
import com.dbProject.service.ThpUserService;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@Service
public class ThpUserServiceImpl implements ThpUserService {

    @Test()
    public void terrrr(){
        System.out.println(signUp(new ThpUser("11", "fddf", "df","df","34443443")));   }
    // JDBC connection parameters
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/finalprojectdb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "1234";

    @Override
    public boolean signUp(ThpUser user) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            // Check if the provided userID (username) is unique
            if (isUserIDExists(connection, user.getUserID(), user.getNationalId())) {
                return false; // User with this username already exists
            }

            String hashedPassword = hashPassword(user.getPassword());

            String insertQuery = "INSERT INTO ThpUser (userID, firstName, lastName, nationalID, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, user.getUserID());
                preparedStatement.setString(2, user.getFirstName());
                preparedStatement.setString(3, user.getLastName());
                preparedStatement.setString(4, user.getNationalId());
                preparedStatement.setString(5, hashedPassword);

                preparedStatement.executeUpdate();
            }

            return true; // User successfully signed up
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  }
    }

    private boolean validateCredentials(ThpUser storedUser, String providedPassword) {
        return BCrypt.checkpw(providedPassword, storedUser.getPassword());
    }
    @Override
    public String loginUser(String providedUserID, String providedPassword) {
        ThpUser user = getUserByUserID(providedUserID);

        if (user != null) {
            // Validate provided password against the stored hashed password
            if (validateCredentials(user, providedPassword)) {
                return "Login successful";
            } else {
                return "Incorrect password";
            }
        } else {
            return "User not found"; // User with the provided username not found
        }
    }
    public ThpUser getUserByUserID(String userID) {
        ThpUser user = null;

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT * FROM ThpUser WHERE userID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new ThpUser();
                        user.setUserID(resultSet.getString("userID"));
                        user.setFirstName(resultSet.getString("firstName"));
                        user.setLastName(resultSet.getString("lastName"));
                        user.setNationalId(resultSet.getString("nationalID"));
                        user.setPassword(resultSet.getString("password"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }


    private boolean isUserIDExists(Connection connection, String userID, String nationalID) throws SQLException {
        String query = "SELECT COUNT(*) FROM ThpUser WHERE userID = ? or nationalID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, nationalID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }
}
