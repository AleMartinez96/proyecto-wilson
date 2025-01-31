package es.xalpha.gym.logica.enums;

import es.xalpha.gym.logica.util.Utils;

public enum TipoDeMembresia {

    ANUAL(0.0), MENSUAL(0.0);

    private double precio;

    TipoDeMembresia(double precio) {
        this.precio = precio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return Utils.capitalizarNombre(this.name());
    }
}
