# inbox-app

## Cassandra Docs
* https://cassandra.apache.org/doc/latest/
* https://cassandra.apache.org/doc/latest/cassandra/architecture/index.html

--

* https://cassandra.apache.org/doc/latest/cassandra/data_modeling/index.html
* https://www.datastax.com/blog/basic-rules-cassandra-data-modeling
* You can also use List, Set, Map as columns in Cassandra: https://www.datastax.com/blog/coming-12-collections-support-cql3
* https://www.datastax.com/blog/cql-improvements-cassandra-21
* https://www.datastax.com/blog/whats-new-cassandra-21-better-implementation-counters
* https://www.datastax.com/blog/lightweight-transactions-cassandra-20
* https://www.datastax.com/examples
* https://www.datastax.com/examples/astra-netflix
* https://www.datastax.com/examples/astra-tik-tok
* https://youtu.be/fcohNYJ1FAI
* https://youtu.be/u6pKIrfJgkU
* https://academy.datastax.com/#/courses/c5b626ca-d619-45b3-adf2-a7d2b940a7ee
* https://www.youtube.com/playlist?list=PLqq-6Pq4lTTYzKjjA1C_jbic95tmq9FQt
* https://docs.datastax.com/en/archived/cql/3.1/cql/cql_using/use_counter_t.html
* https://www.datastax.com/blog/putting-some-structure-storage-engine


## App Design Doc:
* https://drive.google.com/file/d/1RneJhMHor6yS3b2wnxkg-5wLwpyPf39t/view?usp=sharing


## End result
After User SignUp:
<img width="1788" alt="image" src="https://user-images.githubusercontent.com/58611230/202012282-75d79f4d-5199-4678-919e-124458fcb4fc.png">

Empty folder (no sent items yet):
<img width="1792" alt="image" src="https://user-images.githubusercontent.com/58611230/202012450-ee8c3532-a59f-4982-9d33-d987fa83a0f9.png">


Email View:
<img width="1787" alt="image" src="https://user-images.githubusercontent.com/58611230/202012577-4d573907-f865-45dc-841a-483c97214bc4.png">

Composing a new email:
<img width="1790" alt="image" src="https://user-images.githubusercontent.com/58611230/202032480-64263e6b-1ca5-4ca3-ba18-1432fb254a75.png">

A new email lands in all receiving user's inbox:
<img width="1790" alt="image" src="https://user-images.githubusercontent.com/58611230/202032609-5129a94b-6e25-4d69-9b2c-6a09b307fe85.png">

And is also kept in sent items of sender user:
<img width="1792" alt="image" src="https://user-images.githubusercontent.com/58611230/202032833-ca2ab271-6774-4d48-b248-1335993ccc92.png">

Email view:
<img width="1782" alt="image" src="https://user-images.githubusercontent.com/58611230/202033017-9e57b72b-5258-4c2f-a9bf-8b2e8fd6e14e.png">

## Cassandra data model so far:
* These are the two high-level goals for data modeling in Cassandra:
  1. Spread data evenly around the cluster
  2. Minimize the number of partitions you read from
  
Lets see what all tables are created. If we `describe main` cluster, we can see all the table schema.

```
token@cqlsh> use main;
token@cqlsh:main> describe main;

CREATE KEYSPACE main WITH replication = {'class': 'NetworkTopologyStrategy', 'asia-south1': '3'}  AND durable_writes = true;

CREATE TABLE main.folders_by_user (
    user_id text,
    created_at_uuid uuid,
    label text,
    color text,
    PRIMARY KEY (user_id, created_at_uuid, label)
) WITH CLUSTERING ORDER BY (created_at_uuid ASC, label ASC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.UnifiedCompactionStrategy'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE main.messages_by_id (
    id uuid PRIMARY KEY,
    body text,
    "from" text,
    subject text,
    "to" list<text>
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.UnifiedCompactionStrategy'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE main.messages_by_user_folder (
    user_id text,
    label text,
    created_at_uuid uuid,
    "from" text,
    subject text,
    unread boolean,
    PRIMARY KEY ((user_id, label), created_at_uuid)
) WITH CLUSTERING ORDER BY (created_at_uuid DESC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.UnifiedCompactionStrategy'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';
```

## data in tables and end result:
### folders_by_user
<img width="1093" alt="image" src="https://user-images.githubusercontent.com/58611230/202035827-68643202-a717-48ab-b6cc-fd8bdab5d894.png">

### messages_by_id
<img width="1722" alt="image" src="https://user-images.githubusercontent.com/58611230/202036042-ba4d3417-60e2-4950-9443-31d266ad8e4a.png">

### messages_by_user_folder
<img width="1122" alt="image" src="https://user-images.githubusercontent.com/58611230/202036155-743f6b84-c30b-467f-8950-e30713e3fc15.png">

Sending email to multiple users at a time
<img width="1792" alt="image" src="https://user-images.githubusercontent.com/58611230/202037549-d6952d31-e2a6-4ed7-92df-1a4b0dba5d7f.png">

New email received:
<img width="1792" alt="image" src="https://user-images.githubusercontent.com/58611230/202037681-e97733f3-6240-47e5-a05e-27f953e2cf84.png">

<img width="1786" alt="image" src="https://user-images.githubusercontent.com/58611230/202037763-67caab2b-1815-4f27-96ec-4628697bd3a1.png">

DB console after this new message:
<img width="1743" alt="image" src="https://user-images.githubusercontent.com/58611230/202038081-ef7ffece-e9ce-49d9-bf98-0d38d9493273.png">

### Folder wise unread counter
- counter tables in Cassandra has to be a dedicated table.
- i.e., columns in a counter table can be either primarykey (partitioning key or clustering key) column, or counter column itself
- that means, you cannot have non-priparykey, non-counter column in a counter table.
- you can run increment/decrement queries on counter columns of a counter table, like - 
`update COUNTER_TABLE_NAME set COUNTER_COL_NAME = COUNTER_COL_NAME + 1 where SOME_OTHER_COL = SOME_VAL` note that here the record does not have to exist in advance with matching 'where clause'. If the record with the given where clause does not exist, cassandra will create a new entry with initial value for all counter columns as 0. So in this case if there is no entry for `SOME_OTHER_COL = SOME_VAL` when the query is executed, then cassandra will create a new entry and the value for `COUNTER_COL_NAME` for that entry will be updated from 0 to 1.

maintaining unread email counter per folder
<img width="1791" alt="image" src="https://user-images.githubusercontent.com/58611230/203164105-01ed1960-aa47-443f-91f0-c61f292cc035.png">

marking message read/unread:
<img width="1791" alt="image" src="https://user-images.githubusercontent.com/58611230/203164417-44b5a4ba-3d42-4c16-85b0-740dfdb75c22.png">

table data:
<img width="1352" alt="image" src="https://user-images.githubusercontent.com/58611230/203164496-d1d101bf-bd1a-49f7-80d9-49d1ef7f3c0d.png">


