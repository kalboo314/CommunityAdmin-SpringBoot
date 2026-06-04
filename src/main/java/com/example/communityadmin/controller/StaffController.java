package com.example.communityadmin.controller;

import com.example.communityadmin.entity.*;
import com.example.communityadmin.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired private ResidentService residentService;
    @Autowired private DocumentRequestService documentRequestService;
    @Autowired private IssueReportService issueReportService;
    @Autowired private UserService userService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private User getStaff(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null && user.getRole().equals("STAFF")) return user;
        return null;
    }

    // ── Dashboard ─────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User staff = getStaff(session);
        if (staff == null) return "redirect:/login";
        model.addAttribute("pendingRequests", documentRequestService.countByStatus("Submitted"));
        model.addAttribute("myIssues", issueReportService.findByAssignedStaffId(staff.getId()).size());
        model.addAttribute("totalResidents", residentService.countAll());
        model.addAttribute("user", staff);
        return "staff/dashboard";
    }

    // ── Registration Approvals ────────────────────────────────────

    @GetMapping("/registrations")
    public String registrations(HttpSession session, Model model) {
        if (getStaff(session) == null) return "redirect:/login";
        model.addAttribute("pendingUsers", userService.findPending());
        model.addAttribute("user", getStaff(session));
        return "staff/registrations";
    }

    @GetMapping("/registrations/{id}/approve")
    public String approveRegistration(@PathVariable int id, HttpSession session) {
        if (getStaff(session) == null) return "redirect:/login";
        userService.updateActiveStatus(id, true);
        return "redirect:/staff/registrations";
    }

    // ── Resident Management ───────────────────────────────────────

    @GetMapping("/residents")
    public String listResidents(HttpSession session, Model model,
                                @RequestParam(required = false) String keyword) {
        if (getStaff(session) == null) return "redirect:/login";
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("residents", residentService.searchByKeyword(keyword));
        } else {
            model.addAttribute("residents", residentService.findAll());
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("user", getStaff(session));
        return "staff/residents";
    }

    @GetMapping("/residents/add")
    public String addResidentPage(HttpSession session, Model model) {
        if (getStaff(session) == null) return "redirect:/login";
        model.addAttribute("user", getStaff(session));
        return "staff/resident-form";
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
        if (getStaff(session) == null) return "redirect:/login";
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
            return "redirect:/staff/residents";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", getStaff(session));
            return "staff/resident-form";
        }
    }

    @GetMapping("/residents/edit/{id}")
    public String editResidentPage(@PathVariable int id,
                                   HttpSession session, Model model) {
        if (getStaff(session) == null) return "redirect:/login";
        model.addAttribute("resident", residentService.findById(id));
        model.addAttribute("user", getStaff(session));
        return "staff/resident-edit";
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
        if (getStaff(session) == null) return "redirect:/login";
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
        return "redirect:/staff/residents";
    }

    // ── Document Request Management ───────────────────────────────

    @GetMapping("/requests")
    public String listRequests(HttpSession session, Model model) {
        if (getStaff(session) == null) return "redirect:/login";
        model.addAttribute("requests", documentRequestService.findAll());
        model.addAttribute("user", getStaff(session));
        return "staff/requests";
    }

    @GetMapping("/requests/{id}/process")
    public String processRequest(@PathVariable int id, HttpSession session) {
        User staff = getStaff(session);
        if (staff == null) return "redirect:/login";
        try {
            documentRequestService.process(id, staff.getId());
        } catch (RuntimeException e) {
            return "redirect:/staff/requests?error=" + e.getMessage();
        }
        return "redirect:/staff/requests";
    }

    // ── Issue Report Management ───────────────────────────────────

    @GetMapping("/issues")
    public String listIssues(HttpSession session, Model model) {
        User staff = getStaff(session);
        if (staff == null) return "redirect:/login";
        model.addAttribute("myIssues", issueReportService.findByAssignedStaffId(staff.getId()));
        model.addAttribute("allIssues", issueReportService.findByStatus("Reported"));
        model.addAttribute("user", staff);
        return "staff/issues";
    }

    @PostMapping("/issues/{id}/update")
    public String updateIssue(@PathVariable int id,
                              @RequestParam String status,
                              @RequestParam(required = false) String resolutionNotes,
                              @RequestParam(required = false) MultipartFile resolutionPhoto,
                              HttpSession session) {
        User staff = getStaff(session);
        if (staff == null) return "redirect:/login";
        String photoPath = null;
        if (resolutionPhoto != null && !resolutionPhoto.isEmpty()) {
            photoPath = saveFile(resolutionPhoto);
        }
        issueReportService.updateStatus(id, status, resolutionNotes, photoPath, staff.getId());
        return "redirect:/staff/issues";
    }

    private String saveFile(MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}