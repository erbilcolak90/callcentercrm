package com.callcentercrm.www.repositories;

import com.callcentercrm.www.entities.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserDocumentRepository extends MongoRepository<UserDocument,String> {

    Page<UserDocument> findByUserId(String userId, Pageable pageable);

    @Query(value = "{'userId': ?0}",fields = "{'id': 1, 'userId':1 , 'documentType': 1, 'documentURL': 1, 'status': 1, 'statusMessage': 1, 'createDate': 1, 'updateDate':1  }")
    UserDocument findByUserId(String userId);

    @Query(value = "{'id': ?0, 'userId': ?1 }",fields = "{'id': 1, 'userId':1 , 'documentType': 1, 'documentURL': 1, 'status': 1, 'statusMessage': 1}")
    UserDocument getById(String documentId, String userId);

    @Query(value = "{'userId': ?0}",fields = "{'id': 1, 'documentType': 1, 'documentURL': 1, 'status': APPROVED}")
    List<UserDocument> findAll(String userId);

    /*@Query(value = "{'userId': ?0 }",fields = "{'id': 1, 'userId':1 , 'documentType': 1, 'frontURL': 1, 'backURL': 1, 'status': 1, 'statusMessage': 1}")
    Page<UserDocument> getByUserId(String userId, Pageable pageable);
*/
    @Query(value = "{'userId': ?0 }",fields = "{'id': 1, 'userId':1 , 'documentType': 1, 'documentURL': 1, 'status': 1, 'statusMessage': 1}")
    Page<UserDocument> findByIsDeletedFalse(String userId,Pageable pageable);

    @Query(value = "{'userId': ?0}")
    Page<UserDocument> findAll(String userId,Pageable pageable);
}
