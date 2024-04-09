package com.dreamhome.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ApproveReject {
    private UUID id;
    private boolean approveOrReject;
}
