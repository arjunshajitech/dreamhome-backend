package com.dreamhome.request;

import lombok.Data;

import java.util.UUID;

@Data
public class Assign {
    private UUID engineer;
    private UUID projectId;
}
