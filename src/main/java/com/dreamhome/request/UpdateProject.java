package com.dreamhome.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateProject {

    private UUID id;
    long planEstimation = 0;
    long threeDModelEstimation = 0;
    double planAmount = 0;
    double threeDModelAmount = 0;
}
