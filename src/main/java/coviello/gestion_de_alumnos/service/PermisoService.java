package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.OtorgarPermisoRequest;
import coviello.gestion_de_alumnos.dto.PermisoActivoResponse;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermisoService {

    private final UsuarioPermisoRepository usuarioPermisoRepository;
    private final PermisoRepository permisoRepository;
    private final UsuarioRepository usuarioRepository;

    public PermisoService(UsuarioPermisoRepository usuarioPermisoRepository,
                          PermisoRepository permisoRepository,
                          UsuarioRepository usuarioRepository) {
        this.usuarioPermisoRepository = usuarioPermisoRepository;
        this.permisoRepository = permisoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<PermisoActivoResponse> listarPermisosActivos(Long usuarioId) {
        return usuarioPermisoRepository.findPermisosActivosByUsuario(usuarioId, LocalDateTime.now())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public PermisoActivoResponse otorgarPermiso(OtorgarPermisoRequest req, String superAdminUsername) {
        Usuario usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + req.usuarioId()));

        Usuario otorgadoPor = usuarioRepository.findByUsername(superAdminUsername)
                .orElseThrow(() -> new RuntimeException("Super admin no encontrado"));

        Permiso permiso = permisoRepository.findByCodigo(req.codigoPermiso())
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + req.codigoPermiso()));

        UsuarioPermiso up = new UsuarioPermiso();
        up.setUsuario(usuario);
        up.setPermiso(permiso);
        up.setFechaDesde(LocalDateTime.now());
        up.setFechaHasta(req.fechaHasta());
        up.setOtorgadoPor(otorgadoPor);

        return toResponse(usuarioPermisoRepository.save(up));
    }

    @Transactional
    public void revocarPermiso(Long usuarioPermisoId) {
        UsuarioPermiso up = usuarioPermisoRepository.findById(usuarioPermisoId)
                .orElseThrow(() -> new RuntimeException("Permiso asignado no encontrado: " + usuarioPermisoId));
        up.setFechaHasta(LocalDateTime.now());
        usuarioPermisoRepository.save(up);
    }

    public boolean tienePermiso(Long usuarioId, CodigoPermiso codigo) {
        return !usuarioPermisoRepository.findPermisosActivosByUsuarioAndCodigo(
                usuarioId, codigo, LocalDateTime.now()).isEmpty();
    }

    private PermisoActivoResponse toResponse(UsuarioPermiso up) {
        return new PermisoActivoResponse(
                up.getId(),
                up.getPermiso().getCodigo(),
                up.getPermiso().getDescripcion(),
                up.getFechaDesde(),
                up.getFechaHasta(),
                up.getOtorgadoPor().getUsername()
        );
    }
}
