package es.xalpha.gym.logica.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "contactos")
public class Contacto implements Serializable {

    @Id
    @SequenceGenerator(name = "contacto_sequence", sequenceName =
            "contacto_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_contacto")
    @JsonIgnore
    private Long idContacto;

    private String email;
    private String telefono;

    @OneToOne(mappedBy = "contacto", fetch = FetchType.LAZY)
    @JsonIgnore
    private Cliente cliente;

    public Contacto(Long idContacto, String email, String telefono,
                    Cliente cliente) {
        this.idContacto = idContacto;
        this.email = email;
        this.telefono = telefono;
        this.cliente = cliente;
    }

    public Contacto() {
    }

    public Long getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(Long idContacto) {
        this.idContacto = idContacto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
        Contacto contacto = (Contacto) object;
        return Objects.equals(idContacto, contacto.idContacto) &&
               Objects.equals(email, contacto.email) &&
               Objects.equals(telefono, contacto.telefono) &&
               Objects.equals(cliente, contacto.cliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idContacto, email, telefono, cliente);
    }

    @Override
    public String toString() {
        return "Contacto{" + "idContacto=" + idContacto + ", email='" + email +
               '\'' + ", telefono='" + telefono + '\'' + ", cliente=" +
               cliente + '}';
    }
}
