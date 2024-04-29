package com.njha.inboxapp.folder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "unread_email_stats")
@Setter
@Getter
@Builder
public class UnreadEmailStats {
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "label", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    @CassandraType(type = CassandraType.Name.TEXT)
    private String label;

    @CassandraType(type = CassandraType.Name.COUNTER)
    private int unreadCount;
}
