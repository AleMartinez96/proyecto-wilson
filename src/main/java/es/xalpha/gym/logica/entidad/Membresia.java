package es.xalpha.gym.logica.entidad;

import es.xalpha.gym.logica.util.Utils;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "membresias")
public class Membresia implements Serializable {

    @Id
    @SequenceGenerator(name = "membresia_sequence", sequenceName =
            "membresia_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_membresia")
    private Long idMembresia;

    private String tipo;
    private Double monto;

    @Temporal(TemporalType.DATE)
    @Column(name = "inicia_el")
    private LocalDate fechaInicio;

    @Temporal(TemporalType.DATE)
    @Column(name = "finaliza_el")
    private LocalDate fechaFin;

    @OneToOne(mappedBy = "membresia", fetch = FetchType.LAZY)
    private Cliente cliente;

    public Membresia(Long idMembresia, String tipo, Double monto,
                     LocalDate fechaInicio, LocalDate fechaFin,
                     Cliente cliente) {
        this.idMembresia = idMembresia;
        this.tipo = tipo;
        this.monto = monto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cliente = cliente;
    }

    public Membresia() {
    }

    public Long getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(Long idMembresia) {
        this.idMembresia = idMembresia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = Utils.capitalizarNombre(tipo);
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
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
        Membresia membresia = (Membresia) object;
        return Objects.equals(idMembresia, membresia.idMembresia) &&
               Objects.equals(tipo, membresia.tipo) &&
               Objects.equals(monto, membresia.monto) &&
               Objects.equals(fechaInicio, membresia.fechaInicio) &&
               Objects.equals(fechaFin, membresia.fechaFin) &&
               Objects.equals(cliente, membresia.cliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMembresia, tipo, monto, fechaInicio, fechaFin,
                cliente);
    }

    @Override
    public String toString() {
        return "Membresia{" + "idMembresia=" + idMembresia + ", tipo='" + tipo +
               '\'' + ", monto=" + monto + ", fechaInicio=" + fechaInicio +
               ", fechaFin=" + fechaFin + ", cliente=" + cliente + '}';
    }
}
