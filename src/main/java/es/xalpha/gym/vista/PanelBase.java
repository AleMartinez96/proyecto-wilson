package es.xalpha.gym.vista;

import com.toedter.calendar.JDateChooser;
import es.xalpha.gym.logica.util.UtilGUI;
import es.xalpha.gym.logica.util.UtilLogica;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Predicate;

public class PanelBase extends JPanel {

    public boolean datosValidos(List<JComponent> components, String email,
                                String telefono) {
        return UtilGUI.sonComponentesVacios(components) &&
               UtilLogica.esEmailValido(email) &&
               UtilLogica.esNumeroDeTelValido(telefono);
    }

    public void resaltarComponentesVaciosOIncorrectos(List<JComponent> components, JTextField email, JTextField telefono) {
        SwingUtilities.invokeLater(() -> {
            components.forEach(
                    component -> cambiarColorComponente(component, Color.red));
            resaltarSiEsInvalido(email, UtilLogica::esEmailValido, Color.red);
            resaltarSiEsInvalido(telefono, UtilLogica::esNumeroDeTelValido,
                    Color.red);
        });
    }

    public void restaurarColorComponentes(List<JComponent> components,
                                          JTextField email,
                                          JTextField telefono) {
        SwingUtilities.invokeLater(() -> {
            components.forEach(component -> cambiarColorComponente(component,
                    Color.white));
            email.setBackground(Color.white);
            telefono.setBackground(Color.white);
        });
    }

    private void cambiarColorComponente(JComponent component, Color color) {
        if (component instanceof JDateChooser chooser) {
            chooser.getDateEditor().getUiComponent().setBackground(color);
        } else {
            component.setBackground(color);
        }
    }

    public void resaltarSiEsInvalido(JTextField textField,
                                     Predicate<String> validador, Color color) {
        if (!validador.test(textField.getText())) {
            textField.setBackground(color);
        }
    }
}
