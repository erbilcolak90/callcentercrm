package com.callcentercrm.www.controller.user;


import com.callcentercrm.www.entities.User;
import com.callcentercrm.www.inputs.*;
import com.callcentercrm.www.result.Result;
import com.callcentercrm.www.services.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/user")
@ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
public class UserController {

    private CustomUserService customUserService;

    @Autowired
    public UserController(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }



    @PostMapping("/updateUser")
    public Result<String> updateUser(@RequestBody UserInput userInput) {
        Result<String> result = customUserService.updateUser(userInput);
        return new Result<>(result.getMessage(), result.getData(), result.isStatus());
    }

    @PostMapping("/getUserById")
    public Result<User> getUserById(@RequestParam String userId) {
        Result<User> result = customUserService.getUserById(userId);
        return new Result<>(result.getMessage(), result.getData(), result.isStatus());
    }



}
