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
            throw new NonexistentEntityException("El cliente ya no existe.");
        } finally {
            em.close();
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente cliente = em.getReference(Cliente.class, id);
            em.remove(cliente);
            em.getTransaction().commit();
        } catch (EntityNotFoundException e) {
            em.getTransaction().rollback();
            throw new NonexistentEntityException(
                    "El cliente con id " + id + " ya no existe.");
        } finally {
            em.close();
        }
    }

    public List<Cliente> findClienteEntities() {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
            Root<Cliente> root = cq.from(Cliente.class);
            cq.select(root);
            TypedQuery<Cliente> tq = em.createQuery(cq);
            return tq.getResultList();
        }
    }

    public List<Cliente> filtrarClientes(String filtro) {
        return findClienteEntities().stream().filter(
                cliente -> filtrarPor(cliente, filtro)).toList();
    }

    private boolean filtrarPor(Cliente cliente, String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            return false;
        }
        filtro = filtro.toLowerCase();
        return cliente.getNombre().toLowerCase().contains(filtro) ||
               cliente.getApellido().toLowerCase().contains(filtro);
    }

    public List<Cliente> clientesOrdenadosPor(boolean ordenar,
                                              String ordenarPor) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
            Root<Cliente> root = cq.from(Cliente.class);
            Order order = ordenar ? cb.asc(root.get(ordenarPor)) : cb.desc(
                    root.get(ordenarPor));
            cq.orderBy(order);
            return em.createQuery(cq).getResultList();
        }
    }

    public Cliente obtenerClientePorID(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.find(Cliente.class, id);
        }
    }
}
