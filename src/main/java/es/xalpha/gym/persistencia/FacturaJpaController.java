package es.xalpha.gym.persistencia;

import es.xalpha.gym.logica.entidad.Cliente;
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
        return findFacturaEntities(true, -1, -1);
    }

    private List<Factura> findFacturaEntities(boolean all, int maxResults,
                                              int firstResults) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Factura> cq = cb.createQuery(Factura.class);
            Root<Factura> root = cq.from(Factura.class);
            cq.select(root);
            TypedQuery<Factura> tq = em.createQuery(cq);
            if (!all) {
                tq.setMaxResults(maxResults);
                tq.setFirstResult(firstResults);
            }
            return tq.getResultList();
        }
    }

    public List<Factura> facturasOrdenadosPor(boolean ordenar,
                                              String ordenarPor,
                                              String filtro) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Factura> cq = cb.createQuery(Factura.class);
            Root<Factura> root = cq.from(Factura.class);
            if (filtro != null && !filtro.isEmpty()) {
                cq.where(cb.and(generarPredicado(cb, root, filtro)));
            }
            Order order;
            if (ordenar) {
                order = cb.asc(root.get(ordenarPor));
            } else {
                order = cb.desc(root.get(ordenarPor));
            }
            cq.orderBy(order);
            return em.createQuery(cq).getResultList();
        }
    }

    public List<Factura> filtrarFacturas(String filtro) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Factura> cq = cb.createQuery(Factura.class);
            Root<Factura> root = cq.from(Factura.class);
            if (filtro != null && !filtro.isEmpty()) {
                cq.where(cb.and(generarPredicado(cb, root, filtro)));
            }
            cq.select(root);
            TypedQuery<Factura> tq = em.createQuery(cq);
            return tq.getResultList();
        }
    }

    private Predicate[] generarPredicado(CriteriaBuilder cb,
                                         Root<Factura> root, String filtro) {
        String[] partes = filtro.split("\\s+");
        Predicate[] predicates = new Predicate[partes.length];
        Join<Factura, Cliente> joinCliente = root.join("cliente");
        for (int i = 0; i < partes.length; i++) {
            String palabra = "%" + partes[i] + "%";
            predicates[i] = cb.or(cb.like(joinCliente.get("nombre"), palabra),
                    cb.like(joinCliente.get("apellido"), palabra));
        }
        return predicates;
    }

    public Factura obtenerFacturaPor(Object key) {
        try (EntityManager em = getEntityManager()) {
            return em.find(Factura.class, key);
        }
    }
}
