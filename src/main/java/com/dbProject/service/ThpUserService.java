package com.dbProject.service;

import com.dbProject.model.ThpUser;

public interface ThpUserService {
    public boolean signUp(ThpUser user);
    public String loginUser(String providedUserID, String providedPassword);

}
