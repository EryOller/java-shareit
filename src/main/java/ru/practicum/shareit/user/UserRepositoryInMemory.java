package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepositoryInMemory implements UserRepository {
    private Map<Integer, User> users = new HashMap<>();
    private int sequent = 0;

    @Override
    public User saveUser(User user) {
        user.setId(getSequentForUser());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(int userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(int userId) {
        users.remove(userId);
    }

    @Override
    public boolean isValidId(int id) {
        return users.containsKey(id);
    }

    private int getSequentForUser() {
        return ++sequent;
    }
}
