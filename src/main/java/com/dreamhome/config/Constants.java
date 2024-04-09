package com.dreamhome.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class Constants {

    public static final String ADMIN_COOKIE_NAME = "ADMIN_COOKIE";
    public static final  String ENGINEER_COOKIE_NAME = "ENGINEER_COOKIE";
    public static final  String CLIENT_COOKIE_NAME = "CLIENT_COOKIE";

    public static final  String API_V1_ADMIN = "api/v1/admin";
    public static final  String API_V1_ENGINEER = "api/v1/engineer";
    public static final  String API_V1_CLIENT = "api/v1/client";
}
