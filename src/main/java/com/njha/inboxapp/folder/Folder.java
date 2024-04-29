package com.njha.inboxapp.folder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table(value = "folders_by_user")
@Setter
@Getter
@Builder
public class Folder {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "label", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    @CassandraType(type = CassandraType.Name.TEXT)
    private String label;

    @PrimaryKeyColumn(name = "created_at_uuid", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private UUID timeUUID;

    /*
    If we have only user_id as PrimaryKey then for each user we can have only one entry in the table.
    That means for each user we can have only one folder (Which is not what we want - we want multiple
    folders for each user).

    So we'll have to make userId and label together as PrimaryKey. But then we can't have label as
    PartitioningColumn, because we want all folders of a user to go to one partition (node) in the cluster
    and not to different partitions in the cluster. So we make label a ClusteringColumn.

    Also, since we would want folders to show in the order as they are created by user, so we'll also
    add another clustering column with type UUID. Let's keep this column with lower ordinal (higher precedence in sorting)
    compared to 'label' column, since creation time should be the first sorting criteria and label name the second. We probably
    won't even need sorting on label as no two folders can ever be created at the same timestamp (and so sorting on label will
    never get used, but let's keep it anyway). We definitely need label as a clustering column atleast (because otherwise users will
    be able to add multiple folders with same name).
    */

    @Column("color")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String color;

}
