package com.njha.inboxapp.folder;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnreadEmailStatsRepository extends CassandraRepository<UnreadEmailStats, String> {

    List<UnreadEmailStats> findAllByUserId(String userId);
    // method name has to be findAllByUserId (By UserId and not by Id since the partition key column name in UnreadEmailStats.java is userId)
    // if we keep it as findAllById, Spring data would not be able to map it, and it'll throw exception at runtime.
    // If we change the partition key column name in UnreadEmailStats.java to Id however, then we'll need to have method name as findAllById.

    @Query("update unread_email_stats set unreadcount = unreadcount + 1 where user_id = ?0 and label = ?1")
    public void incrementUnreadCount(String userId, String label);

    @Query("update unread_email_stats set unreadcount = unreadcount - 1 where user_id = ?0 and label = ?1")
    public void decrementUnreadCount(String userId, String label);

}
