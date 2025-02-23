package es.xalpha.gym.vista;

import com.toedter.calendar.JDateChooser;
import es.xalpha.gym.logica.util.ControladorDeValidacionYComponentes;

import javax.swing.*;
import java.awt.*;
import java.util.function.Predicate;

public abstract class PanelBase extends JPanel implements ControladorDeValidacionYComponentes {

    public void cambiarColorComponente(JComponent component, Color color) {
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
