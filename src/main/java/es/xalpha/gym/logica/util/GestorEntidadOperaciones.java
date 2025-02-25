package es.xalpha.gym.logica.util;

import es.xalpha.gym.logica.util.exception.NonexistentEntityException;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

import java.util.List;

public abstract class GestorEntidadOperaciones<T> {

    private final EntityManagerFactory emf;
    private final Class<T> entidadDeClase;

    public GestorEntidadOperaciones(Class<T> entidadClase) {
        this.entidadDeClase = entidadClase;
        emf = Persistence.createEntityManagerFactory("gymPU");
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(T object) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public void edit(T object) throws NonexistentEntityException {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(object);
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
            T object = em.getReference(entidadDeClase, id);
            em.remove(object);
            em.getTransaction().commit();
        } catch (EntityNotFoundException e) {
            em.getTransaction().rollback();
            throw new NonexistentEntityException(
                    "El cliente con id " + id + " ya no existe.");
        } finally {
            em.close();
        }
    }

    public List<T> findItemEntities() {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entidadDeClase);
            Root<T> root = cq.from(entidadDeClase);
            cq.select(root);
            TypedQuery<T> tq = em.createQuery(cq);
            return tq.getResultList();
        }
    }

    public T obtenerItemPorID(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.find(entidadDeClase, id);
        }
    }

    public List<T> filtrarItems(String filtro) {
        return filtroValido(
                filtro) ? findItemEntities() :
                findItemEntities().stream().filter(
                item -> filtrarPor(item, filtro)).toList();
    }

    private boolean filtroValido(String filtro) {
        return filtro == null || filtro.trim().isEmpty() ||
               filtro.equalsIgnoreCase("buscar");
    }

    public List<T> itemsOrdenadosPor(boolean ordenar, String ordenarPor) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entidadDeClase);
            Root<T> root = cq.from(entidadDeClase);
            Order order = ordenar ? cb.asc(root.get(ordenarPor)) : cb.desc(
                    root.get(ordenarPor));
            cq.orderBy(order);
            return em.createQuery(cq).getResultList();
        }
    }

    public abstract boolean filtrarPor(T object, String filtro);
}
