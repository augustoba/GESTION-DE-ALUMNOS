package coviello.gestion_de_alumnos.service;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.repository.CarreraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    // Crear nueva carrera
    public Carrera crearCarrera(Carrera carrera) {
        return carreraRepository.save(carrera);
    }

    // Obtener todas las carreras
    public List<Carrera> obtenerTodas() {
        return carreraRepository.findAll();
    }

    // Obtener por ID (lanza excepción si no existe)
    public Carrera obtenerPorId(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada con ID: " + id));
    }

    // Actualizar carrera
    public Carrera actualizarCarrera(Long id, Carrera carreraActualizada) {
        Carrera carrera = obtenerPorId(id);
        carrera.setNombre(carreraActualizada.getNombre());
        carrera.setDescripcion(carreraActualizada.getDescripcion());
        return carreraRepository.save(carrera);
    }

    // Eliminar carrera
    public void eliminarCarrera(Long id) {
        if (!carreraRepository.existsById(id)) {
            throw new RuntimeException("Carrera no encontrada con ID: " + id);
        }
        carreraRepository.deleteById(id);
    }

    // Buscar por nombre
    public List<Carrera> buscarPorNombre(String nombre) {
        return carreraRepository.findByNombreContainingIgnoreCase(nombre);
    }
}