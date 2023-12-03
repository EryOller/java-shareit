package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDtoRs save(UserCreateDtoRq userDto) {
//        try {
            return userMapper.toUserDtoRs(userRepository.save(userMapper.toUser(userDto)));
//        } catch (SQLException e) {
//            throw new DuplicateException("Email already exists!");
//        }


//        if (checkDuplicateEmail(userMapper.toUser(userDto))) {
//            throw new DuplicateException("Email already exists!");
//        } else {
//            return userMapper.toUserDtoRs(userRepository.save(userMapper.toUser(userDto)));
//        }
    }

    @Override
    public UserDtoRs findById(int userId) {
        try {
            return userMapper.toUserDtoRs(findUserById(userId));
        } catch (NoSuchElementException e) {
            throw new IdNotFoundException("User with id " + userId + " not found");
        }
    }

    @Override
    public List<UserDtoRs> getUsers() {
        return userMapper.toListItemDtoRs(userRepository.findAll());
    }

    @Override
    public UserDtoRs updateUserById(int userid, UserUpdateDtoRq userDto) {
        if (isValidId(userid)) {
            User userUpdate = userMapper.toUser(userDto);
            userUpdate.setId(userid);
            if (checkDuplicateEmail(userUpdate)) {
                throw new DuplicateException("Email already exists!");
            } else {
                User userInRepository = userRepository.findById(userid).get();
                //User userInRepository = userRepository.findById(userid);
                userUpdate = updateUserByField(userInRepository, userUpdate);
                return userMapper.toUserDtoRs(userRepository.save(userUpdate));
            }
        } else {
            throw new IdNotFoundException("Not found user by id");
        }
    }

    @Override
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean isValidId(int id) {
        return userRepository.existsById(id);
    }

    public boolean checkDuplicateEmail(User user) {
        return userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && user.getId() != u.getId());
    }

    public User findUserById(int userId) {
        return userRepository.findById(userId).get();
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
