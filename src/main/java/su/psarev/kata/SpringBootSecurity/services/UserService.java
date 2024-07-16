package su.psarev.kata.SpringBootSecurity.services;

import org.springframework.security.core.userdetails.UserDetails;
import su.psarev.kata.SpringBootSecurity.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void save(User user);

    List<User> findAll();

    User loadUserById(Long id);

    UserDetails loadUserByUsername(String username);

    Optional<User> findByUsername(String username);

    void updateUserById(Long id, User user);

    void deleteUserById(Long id);
}
