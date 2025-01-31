package es.xalpha.gym.contoladora;

import es.xalpha.gym.logica.entidad.Cliente;
import es.xalpha.gym.logica.entidad.Factura;
import es.xalpha.gym.persistencia.ClienteJpaController;
import es.xalpha.gym.persistencia.FacturaJpaController;
import es.xalpha.gym.persistencia.exceptions.NonexistentEntityException;

import java.util.List;

public class PersistenceController {

    ClienteJpaController clienteJpaController = new ClienteJpaController();
    FacturaJpaController facturaJpaController = new FacturaJpaController();

    public void crearCliente(Cliente cliente) {
        clienteJpaController.create(cliente);
    }

    public List<Cliente> getListaClientes() {
        return clienteJpaController.findClienteEntities();
    }

    public Cliente getCliente(Long id) {
        return clienteJpaController.obtenerClientePor(id);
    }

    public void editarCliente(Cliente cliente) throws NonexistentEntityException {
        clienteJpaController.edit(cliente);
    }

    public void eliminarCliente(Long idCliente) throws NonexistentEntityException {
        clienteJpaController.destroy(idCliente);
    }

    public List<Cliente> getListaOrdenadaCliente(boolean orden,
                                                 String ordenarPor,
                                                 String filtro) {
        return clienteJpaController.clientesOrdenadosPor(orden, ordenarPor,
                filtro);
    }

    public List<Factura> getListaOrdenadaFactura(boolean orden,
                                                 String ordenarPor,
                                                 String filtro) {
        return facturaJpaController.facturasOrdenadosPor(orden, ordenarPor,
                filtro);
    }

    public List<Factura> getListaFacturas() {
        return facturaJpaController.findFacturaEntities();
    }

    public List<Cliente> filtrarClientes(String filtro) {
        return clienteJpaController.filtrarClientes(filtro);
    }

    public List<Factura> filtrarFacturas(String filtro) {
        return facturaJpaController.filtrarFacturas(filtro);
    }

    public Factura getFactura(Long idFactura) {
        return facturaJpaController.obtenerFacturaPor(idFactura);
    }
}
