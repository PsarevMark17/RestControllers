package su.psarev.kata.SpringBootSecurity.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import su.psarev.kata.SpringBootSecurity.entities.User;
import su.psarev.kata.SpringBootSecurity.entities.Role;
import su.psarev.kata.SpringBootSecurity.services.UserService;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final UserService userService;

    public CommandLineRunnerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (!userService.readAllUsers().stream().flatMap(user -> user.getAuthorities().stream()).collect(Collectors.toSet()).contains(Role.ADMIN)) {
            userService.readUserByUsername("admin@mail.su").ifPresent(admin -> userService.deleteUserById(admin.getId()));
            userService.createUser(
                    new User("admin@mail.su",
                            "Admin123",
                            "Админов",
                            "Админ",
                            "Админович",
                            LocalDate.of(1970, 1, 1),
                            Set.of(Role.ADMIN)));
        }
        if (!userService.readAllUsers().stream().flatMap(user -> user.getAuthorities().stream()).collect(Collectors.toSet()).contains(Role.USER)) {
            userService.readUserByUsername("user@mail.su").ifPresent(user -> userService.deleteUserById(user.getId()));
            userService.createUser(
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