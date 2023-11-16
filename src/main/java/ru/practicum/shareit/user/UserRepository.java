package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    User updateUser(User user);

    User findById(int userId);

    List<User> getUsers();

    void deleteUserById(int userId);

    boolean isValidId(int id);
}
