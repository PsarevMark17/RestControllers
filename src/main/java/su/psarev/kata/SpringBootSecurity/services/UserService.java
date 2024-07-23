package su.psarev.kata.SpringBootSecurity.services;

import su.psarev.kata.SpringBootSecurity.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void createUser(User user);

    List<User> readAllUsers();

    User readUserById(Long id);

    Optional<User> readUserByUsername(String username);

    void updateUser(User user);

    void deleteUserById(Long id);
}