CREATE TABLE ThpUserAccount (
    accountID INT AUTO_INCREMENT PRIMARY KEY,
    userID varchar(255),
    cardNumber VARCHAR(16) UNIQUE,
    shebaNumber VARCHAR(26) UNIQUE,
    balance DECIMAL(19, 2) DEFAULT 0,
    dailySatnaPayaLimit DECIMAL(19, 2) DEFAULT 40000000.00,
    lastResetDate DATE DEFAULT CURRENT_DATE,
    dailyLimit DECIMAL(19, 2) DEFAULT 10000000.00,
    FOREIGN KEY (userID) REFERENCES ThpUser(userID)

);
