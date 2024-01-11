package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    private User userOne;

    @BeforeEach
    public void addUsers() {
        userOne = User.builder()
                .email("mail1@mail.ru")
                .name("name1")
                .build();
        userRepository.save(userOne);
        User userTwo = User.builder()
                .email("mail2@mail.ru")
                .name("name2")
                .build();
        userRepository.save(userTwo);
    }

    @Test
    void getUserByEmailOneUser() {
       User user = userRepository.getUserByEmail(userOne.getEmail());
        assertThat(user.getName(), equalTo(userOne.getName()));
        assertThat(user.getEmail(), equalTo(userOne.getEmail()));
    }
}
