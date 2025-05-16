package com.example.Bookstore.Controllers;

import com.example.Bookstore.Repositories.UserRepository;
import com.example.Bookstore.DataBases.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalUserInfo {

    @Autowired
    private UserRepository userRepository;

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
