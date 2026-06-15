package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @deprecated Reemplazada por DocumentoChecklist (proceso presencial)
 *             y DocumentoDigital (subida durante el año).
 *             Se mantiene temporalmente para evitar errores de compilación
 *             mientras se migran los servicios y controladores.
 */
@Deprecated
@Entity
@Table(name = "documento_legacy")
@Data
@NoArgsConstructor
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
