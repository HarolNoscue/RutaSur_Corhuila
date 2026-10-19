package com.rutasur.repository;

import com.rutasur.model.Experiencia;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Experiencia e where e.id=:id")
    Optional<Experiencia> bloquearFila(@Param("id") Long id);
}
