package com.finanzas.service;

import com.finanzas.model.Transaccion;
import lombok.AllArgsConstructor;
import lombok.Data;

// Representa la respuesta al registrar una transaccion: la transaccion en si,
// mas informacion de si esta cerca o se paso del presupuesto de su categoria.
// No bloqueamos el gasto (el dinero ya se gasto en la vida real), solo alertamos.
@Data
@AllArgsConstructor
public class TransaccionResultado {
    private Transaccion transaccion;
    private Double totalGastadoEnCategoria; // acumulado del mes en esa categoria
    private Double limitePresupuesto;         // null si no hay presupuesto definido
    private Double porcentajeUsado;            // null si no hay presupuesto definido
    private boolean presupuestoExcedido;
}
