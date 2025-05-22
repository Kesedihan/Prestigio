package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Usuario;

import java.util.List;

public interface CitaService {
    Cita agendarCita(Cita cita);
    List<Cita> obtenerCitasDeUsuario(Usuario usuario);
}

