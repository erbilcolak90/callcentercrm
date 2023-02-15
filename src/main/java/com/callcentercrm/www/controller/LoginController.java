package com.callcentercrm.www.controller;

import com.callcentercrm.www.auth.TokenManager;
import com.callcentercrm.www.inputs.LoginRequest;
import com.callcentercrm.www.inputs.UserInput;
import com.callcentercrm.www.result.Result;
import com.callcentercrm.www.services.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private CustomUserService customUserService;


    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest loginRequest) {
        try{

            if(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword())) != null){
                String token = tokenManager.generateToken(loginRequest.getUsername());
                String userId = tokenManager.parseUserIdFromToken(token);
                Result<String> result = new Result<>();
                result.setMessage(token);
                result.setData(userId);
                return new Result<>(userId,token,true);
            }else{
                return new Result<>("","",false);
            }

        }catch (UsernameNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        if(tokenManager.tokenValidate(token)){
            return new Result<>("Logout Succes",tokenManager.logoutToken(token),true);
        }else{
            return new Result<>("invalid token",null,false);
        }

    }

    @PostMapping("/signUp")
    public Result<String> signUp(@RequestBody UserInput userInput) {
        Result<String> result = customUserService.signUp(userInput);
        return new Result<>(result.getMessage(), result.getData(), result.isStatus());
    }

}
