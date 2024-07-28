package su.psarev.kata.SpringBootSecurity.utils;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import su.psarev.kata.SpringBootSecurity.entities.User;
import su.psarev.kata.SpringBootSecurity.services.UserService;

@Component
public class UserValidatorCreate implements Validator {
    private final UserService userService;

    public UserValidatorCreate(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        if (userService.readUserByUsername(((User) target).getUsername()).isPresent()) {
            errors.rejectValue("username", "", "Пользователь с таким адресом электронной почты уже существует");
        }
    }
}