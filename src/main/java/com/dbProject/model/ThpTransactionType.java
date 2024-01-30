package com.dbProject.model;

public enum ThpTransactionType {
    SATNA(0),
    PAYA(1),
    CartTOCart(2);

    private int type;

    ThpTransactionType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
