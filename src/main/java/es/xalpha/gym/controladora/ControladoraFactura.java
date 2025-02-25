package es.xalpha.gym.controladora;

import es.xalpha.gym.logica.entidad.Factura;
import es.xalpha.gym.persistencia.FacturaPersistenceController;

import java.util.List;

public class ControladoraFactura {

    FacturaPersistenceController controlPersis = new FacturaPersistenceController();

    public List<Factura> getListaOrdenadaFactura(boolean orden,
                                                 String ordenarPor) {
        return controlPersis.getListaOrdenadaFactura(orden, ordenarPor);
    }

    public List<Factura> getListaFacturas() {
        return controlPersis.getListaFacturas();
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
