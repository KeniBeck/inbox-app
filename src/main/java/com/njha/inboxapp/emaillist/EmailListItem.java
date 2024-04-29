package com.njha.inboxapp.emaillist;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table(value = "messages_by_user_folder")
@Setter
@Getter
@Builder
public class EmailListItem {

    @PrimaryKey
    private EmailListItemKey key;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String from;

    @CassandraType(type = CassandraType.Name.TEXT)
    private String subject;

    @CassandraType(type = CassandraType.Name.BOOLEAN)
    private Boolean unread;
}
