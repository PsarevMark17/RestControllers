package su.psarev.kata.SpringBootSecurity.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import su.psarev.kata.SpringBootSecurity.entities.User;
import su.psarev.kata.SpringBootSecurity.services.UserService;
import su.psarev.kata.SpringBootSecurity.utils.UserValidatorCreate;
import su.psarev.kata.SpringBootSecurity.utils.UserValidatorUpdate;

import java.util.*;

@RestController
@RequestMapping("/api")
public class RestControllerImpl {
    UserService userService;
    private final UserValidatorCreate userValidatorCreate;
    private final UserValidatorUpdate userValidatorUpdate;

    public RestControllerImpl(UserService userService, UserValidatorCreate userValidatorCreate, UserValidatorUpdate userValidatorUpdate) {
        this.userService = userService;
        this.userValidatorCreate = userValidatorCreate;
        this.userValidatorUpdate = userValidatorUpdate;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/authentication")
    public User getAuthentication() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/")
    public List<User> getAllUsers() {
        return userService.readAllUsers();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.readUserById(id);
    }

    @PostMapping("/")
    public ResponseEntity<Void> createUser(@RequestBody @Valid User user, BindingResult bindingResult) throws MethodArgumentNotValidException {
        userValidatorCreate.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            //noinspection DataFlowIssue
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(
                                userService.createUser(user).getId()
                        )
                        .toUri()
        ).build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/")
    public void updateUser(@RequestBody User user, BindingResult bindingResult) throws MethodArgumentNotValidException {
        userValidatorUpdate.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            //noinspection DataFlowIssue
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
        userService.updateUser(user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Collection<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, Collection<String>> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> errors.computeIfAbsent(((FieldError) error).getField(), k -> new HashSet<>()).add(error.getDefaultMessage()));
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return exception.getMessage();
    }
}