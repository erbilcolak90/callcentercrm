package com.callcentercrm.www.csv;

import com.callcentercrm.www.entities.User;
import com.callcentercrm.www.repositories.UserRepository;
import com.callcentercrm.www.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CSVService {

    @Autowired
    private UserRepository userRepository;

    public Result<List<User>> save(MultipartFile file){
        try {
            List<User> users = CSVHelper.csvUsers(file.getInputStream());
            userRepository.saveAll(users);
            return new Result<>("",users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
