package es.xalpha.gym.logica.util.enums;

import es.xalpha.gym.controladora.ControladoraCliente;

public enum ControladoraClienteSingleton {
    INSTANCIA;
    private ControladoraCliente controllerCliente;

    public ControladoraCliente getControllerLogica() {
        if (controllerCliente == null) {
            controllerCliente = new ControladoraCliente();
        }
        return controllerCliente;
    }

}
