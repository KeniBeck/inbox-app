package com.njha.inboxapp.folder;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends CassandraRepository<Folder, String> {

    List<Folder> findAllByUserId(String userId);
    // method name has to be findAllByUserId (By UserId and not by Id since the partition key column name in Folder.java is userId)
    // if we keep it as findAllById, Spring data would not be able to map it, and it'll throw exception at runtime.
    // If we change the partition key column name in Folder.java to Id however, then we'll need to have method name as findAllById.

}
