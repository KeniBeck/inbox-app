package com.njha.inboxapp.emaillist;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.UUID;

@PrimaryKeyClass
@Setter
@Getter
@Builder
public class EmailListItemKey {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "label", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String label;

    @PrimaryKeyColumn(name = "created_at_uuid", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID timeUUID;
}
