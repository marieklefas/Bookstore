package com.example.Bookstore.Controllers;

import com.example.Bookstore.Repositories.UserRepository;
import com.example.Bookstore.DataBases.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;

/**
 * Глобальный контроллер-советник, предоставляющий общую информацию о пользователе
 * для всех представлений приложения. Добавляет атрибуты в модель каждого запроса.
 */
@ControllerAdvice
public class GlobalUserInfo {

    @Autowired
    private UserRepository userRepository;

    /**
     * Добавляет имя текущего аутентифицированного пользователя в модель каждого запроса.
     * Если пользователь не аутентифицирован, атрибут не добавляется.
     *
     * @param principal Объект Principal, представляющий текущего аутентифицированного пользователя
     * @return Имя пользователя или "Пользователь", если имя не найдено, или null если пользователь не аутентифицирован
     */
    @ModelAttribute("firstName")
    public String populateFirstName(Principal principal) {
        if (principal != null) {
            return userRepository.findByUsername(principal.getName())
                    .map(User::getFirstName)
                    .orElse("Пользователь");
        }
        return null;
    }
}
