package com.njha.inboxapp.folder;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void createStandardFoldersForNewUser(String userId) {
        folderRepository.save(Folder.builder().userId(userId).label("Recibidos").timeUUID(Uuids.timeBased()).color("Blue").build());
        folderRepository.save(Folder.builder().userId(userId).label("Enviar").timeUUID(Uuids.timeBased()).color("Green").build());
        folderRepository.save(Folder.builder().userId(userId).label("Importante").timeUUID(Uuids.timeBased()).color("Red").build());
        folderRepository.save(Folder.builder().userId(userId).label("Favoritos").timeUUID(Uuids.timeBased()).color("Yellow").build());
    }

    public List<Folder> findAllByUserId(String userId) {
        return folderRepository.findAllByUserId(userId);
    }

    public List<Folder> getAllFoldersForUser(String userId) {
        List<Folder> userFolders = folderRepository.findAllByUserId(userId);

        // Show default folder to users if for whatever reasons user folders were not created on signup, or if we are unable to access it
        if (userFolders == null || userFolders.size() == 0) {
            userFolders = Arrays.asList(
                    Folder.builder().userId(userId).label("Recibidos").color("Blue").build(),
                    Folder.builder().userId(userId).label("Enviar").color("Green").build(),
                    Folder.builder().userId(userId).label("Importante").color("Red").build(),
                    Folder.builder().userId(userId).label("Favoritos").color("Yellow").build()
            );
        }

        return userFolders;
    }

    public Map<String, Integer> getUnreadEmailStats(String userId) {
        List<UnreadEmailStats> unreadEmailStats = unreadEmailStatsRepository.findAllByUserId(userId);
        Map<String, Integer> folderUnreadCountMap = unreadEmailStats.stream()
                .collect(Collectors.toMap(UnreadEmailStats::getLabel, UnreadEmailStats::getUnreadCount));
        return folderUnreadCountMap;
    }
}
