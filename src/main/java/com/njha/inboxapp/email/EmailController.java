package com.njha.inboxapp.email;

import com.njha.inboxapp.folder.Folder;
import com.njha.inboxapp.folder.FolderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@Slf4j
public class EmailController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/emails/{id}")
    public String viewEmail(
            @PathVariable UUID id,
            Model model, @AuthenticationPrincipal OAuth2User principal,
            @RequestParam String folder
    ) {
        if (principal == null || principal.getAttribute("login") == null) {
            return "index";
        }

        String userName = principal.getAttribute("name");
        model.addAttribute("userName", userName);

        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderService.getAllFoldersForUser(userId);
        model.addAttribute("userFolders", userFolders);

        Email email = emailService.findEmailById(id);
        model.addAttribute("email", email);
        String toIds = String.join(", ", email.getTo());
        model.addAttribute("toIds", toIds);

        if (Strings.isBlank(folder)) {
            folder = "Inbox";
        }
        model.addAttribute("folderName", folder);
        emailService.markEmailUnread(id, folder, userId);

        Map<String, Integer> folderUnreadCountMap = folderService.getUnreadEmailStats(userId);
        model.addAttribute("folderToUnreadCounts", folderUnreadCountMap);

        return "email-page";
    }

    @GetMapping("/compose")
    public String getComposeEmailPage(
            Model model,
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(required = false) String toIds
    ) {
        if (principal == null || principal.getAttribute("login") == null) {
            return "index";
        }

        String userName = principal.getAttribute("name");
        model.addAttribute("userName", userName);

        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderService.getAllFoldersForUser(userId);
        model.addAttribute("userFolders", userFolders);

        List<String> uniqueToIds = splitToIds(toIds);
        model.addAttribute("toIds", String.join(", ", uniqueToIds));

        Map<String, Integer> folderUnreadCountMap = folderService.getUnreadEmailStats(userId);
        model.addAttribute("folderToUnreadCounts", folderUnreadCountMap);

        return "compose-page";
    }

    private List<String> splitToIds(String toIds) {
        if (Strings.isBlank(toIds)) {
            return new ArrayList<>();
        }

        String[] toIdArr = toIds.split(",");
        List<String> uniqueToIds = Arrays.stream(toIdArr)
                .map(toId -> toId.trim())
                .filter(toId -> !toId.isEmpty())
                .distinct()
                .toList();
        return uniqueToIds;
    }

    @PostMapping(value = "/sendEmail")
    public ModelAndView sendEmail(
            @RequestBody MultiValueMap<String, String> formData,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        if (principal == null || principal.getAttribute("login") == null) {
            return new ModelAndView("redirect:/");
        }

        String toUserIds = formData.getFirst("toUserIds");
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");
        String from = principal.getAttribute("login");

        List<String> uniqueToIds = splitToIds(toUserIds);
        emailService.sendEmail(uniqueToIds, from, subject, body);

        return new ModelAndView("redirect:/");
    }

}
