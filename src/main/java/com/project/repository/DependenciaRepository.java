package com.project.repository;

import com.project.entity.Dependencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DependenciaRepository extends JpaRepository<Dependencia,Long> {
}
