package com.dreamhome.request;

import com.dreamhome.table.enumeration.Role;
import lombok.Data;

@Data
public class EngineerSignup {
    String name;
    String email;
    String password;
    String phone;
    String jobTitle;
    double yearOfExperience = 0;
    private Role role = Role.ENGINEER;
}
