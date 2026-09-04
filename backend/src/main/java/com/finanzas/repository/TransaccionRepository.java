package com.finanzas.repository;

import com.finanzas.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByUsuarioId(Long usuarioId);
    List<Transaccion> findByCategoriaIdAndFechaBetween(Long categoriaId, LocalDateTime inicio, LocalDateTime fin);
    List<Transaccion> findByUsuarioIdAndFechaBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
}
