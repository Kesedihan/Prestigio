package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Rol;
import com.proyecto.prestigio.repository.RolRepository;
import com.proyecto.prestigio.service.RolService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public Rol crearRol(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public Optional<Rol> obtenerPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    @Override
    public List<Rol> obtenerTodos() {
        return rolRepository.findAll();
    }
}

