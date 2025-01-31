package es.xalpha.gym.persistencia;

import es.xalpha.gym.logica.entidad.Cliente;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import es.xalpha.gym.persistencia.exceptions.NonexistentEntityException;

import java.util.List;

public class ClienteJpaController implements Serializable {

    private final EntityManagerFactory emf;

    public ClienteJpaController() {
        this.emf = Persistence.createEntityManagerFactory("gymPU");
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public void edit(Cliente cliente) throws NonexistentEntityException {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(cliente);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            String error = "El cliente con id " + cliente.getIdCliente() +
                           " ya no existe.";
            String msg = mensajeError(ex, cliente, error);
            if (!msg.contains("ya no existe")) {
                throw ex;
            }
            Long id = cliente.getIdCliente();
            if (obtenerClientePor(id) == null) {
                throw new NonexistentEntityException(msg);
            }
        } finally {
            em.close();
        }
    }

    private String mensajeError(Exception ex, Object obj, String mensaje) {
        return ex == null || obj == null ? "" : (
                ex.getLocalizedMessage() == null ||
                ex.getLocalizedMessage().isEmpty()) ? mensaje :
                ex.getLocalizedMessage();
    }

    public void destroy(Long id) throws NonexistentEntityException {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            try {
                Cliente cliente = em.getReference(Cliente.class, id);
                em.remove(cliente);
                em.getTransaction().commit();
            } catch (EntityNotFoundException e) {
                em.getTransaction().rollback();
                String error = "El cliente con id " + id + " ya no existe.";
                throw new NonexistentEntityException(
                        mensajeError(e, id, error));
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults,
                                              int firstResult) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
            Root<Cliente> root = cq.from(Cliente.class);
            cq.select(root);
            TypedQuery<Cliente> tq = em.createQuery(cq);
            if (!all) {
                tq.setMaxResults(maxResults);
                tq.setFirstResult(firstResult);
            }
            return tq.getResultList();
        }
    }

    public List<Cliente> filtrarClientes(String filtro) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
            Root<Cliente> root = cq.from(Cliente.class);
            if ((filtro != null && !filtro.isEmpty())) {
                cq.where(cb.and(generarPredicado(cb, root, filtro)));
            }
            cq.select(root);
            TypedQuery<Cliente> tq = em.createQuery(cq);
            return tq.getResultList();
        }
    }

    private Predicate[] generarPredicado(CriteriaBuilder cb,
                                         Root<Cliente> root, String filtro) {
        String[] partes = filtro.split("\\s+");
        Predicate[] predicates = new Predicate[partes.length];
        for (int i = 0; i < partes.length; i++) {
            String palabra = "%" + partes[i] + "%";
            predicates[i] = cb.or(cb.like(root.get("nombre"), palabra),
                    cb.like(root.get("apellido"), palabra));
        }
        return predicates;
    }

    public List<Cliente> clientesOrdenadosPor(boolean ordenar,
                                              String ordenarPor,
                                              String filtro) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
            Root<Cliente> root = cq.from(Cliente.class);
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

    public Cliente obtenerClientePor(Object key) {
        try (EntityManager em = getEntityManager()) {
            return em.find(Cliente.class, key);
        }
    }
}
