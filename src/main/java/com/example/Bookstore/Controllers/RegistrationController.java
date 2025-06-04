package com.example.Bookstore.Controllers;

import com.example.Bookstore.Repositories.UserRepository;
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

/**
 * Контроллер для регистрации и аутентификации пользователей.
 * Обрабатывает запросы, связанные с регистрацией новых пользователей и входом в систему.
 */
@Controller
public class RegistrationController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    /**
     * Отображает страницу входа в систему.
     *
     * @return Название представления страницы входа
     */
    @GetMapping("/login")
    public String personlogin() {
        return "Toolbar/login";
    }

    /**
     * Отображает форму регистрации нового пользователя.
     *
     * @param model Модель для передачи данных в представление
     * @return Название представления страницы регистрации
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "Toolbar/register";
    }

    /**
     * Обрабатывает отправку формы регистрации.
     * Проверяет корректность введенных данных и регистрирует нового пользователя.
     *
     * @param user Данные пользователя из формы регистрации
     * @param confirmPassword Подтверждение пароля
     * @param model Модель для передачи сообщений об ошибках
     * @return Перенаправление на главную страницу при успешной регистрации или возврат на форму с ошибками
     */
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
            userRepository.save(user);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Пользователь уже существует");
            return "Toolbar/register";
        }
    }
}
