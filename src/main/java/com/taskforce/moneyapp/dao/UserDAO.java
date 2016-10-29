package com.taskforce.moneyapp.dao;

import com.taskforce.moneyapp.objectModel.User;

import java.util.List;


public interface UserDAO {
    List<User> getAllUsers();
    User getUserById(long userId);
    User getUserByName(String userName);
    int insertUser(User user);
    int updateUser(User user);
    int deleteUser(long userId);

}
