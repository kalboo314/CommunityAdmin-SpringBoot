package com.example.communityadmin.controller;

import com.example.communityadmin.entity.Resident;
import com.example.communityadmin.entity.User;
import com.example.communityadmin.service.ResidentService;
import com.example.communityadmin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private ResidentService residentService;

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = userService.login(username, password);
        if (user == null) {
            model.addAttribute("error", "Invalid username or password");
            return "auth/login";
        }
        session.setAttribute("loggedInUser", user);

        // If resident, also store their resident profile in session
        if (user.getRole().equals("RESIDENT")) {
            Resident resident = residentService.findByUserId(user.getId());
            session.setAttribute("residentProfile", resident);
        }

        switch (user.getRole()) {
            case "ADMIN": return "redirect:/admin/dashboard";
            case "STAFF": return "redirect:/staff/dashboard";
            case "RESIDENT": return "redirect:/resident/dashboard";
            default: return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String role,
                           Model model) {
        try {
            User user = new User();
            user.setFullName(fullName);
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            userService.register(user);
            return "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}