package es.xalpha.gym.contoladora;

import es.xalpha.gym.logica.entidad.*;
import es.xalpha.gym.persistencia.exceptions.NonexistentEntityException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ControladoraLogica {

    PersistenceController controlPersis = new PersistenceController();

    public void crearCliente(String nombre, String apellido, String calle,
                             String barrio, String email, String telefono,
                             String tipo, Double precio, LocalDate fechaNac,
                             LocalDate fechaInicio, LocalDate fechaFin,
                             String nombreLocal) {

        Cliente cliente = new Cliente();
        Domicilio domicilio = new Domicilio();
        Contacto contacto = new Contacto();
        Membresia membresia = new Membresia();
        Factura factura = new Factura();

        cliente.setApellido(apellido);
        cliente.setNombre(nombre);
        cliente.setFechaNac(fechaNac);
        cliente.setEdad();

        domicilio.setBarrio(barrio);
        domicilio.setCalle(calle);
        cliente.setDomicilio(domicilio);
        domicilio.setCliente(cliente);

        contacto.setEmail(email);
        contacto.setTelefono(telefono);
        cliente.setContacto(contacto);
        contacto.setCliente(cliente);

        membresia.setFechaFin(fechaFin);
        membresia.setFechaInicio(fechaInicio);
        membresia.setMonto(precio);
        membresia.setTipo(tipo);
        cliente.setMembresia(membresia);
        membresia.setCliente(cliente);

        factura.setFechaEmision(LocalDate.now());
        factura.setNomLocal(nombreLocal);
        factura.setNroFactura(UUID.randomUUID());
        factura.setMonto(precio);
        cliente.setFactura(factura);
        factura.setCliente(cliente);

        controlPersis.crearCliente(cliente);
    }

    public List<Cliente> getListaClientes() {
        return controlPersis.getListaClientes();
    }

    public Cliente getCliente(Long id) {
        return controlPersis.getCliente(id);
    }

    public void editarCliente(Cliente cliente, String nombre, String apellido
            , String calle, String barrio, String email, String tel,
                              String tipo, Double precio, LocalDate fechaNac,
                              LocalDate fechaInicio, LocalDate fechaFin) throws NonexistentEntityException {

        Domicilio domicilio = cliente.getDomicilio();
        Contacto contacto = cliente.getContacto();
        Membresia membresia = cliente.getMembresia();

        cliente.setApellido(apellido);
        cliente.setNombre(nombre);
        cliente.setFechaNac(fechaNac);
        cliente.setEdad();

        domicilio.setBarrio(barrio);
        domicilio.setCalle(calle);
        domicilio.setCliente(cliente);

        contacto.setEmail(email);
        contacto.setTelefono(tel);

        membresia.setFechaFin(fechaFin);
        membresia.setFechaInicio(fechaInicio);
        membresia.setMonto(precio);
        membresia.setTipo(tipo);

        controlPersis.editarCliente(cliente);
    }

    public void eliminarCliente(Long idCliente) throws NonexistentEntityException {
        controlPersis.eliminarCliente(idCliente);
    }

    public List<Cliente> getListaOrdenadaCliente(boolean orden,
                                                 String ordenarPor) {
        return controlPersis.getListaOrdenadaCliente(orden, ordenarPor);
    }

    public List<Factura> getListaOrdenadaFactura(boolean orden,
                                                 String ordenarPor) {
        return controlPersis.getListaOrdenadaFactura(orden, ordenarPor);
    }

    public List<Factura> getListaFacturas() {
        return controlPersis.getListaFacturas();
    }

    public List<Cliente> filtrarClientes(String filtro) {
        return controlPersis.filtrarClientes(filtro);
    }

    public List<Factura> filtrarFacturas(String filtro) {
        return controlPersis.filtrarFacturas(filtro);
    }

    public Factura getFactura(Long idFactura) {
        return controlPersis.getFactura(idFactura);
    }

    public void actualizarNombreDeLocalEnFactura(String nombre) {
        controlPersis.actualizarNombreDeLocalEnFactura(nombre);
    }
}
