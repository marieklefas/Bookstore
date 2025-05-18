package com.example.Bookstore.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.Bookstore.DataBases.User;
import com.example.Bookstore.Services.UserService;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Period;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String personlogin() {
        return "Toolbar/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "Toolbar/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, @RequestParam("confirmPassword") String confirmPassword, Model model) {
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "Toolbar/register";
        }

        LocalDate birthDate = user.getBirthDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 12 || age > 120) {
            model.addAttribute("error", "Возраст должен быть от 12 до 120 лет");
            return "Toolbar/register";
        }

        if (userService.registerUser(user)) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Пользователь уже существует");
            return "Toolbar/register";
        }
    }
}
