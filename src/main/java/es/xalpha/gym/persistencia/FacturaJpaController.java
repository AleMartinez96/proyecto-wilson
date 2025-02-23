package es.xalpha.gym.persistencia;

import es.xalpha.gym.logica.entidad.Factura;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.io.Serializable;
import java.util.List;

public class FacturaJpaController implements Serializable {

    private final EntityManagerFactory emf;

    public FacturaJpaController() {
        emf = Persistence.createEntityManagerFactory("gymPU");
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Factura> findFacturaEntities() {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Factura> cq = cb.createQuery(Factura.class);
            Root<Factura> root = cq.from(Factura.class);
            cq.select(root);
            TypedQuery<Factura> tq = em.createQuery(cq);
            return tq.getResultList();
        }
    }

    public void actualizarNombreDeLocalEnFactura(String nombre) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<Factura> lista = findFacturaEntities();
            lista.forEach(factura -> {
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

    public List<Factura> facturasOrdenadosPor(boolean ordenar,
                                              String ordenarPor) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Factura> cq = cb.createQuery(Factura.class);
            Root<Factura> root = cq.from(Factura.class);
            Order order = ordenar ? cb.asc(root.get(ordenarPor)) : cb.desc(
                    root.get(ordenarPor));
            cq.orderBy(order);
            return em.createQuery(cq).getResultList();
        }
    }

    public List<Factura> filtrarFacturas(String filtro) {
        return filtro == null ||
               filtro.isEmpty() ? findFacturaEntities() :
                findFacturaEntities().stream().filter(
                factura -> filtrarPor(factura, filtro)).toList();
    }

    private boolean filtrarPor(Factura factura, String filtro) {
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

    public Factura obtenerFacturaPorID(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.find(Factura.class, id);
        }
    }
}
