package com.finanzas.service;

import com.finanzas.model.*;
import com.finanzas.repository.PresupuestoRepository;
import com.finanzas.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Registra una transaccion y, si es un GASTO, calcula cuanto se lleva
     * gastado en esa categoria durante el mes y lo compara contra el
     * presupuesto definido (si existe).
     *
     * Importante: a diferencia del control de stock del proyecto de inventario,
     * aqui NO se bloquea la operacion aunque se pase del presupuesto -- el
     * dinero ya se gasto en la vida real, lo unico que puede hacer el sistema
     * es avisar. Por eso el resultado incluye un flag "presupuestoExcedido"
     * en vez de lanzar una excepcion que impida guardar.
     */
    @Transactional
    public TransaccionResultado registrarTransaccion(Transaccion transaccion) {
        Transaccion guardada = transaccionRepository.save(transaccion);

        Categoria categoria = guardada.getCategoria();

        // Si es un ingreso, no hay presupuesto que comparar -- se guarda y ya
        if (categoria.getTipo() == TipoCategoria.INGRESO) {
            return new TransaccionResultado(guardada, null, null, null, false);
        }

        String mesActual = YearMonth.from(guardada.getFecha()).format(FORMATO_MES);
        LocalDateTime inicioMes = YearMonth.from(guardada.getFecha()).atDay(1).atStartOfDay();
        LocalDateTime finMes = YearMonth.from(guardada.getFecha()).atEndOfMonth().atTime(23, 59, 59);

        // Suma todo lo gastado en esta categoria durante el mes de la transaccion
        Double totalGastado = transaccionRepository
                .findByCategoriaIdAndFechaBetween(categoria.getId(), inicioMes, finMes)
                .stream()
                .mapToDouble(Transaccion::getMonto)
                .sum();

        Optional<Presupuesto> presupuesto = presupuestoRepository
                .findByCategoriaIdAndMes(categoria.getId(), mesActual);

        if (presupuesto.isEmpty()) {
            // No hay presupuesto definido para esta categoria/mes: se guarda sin alerta
            return new TransaccionResultado(guardada, totalGastado, null, null, false);
        }

        Double limite = presupuesto.get().getMontoLimite();
        Double porcentaje = (totalGastado / limite) * 100;
        boolean excedido = totalGastado > limite;

        return new TransaccionResultado(guardada, totalGastado, limite, porcentaje, excedido);
    }

    public List<Transaccion> listarPorUsuario(Long usuarioId) {
        return transaccionRepository.findByUsuarioId(usuarioId);
    }

    public List<Transaccion> reportePorRangoFechas(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        return transaccionRepository.findByUsuarioIdAndFechaBetween(usuarioId, inicio, fin);
    }

    // Balance simple: suma de ingresos menos suma de gastos, en un rango de fechas
    public Double calcularBalance(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        List<Transaccion> transacciones = transaccionRepository.findByUsuarioIdAndFechaBetween(usuarioId, inicio, fin);

        double ingresos = transacciones.stream()
                .filter(t -> t.getCategoria().getTipo() == TipoCategoria.INGRESO)
                .mapToDouble(Transaccion::getMonto)
                .sum();

        double gastos = transacciones.stream()
                .filter(t -> t.getCategoria().getTipo() == TipoCategoria.GASTO)
                .mapToDouble(Transaccion::getMonto)
                .sum();

        return ingresos - gastos;
    }
}
