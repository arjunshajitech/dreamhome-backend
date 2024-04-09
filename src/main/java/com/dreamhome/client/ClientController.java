package com.dreamhome.client;


import com.dreamhome.config.Constants;
import com.dreamhome.config.CookieHelper;
import com.dreamhome.config.PasswordEncoder;
import com.dreamhome.exception.CustomBadRequestException;
import com.dreamhome.exception.CustomUnauthorizedException;
import com.dreamhome.repository.UserRepository;
import com.dreamhome.request.ClientSignup;
import com.dreamhome.request.Login;
import com.dreamhome.request.Success;
import com.dreamhome.table.Users;
import com.dreamhome.table.enumeration.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_V1_CLIENT)
public class ClientController {

    final UserRepository userRepository;
    final CookieHelper cookieHelper;

    @PostMapping("/signup")
    public Success signup(@RequestBody ClientSignup clientSignup, HttpServletRequest request,
                          HttpServletResponse response) throws CustomBadRequestException {

        boolean isEmailExists = userRepository.existsByEmailAndRole(clientSignup.getEmail(),Role.CLIENT);
        boolean isPhoneExists = userRepository.existsByPhoneAndRole(clientSignup.getPhone(),Role.CLIENT);
        if(!isEmailExists || !isPhoneExists) throw new CustomBadRequestException("Email or phone exists.");

        userRepository.save(
                new Users(clientSignup.getRole(),false,false,0,
                        null,clientSignup.getPhone(),PasswordEncoder.encodePassword(clientSignup.getPassword()),
                        clientSignup.getEmail(),clientSignup.getName())
        );
        return new Success("Signup successful");
    }

    @PostMapping("/login")
    public Success login(@RequestBody Login login, HttpServletResponse response) throws CustomBadRequestException {

        Users user = userRepository.findByEmailAndRole(login.getEmail(), Role.CLIENT);
        if (user == null || !user.isApproved()) throw new CustomBadRequestException("Bad Credentials.");
        if (!PasswordEncoder.isPasswordMatch(login.getPassword(),user.getPassword())) throw new CustomBadRequestException("Bad Credentials.");

        cookieHelper.setCookie(response,user,Constants.CLIENT_COOKIE_NAME);
        return new Success("Successfully logged in.");
    }

    @PostMapping("/logout")
    public Success logout(HttpServletRequest request, HttpServletResponse response) {
        cookieHelper.deleteCookie(request,response,Constants.CLIENT_COOKIE_NAME);
        return new Success("Successfully logged out.");
    }

    @GetMapping("/profile")
    public Users getProfile(HttpServletRequest request) throws CustomUnauthorizedException {
        return sessionCheck(request);
    }

    private Users sessionCheck(HttpServletRequest request) throws CustomUnauthorizedException {
        String cookieId = cookieHelper.getCookieValue(request,Constants.CLIENT_COOKIE_NAME);
        Users users = userRepository.findByCookie(UUID.fromString(cookieId));
        if (users != null && users.isApproved()) return users;
        else throw new CustomUnauthorizedException("Unauthorized.");
    }
}
