package com.callcentercrm.www.repositories;

import com.callcentercrm.www.entities.Process;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProcessRepository extends MongoRepository<Process,String> {

    @Query(value = "{'inspectorId': ?0}")
    Page<Process> findAll(String userId, Pageable pageable);

    @Query(value = "{'inspectorId' : ?0 , 'processName' : ?1 }")
    Page<Process> findAll(String userId,String processName, Pageable pageable);
}
