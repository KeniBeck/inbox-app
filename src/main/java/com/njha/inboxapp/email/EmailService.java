package com.njha.inboxapp.email;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.njha.inboxapp.emaillist.EmailListItem;
import com.njha.inboxapp.emaillist.EmailListItemKey;
import com.njha.inboxapp.emaillist.EmailListItemRepository;
import com.njha.inboxapp.folder.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmailUtil(String toUserId, String fromUserId, String label, String subject, String body) {
        UUID createdAt = Uuids.timeBased();
        EmailListItemKey key = EmailListItemKey.builder().userId(toUserId).label(label).timeUUID(createdAt).build();
        EmailListItem accountCreatedNotifEmailItemList = EmailListItem.builder()
                .key(key)
                .subject(subject)
                .from(fromUserId)
                .unread(true)
                .build();
        emailListItemRepository.save(accountCreatedNotifEmailItemList);

        Email email = Email.builder()
                .id(createdAt)
                .from(fromUserId)
                .to(Arrays.asList(toUserId))
                .subject(subject)
                .body(body)
                .build();
        emailRepository.save(email);
        unreadEmailStatsRepository.incrementUnreadCount(toUserId, label);
    }

    public void sendWelcomeEmails(String userId) {
        String sub = "New VolcanoMail Account Created!";
        String body = "Hello! Your new VolcanoMail account is created and is ready for use.";
        sendEmailUtil(userId, "admin@volcanomail", "Inbox", sub, body);

        sub = "Welcome To VolcanoMail!";
        body = "Hello! Welcome to VolcanoMail! Be hot as a Volcano and let the explosion of emails begin.";
        sendEmailUtil(userId, "admin@volcanomail", "Inbox", sub, body);
    }

    public Email findEmailById(UUID id) {
        return emailRepository.findById(id).get();
    }

    public void sendEmail(List<String> toIds, String fromId, String subject, String body) {
        UUID createdAt = Uuids.timeBased();
        // create teh actual message record first
        Email email = Email.builder()
                .id(createdAt)
                .from(fromId)
                .to(toIds)
                .subject(subject)
                .body(body)
                .build();
        emailRepository.save(email);

        // all recipients will receive the message in their inbox folder
        toIds.forEach(toId -> {
            EmailListItemKey key = EmailListItemKey.builder().userId(toId).label("Inbox").timeUUID(createdAt).build();
            EmailListItem accountCreatedNotifEmailItemList = EmailListItem.builder()
                    .key(key)
                    .subject(subject)
                    .from(fromId)
                    .unread(true)
                    .build();
            emailListItemRepository.save(accountCreatedNotifEmailItemList);
            unreadEmailStatsRepository.incrementUnreadCount(toId, "Inbox");
        });

        // sender user will have the message in their sent item
        EmailListItemKey key = EmailListItemKey.builder().userId(fromId).label("Sent").timeUUID(createdAt).build();
        EmailListItem accountCreatedNotifEmailItemList = EmailListItem.builder()
                .key(key)
                .subject(subject)
                .from(fromId)
                .unread(false) // user who wrote the message has already read it, so messages in sent items will be marked read by default
                .build();
        emailListItemRepository.save(accountCreatedNotifEmailItemList);
        // unreadEmailStatsRepository.incrementUnreadCount(fromId, "Sent"); // we don't need unread count for sent folder
    }

    public void markEmailUnread(UUID id, String folder, String userId) {
        EmailListItemKey emailListItemKey = EmailListItemKey.builder()
                .userId(userId)
                .label(folder)
                .timeUUID(id)
                .build();

        Optional<EmailListItem> emailListItemOptional = emailListItemRepository.findById(emailListItemKey);
        if (emailListItemOptional.isPresent()) {
            EmailListItem emailListItem = emailListItemOptional.get();
            if (emailListItem.getUnread()) {
                emailListItem.setUnread(false);
                emailListItemRepository.save(emailListItem);
                unreadEmailStatsRepository.decrementUnreadCount(userId, folder);
            }
        }
    }
}
