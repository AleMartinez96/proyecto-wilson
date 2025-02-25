package es.xalpha.gym.persistencia;

import es.xalpha.gym.logica.entidad.Cliente;
import es.xalpha.gym.controladora.jpa.ClienteJpaController;
import es.xalpha.gym.logica.util.exception.NonexistentEntityException;

import java.util.List;

public class ClientePersistenceController {

    ClienteJpaController clienteJpaController = new ClienteJpaController();

    public void crearCliente(Cliente cliente) {
        clienteJpaController.create(cliente);
    }

    public List<Cliente> getListaClientes() {
        return clienteJpaController.findItemEntities();
    }

    public Cliente getCliente(Long id) {
        return clienteJpaController.obtenerItemPorID(id);
    }

    public void editarCliente(Cliente cliente) throws NonexistentEntityException {
        clienteJpaController.edit(cliente);
    }

    public void eliminarCliente(Long idCliente) throws NonexistentEntityException {
        clienteJpaController.destroy(idCliente);
    }

    public List<Cliente> getListaOrdenadaCliente(boolean orden,
                                                 String ordenarPor) {
        return clienteJpaController.itemsOrdenadosPor(orden, ordenarPor);
    }

    public List<Cliente> filtrarClientes(String filtro) {
        return clienteJpaController.filtrarItems(filtro);
    }
}
