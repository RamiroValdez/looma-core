package com.amool.hexagonal.adapters.out.persistence.repository;

import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<LanguageEntity, Long> {
}
