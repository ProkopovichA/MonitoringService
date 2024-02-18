/**
 * Класс "список" пользователей
 * хранит в себе коллекцию обектов "User"
 * реализован как синглтон
 */

package com.prokopovich.repo;

import com.prokopovich.model.User;

public interface UsersRepo {
    boolean addUser(User user);
    User findUserByLogin(String login);
    User findUserById(int id);


}
