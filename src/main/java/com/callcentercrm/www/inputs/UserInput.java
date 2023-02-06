package com.callcentercrm.www.inputs;

import com.callcentercrm.www.entities.User;
import com.callcentercrm.www.enums.Role;
import com.callcentercrm.www.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInput{

    private String id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private Status status;

}
