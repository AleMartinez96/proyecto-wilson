package es.xalpha.gym.logica.entidad;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static es.xalpha.gym.logica.util.Utils.capitalizarNombre;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

    @Id
    @SequenceGenerator(name = "cliente_sequence", sequenceName =
            "cliente_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_cliente")
    private Long idCliente;

    private String apellido;
    private String nombre;
    private Integer edad;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_de_nacimiento")
    private LocalDate fechaNac;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "id_contacto", referencedColumnName = "id_contacto")
    private Contacto contacto;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion")
    private Domicilio domicilio;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "id_membresia", referencedColumnName = "id_membresia")
    private Membresia membresia;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "id_factura", referencedColumnName = "id_factura")
    private Factura factura;

    public Cliente(Long idCliente, String apellido, String nombre,
                   LocalDate fechaNac, Contacto contacto, Domicilio domicilio
            , Membresia membresia, Factura factura) {
        this.idCliente = idCliente;
        this.apellido = apellido;
        this.nombre = nombre;
        this.fechaNac = fechaNac;
        this.contacto = contacto;
        this.domicilio = domicilio;
        this.membresia = membresia;
        this.factura = factura;
    }

    public Cliente() {
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = capitalizarNombre(apellido);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = capitalizarNombre(nombre);
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    public Membresia getMembresia() {
        return membresia;
    }

    public void setMembresia(Membresia membresia) {
        this.membresia = membresia;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }


    public Integer getEdad() {
        return edad;
    }

    public void setEdad() {
        this.edad =
                fechaNac == null ? 0 : (int) ChronoUnit.YEARS.between(fechaNac,
                        LocalDate.now());
    }

    public String nombreCompleto() {
        return apellido + " " + nombre;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Cliente cliente = (Cliente) object;
        return Objects.equals(idCliente, cliente.idCliente) &&
               Objects.equals(apellido, cliente.apellido) &&
               Objects.equals(nombre, cliente.nombre) &&
               Objects.equals(contacto, cliente.contacto) &&
               Objects.equals(domicilio, cliente.domicilio) &&
               Objects.equals(membresia, cliente.membresia) &&
               Objects.equals(factura, cliente.factura);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCliente, apellido, nombre, contacto, domicilio,
                membresia, factura);
    }

    @Override
    public String toString() {
        return "Cliente{" + "idCliente=" + idCliente + ", apellido='" +
               apellido + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}
