package com.callcentercrm.www.repositories;

import com.callcentercrm.www.entities.TradingAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TradingAccountRepository extends MongoRepository<TradingAccount,String> {

    @Query(value = "{'userId: ?0'}",fields = "{'id': 1, 'tradingAccountType': 1, 'tradingAccountCategory': 1, 'currency': 1, 'balance': 1, 'leverage': 1}")
    Page<TradingAccount> findByIsDeletedFalse(String userId,Pageable pageable);

    @Query(value = "{'userId': ?0}")
    Page<TradingAccount> findAll(String userId,Pageable pageable);

    @Query(value = "{'userId: ?0'}",fields = "{'id': 1, 'tradingAccountType': 1, 'tradingAccountCategory': 1, 'currency': 1, 'balance': 1, 'leverage': 1}")
    TradingAccount getById(String id);


}
