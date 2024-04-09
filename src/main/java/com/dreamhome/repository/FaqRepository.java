package com.dreamhome.repository;

import com.dreamhome.table.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FaqRepository extends JpaRepository<Faq, UUID> {
}
