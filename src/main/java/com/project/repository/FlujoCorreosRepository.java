package com.project.repository;

import com.project.entity.FlujoCorreos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FlujoCorreosRepository extends JpaRepository<FlujoCorreos,Long> {
}
