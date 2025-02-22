package es.xalpha.gym.logica.entidad;

import es.xalpha.gym.logica.util.UtilLogica;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "facturas")
public class Factura implements Serializable {

    @Id
    @SequenceGenerator(name = "factura_sequence", sequenceName =
            "factura_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_factura")
    private Long idFactura;

    @Column(name = "nombre_local")
    private String nomLocal;

    @JdbcType(VarcharJdbcType.class)
    @Column(name = "nro_factura")
    private UUID nroFactura;

    @Column(name = "fecha_de_emision")
    private LocalDate fechaEmision;
    private Double monto;

    @OneToOne(mappedBy = "factura", fetch = FetchType.LAZY, cascade =
            CascadeType.PERSIST)
    private Cliente cliente;

    public Factura(Long idFactura, String nomLocal, UUID nroFactura,
                   LocalDate fechaEmision, Double monto, Cliente clientes) {
        this.idFactura = idFactura;
        this.nomLocal = nomLocal;
        this.nroFactura = nroFactura;
        this.fechaEmision = fechaEmision;
        this.monto = monto;
        this.cliente = clientes;
    }

    public Factura() {
    }

    public Long getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura = idFactura;
    }

    public String getNomLocal() {
        return nomLocal;
    }

    public void setNomLocal(String nomLocal) {
        this.nomLocal = UtilLogica.capitalizarNombre(nomLocal);
    }

    public UUID getNroFactura() {
        return nroFactura;
    }

    public void setNroFactura(UUID nroFactura) {
        this.nroFactura = nroFactura;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
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
        Factura factura = (Factura) object;
        return Objects.equals(idFactura, factura.idFactura) &&
               Objects.equals(nomLocal, factura.nomLocal) &&
               Objects.equals(nroFactura, factura.nroFactura) &&
               Objects.equals(fechaEmision, factura.fechaEmision) &&
               Objects.equals(monto, factura.monto) &&
               Objects.equals(cliente, factura.cliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFactura, nomLocal, nroFactura, fechaEmision,
                monto, cliente);
    }

    @Override
    public String toString() {
        return "Factura{" + "idFactura=" + idFactura + ", nomLocal='" +
               nomLocal + '\'' + ", nroFactura=" + nroFactura +
               ", fechaEmision=" + fechaEmision + ", monto=" + monto +
               ", clientes=" + cliente + '}';
    }
}
