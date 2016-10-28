package com.taskforce.moneyapp.dao;

import com.taskforce.moneyapp.objectModel.User;

import java.util.List;


public interface UserDAO {

    List<User> getAllUsers();
    User getUserById(long userId);
    User getUserByName(String userName);
    boolean insertUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(long userId);
    void createUserTable();

}
