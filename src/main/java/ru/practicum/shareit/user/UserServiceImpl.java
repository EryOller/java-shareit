package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDtoRs save(UserCreateDtoRq userDto) {
        if (checkDuplicateEmail(
                UserMapper.INSTANCE.toUser(userDto))) {
            throw new DuplicateException("Email already exists!");
        } else {
            return UserMapper.INSTANCE.toUserDtoRs(userRepository.saveUser(UserMapper.INSTANCE.toUser(userDto)));
        }
    }

    @Override
    public UserDtoRs findById(int userId) {
        return UserMapper.INSTANCE.toUserDtoRs(findUserById(userId));
    }

    @Override
    public List<UserDtoRs> getUsers() {
        return UserMapper.INSTANCE.toListItemDtoRs(userRepository.getUsers());
    }

    @Override
    public UserDtoRs updateUserById(int userid, UserUpdateDtoRq userDto) {
        if (isValidId(userid)) {
            User userUpdate = UserMapper.INSTANCE.toUser(userDto);
            userUpdate.setId(userid);
            if (checkDuplicateEmail(userUpdate)) {
                throw new DuplicateException("Email already exists!");
            } else {
                User userInRepository = userRepository.findById(userid);
                userUpdate = updateUserByField(userInRepository, userUpdate);
                return UserMapper.INSTANCE.toUserDtoRs(userRepository.updateUser(userUpdate));
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

    public User findUserById(int userId) {
        return userRepository.findById(userId);
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
}
