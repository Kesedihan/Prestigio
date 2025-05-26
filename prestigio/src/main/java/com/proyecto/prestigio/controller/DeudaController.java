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

/**
 * Controlador de Spring MVC para la administración de deudas y abonos.
 * Este controlador permite a los administradores:
 * <ul>
 * <li>Visualizar todas las deudas registradas.</li>
 * <li>Crear nuevas deudas (asociadas a una persona y con un monto total).</li>
 * <li>Registrar abonos parciales o totales a deudas existentes.</li>
 * </ul>
 * La ruta base para todas las operaciones de este controlador es "/admin/deudas".
 * Utiliza {@code @Controller} para trabajar con vistas (plantillas HTML) y servir la interfaz de gestión de deudas.
 */
@Controller
@RequestMapping("/admin/deudas")
public class DeudaController {

    private final DeudaRepository deudaRepository;
    private final AbonoRepository abonoRepository;

    /**
     * Constructor para inyectar las dependencias de los repositorios de Deuda y Abono.
     * Spring se encarga de proporcionar las instancias (beans) de estos repositorios.
     *
     * @param deudaRepository Repositorio para acceder y gestionar los datos de las deudas.
     * @param abonoRepository Repositorio para acceder y gestionar los datos de los abonos.
     */
    public DeudaController(DeudaRepository deudaRepository, AbonoRepository abonoRepository) {
        this.deudaRepository = deudaRepository;
        this.abonoRepository = abonoRepository;
    }

    /**
     * Maneja las solicitudes GET a la ruta base "/admin/deudas".
     * Este método recupera todas las deudas de la base de datos y las añade al modelo
     * para ser mostradas en la vista de administración de deudas.
     *
     * @param model Objeto {@link Model} de Spring UI para añadir atributos que serán utilizados por la vista.
     * @return El nombre de la vista ("admin-deudas.html") que muestra el listado de todas las deudas.
     */
    @GetMapping
    public String verDeudas(Model model) {
        // Recupera todas las deudas de la base de datos.
        // Podría ser útil añadir una ordenación o filtrado en una aplicación real.
        model.addAttribute("deudas", deudaRepository.findAll());
        // Retorna el nombre de la plantilla HTML a renderizar.
        return "admin-deudas";
    }

    /**
     * Maneja las solicitudes POST a la ruta "/admin/deudas/nueva" para crear una nueva deuda.
     * Este método recibe los detalles de la nueva deuda (nombre de la persona, descripción, monto total),
     * crea una nueva instancia de {@link Deuda} y la guarda en la base de datos.
     *
     * @param nombrePersona El nombre de la persona o entidad asociada a la deuda.
     * @param descripcion   Una descripción detallada de la deuda.
     * @param montoTotal    El monto total inicial de la deuda.
     * @return Una redirección a la página de administración de deudas ("/admin/deudas") después de crear la deuda.
     */
    @PostMapping("/nueva")
    public String crearDeuda(@RequestParam String nombrePersona,
                             @RequestParam String descripcion,
                             @RequestParam BigDecimal montoTotal) {
        Deuda deuda = new Deuda();
        deuda.setNombrePersona(nombrePersona);
        deuda.setDescripcion(descripcion);
        deuda.setMontoTotal(montoTotal);
        // Al momento de crear la deuda, el monto pagado sería 0,
        // lo que se reflejará a través de los abonos asociados.
        deudaRepository.save(deuda); // Guarda la nueva deuda en la base de datos.
        return "redirect:/admin/deudas"; // Redirige de vuelta a la lista de deudas.
    }

    /**
     * Maneja las solicitudes POST a la ruta "/admin/deudas/abono" para registrar un abono a una deuda existente.
     * Este método recibe el ID de la deuda a la que se va a abonar y el monto del abono.
     * Crea una nueva instancia de {@link Abono}, la asocia a la deuda y la guarda en la base de datos.
     * @param deudaId El ID de la deuda a la que se va a registrar el abono.
     * @param monto   El monto del abono que se va a registrar.
     * @return Una redirección a la página de administración de deudas ("/admin/deudas") después de registrar el abono.
     * @throws java.util.NoSuchElementException Si la deuda con el {@code deudaId} proporcionado no se encuentra.
     */
    @PostMapping("/abono")
    public String registrarAbono(@RequestParam Long deudaId,
                                 @RequestParam BigDecimal monto) {
        // Busca la deuda a la que se le hará el abono. Lanza excepción si no se encuentra.
        Deuda deuda = deudaRepository.findById(deudaId).orElseThrow();

        // Crea una nueva instancia de Abono.
        Abono abono = new Abono();
        abono.setDeuda(deuda); // Asocia el abono a la deuda encontrada.
        abono.setMonto(monto); // Establece el monto del abono.
        abono.setFecha(LocalDate.now()); // Registra la fecha actual del abono.

        abonoRepository.save(abono); // Guarda el nuevo abono en la base de datos.
        // Nota: La lógica para actualizar el 'monto pagado' o 'saldo restante' de la deuda
        // debería manejarse en el modelo Deuda (calculado dinámicamente) o en un servicio
        // para mantener la consistencia con todos los abonos de esa deuda.
        // Aquí solo se registra el abono, no se actualiza directamente la Deuda.

        return "redirect:/admin/deudas"; // Redirige de vuelta a la vista de deudas.
    }
}