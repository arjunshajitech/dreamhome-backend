package com.dreamhome.table;

import com.dreamhome.table.enumeration.ProjectStatus;
import com.dreamhome.table.enumeration.Style;
import com.dreamhome.table.enumeration.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String location;
    @Enumerated(EnumType.STRING)
    private Style architectureStyle;
    private long timeline;
    private String description;
    private UUID engineerId;
    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.UNASSIGNED;
    private long planEstimation;
    private long threeDModelEstimation;
    private double planAmount = 0;
    private double threeDModelAmount = 0;
    private boolean threeDModelAmountPaid = false;
    private boolean planAmountPaid = false;
}
