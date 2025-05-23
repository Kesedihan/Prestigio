package com.proyecto.prestigio.controller;


import com.proyecto.prestigio.model.Abono;
import com.proyecto.prestigio.model.Deuda;
import com.proyecto.prestigio.repository.AbonoRepository;
import com.proyecto.prestigio.repository.DeudaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/deudas")
public class DeudaController {

    private final DeudaRepository deudaRepository;
    private final AbonoRepository abonoRepository;

    public DeudaController(DeudaRepository deudaRepository, AbonoRepository abonoRepository) {
        this.deudaRepository = deudaRepository;
        this.abonoRepository = abonoRepository;
    }

    @GetMapping
    public String verDeudas(Model model) {
        model.addAttribute("deudas", deudaRepository.findAll());
        return "admin-deudas";
    }

    @PostMapping("/nueva")
    public String crearDeuda(@RequestParam String nombrePersona,
                             @RequestParam String descripcion,
                             @RequestParam BigDecimal montoTotal) {
        Deuda deuda = new Deuda();
        deuda.setNombrePersona(nombrePersona);
        deuda.setDescripcion(descripcion);
        deuda.setMontoTotal(montoTotal);
        deudaRepository.save(deuda);
        return "redirect:/admin/deudas";
    }

    @PostMapping("/abono")
    public String registrarAbono(@RequestParam Long deudaId,
                                 @RequestParam BigDecimal monto) {
        Deuda deuda = deudaRepository.findById(deudaId).orElseThrow();
        Abono abono = new Abono();
        abono.setDeuda(deuda);
        abono.setMonto(monto);
        abono.setFecha(LocalDate.now());
        abonoRepository.save(abono);
        return "redirect:/admin/deudas";
    }
}

