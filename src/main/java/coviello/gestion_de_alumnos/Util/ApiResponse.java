package coviello.gestion_de_alumnos.Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private String mensaje;
    private Object data;
}