package es.xalpha.gym.persistencia;

import es.xalpha.gym.logica.entidad.Factura;
import es.xalpha.gym.controladora.jpa.FacturaJpaController;

import java.util.List;

public class FacturaPersistenceController {

    FacturaJpaController facturaJpaController = new FacturaJpaController();

    public List<Factura> getListaOrdenadaFactura(boolean orden,
                                                 String ordenarPor) {
        return facturaJpaController.itemsOrdenadosPor(orden, ordenarPor);
    }

    public List<Factura> getListaFacturas() {
        return facturaJpaController.findItemEntities();
    }


    public List<Factura> filtrarFacturas(String filtro) {
        return facturaJpaController.filtrarItems(filtro);
    }

    public Factura getFactura(Long idFactura) {
        return facturaJpaController.obtenerItemPorID(idFactura);
    }

    public void actualizarNombreDeLocalEnFactura(String nombre) {
        facturaJpaController.actualizarNombreDeLocalEnFactura(nombre);
    }
}
