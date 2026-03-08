package com.iglesia.repository;

import com.iglesia.model.Church;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChurchRepository extends JpaRepository<Church, Long> {
    boolean existsByNameIgnoreCase(String name);
}
