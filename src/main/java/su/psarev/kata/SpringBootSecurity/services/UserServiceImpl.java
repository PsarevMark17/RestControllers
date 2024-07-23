package su.psarev.kata.SpringBootSecurity.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.psarev.kata.SpringBootSecurity.entities.User;
import su.psarev.kata.SpringBootSecurity.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> readAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return user.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> readUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        if ("Z5F7VnqXxNgeBtdkWsX8sm9bhBmVC9ryH2Pd3y8uQDGyWJtTd3VjfGqT57aKsdkZJXGBCBuXBCRuxVk79wA3MRXJCsfTqDAxTMHdtrxv5NfMss6ft34R8DQJK82YDND5GmV3fPYyVRxUntJQ6ewk4cFW7Ew5dnxnmpHcEjz9tf3x59vJrx9mas6S8YJ5B2TBgNwDpwSzzMesRy2baaZ5pwMp65Sg3Nk7Ttjw8nFJMWsHw5bY9bkwMfBmdWtdzmbq".equals(user.getPassword())) {
            userRepository.findUserPasswordById(user.getId()).ifPresent(user::setPassword);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}