package com.callcentercrm.www.repositories;

import com.callcentercrm.www.entities.ReceiverTransferAccount;
import com.callcentercrm.www.entities.TradingAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ReceiverTransferAccountRepository extends MongoRepository<ReceiverTransferAccount,String> {

    @Query(value = "{'userId': ?0 }",fields = "{'id': 1, 'name': 1, 'bankName': 1, 'iban': 1}")
    Page<ReceiverTransferAccount> findByIsDeletedFalse(String userId, Pageable pageable);

    @Query(value = "{'userId': ?0}")
    Page<ReceiverTransferAccount> findById(String userId,Pageable pageable);


}
