package com.dreamhome.config;

import com.dreamhome.repository.UserRepository;
import com.dreamhome.table.Users;
import com.dreamhome.table.enumeration.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfig {

    final UserRepository userRepository;

    @PostConstruct
    public void initAdmin() {

        log.info("############ INIT ADMIN ###################");
        String password = "123456";
        List<Users> admins = new ArrayList<>();
        admins.add(new Users(
                Role.ADMIN,true,true,
                0,null,"0000000000",
                PasswordEncoder.encodePassword(password),"admin@admin.com","Admin Admin"
        ));
        admins.add(new Users(
                Role.ADMIN,true,true,
                0,null,"1111111111",
                PasswordEncoder.encodePassword(password),"superadmin@admin.com","Super Admin"
        ));

        admins.forEach(admin -> {
            boolean isAdminEmailExists = userRepository.existsByEmailAndRole(admin.getEmail(),Role.ADMIN);
            boolean isAdminPhoneExists = userRepository.existsByPhoneAndRole(admin.getPhone(),Role.ADMIN);
            if (!isAdminEmailExists && !isAdminPhoneExists) {
                userRepository.save(admin);
            }
        });
    }
}
