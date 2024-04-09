package com.dreamhome.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UploadImage {
    private UUID imageId;
    private UUID projectId;
}
