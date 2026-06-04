package coviello.gestion_de_alumnos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Preinscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String direccion;
    private String localidad;
    private LocalDate fechaNacimiento;
    private String lugarNacimiento;
    private String nacionalidad;
    private Boolean egresadoSecundaria;
    private String egresadoDe;
    private String tituloDe;
    private Boolean debeMaterias;
    @Column(length = 1000)
    private String materiasAdeudadas;
    @Column(length = 500)
    private String afeccionEspecifica;
    private String grupoSanguineo;

    // Checklist de requisitos — completa el admin presencialmente
    private Boolean reqTituloSecundario;
    private Boolean reqConstanciaTituloTramite;
    private Boolean reqDni;
    private Boolean reqFoto;
    private Boolean reqActaNacimiento;
    private Boolean reqPsicofisico;
    private Boolean reqBuenaConducta;

    @ManyToOne
    private Carrera carrera;

    private Boolean pagoValidado;
    private Boolean documentosCompletos;

    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPreinscripcion estado;

    @JsonIgnore
    @OneToMany(mappedBy = "preinscripcion", cascade = CascadeType.ALL)
    private List<Documento> documentos;

    public Preinscripcion() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getLugarNacimiento() { return lugarNacimiento; }
    public void setLugarNacimiento(String lugarNacimiento) { this.lugarNacimiento = lugarNacimiento; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public Boolean getEgresadoSecundaria() { return egresadoSecundaria; }
    public void setEgresadoSecundaria(Boolean egresadoSecundaria) { this.egresadoSecundaria = egresadoSecundaria; }

    public String getEgresadoDe() { return egresadoDe; }
    public void setEgresadoDe(String egresadoDe) { this.egresadoDe = egresadoDe; }

    public String getTituloDe() { return tituloDe; }
    public void setTituloDe(String tituloDe) { this.tituloDe = tituloDe; }

    public Boolean getDebeMaterias() { return debeMaterias; }
    public void setDebeMaterias(Boolean debeMaterias) { this.debeMaterias = debeMaterias; }

    public String getMateriasAdeudadas() { return materiasAdeudadas; }
    public void setMateriasAdeudadas(String materiasAdeudadas) { this.materiasAdeudadas = materiasAdeudadas; }

    public String getAfeccionEspecifica() { return afeccionEspecifica; }
    public void setAfeccionEspecifica(String afeccionEspecifica) { this.afeccionEspecifica = afeccionEspecifica; }

    public String getGrupoSanguineo() { return grupoSanguineo; }
    public void setGrupoSanguineo(String grupoSanguineo) { this.grupoSanguineo = grupoSanguineo; }

    public Boolean getReqTituloSecundario() { return reqTituloSecundario; }
    public void setReqTituloSecundario(Boolean reqTituloSecundario) { this.reqTituloSecundario = reqTituloSecundario; }

    public Boolean getReqConstanciaTituloTramite() { return reqConstanciaTituloTramite; }
    public void setReqConstanciaTituloTramite(Boolean reqConstanciaTituloTramite) { this.reqConstanciaTituloTramite = reqConstanciaTituloTramite; }

    public Boolean getReqDni() { return reqDni; }
    public void setReqDni(Boolean reqDni) { this.reqDni = reqDni; }

    public Boolean getReqFoto() { return reqFoto; }
    public void setReqFoto(Boolean reqFoto) { this.reqFoto = reqFoto; }

    public Boolean getReqActaNacimiento() { return reqActaNacimiento; }
    public void setReqActaNacimiento(Boolean reqActaNacimiento) { this.reqActaNacimiento = reqActaNacimiento; }

    public Boolean getReqPsicofisico() { return reqPsicofisico; }
    public void setReqPsicofisico(Boolean reqPsicofisico) { this.reqPsicofisico = reqPsicofisico; }

    public Boolean getReqBuenaConducta() { return reqBuenaConducta; }
    public void setReqBuenaConducta(Boolean reqBuenaConducta) { this.reqBuenaConducta = reqBuenaConducta; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    public Boolean getPagoValidado() { return pagoValidado; }
    public void setPagoValidado(Boolean pagoValidado) { this.pagoValidado = pagoValidado; }

    public Boolean getDocumentosCompletos() { return documentosCompletos; }
    public void setDocumentosCompletos(Boolean documentosCompletos) { this.documentosCompletos = documentosCompletos; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public EstadoPreinscripcion getEstado() { return estado; }
    public void setEstado(EstadoPreinscripcion estado) { this.estado = estado; }

    public List<Documento> getDocumentos() { return documentos; }
    public void setDocumentos(List<Documento> documentos) { this.documentos = documentos; }

    @Override
    public String toString() {
        return "Preinscripcion{id=" + id + ", nombre='" + nombre + "', apellido='" + apellido +
                "', dni='" + dni + "', email='" + email + "', direccion='" + direccion +
                "', fechaNacimiento=" + fechaNacimiento + ", estado=" + estado + '}';
    }
}
