package es.xalpha.gym.logica.util;

import es.xalpha.gym.contoladora.ControladoraLogica;

public enum ControladoraLogicaSingleton {
    INSTANCIA;
    private ControladoraLogica controller;

    public ControladoraLogica getController() {
        if (controller == null) {
            controller = new ControladoraLogica();
        }
        return controller;
    }
}
