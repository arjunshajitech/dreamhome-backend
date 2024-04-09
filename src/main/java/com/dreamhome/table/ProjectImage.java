package com.dreamhome.table;

import com.dreamhome.table.enumeration.ImageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class ProjectImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID engineerId;
    private UUID clientId;
    private UUID projectId;
    private UUID imageId;
    private boolean approved;
    private String reason;
    @Enumerated(EnumType.STRING)
    private ImageType type;

    public ProjectImage(UUID engineerId, UUID clientId, UUID projectId, UUID imageId, ImageType type) {
        this.engineerId = engineerId;
        this.clientId = clientId;
        this.projectId = projectId;
        this.imageId = imageId;
        this.type = type;
    }
}
