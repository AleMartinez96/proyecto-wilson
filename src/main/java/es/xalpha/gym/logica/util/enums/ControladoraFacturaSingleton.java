package es.xalpha.gym.logica.util.enums;

import es.xalpha.gym.controladora.ControladoraFactura;

public enum ControladoraFacturaSingleton {
    INSTANCIA;
    private ControladoraFactura controllerFactura;

    public ControladoraFactura getControllerFactura() {
        if (controllerFactura == null) {
            controllerFactura = new ControladoraFactura();
        }
        return controllerFactura;
    }
}
