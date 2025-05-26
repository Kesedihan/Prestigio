package com.proyecto.prestigio.service;

import com.proyecto.prestigio.model.Cita;
import com.proyecto.prestigio.model.Usuario;
import com.proyecto.prestigio.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de lógica de negocio para la gestión de citas.
 * Esta clase implementa la interfaz {@link CitaService} y proporciona las operaciones
 * relacionadas con la entidad {@link Cita}, como agendar nuevas citas y obtener
 * las citas de un usuario específico.
 * Utiliza {@code @Service} para ser reconocido como un componente de servicio de Spring.
 */
@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    /**
     * Constructor para inyectar la dependencia del repositorio de citas.
     * Spring se encarga de proporcionar la instancia de {@link CitaRepository}.
     * La inyección por constructor es la forma preferida en Spring para manejar dependencias,
     * ya que facilita la inmutabilidad y la testabilidad.
     *
     * @param citaRepository Repositorio para acceder y gestionar los datos de las citas.
     */
    public CitaServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    /**
     * Agenda (guarda) una nueva cita en la base de datos.
     *
     * @param cita El objeto {@link Cita} a ser agendado.
     * @return El objeto {@link Cita} persistido, que incluirá el ID generado por la base de datos.
     * @Override indica que este método implementa un método de la interfaz {@link CitaService}.
     */
    @Override
    public Cita agendarCita(Cita cita) {
        // Delega la operación de guardado directamente al repositorio.
        return citaRepository.save(cita);
    }

    /**
     * Obtiene una lista de todas las citas agendadas para un usuario específico.
     *
     * @param usuario El objeto {@link Usuario} del cual se desean obtener las citas.
     * @return Una {@link List} de objetos {@link Cita} asociados al usuario.
     * @Override indica que este método implementa un método de la interfaz {@link CitaService}.
     */
    @Override
    public List<Cita> obtenerCitasDeUsuario(Usuario usuario) {
        // Delega la operación de búsqueda de citas por usuario al repositorio.
        // Se asume que el repositorio tiene un método findByUsuario definido.
        return citaRepository.findByUsuario(usuario);
    }
}