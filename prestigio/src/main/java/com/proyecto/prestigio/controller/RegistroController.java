package com.proyecto.prestigio.controller;

import com.proyecto.prestigio.model.Rol;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.RolRepository;
import com.proyecto.prestigio.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegistroController(UsuarioRepository usuarioRepository, RolRepository rolRepository, BCryptPasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = encoder;
    }

    @GetMapping("/registro")
    public String formularioRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String nombre,
                                   @RequestParam String email,
                                   @RequestParam String telefono,
                                   @RequestParam String password) {


        if (usuarioRepository.findByEmail(email).isPresent()) {
            return "redirect:/registro?error=email"; // ya existe
        }

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseGet(() -> rolRepository.save(new Rol(null, "CLIENTE", null)));

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRoles(Set.of(rolCliente));

        usuarioRepository.save(usuario);
        return "redirect:/";
    }

}
