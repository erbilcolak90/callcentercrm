package com.callcentercrm.www.repositories;

import com.callcentercrm.www.entities.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TransferRepository extends MongoRepository<Transfer,String> {

    @Query(value = "{'id': ?0}",fields = "{'userId': 1, 'transferType': 1, 'tradingAccountId': 1, 'amount': 1, 'receiverTransferAccountId': 1, 'transferStatus': 1}")
    Transfer getById(String transferId);


    @Query(fields = "{'userId': 1, 'transferType': 1, 'tradingAccountId': 1, 'amount': 1, 'receiverTransferAccountId': 1, 'transferStatus': 1}")
    Page<Transfer> findByIsDeletedFalse(Pageable pageable);

}
