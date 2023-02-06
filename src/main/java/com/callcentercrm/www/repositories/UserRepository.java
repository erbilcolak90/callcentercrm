package com.callcentercrm.www.repositories;

import com.callcentercrm.www.entities.User;
import com.callcentercrm.www.enums.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User,String> {

    User findByUsername(String username);

    @Query(value = "{ 'id': ?0 }",fields = "{'id':1, 'name':1, 'surname':1, 'username':1, 'email':1, 'phoneNumber':1, 'status':1 }")
    User getById(String id);

    @Query(fields = "{'id': 1, 'username': 1}")
    List<User> findByRoles(Role role);

}
