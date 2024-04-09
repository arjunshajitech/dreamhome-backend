package com.dreamhome.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ImageApproveReject {
    private UUID id;
    private boolean approve;
    private String reason;
}
