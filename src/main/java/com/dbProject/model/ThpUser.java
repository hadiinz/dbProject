package com.dbProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThpUser {
    private String userID;
    private String password;
    private String firstName;
    private String lastName;
    private String nationalId;
}
