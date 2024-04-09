package com.dreamhome.request;

import com.dreamhome.table.enumeration.Role;
import lombok.Data;

@Data
public class ClientSignup {
    String name;
    String email;
    String password;
    String phone;
    private Role role = Role.CLIENT;
}
