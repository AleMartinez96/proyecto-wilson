package es.xalpha.gym.logica.util;

import es.xalpha.gym.logica.entidad.Contacto;
import es.xalpha.gym.logica.entidad.Domicilio;

import java.util.ArrayList;
import java.util.List;

public class Configuracion {

    private String nombre;
    private Domicilio domicilio;
    private Contacto contacto;
    private List<String> membresias;

    public Configuracion(String nombre) {
        this.nombre = nombre;
        this.domicilio = new Domicilio();
        this.contacto = new Contacto();
        this.membresias = new ArrayList<>();
    }

    public Configuracion() {
        this.domicilio = new Domicilio();
        this.contacto = new Contacto();
        this.membresias = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Utils.capitalizarNombre(nombre);
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public List<String> getMembresias() {
        return membresias;
    }

    public void setMembresias(List<String> membresias) {
        this.membresias.clear();
        this.membresias = membresias;
    }
}
