package su.psarev.kata.SpringBootSecurity.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import su.psarev.kata.SpringBootSecurity.entities.User;
import su.psarev.kata.SpringBootSecurity.services.UserServiceImpl;

import java.util.Optional;
import java.util.Set;

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
        if (user.getPassword().isEmpty()) {
            user.setPassword("Z5F7VnqXxNgeBtdkWsX8sm9bhBmVC9ryH2Pd3y8uQDGyWJtTd3VjfGqT57aKsdkZJXGBCBuXBCRuxVk79wA3MRXJCsfTqDAxTMHdtrxv5NfMss6ft34R8DQJK82YDND5GmV3fPYyVRxUntJQ6ewk4cFW7Ew5dnxnmpHcEjz9tf3x59vJrx9mas6S8YJ5B2TBgNwDpwSzzMesRy2baaZ5pwMp65Sg3Nk7Ttjw8nFJMWsHw5bY9bkwMfBmdWtdzmbq");
        }
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Set<ConstraintViolation<User>> violations = validatorFactory.getValidator().validate(user);
            if (!violations.isEmpty()) {
                violations.forEach(error -> errors.rejectValue(error.getPropertyPath().toString(),"", error.getMessage()));
            }
        }
        Optional<User> probablyUser = userServiceImpl.readUserByUsername(user.getUsername());
        if (probablyUser.isPresent()) {
            if (!probablyUser.get().getId().equals(user.getId())) {
                errors.rejectValue("username", "", "Пользователь с таким адресом электронной почты уже существует");
            }
        }
    }
}