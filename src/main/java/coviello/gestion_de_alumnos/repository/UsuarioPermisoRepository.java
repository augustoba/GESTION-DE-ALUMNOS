package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.CodigoPermiso;
import coviello.gestion_de_alumnos.model.UsuarioPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UsuarioPermisoRepository extends JpaRepository<UsuarioPermiso, Long> {

    List<UsuarioPermiso> findByUsuarioId(Long usuarioId);

    @Query("""
           SELECT up FROM UsuarioPermiso up
           WHERE up.usuario.id = :usuarioId
             AND up.permiso.codigo = :codigo
             AND (up.fechaHasta IS NULL OR up.fechaHasta > :ahora)
           """)
    List<UsuarioPermiso> findPermisosActivosByUsuarioAndCodigo(
            @Param("usuarioId") Long usuarioId,
            @Param("codigo") CodigoPermiso codigo,
            @Param("ahora") LocalDateTime ahora);

    @Query("""
           SELECT up FROM UsuarioPermiso up
           WHERE up.usuario.id = :usuarioId
             AND (up.fechaHasta IS NULL OR up.fechaHasta > :ahora)
           """)
    List<UsuarioPermiso> findPermisosActivosByUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("ahora") LocalDateTime ahora);
}
