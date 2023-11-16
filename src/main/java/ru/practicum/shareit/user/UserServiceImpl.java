package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDtoRs save(UserCreateDtoRq userDto) {
        if (checkDuplicateEmail(userMapper.userCreateDtoToUser(userDto))) {
            throw new DuplicateException("Email already exists!");
        } else {
            return userMapper.userToUserDtoRs(userRepository.saveUser(userMapper.userCreateDtoToUser(userDto)));
        }
    }

    @Override
    public UserDtoRs findById(int userId) {
        return userMapper.userToUserDtoRs(findUserById(userId));
    }

    @Override
    public List<UserDtoRs> getUsers() {
        return userToUserDtoRsFromList(userRepository.getUsers());
    }

    @Override
    public UserDtoRs updateUserById(int userid, UserUpdateDtoRq userDto) {
        if (isValidId(userid)) {
            User userUpdate = userMapper.userUpdateDtoToUser(userDto);
            userUpdate.setId(userid);
            if (checkDuplicateEmail(userUpdate)) {
                throw new DuplicateException("Email already exists!");
            } else {
                User userInRepository = userRepository.findById(userid);
                userUpdate = updateUserByField(userInRepository, userUpdate);
                return userMapper.userToUserDtoRs(userRepository.updateUser(userUpdate));
            }
        } else {
            throw new IdNotFoundException("Not found user by id");
        }
    }

    @Override
    public void deleteUserById(int userId) {
        userRepository.deleteUserById(userId);
    }

    @Override
    public boolean isValidId(int id) {
        return userRepository.isValidId(id);
    }

    public boolean checkDuplicateEmail(User user) {
        return userRepository.getUsers().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && user.getId() != u.getId());
    }

    private User updateUserByField(User user, User userUpdate) {
        if (userUpdate.getName() != null && !"".equals(userUpdate.getName())) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null && !"".equals(userUpdate.getName())) {
            user.setEmail(userUpdate.getEmail());
        }
        return user;
    }

    private List<UserDtoRs> userToUserDtoRsFromList(List<User> userList) {
        List<UserDtoRs> userDtoRsList = new ArrayList<>();
        for (User user : userList) {
            userDtoRsList.add(userMapper.userToUserDtoRs(user));
        }
        return userDtoRsList;
    }

    public User findUserById(int userId) {
        return userRepository.findById(userId);
    }
}
