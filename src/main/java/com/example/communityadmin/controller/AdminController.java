package com.example.communityadmin.controller;

import com.example.communityadmin.entity.*;
import com.example.communityadmin.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private ResidentService residentService;
    @Autowired private DocumentRequestService documentRequestService;
    @Autowired private IssueReportService issueReportService;

    private User getAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null && user.getRole().equals("ADMIN")) return user;
        return null;
    }

    // ── Dashboard ─────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("totalResidents", residentService.countAll());
        model.addAttribute("totalRequests", documentRequestService.countAll());
        model.addAttribute("pendingRequests", documentRequestService.countByStatus("Submitted"));
        model.addAttribute("totalIssues", issueReportService.countAll());
        model.addAttribute("unresolvedIssues", issueReportService.countByStatus("Reported"));
        model.addAttribute("totalUsers", userService.findAll().size());
        model.addAttribute("user", getAdmin(session));
        return "admin/dashboard";
    }

    // ── User Management ───────────────────────────────────────────

    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", getAdmin(session));
        return "admin/users";
    }

    @GetMapping("/users/deactivate/{id}")
    public String deactivateUser(@PathVariable int id, HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";
        userService.updateActiveStatus(id, false);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/activate/{id}")
    public String activateUser(@PathVariable int id, HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";
        userService.updateActiveStatus(id, true);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable int id, HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";
        userService.delete(id);
        return "redirect:/admin/users";
    }

    // ── Resident Management ───────────────────────────────────────

    @GetMapping("/residents")
    public String listResidents(HttpSession session, Model model,
                                @RequestParam(required = false) String keyword) {
        if (getAdmin(session) == null) return "redirect:/login";
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("residents", residentService.searchByKeyword(keyword));
        } else {
            model.addAttribute("residents", residentService.findAll());
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("user", getAdmin(session));
        return "admin/residents";
    }

    @GetMapping("/residents/add")
    public String addResidentPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("user", getAdmin(session));
        return "admin/resident-form";
    }

    @PostMapping("/residents/add")
    public String addResident(@RequestParam String nik,
                              @RequestParam String fullName,
                              @RequestParam String dateOfBirth,
                              @RequestParam String gender,
                              @RequestParam String address,
                              @RequestParam String religion,
                              @RequestParam String occupation,
                              @RequestParam String maritalStatus,
                              HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        try {
            Resident resident = new Resident();
            resident.setNik(nik);
            resident.setFullName(fullName);
            resident.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
            resident.setGender(gender);
            resident.setAddress(address);
            resident.setReligion(religion);
            resident.setOccupation(occupation);
            resident.setMaritalStatus(maritalStatus);
            residentService.save(resident);
            return "redirect:/admin/residents";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", getAdmin(session));
            return "admin/resident-form";
        }
    }

    @GetMapping("/residents/edit/{id}")
    public String editResidentPage(@PathVariable int id,
                                   HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("resident", residentService.findById(id));
        model.addAttribute("user", getAdmin(session));
        return "admin/resident-edit";
    }

    @PostMapping("/residents/edit/{id}")
    public String editResident(@PathVariable int id,
                               @RequestParam String nik,
                               @RequestParam String fullName,
                               @RequestParam String dateOfBirth,
                               @RequestParam String gender,
                               @RequestParam String address,
                               @RequestParam String religion,
                               @RequestParam String occupation,
                               @RequestParam String maritalStatus,
                               HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";
        Resident resident = residentService.findById(id);
        resident.setNik(nik);
        resident.setFullName(fullName);
        resident.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
        resident.setGender(gender);
        resident.setAddress(address);
        resident.setReligion(religion);
        resident.setOccupation(occupation);
        resident.setMaritalStatus(maritalStatus);
        residentService.update(resident);
        return "redirect:/admin/residents";
    }

    @GetMapping("/residents/delete/{id}")
    public String deleteResident(@PathVariable int id, HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";
        residentService.delete(id);
        return "redirect:/admin/residents";
    }

    // ── Document Request Management ───────────────────────────────

    @GetMapping("/requests")
    public String listRequests(HttpSession session, Model model,
                               @RequestParam(required = false) String status) {
        if (getAdmin(session) == null) return "redirect:/login";
        if (status != null && !status.isEmpty()) {
            model.addAttribute("requests", documentRequestService.findByStatus(status));
        } else {
            model.addAttribute("requests", documentRequestService.findAll());
        }
        model.addAttribute("selectedStatus", status);
        model.addAttribute("user", getAdmin(session));
        return "admin/requests";
    }

    @GetMapping("/requests/{id}/approve")
    public String approveRequest(@PathVariable int id, HttpSession session) {
        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";
        documentRequestService.approve(id, admin.getId());
        return "redirect:/admin/requests";
    }

    @GetMapping("/requests/{id}/ready")
    public String markReady(@PathVariable int id, HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";
        documentRequestService.markReady(id);
        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/{id}/reject")
    public String rejectRequest(@PathVariable int id,
                                @RequestParam String reason,
                                HttpSession session) {
        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";
        documentRequestService.reject(id, admin.getId(), reason);
        return "redirect:/admin/requests";
    }

    // ── Issue Report Management ───────────────────────────────────

    @GetMapping("/issues")
    public String listIssues(HttpSession session, Model model,
                             @RequestParam(required = false) String status) {
        if (getAdmin(session) == null) return "redirect:/login";
        if (status != null && !status.isEmpty()) {
            model.addAttribute("issues", issueReportService.findByStatus(status));
        } else {
            model.addAttribute("issues", issueReportService.findAll());
        }
        model.addAttribute("staff", userService.findByRole("STAFF"));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("user", getAdmin(session));
        return "admin/issues";
    }

    @PostMapping("/issues/{id}/assign")
    public String assignIssue(@PathVariable int id,
                              @RequestParam int staffId,
                              HttpSession session) {
        if (getAdmin(session) == null) return "redirect:/login";
        issueReportService.assign(id, staffId);
        return "redirect:/admin/issues";
    }
}