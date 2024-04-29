# Notes about Cassandra

## Some points
* **Column Family** – Column Families in Cassandra are like tables in Relational Databases. Each Column Family contains a collection of rows which are represented by a Map<RowKey, SortedMap<ColumnKey, ColumnValue>>. The key gives the ability to access related data together
* These are the two high-level goals for data modeling in Cassandra:
  1. Spread data evenly around the cluster
  2. Minimize the number of partitions you read from
* About writes in cassandra:
  - Writes in Cassandra aren't free, but they're awfully cheap. 
  - Cassandra is optimized for high write throughput, and almost all writes are equally efficient. 
  - **If you can perform extra writes to improve the efficiency of your read queries, it's almost always a good tradeoff. Reads tend to be more expensive and are much more difficult to tune.**
  - So write into multiple tables (same data in multiple table) for faster reads.
* About data duplication:
  - It is OK to denormalize and duplicate the data to support various kinds of queries (for faster reads).
  - Denormalization and duplication of data is a fact of life with Cassandra. Don't be afraid of it. 
  - Disk space is generally the cheapest resource (compared to CPU, memory, disk IOPs, or network), and Cassandra is architected around that fact. 
  - In order to get the most efficient reads, you often need to duplicate data. Besides, Cassandra doesn't have JOINs, and you don't really want to use those in a distributed fashion.


## Docs and videos
* https://cassandra.apache.org/doc/latest/
* https://cassandra.apache.org/doc/latest/cassandra/data_modeling/index.html
* https://www.datastax.com/blog/basic-rules-cassandra-data-modeling
* https://www.datastax.com/blog/coming-12-collections-support-cql3
* https://www.datastax.com/blog/cql-improvements-cassandra-21
* https://www.datastax.com/blog/whats-new-cassandra-21-better-implementation-counters
* https://www.datastax.com/blog/lightweight-transactions-cassandra-20
* https://www.datastax.com/examples
* https://www.datastax.com/examples/astra-netflix
* https://www.datastax.com/examples/astra-tik-tok
* https://youtu.be/fcohNYJ1FAI
* https://youtu.be/u6pKIrfJgkU
* https://academy.datastax.com/#/courses/c5b626ca-d619-45b3-adf2-a7d2b940a7ee
* https://www.baeldung.com/cassandra-with-java
* https://www.baeldung.com/cassandra-data-modeling
