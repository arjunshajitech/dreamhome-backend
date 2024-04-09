package com.dreamhome.table;

import com.dreamhome.table.enumeration.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private UUID cookie;
    private String jobTitle;
    private double yearOfExperience = 0;
    private boolean approved = false;
    private boolean active = false;
    @Enumerated(EnumType.STRING)
    private Role role = Role.ADMIN;

    public Users(Role role, boolean active, boolean approved,
                 double yearOfExperience, String jobTitle, String phone,
                 String password, String email, String name) {
        this.role = role;
        this.active = active;
        this.approved = approved;
        this.yearOfExperience = yearOfExperience;
        this.jobTitle = jobTitle;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.name = name;
    }
}
