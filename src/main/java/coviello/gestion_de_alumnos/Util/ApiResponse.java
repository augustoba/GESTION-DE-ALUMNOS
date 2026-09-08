package coviello.gestion_de_alumnos.Util;

public class ApiResponse {

    private String mensaje;
    private Object data;

    public ApiResponse() {
    }

    public ApiResponse(String mensaje, Object data) {
        this.mensaje = mensaje;
        this.data = data;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
