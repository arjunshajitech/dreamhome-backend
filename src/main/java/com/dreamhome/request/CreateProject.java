package com.dreamhome.request;

import com.dreamhome.table.enumeration.ProjectStatus;
import com.dreamhome.table.enumeration.Style;
import com.dreamhome.table.enumeration.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateProject {

    String name;
    Type type;
    Style architectureStyle;
    long timeline;
    String description;
}
