package com.example.communityadmin.controller;

import com.example.communityadmin.entity.*;
import com.example.communityadmin.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/resident")
public class ResidentController {

    @Autowired private DocumentRequestService documentRequestService;
    @Autowired private IssueReportService issueReportService;
    @Autowired private ResidentService residentService;

    private User getResident(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null && user.getRole().equals("RESIDENT")) return user;
        return null;
    }

    private Resident getResidentProfile(HttpSession session) {
        return (Resident) session.getAttribute("residentProfile");
    }

    // ── Dashboard ─────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = getResident(session);
        if (user == null) return "redirect:/login";
        Resident resident = getResidentProfile(session);
        if (resident == null) {
            model.addAttribute("noProfile", true);
            model.addAttribute("user", user);
            return "resident/dashboard";
        }
        model.addAttribute("myRequests", documentRequestService.findByResidentId(resident.getId()));
        model.addAttribute("myIssues", issueReportService.findByResidentId(resident.getId()));
        model.addAttribute("user", user);
        model.addAttribute("resident", resident);
        return "resident/dashboard";
    }

    // ── Document Requests ─────────────────────────────────────────

    @GetMapping("/requests")
    public String myRequests(HttpSession session, Model model) {
        User user = getResident(session);
        if (user == null) return "redirect:/login";
        Resident resident = getResidentProfile(session);
        if (resident == null) return "redirect:/resident/dashboard";
        model.addAttribute("requests", documentRequestService.findByResidentId(resident.getId()));
        model.addAttribute("user", user);
        return "resident/requests";
    }

    @GetMapping("/requests/new")
    public String newRequestPage(HttpSession session, Model model) {
        User user = getResident(session);
        if (user == null) return "redirect:/login";
        Resident resident = getResidentProfile(session);
        if (resident == null) return "redirect:/resident/dashboard";
        model.addAttribute("user", user);
        return "resident/request-form";
    }

    @PostMapping("/requests/submit")
    public String submitRequest(@RequestParam String documentType,
                                @RequestParam String purpose,
                                HttpSession session, Model model) {
        User user = getResident(session);
        if (user == null) return "redirect:/login";
        Resident resident = getResidentProfile(session);
        if (resident == null) return "redirect:/resident/dashboard";
        DocumentRequest request = new DocumentRequest();
        request.setResidentId(resident.getId());
        request.setDocumentType(documentType);
        request.setPurpose(purpose);
        documentRequestService.submit(request);
        return "redirect:/resident/requests?submitted=true";
    }

    // ── Issue Reports ─────────────────────────────────────────────

    @GetMapping("/issues")
    public String myIssues(HttpSession session, Model model) {
        User user = getResident(session);
        if (user == null) return "redirect:/login";
        Resident resident = getResidentProfile(session);
        if (resident == null) return "redirect:/resident/dashboard";
        model.addAttribute("issues", issueReportService.findByResidentId(resident.getId()));
        model.addAttribute("user", user);
        return "resident/issues";
    }

    @GetMapping("/issues/new")
    public String newIssuePage(HttpSession session, Model model) {
        User user = getResident(session);
        if (user == null) return "redirect:/login";
        Resident resident = getResidentProfile(session);
        if (resident == null) return "redirect:/resident/dashboard";
        model.addAttribute("user", user);
        return "resident/issue-form";
    }

    @PostMapping("/issues/submit")
    public String submitIssue(@RequestParam String category,
                              @RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String location,
                              HttpSession session) {
        User user = getResident(session);
        if (user == null) return "redirect:/login";
        Resident resident = getResidentProfile(session);
        if (resident == null) return "redirect:/resident/dashboard";
        IssueReport report = new IssueReport();
        report.setResidentId(resident.getId());
        report.setCategory(category);
        report.setTitle(title);
        report.setDescription(description);
        report.setLocation(location);
        issueReportService.submit(report);
        return "redirect:/resident/issues?submitted=true";
    }
}