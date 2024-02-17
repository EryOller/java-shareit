package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDtoRs save(UserCreateDtoRq userDto) {
        try {
            return userMapper.toUserDtoRs(userRepository.save(userMapper.toUser(userDto)));
        } catch (Exception e) {
            throw new DuplicateException("The mail already exists ");
        }
    }

    @Override
    public UserDtoRs findById(int userId) {
        return userMapper.toUserDtoRs(findUserById(userId));
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
                userUpdate = updateUserByField(userInRepository, userUpdate);
                return userMapper.toUserDtoRs(userRepository.save(userUpdate));
            }
        } else {
            throw new EntityNotFoundException("Not found user by id");
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

    public boolean  checkDuplicateEmail(User user) {
        User userFromRepository = userRepository.getUserByEmail(user.getEmail());
        if (userFromRepository != null && userFromRepository.getId() != user.getId()) {
            return true;
        } else {
            return false;
        }
    }

    public User findUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
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
