package es.xalpha.gym.logica.util.interfaces;

import javax.swing.*;
import java.util.List;

public interface ControladorDeValidacionYComponentes {

    boolean datosValidos(List<JComponent> components, String email,
                         String telefono);

    void resaltarComponentesVaciosOIncorrectos(List<JComponent> components,
                                               JTextField email,
                                               JTextField telefono);

    void restaurarColorComponentes(List<JComponent> components,
                                   JTextField email, JTextField telefono);
}
