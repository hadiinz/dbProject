CREATE TABLE ThpUser (
    userID varchar(255) PRIMARY KEY,
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    nationalID VARCHAR(10) UNIQUE,
    password VARCHAR(255) NOT NULL,
    dailyLimit DECIMAL(19, 2) DEFAULT 10000000.00,
    lastResetDate DATE DEFAULT CURRENT_DATE
);