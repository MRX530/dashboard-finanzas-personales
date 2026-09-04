package com.finanzas.service;

import com.finanzas.model.Presupuesto;
import com.finanzas.repository.PresupuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PresupuestoService {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    public List<Presupuesto> listarPorUsuarioYMes(Long usuarioId, String mes) {
        return presupuestoRepository.findByUsuarioIdAndMes(usuarioId, mes);
    }

    public Presupuesto guardar(Presupuesto presupuesto) {
        return presupuestoRepository.save(presupuesto);
    }
}
