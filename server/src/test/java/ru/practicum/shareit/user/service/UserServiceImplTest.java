package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    @Autowired
    private final UserServiceImpl userService;

    @MockBean
    private final UserRepository userRepository;
    private static UserCreateDtoRq userCreateFromRq;
    private static UserUpdateDtoRq userUpdateFromRq;

    @BeforeAll
    public static void setUp() {
        userCreateFromRq = UserCreateDtoRq.builder()
                .name("Oleg")
                .email("Oleg@test.ru")
                .build();
        userUpdateFromRq = UserUpdateDtoRq.builder()
                .name("Stepan")
                .email("Stepan@test.ru")
                .build();
    }

    @Test
    void create() {
        User user = User.builder()
                .id(1)
                .name(userCreateFromRq.getName())
                .email(userCreateFromRq.getEmail())
                .build();

        when(userRepository.save(any()))
                .thenReturn(user);

        UserDtoRs userDtoRs = userService.save(userCreateFromRq);
        assertThat(userDtoRs.getName(), equalTo(userCreateFromRq.getName()));
        assertThat(userDtoRs.getEmail(), equalTo(userCreateFromRq.getEmail()));
    }


    @Test
    void update() {
        User userOld = User.builder()
                .id(1)
                .name("nameOld")
                .email("userOld@email.com")
                .build();

        User userNew = User.builder()
                .id(1)
                .name(userUpdateFromRq.getName())
                .email(userUpdateFromRq.getEmail())
                .build();

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(userOld));

        when(userRepository.save(any()))
                .thenReturn(userNew);
        when(userRepository.existsById(any()))
                .thenReturn(true);

        UserDtoRs userDtoRs = userService.updateUserById(1, userUpdateFromRq);

        assertThat(userDtoRs.getName(), equalTo(userUpdateFromRq.getName()));
        assertThat(userDtoRs.getEmail(), equalTo(userUpdateFromRq.getEmail()));
    }

    @Test
    void getNotFoundException() {
        when(userRepository.findById(1))
                .thenReturn(Optional.empty());

        final EntityNotFoundException updateException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserById(1, userUpdateFromRq)
        );

        final EntityNotFoundException getException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.findById(1)
        );

        assertThat(updateException.getMessage(), equalTo("Not found user by id"));
        assertThat(getException.getMessage(), equalTo("User with id 1 not found"));
    }

    @Test
    void getUser() {
        User user = User.builder()
                .id(1)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        UserDtoRs userDtoRs = userService.findById(1);

        assertThat(userDtoRs.getName(), equalTo("name"));
        assertThat(userDtoRs.getEmail(), equalTo("user@email.com"));
    }

    @Test
    void delete() {
        userService.deleteUserById(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void getAll() {

        List<User> users = List.of(
                User.builder()
                .id(1)
                .name("Dima")
                .email("dima@email.com")
                .build(),
                User.builder()
                        .id(2)
                        .name("Roma")
                        .email("roma@email.com")
                        .build()
        );

        when(userRepository.findAll())
                .thenReturn(users);

        List<UserDtoRs> userDtoRsList = userService.getUsers();

        assertThat(userDtoRsList.size(), equalTo(2));
        assertThat(userDtoRsList.get(0).getName(), equalTo("Dima"));
        assertThat(userDtoRsList.get(0).getEmail(), equalTo("dima@email.com"));
        assertThat(userDtoRsList.get(1).getName(), equalTo("Roma"));
        assertThat(userDtoRsList.get(1).getEmail(), equalTo("roma@email.com"));
    }
}