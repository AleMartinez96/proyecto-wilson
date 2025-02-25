package es.xalpha.gym.logica.util.gui;

import com.toedter.calendar.JDateChooser;
import es.xalpha.gym.vista.gestion.cliente.RegistrarCliente;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UtilGUI {

    public static void mensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipoMensaje);
    }

    public static Integer opcion(String mensaje, String titulo,
                                 int tipoOpcion) {
        return JOptionPane.showConfirmDialog(null, mensaje, titulo, tipoOpcion);
    }

    public static void setCalendario(JDateChooser calendario) {
        calendario.setIcon(new ImageIcon(Objects.requireNonNull(
                RegistrarCliente.class.getResource("/image/calendario.png"))));
        calendario.getDateEditor().getUiComponent().setEnabled(false);
        calendario.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calendario.getCalendarButton().setBackground(new Color(0, 0, 0, 0));
        calendario.getCalendarButton().setToolTipText("calendario");
        calendario.getCalendarButton().setBorder(null);
        JTextField txtCalendario =
                (JTextField) calendario.getDateEditor().getUiComponent();
        txtCalendario.setDisabledTextColor(Color.black);
        Locale locale = Locale.of("es", "ES");
        calendario.setLocale(locale);
        calendario.setDateFormatString("dd-MM-yyyy");
    }

    public static boolean esTextoVacio(JComponent component) {
        return component instanceof JTextField textField &&
               textField.getText().isEmpty();
    }

    public static boolean esCalendarioVacio(JComponent component) {
        return component instanceof JDateChooser chooser &&
               chooser.getDate() == null;
    }

    public static boolean sonTextFieldsVacios(List<JTextField> TextFields) {
        List<JTextField> vacio = TextFields.stream().filter(
                UtilGUI::esTextoVacio).toList();
        return vacio.isEmpty();
    }

    public static boolean sonComponentesVacios(List<JComponent> components) {
        List<JComponent> componentesVacios = components.stream().filter(
                component -> esCalendarioVacio(component) ||
                             esTextoVacio(component)).toList();
        return componentesVacios.isEmpty();
    }

}
