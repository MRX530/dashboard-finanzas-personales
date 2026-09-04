package com.finanzas.controller;

import com.finanzas.model.Transaccion;
import com.finanzas.service.TransaccionResultado;
import com.finanzas.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = "*")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @PostMapping
    public TransaccionResultado registrar(@RequestBody Transaccion transaccion) {
        return transaccionService.registrarTransaccion(transaccion);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Transaccion> listarPorUsuario(@PathVariable Long usuarioId) {
        return transaccionService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/balance")
    public Map<String, Double> balance(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        Double balance = transaccionService.calcularBalance(usuarioId, inicio, fin);
        return Map.of("balance", balance);
    }
}
