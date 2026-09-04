package com.finanzas.repository;

import com.finanzas.model.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    List<Presupuesto> findByUsuarioIdAndMes(Long usuarioId, String mes);
    Optional<Presupuesto> findByCategoriaIdAndMes(Long categoriaId, String mes);
}
