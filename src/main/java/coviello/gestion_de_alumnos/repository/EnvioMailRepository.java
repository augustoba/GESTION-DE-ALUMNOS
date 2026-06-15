package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.EnvioMail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvioMailRepository extends JpaRepository<EnvioMail, Long> {

    Page<EnvioMail> findAllByOrderByEnviadoEnDesc(Pageable pageable);

    Page<EnvioMail> findByEnviadoPorIdOrderByEnviadoEnDesc(Long usuarioId, Pageable pageable);
}
