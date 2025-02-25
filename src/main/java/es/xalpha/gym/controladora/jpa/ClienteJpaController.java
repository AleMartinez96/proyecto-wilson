package es.xalpha.gym.controladora.jpa;

import es.xalpha.gym.logica.entidad.Cliente;

import java.io.Serializable;

import es.xalpha.gym.logica.util.GestorEntidadOperaciones;

public class ClienteJpaController extends GestorEntidadOperaciones<Cliente> implements Serializable {

    public ClienteJpaController() {
        super(Cliente.class);
    }

    @Override
    public boolean filtrarPor(Cliente cliente, String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            return false;
        }
        filtro = filtro.toLowerCase();
        return cliente.getNombre().toLowerCase().contains(filtro) ||
               cliente.getApellido().toLowerCase().contains(filtro);
    }
}
