package com.dreamhome.repository;

import com.dreamhome.table.Users;
import com.dreamhome.table.enumeration.Role;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
    boolean existsByEmailAndRole(String email, Role role);

    boolean existsByPhoneAndRole(String phone, Role role);

    Users findByEmailAndRole(String email, Role role);
}
