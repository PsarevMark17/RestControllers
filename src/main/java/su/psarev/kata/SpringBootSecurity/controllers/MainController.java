package su.psarev.kata.SpringBootSecurity.controllers;

import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import su.psarev.kata.SpringBootSecurity.entities.Role;
import su.psarev.kata.SpringBootSecurity.entities.User;
import su.psarev.kata.SpringBootSecurity.services.UserServiceImpl;
import su.psarev.kata.SpringBootSecurity.utils.UserValidatorCreate;
import su.psarev.kata.SpringBootSecurity.utils.UserValidatorUpdate;
import su.psarev.kata.SpringBootSecurity.utils.Util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private final UserServiceImpl userServiceImpl;
    private final UserValidatorCreate userValidatorCreate;
    private final UserValidatorUpdate userValidatorUpdate;

    public MainController(UserServiceImpl userServiceImpl, UserValidatorCreate userValidatorCreate, UserValidatorUpdate userValidatorUpdate) {
        this.userServiceImpl = userServiceImpl;
        this.userValidatorCreate = userValidatorCreate;
        this.userValidatorUpdate = userValidatorUpdate;
    }

    @GetMapping("/")
    public String getIndex() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getAuthorities().contains(Role.ADMIN) ? "redirect:/admin" : user.getAuthorities().contains(Role.USER) ? "redirect:/user" : "redirect:/error";
        } catch (ClassCastException e) {
            return "index";
        }
    }

    @GetMapping("/user")
    public String getUser(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("roles", Util.setToString(user.getAuthorities()));
        return "user";
    }

    @GetMapping("/admin")
    public String getAdmin(Model model) {
        List<User> users = userServiceImpl.findAll();
        model.addAttribute("users", users);
        model.addAttribute("users_roles", users.stream().map(user -> Util.setToString(user.getAuthorities())).collect(Collectors.toList()));
        model.addAttribute("newUser", new User());
        model.addAttribute("roles", Set.of(Role.ADMIN, Role.USER));
        return "admin";
    }

    @PostMapping("/admin")
    public String postAdmin(@ModelAttribute("newUser") @Valid User newUser, BindingResult bindingResult, Model model) {
        userValidatorCreate.validate(newUser, bindingResult);
        if (bindingResult.hasErrors()) {
            List<User> users = userServiceImpl.findAll();
            model.addAttribute("users", users);
            model.addAttribute("users_roles", users.stream().map(user -> Util.setToString(user.getAuthorities())).collect(Collectors.toList()));
            model.addAttribute("newUser", newUser);
            model.addAttribute("roles", Set.of(Role.ADMIN, Role.USER));
            return "admin";
        }
        userServiceImpl.save(newUser);
        return "redirect:/admin";
    }

    @PostMapping("/admin/delete")
    public String delete(@ModelAttribute("deleteId") Long deleteId) {
        userServiceImpl.deleteUserById(deleteId);
        return "redirect:/admin";
    }

    @GetMapping("/admin/update")
    public String update(@RequestParam("id") Long id, Model model) {
        User user = userServiceImpl.loadUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", Set.of(Role.ADMIN, Role.USER));
        return "update";
    }

    @PostMapping("/admin/update")
    public String update(@RequestParam("id") Long id, @ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        userValidatorUpdate.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", Set.of(Role.ADMIN, Role.USER));
            return "update";
        }
        userServiceImpl.updateUserById(id, user);
        return "redirect:/admin";
    }
}