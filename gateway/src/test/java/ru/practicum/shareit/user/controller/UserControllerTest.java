package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserCreateDtoRq userDtoCreateTest;

    private UserDtoRs userDtoCreated;

    private UserUpdateDtoRq userDtoUpdateTest;

    private UserDtoRs userDtoUpdated;

    @BeforeEach
    void setUp() {
        userDtoCreateTest = UserCreateDtoRq.builder()
                .name("userCreate")
                .email("userTest@email.com")
                .build();
        userDtoCreated = UserDtoRs.builder()
                .id(1)
                .name("userCreate")
                .email("userTest@email.com")
                .build();
        userDtoUpdateTest = UserUpdateDtoRq.builder()
                .name("userUpdate")
                .build();
        userDtoUpdated = UserDtoRs.builder()
                .id(1)
                .name("userUpdate")
                .email("userTest@email.com")
                .build();
    }

    @AfterEach
    void tearDown() {
        userDtoCreateTest = null;
        userDtoCreated = null;
        userDtoUpdateTest = null;
        userDtoUpdated = null;
    }

    @Test
    void create() throws Exception {
        when(userService.save(any(UserCreateDtoRq.class)))
                .thenReturn(userDtoCreated);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDtoCreated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoCreated.getEmail())));
    }

    @Test
    void update() throws Exception {
        when(userService.updateUserById(anyInt(),any(UserUpdateDtoRq.class))).thenReturn(userDtoUpdated);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdated.getEmail())));
    }

    @Test
    void getUserDto() throws Exception {
        when(userService.findById(1)).thenReturn(userDtoUpdated);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdated.getEmail())));
    }

    @Test
    void deleteUserDto() throws Exception {
        mvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService).deleteUserById(1);
    }

    @Test
    void getAll() throws Exception {
        when(userService.getUsers())
                .thenReturn(List.of(userDtoUpdated));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoUpdated.getEmail())));
    }

    @Test
    void throwDuplicateException() {

        when(userService.save(any()))
                .thenThrow(new DuplicateException("Почта уже существует"));

        DuplicateException duplicateException;

        duplicateException = Assertions.assertThrows(DuplicateException.class,
                () -> userService.save(userDtoCreateTest));
        assertThat(duplicateException.getMessage(), is("Почта уже существует"));
    }
}
