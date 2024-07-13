package su.psarev.kata.SpringBootSecurity.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import su.psarev.kata.SpringBootSecurity.entities.User;
import su.psarev.kata.SpringBootSecurity.entities.Role;
import su.psarev.kata.SpringBootSecurity.services.UserServiceImpl;

import java.time.LocalDate;
import java.util.Set;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final UserServiceImpl userServiceImpl;

    public CommandLineRunnerImpl(UserServiceImpl UserServiceImpl) {
        this.userServiceImpl = UserServiceImpl;
    }

    @Override
    public void run(String... args) {
        if (userServiceImpl.findAll().isEmpty()){
            userServiceImpl.save(
                    new User("admin@mail.su",
                            "Admin123",
                            "Админов",
                            "Админ",
                            "Админович",
                            LocalDate.of(1970, 1, 1),
                            Set.of(Role.ADMIN, Role.USER)));
            userServiceImpl.save(
                    new User("user@mail.su",
                            "User1234",
                            "Пользователев",
                            "Пользователь",
                            "Пользователевич",
                            LocalDate.of(1970, 1, 1),
                            Set.of(Role.USER)));
        }
    }
}