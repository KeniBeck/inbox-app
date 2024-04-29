package com.njha.inboxapp.home;

import com.njha.inboxapp.emaillist.EmailListDto;
import com.njha.inboxapp.emaillist.EmailListItemService;
import com.njha.inboxapp.folder.Folder;
import com.njha.inboxapp.folder.FolderService;
import com.njha.inboxapp.folder.UnreadEmailStats;
import com.njha.inboxapp.folder.UnreadEmailStatsRepository;
import com.njha.inboxapp.user.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class InboxController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailListItemService emailListItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    @GetMapping("/")
    public String homePage(
            Model model,
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(required = false) String folder
    ) {
        if (principal == null || principal.getAttribute("login") == null) {
            return "index";
        }

        String userName = principal.getAttribute("name");
        model.addAttribute("userName", userName);

        String userId = principal.getAttribute("login");
        userService.takeCareOfNewUser(userId);

        List<Folder> userFolders = folderService.getAllFoldersForUser(userId);
        model.addAttribute("userFolders", userFolders);

        // get messages for default folder - Inbox
        if (Strings.isBlank(folder)) {
            folder = "Inbox";
        }
        model.addAttribute("folderName", folder);
        List<EmailListDto> folderEmails = emailListItemService.findAllMessagesByUserAndFolder(userId, folder);
        model.addAttribute("folderEmails", folderEmails);

        Map<String, Integer> folderUnreadCountMap = folderService.getUnreadEmailStats(userId);
        model.addAttribute("folderToUnreadCounts", folderUnreadCountMap);

        return "inbox-page";
    }
}
