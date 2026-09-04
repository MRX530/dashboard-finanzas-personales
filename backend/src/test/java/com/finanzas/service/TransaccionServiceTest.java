package com.finanzas.service;

import com.finanzas.model.*;
import com.finanzas.repository.PresupuestoRepository;
import com.finanzas.repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @InjectMocks
    private TransaccionService transaccionService;

    private Categoria categoriaGasto;
    private Categoria categoriaIngreso;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        categoriaGasto = new Categoria();
        categoriaGasto.setId(10L);
        categoriaGasto.setNombre("Comida");
        categoriaGasto.setTipo(TipoCategoria.GASTO);
        categoriaGasto.setUsuario(usuario);

        categoriaIngreso = new Categoria();
        categoriaIngreso.setId(20L);
        categoriaIngreso.setNombre("Salario");
        categoriaIngreso.setTipo(TipoCategoria.INGRESO);
        categoriaIngreso.setUsuario(usuario);
    }

    @Test
    void gastoDentroDelPresupuesto_noMarcaExcedido() {
        Transaccion nueva = new Transaccion();
        nueva.setId(1L);
        nueva.setCategoria(categoriaGasto);
        nueva.setMonto(50.0);
        nueva.setFecha(LocalDateTime.of(2026, 9, 10, 12, 0));

        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(nueva);

        // Ya hay 50 gastados este mes en Comida (la transaccion que acabamos de guardar)
        when(transaccionRepository.findByCategoriaIdAndFechaBetween(eq(10L), any(), any()))
                .thenReturn(List.of(nueva));

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setMontoLimite(200.0);
        when(presupuestoRepository.findByCategoriaIdAndMes(10L, "2026-09"))
                .thenReturn(Optional.of(presupuesto));

        TransaccionResultado resultado = transaccionService.registrarTransaccion(nueva);

        assertEquals(50.0, resultado.getTotalGastadoEnCategoria());
        assertEquals(200.0, resultado.getLimitePresupuesto());
        assertEquals(25.0, resultado.getPorcentajeUsado()); // 50/200 = 25%
        assertFalse(resultado.isPresupuestoExcedido());
    }

    @Test
    void gastoQueSuperaElPresupuesto_marcaExcedido() {
        Transaccion nueva = new Transaccion();
        nueva.setId(2L);
        nueva.setCategoria(categoriaGasto);
        nueva.setMonto(250.0);
        nueva.setFecha(LocalDateTime.of(2026, 9, 15, 12, 0));

        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(nueva);
        when(transaccionRepository.findByCategoriaIdAndFechaBetween(eq(10L), any(), any()))
                .thenReturn(List.of(nueva)); // total del mes: 250

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setMontoLimite(200.0);
        when(presupuestoRepository.findByCategoriaIdAndMes(10L, "2026-09"))
                .thenReturn(Optional.of(presupuesto));

        TransaccionResultado resultado = transaccionService.registrarTransaccion(nueva);

        // Clave: la transaccion SI se guarda (se llamo a save), a diferencia
        // del inventario donde una salida sin stock se rechaza por completo.
        verify(transaccionRepository).save(nueva);
        assertTrue(resultado.isPresupuestoExcedido());
        assertEquals(250.0, resultado.getTotalGastadoEnCategoria());
    }

    @Test
    void gastoSinPresupuestoDefinido_noFalla() {
        Transaccion nueva = new Transaccion();
        nueva.setId(3L);
        nueva.setCategoria(categoriaGasto);
        nueva.setMonto(30.0);
        nueva.setFecha(LocalDateTime.of(2026, 9, 5, 12, 0));

        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(nueva);
        when(transaccionRepository.findByCategoriaIdAndFechaBetween(eq(10L), any(), any()))
                .thenReturn(List.of(nueva));
        when(presupuestoRepository.findByCategoriaIdAndMes(10L, "2026-09"))
                .thenReturn(Optional.empty()); // no hay presupuesto para esta categoria/mes

        TransaccionResultado resultado = transaccionService.registrarTransaccion(nueva);

        assertNull(resultado.getLimitePresupuesto());
        assertNull(resultado.getPorcentajeUsado());
        assertFalse(resultado.isPresupuestoExcedido());
    }

    @Test
    void unIngreso_noConsultaPresupuesto() {
        Transaccion nueva = new Transaccion();
        nueva.setId(4L);
        nueva.setCategoria(categoriaIngreso);
        nueva.setMonto(1000.0);
        nueva.setFecha(LocalDateTime.of(2026, 9, 1, 9, 0));

        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(nueva);

        TransaccionResultado resultado = transaccionService.registrarTransaccion(nueva);

        // Un ingreso nunca deberia consultar presupuestos (no tiene sentido)
        verify(presupuestoRepository, never()).findByCategoriaIdAndMes(anyLong(), anyString());
        assertFalse(resultado.isPresupuestoExcedido());
    }
}
