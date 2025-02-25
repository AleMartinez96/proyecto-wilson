package es.xalpha.gym.controladora.jpa;

import es.xalpha.gym.logica.entidad.Factura;
import es.xalpha.gym.logica.util.GestorEntidadOperaciones;
import jakarta.persistence.EntityManager;

import java.io.Serializable;

public class FacturaJpaController extends GestorEntidadOperaciones<Factura> implements Serializable {

    public FacturaJpaController() {
        super(Factura.class);
    }

    public void actualizarNombreDeLocalEnFactura(String nombre) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            findItemEntities().forEach(factura -> {
                factura.setNomLocal(nombre);
                em.merge(factura);
            });
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean filtrarPor(Factura factura, String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            return false;
        }
        filtro = filtro.toLowerCase();
        return factura.getCliente().getNombre().toLowerCase().contains(
                filtro) ||
               factura.getCliente().getApellido().toLowerCase().contains(
                       filtro) ||
               factura.getFechaEmision().toString().contains(filtro);
    }
}
