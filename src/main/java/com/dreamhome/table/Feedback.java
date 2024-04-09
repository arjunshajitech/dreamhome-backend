package com.dreamhome.table;

import com.dreamhome.table.enumeration.Raiting;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID engineerId;
    private UUID projectId;
    private String content;
    @Enumerated(EnumType.STRING)
    private Raiting raiting;
}
