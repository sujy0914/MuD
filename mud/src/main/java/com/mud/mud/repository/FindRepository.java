package com.mud.mud.repository;

import com.mud.mud.entity.Find;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindRepository extends JpaRepository<Find, Long> {
    Find findByToken(String token);
}
