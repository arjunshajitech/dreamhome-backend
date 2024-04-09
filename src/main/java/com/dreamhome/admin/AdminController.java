package com.dreamhome.admin;


import com.dreamhome.config.Constants;
import com.dreamhome.config.CookieHelper;
import com.dreamhome.config.PasswordEncoder;
import com.dreamhome.exception.CustomBadRequestException;
import com.dreamhome.repository.UserRepository;
import com.dreamhome.request.Login;
import com.dreamhome.request.Success;
import com.dreamhome.table.Users;
import com.dreamhome.table.enumeration.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_V1_ADMIN)
public class AdminController {

    final UserRepository userRepository;
    final CookieHelper cookieHelper;

    @PostMapping("")
    public Success login(@RequestBody Login login, HttpServletResponse response,
                         HttpServletRequest request) throws CustomBadRequestException {

        Users user = userRepository.findByEmailAndRole(login.getEmail(), Role.ADMIN);
        if (user == null) throw new CustomBadRequestException("Bad Credentials.");
        if (!PasswordEncoder.isPasswordMatch(login.getPassword(),user.getPassword())) throw new CustomBadRequestException("Bad Credentials.");

        cookieHelper.setCookie(response,user,Constants.ADMIN_COOKIE_NAME);
        return new Success("Successfully logged in.");
    }
}
