package com.dreamhome.admin;


import com.dreamhome.config.Constants;
import com.dreamhome.config.CookieHelper;
import com.dreamhome.config.PasswordEncoder;
import com.dreamhome.exception.CustomBadRequestException;
import com.dreamhome.exception.CustomUnauthorizedException;
import com.dreamhome.repository.UserRepository;
import com.dreamhome.request.ApproveReject;
import com.dreamhome.request.Login;
import com.dreamhome.request.Success;
import com.dreamhome.table.Users;
import com.dreamhome.table.enumeration.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_V1_ADMIN)
public class AdminController {

    final UserRepository userRepository;
    final CookieHelper cookieHelper;

    @PostMapping("/login")
    public Success login(@RequestBody Login login, HttpServletResponse response) throws CustomBadRequestException {

        Users user = userRepository.findByEmailAndRole(login.getEmail(), Role.ADMIN);
        if (user == null) throw new CustomBadRequestException("Bad Credentials.");
        if (!PasswordEncoder.isPasswordMatch(login.getPassword(),user.getPassword())) throw new CustomBadRequestException("Bad Credentials.");

        cookieHelper.setCookie(response,user,Constants.ADMIN_COOKIE_NAME);
        return new Success("Successfully logged in.");
    }

    @PostMapping("/logout")
    public Success logout(HttpServletRequest request, HttpServletResponse response) {
        cookieHelper.deleteCookie(request,response,Constants.ADMIN_COOKIE_NAME);
        return new Success("Successfully logged out.");
    }

    @GetMapping("/profile")
    public Users getProfile(HttpServletRequest request) throws CustomUnauthorizedException {
        return sessionCheck(request);
    }

    @PostMapping("/approve/client")
    public Success approveOrRejectClient(@RequestBody ApproveReject approveReject, HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        sessionCheck(request);
        Users client = userRepository.findByIdAndRole(approveReject.getId(),Role.CLIENT);
        if (client == null)
            throw new CustomBadRequestException("User not found.");

        client.setApproved(approveReject.isApproveOrReject());
        userRepository.save(client);
        return new Success("Successfully approved user.");
    }

    @PostMapping("/approve/engineer")
    public Success approveOrRejectEngineer(@RequestBody ApproveReject approveReject, HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        sessionCheck(request);
        Users engineer = userRepository.findByIdAndRole(approveReject.getId(),Role.ENGINEER);
        if (engineer == null)
            throw new CustomBadRequestException("User not found.");

        engineer.setApproved(approveReject.isApproveOrReject());
        userRepository.save(engineer);
        return new Success("Successfully approved user.");
    }

    private Users sessionCheck(HttpServletRequest request) throws CustomUnauthorizedException {
        String cookieId = cookieHelper.getCookieValue(request,Constants.ADMIN_COOKIE_NAME);
        Users users = userRepository.findByCookie(UUID.fromString(cookieId));
        if (users != null)
            return users;
        else throw new CustomUnauthorizedException("Unauthorized.");
    }
}
