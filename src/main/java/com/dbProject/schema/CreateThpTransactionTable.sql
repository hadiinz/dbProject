CREATE TABLE ThpTransaction (
    traceNumber VARCHAR(255) PRIMARY KEY,
    sourceCardNumber VARCHAR(255),
    destinationCardNumber VARCHAR(255),
    amount DECIMAL(19, 2),
    isSuccess BOOLEAN,
    dateTime TIMESTAMP,
    transactionType INT  CHECK (transactionType IN (0, 1, 2)),
    FOREIGN KEY (sourceCartNumber) REFERENCES ThpUserAccount(cartNumber),
    FOREIGN KEY (destinationCartNumber) REFERENCES ThpUserAccount(cartNumber)
);