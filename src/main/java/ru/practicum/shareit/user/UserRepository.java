package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer>/*, QuerydslPredicateExecutor<User>*/ {
  /*  User saveUser(User user);

    User updateUser(User user);

    User findByUserId(int userId);

    List<User> getUsers();

    void deleteUserById(int userId);

    boolean isValidId(int id);*/
}
