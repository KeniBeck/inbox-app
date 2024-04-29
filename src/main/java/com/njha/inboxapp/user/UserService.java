package com.njha.inboxapp.user;

import com.njha.inboxapp.email.EmailService;
import com.njha.inboxapp.emaillist.EmailListItemService;
import com.njha.inboxapp.folder.Folder;
import com.njha.inboxapp.folder.FolderService;
import com.njha.inboxapp.folder.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

    public void takeCareOfNewUser(String userId) {
        List<Folder> userFolders = folderService.findAllByUserId(userId);
        boolean isNewUser = false;
        if (userFolders == null || userFolders.size() == 0) {
            isNewUser = true;
        }

        if (isNewUser) {
            folderService.createStandardFoldersForNewUser(userId);
            emailService.sendWelcomeEmails(userId);
        }
    }
}
