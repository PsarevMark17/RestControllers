package su.psarev.kata.SpringBootSecurity.utils;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import su.psarev.kata.SpringBootSecurity.entities.User;
import su.psarev.kata.SpringBootSecurity.services.UserServiceImpl;

import java.util.Optional;

@Component
public class UserValidatorUpdate implements Validator {
    private final UserServiceImpl userServiceImpl;

    public UserValidatorUpdate(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        User user = (User) target;
        Optional<User> probablyUser = userServiceImpl.findByUsername(user.getUsername());
        if (probablyUser.isPresent()) {
            if (!probablyUser.get().getId().equals(user.getId())) {
                errors.rejectValue("username", "", "Пользователь с таким адресом электронной почты уже существует");
            }
        }
    }
}