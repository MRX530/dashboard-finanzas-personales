package com.finanzas.controller;

import com.finanzas.model.Presupuesto;
import com.finanzas.service.PresupuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presupuestos")
@CrossOrigin(origins = "*")
public class PresupuestoController {

    @Autowired
    private PresupuestoService presupuestoService;

    // ej: /api/presupuestos/usuario/1?mes=2026-09
    @GetMapping("/usuario/{usuarioId}")
    public List<Presupuesto> listarPorUsuarioYMes(@PathVariable Long usuarioId, @RequestParam String mes) {
        return presupuestoService.listarPorUsuarioYMes(usuarioId, mes);
    }

    @PostMapping
    public Presupuesto crear(@RequestBody Presupuesto presupuesto) {
        return presupuestoService.guardar(presupuesto);
    }
}
