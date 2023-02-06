package com.callcentercrm.www.services;

import com.callcentercrm.www.auth.CustomUserDetailsService;
import com.callcentercrm.www.auth.TokenManager;
import com.callcentercrm.www.entities.*;
import com.callcentercrm.www.enums.*;
import com.callcentercrm.www.inputs.*;
import com.callcentercrm.www.repositories.*;
import com.callcentercrm.www.result.Result;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomUserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenManager tokenManager;

    @Autowired
    public CustomUserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, TokenManager tokenManager) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenManager = tokenManager;
    }

    // user operations
    @Transactional
    public Result<String> signUp(UserInput userInput) {

        User usernameIsExist = userRepository.findByUsername(userInput.getUsername());
        if (usernameIsExist == null || usernameIsExist.getUsername().isEmpty()) {
            User user = new User();
            user.setName(userInput.getName());
            user.setSurname(userInput.getSurname());
            user.setUsername(userInput.getUsername());
            user.setEmail(userInput.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(userInput.getPassword()));
            user.setPhoneNumber(userInput.getPhoneNumber());
            List<Role> userRoles = new ArrayList<>();
            userRoles.add(Role.USER);
            user.setRoles(userRoles);
            user.setStatus(Status.PENDING);
            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            user.setDeleted(false);

            userRepository.save(user);
            String token = tokenManager.generateToken(userInput.getUsername());

            return new Result<>(token, "username : " + user.getUsername() + " id : " + user.getId());
        } else {
            return new Result<>("Username or email is already exist.", null);
        }

    }

    @Transactional
    public Result<String> updateUser(UserInput userInput) {

        User user = userRepository.findById(userInput.getId()).orElseThrow();

        if (userInput.getUsername() != null) {
            User isUsernameExist = userRepository.findByUsername(userInput.getUsername());
            if (isUsernameExist == null) {
                user.setUsername(userInput.getUsername());
            } else {
                return new Result<>("Username is already exist", null);
            }
        }
        if (userInput.getPassword() != null && !userInput.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(userInput.getPassword()));
        }
        updateInformation(userInput, user);
        userRepository.save(user);

        return new Result<>("Success", "Update completed");
    }

    public Result<User> getUserById(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // if user has a admin role; user can check all users
        if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return new Result<>("success", userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found")));
        }
        // if userId from authentication and userId from request different.
        if (!authentication.getName().equals(userId)) {
            return new Result<>("forbidden", null);
        } else {
            User user = userRepository.getById(userId);

            return new Result<>("User Information:", user);
        }

    }

    //just for admins
    @Transactional
    public Result<String> addRoleToUser(String userId, String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {

            User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            List<Role> roles = user.getRoles();
            String upperCaseRoleName = roleName.toUpperCase();
            Role upperCaseRole = Role.valueOf(upperCaseRoleName);
            if (roles.contains(upperCaseRole)) {
                return new Result<>("User already has this role", null);
            } else {
                for (Role item : Role.values()) {
                    if (upperCaseRole.equals(item)) {
                        roles.add(item);
                        user.setUpdateDate(new Date());
                        userRepository.save(user);
                        return new Result<>("Role is added", null);
                    }
                }
            }

        }

        return new Result<>("This role is not supported", null);
    }

    //just for admins
    public Result<Page<User>> getAllUsers(PaginationInput paginationInput) {
        Pageable pageable = PageRequest.of(paginationInput.getPage(), paginationInput.getSize(), Sort.by(Sort.Direction.ASC, paginationInput.getSortBy()));
        return new Result<>("Success", userRepository.findAll(pageable));
    }

    //just for admims
    public String shuffleAdmin(){
        List<User> admins = userRepository.findByRoles(Role.ADMIN);
        Random random = new Random();
        int randomItem = random.nextInt(admins.size());

        return admins.get(randomItem).getId();

    }

    public void updateDeposit(String transferId){

    }

    //token manager using this method.
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByUserId(String userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    public void updateInformation(UserInput userInput, User user) {

        if (userInput.getName() != null && !userInput.getName().isEmpty()) {
            user.setName(userInput.getName());
        }
        if (userInput.getSurname() != null && !userInput.getSurname().isEmpty()) {
            user.setSurname(userInput.getSurname());
        }
        if (userInput.getEmail() != null && !userInput.getEmail().isEmpty()) {
            user.setEmail(userInput.getEmail());
        }
        if (userInput.getPhoneNumber() != null && !userInput.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(userInput.getPhoneNumber());
        }

        user.setUpdateDate(new Date());
    }


}

