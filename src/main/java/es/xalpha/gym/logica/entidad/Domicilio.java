package es.xalpha.gym.logica.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.xalpha.gym.logica.util.UtilLogica;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "direcciones")
public class Domicilio implements Serializable {

    @Id
    @SequenceGenerator(name = "direccion_sequence", sequenceName =
            "direccion_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_direccion")
    @JsonIgnore
    private Long idDireccion;

    private String calle;
    @JsonIgnore
    private String barrio;

    @OneToOne(mappedBy = "domicilio", fetch = FetchType.LAZY)
    @JsonIgnore
    private Cliente cliente;

    public Domicilio(Long idDireccion, String calle, String barrio,
                     Cliente cliente) {
        this.idDireccion = idDireccion;
        this.calle = calle;
        this.barrio = barrio;
        this.cliente = cliente;
    }

    public Domicilio() {
    }

    public Long getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Long idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = UtilLogica.capitalizarNombre(calle);
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = UtilLogica.capitalizarNombre(barrio);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Domicilio domicilio = (Domicilio) object;
        return Objects.equals(idDireccion, domicilio.idDireccion) &&
               Objects.equals(calle, domicilio.calle) &&
               Objects.equals(barrio, domicilio.barrio) &&
               Objects.equals(cliente, domicilio.cliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDireccion, calle, barrio, cliente);
    }

    @Override
    public String toString() {
        return "Domicilio{" + "idDireccion=" + idDireccion + ", calle='" +
               calle + '\'' + ", barrio='" + barrio + '\'' + ", cliente=" +
               cliente + '}';
    }
}
