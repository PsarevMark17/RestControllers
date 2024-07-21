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
import su.psarev.kata.SpringBootSecurity.services.UserService;
import su.psarev.kata.SpringBootSecurity.utils.UserValidatorCreate;
import su.psarev.kata.SpringBootSecurity.utils.UserValidatorUpdate;
import su.psarev.kata.SpringBootSecurity.utils.Util;

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
        model.addAttribute("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        model.addAttribute("newUser", new User());
        model.addAttribute("updUser", new User());
        addDefaultAttributes(model);
        return "admin";
    }

    @PostMapping("/admin/create")
    public String postAdmin(@ModelAttribute("newUser") @Valid User newUser, BindingResult bindingResult, Model model) {
        userValidatorCreate.validate(newUser, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            model.addAttribute("newUser", newUser);
            model.addAttribute("updUser", new User());
            addDefaultAttributes(model);
            return "admin";
        }
        userService.save(newUser);
        return "redirect:/";
    }

    @PostMapping("/admin/delete")
    public String delete(@ModelAttribute("deleteId") Long deleteId) {
        userService.deleteUserById(deleteId);
        return "redirect:/";
    }

    @PostMapping("/admin/update")
    public String update(@RequestParam("id") Long id, @ModelAttribute("updUser") User updUser, BindingResult bindingResult, Model model) {
        if (updUser.getPassword().isEmpty()) {
            updUser.setPassword("Z5F7VnqXxNgeBtdkWsX8sm9bhBmVC9ryH2Pd3y8uQDGyWJtTd3VjfGqT57aKsdkZJXGBCBuXBCRuxVk79wA3MRXJCsfTqDAxTMHdtrxv5NfMss6ft34R8DQJK82YDND5GmV3fPYyVRxUntJQ6ewk4cFW7Ew5dnxnmpHcEjz9tf3x59vJrx9mas6S8YJ5B2TBgNwDpwSzzMesRy2baaZ5pwMp65Sg3Nk7Ttjw8nFJMWsHw5bY9bkwMfBmdWtdzmbq");
        }
        userValidatorUpdate.validate(updUser, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            model.addAttribute("newUser", new User());
            model.addAttribute("updUser", updUser);
            addDefaultAttributes(model);
            return "admin";
        }
        userService.updateUserById(id, updUser);
        return "redirect:/";
    }

    public void addDefaultAttributes(Model model) {
        //noinspection InstantiationOfUtilityClass
        model
                .addAttribute("users", userService.findAll())
                .addAttribute("Util", new Util())
                .addAttribute("ROLE_ADMIN", Role.ADMIN)
                .addAttribute("ROLE_USER", Role.USER)
                .addAttribute("roles", Set.of(Role.ADMIN, Role.USER));
    }
}