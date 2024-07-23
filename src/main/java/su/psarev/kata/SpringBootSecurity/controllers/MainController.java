package su.psarev.kata.SpringBootSecurity.controllers;

import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import su.psarev.kata.SpringBootSecurity.entities.Role;
import su.psarev.kata.SpringBootSecurity.entities.User;
import su.psarev.kata.SpringBootSecurity.services.UserService;
import su.psarev.kata.SpringBootSecurity.utils.UserValidatorCreate;
import su.psarev.kata.SpringBootSecurity.utils.UserValidatorUpdate;

import java.util.Set;

@Controller
public class MainController {
    private final UserService userService;
    private final UserValidatorCreate userValidatorCreate;
    private final UserValidatorUpdate userValidatorUpdate;

    public MainController(UserService userService, UserValidatorCreate userValidatorCreate, UserValidatorUpdate userValidatorUpdate) {
        this.userService = userService;
        this.userValidatorCreate = userValidatorCreate;
        this.userValidatorUpdate = userValidatorUpdate;
    }

    @GetMapping("/")
    public String getAdmin(Model model) {
        addDefaultAttributes(model, new User(), new User());
        return "admin";
    }

    @PostMapping("/admin/create")
    public String postAdmin(@ModelAttribute("newUser") @Valid User newUser, BindingResult bindingResult, Model model) {
        userValidatorCreate.validate(newUser, bindingResult);
        if (bindingResult.hasErrors()) {
            addDefaultAttributes(model, newUser, new User());
            return "admin";
        }
        userService.createUser(newUser);
        return "redirect:/";
    }

    @PostMapping("/admin/delete")
    public String delete(@ModelAttribute("deleteId") Long deleteId) {
        userService.deleteUserById(deleteId);
        return "redirect:/";
    }

    @PostMapping("/admin/update")
    public String update(@ModelAttribute("updUser") User updUser, BindingResult bindingResult, Model model) {
        userValidatorUpdate.validate(updUser, bindingResult);
        if (bindingResult.hasErrors()) {
            addDefaultAttributes(model, new User(), updUser);
            return "admin";
        }
        userService.updateUser(updUser);
        return "redirect:/";
    }

    public void addDefaultAttributes(Model model, User newUser, User updUser) {
        model
                .addAttribute("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .addAttribute("users", userService.readAllUsers())
                .addAttribute("ROLE_ADMIN", Role.ADMIN)
                .addAttribute("ROLE_USER", Role.USER)
                .addAttribute("roles", Set.of(Role.ADMIN, Role.USER))
                .addAttribute("newUser", newUser)
                .addAttribute("updUser", updUser);
    }
}