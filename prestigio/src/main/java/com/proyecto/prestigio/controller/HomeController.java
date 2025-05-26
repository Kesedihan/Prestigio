package com.proyecto.prestigio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador principal que maneja las solicitudes relacionadas con las vistas de inicio y login.
 * Este controlador es responsable de servir las páginas HTML de la aplicación,
 * funcionando como un controlador tradicional de Spring MVC.
 * La anotación {@code @Controller} indica que esta clase es un componente de Spring y maneja peticiones web,
 * devolviendo nombres de vistas que serán resueltas por un ViewResolver (ej. Thymeleaf).
 */
@Controller
public class HomeController {

    /**
     * Maneja las solicitudes GET para la ruta raíz ("/") de la aplicación.
     * Cuando un usuario accede a la URL base de la aplicación (ej. http://localhost:8080/),
     * este método devuelve la plantilla HTML correspondiente a la página de inicio.
     *
     * @return El nombre de la vista ("index.html") que se mostrará como página principal.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Maneja las solicitudes GET para la ruta "/login".
     * Este método es responsable de servir la página del formulario de inicio de sesión.
     *
     * @return El nombre de la vista ("login.html") que contiene el formulario de login.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

