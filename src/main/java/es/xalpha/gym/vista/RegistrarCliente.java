package es.xalpha.gym.vista;

import com.toedter.calendar.JDateChooser;
import es.xalpha.gym.contoladora.ControladoraLogica;
import es.xalpha.gym.logica.util.Utils;

import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.xalpha.gym.logica.util.Utils.esEmailValido;

public class RegistrarCliente extends JPanel {

    private final ControladoraLogica controller;
    private final Principal frame;
    private static Timer timer;

    public RegistrarCliente(ControladoraLogica controller, Principal frame) {
        Principal.estilo();
        this.controller = controller;
        this.frame = frame;
        initComponents();
        setCalendario(calendarioNac);
        setCalendario(calendarioInicio);
        setCalendario(calendarioFin);
        btnListener();
        txtListener();
    }

    private void btnListener() {
        btnGuardar.addActionListener(_ -> guardarDatos());
        btnLimpiar.addActionListener(_ -> limpiar());
    }

    private void txtListener() {
        txtMonto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String texto = txtMonto.getText() + e.getKeyChar();
                Pattern pattern = Pattern.compile("^(\\d+.?)(\\d+)?$");
                Matcher matcher = pattern.matcher(texto);
                if (!matcher.matches() || texto.length() > 15) {
                    e.consume();
                }
            }
        });

        txtTelefono.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
    }

    private void guardarDatos() {
        List<JComponent> components = List.of(txtApellido, txtNombre, txtCalle,
                txtBarrio, txtTelefono, calendarioNac, calendarioFin,
                calendarioInicio, txtMonto);
        if (datosValidos(components, txtEmail.getText())) {
            String nombre = txtNombre.getText();
            String nombreLocal = frame.getNombreLocal();
            String apellido = txtApellido.getText();
            String calle = txtCalle.getText();
            String barrio = txtBarrio.getText();
            String email = txtEmail.getText();
            String tel = txtTelefono.getText();
            String tipo = cbxMembresia.getSelectedItem() ==
                          null ? "" : (String) cbxMembresia.getSelectedItem();
            Double monto = Double.parseDouble(txtMonto.getText());
            LocalDate nacimiento = Utils.obtenerLocalDate(
                    calendarioNac.getDate());
            LocalDate fechaInicio = Utils.obtenerLocalDate(
                    calendarioInicio.getDate());
            LocalDate fechaFin = Utils.obtenerLocalDate(
                    calendarioFin.getDate());

            controller.crearCliente(nombre, apellido, calle, barrio, email, tel,
                    tipo, monto, nacimiento, fechaInicio, fechaFin,
                    nombreLocal);
            Utils.mensaje("Los datos del cliente fueron guardados con éxito.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
        } else {
            cambiarColor(components, txtEmail);
            Utils.mensaje(
                    "Algunos campos no están completos. Por favor, revise " +
                    "antes de proceder.", "Cuidado",
                    JOptionPane.WARNING_MESSAGE);
        }
        restaurarColor(components, txtEmail);
    }

    public static boolean datosValidos(List<JComponent> components,
                                       String email) {
        return sonComponentesVacios(components) && esEmailValido(email);
    }

    private static boolean sonComponentesVacios(List<JComponent> components) {
        return components.stream().allMatch(
                component -> !esTextoVacio(component) &&
                             !esCalendarioVacio(component));
    }

    private static boolean esTextoVacio(JComponent component) {
        return JTextField.class.equals(component.getClass()) &&
               ((JTextField) component).getText().isEmpty();
    }

    private static boolean esCalendarioVacio(JComponent component) {
        return JDateChooser.class.equals(component.getClass()) &&
               ((JDateChooser) component).getDate() == null;
    }

    public static void cambiarColor(List<JComponent> components,
                                    JTextField texto) {
        cambiarColorTextField(components);
        cambiarColorCalendar(components);
        cambiarColor(texto, !esEmailValido(texto.getText()));
    }

    private static void cambiarColorTextField(List<JComponent> components) {
        List<JTextField> lista = components.stream().filter(
                component -> JTextField.class.equals(component.getClass())).map(
                component -> (JTextField) component).toList();
        lista.forEach(texto -> cambiarColor(texto, texto.getText().isEmpty()));
    }

    private static void cambiarColorCalendar(List<JComponent> components) {
        List<JDateChooser> lista = components.stream().filter(
                component -> JDateChooser.class.equals(
                        component.getClass())).map(
                component -> (JDateChooser) component).toList();
        lista.forEach(calendar -> cambiarColor(
                calendar.getDateEditor().getUiComponent(),
                calendar.getDate() == null));
    }

    private static void cambiarColor(JComponent component, boolean cambiar) {
        component.setBackground(Color.white);
        if (cambiar) {
            component.setBackground(Color.red);
        }
    }

    public static void restaurarColor(List<JComponent> components,
                                      JTextField texto) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(5000, _ -> SwingUtilities.invokeLater(() -> {
            components.forEach(component -> {
                if (JDateChooser.class.equals(component.getClass())) {
                    ((JDateChooser) component).getDateEditor().getUiComponent().setBackground(
                            Color.white);
                }
                component.setBackground(Color.white);
            });
            texto.setBackground(Color.white);
            timer.stop();
        }));
        timer.setRepeats(false);
        timer.start();
    }

    public static void setCalendario(JDateChooser calendario) {
        calendario.setIcon(new ImageIcon(Objects.requireNonNull(
                RegistrarCliente.class.getResource("/calendario.png"))));
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

    private void limpiar() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtCalle.setText("");
        txtBarrio.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        cbxMembresia.setSelectedIndex(0);
        txtMonto.setText("");
        calendarioNac.setDate(null);
        calendarioInicio.setDate(null);
        calendarioFin.setDate(null);
    }

    public void setComboBox(List<String> lista) {
        cbxMembresia.removeAllItems();
        lista.forEach(cbxMembresia::addItem);
    }

    public JPanel getPanelEdicion() {
        return panelEdicion;
    }

    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEdicion = new JPanel();
        panelDatosPersonales = new JPanel();
        lblApellido = new JLabel();
        txtApellido = new JTextField();
        lblNombre = new JLabel();
        txtNombre = new JTextField();
        lblFecha = new JLabel();
        lblDatosPersonales = new JLabel();
        txtBarrio = new JTextField();
        lblBarrio = new JLabel();
        lblCalle = new JLabel();
        txtCalle = new JTextField();
        lblDomicilio = new JLabel();
        calendarioNac = new com.toedter.calendar.JDateChooser();
        panelDomicilio = new JPanel();
        lblDatosPersonales1 = new JLabel();
        lblMembresia = new JLabel();
        cbxMembresia = new JComboBox<>();
        lblMonto = new JLabel();
        txtMonto = new JTextField();
        lblMembresia1 = new JLabel();
        calendarioInicio = new com.toedter.calendar.JDateChooser();
        lblMembresia2 = new JLabel();
        calendarioFin = new com.toedter.calendar.JDateChooser();
        lblContacto = new JLabel();
        lblEmail = new JLabel();
        txtEmail = new JTextField();
        lblTelefono = new JLabel();
        txtTelefono = new JTextField();
        btnGuardar = new es.xalpha.gym.vista.BotonRedondeado();
        btnLimpiar = new es.xalpha.gym.vista.BotonRedondeado();

        setPreferredSize(new Dimension(800, 500));

        panelEdicion.setOpaque(false);
        panelEdicion.setPreferredSize(new Dimension(800, 500));

        panelDatosPersonales.setOpaque(false);
        panelDatosPersonales.setPreferredSize(new Dimension(290, 300));

        lblApellido.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblApellido.setForeground(new Color(255, 255, 255));
        lblApellido.setText("Apellido");
        lblApellido.setPreferredSize(new Dimension(54, 20));

        txtApellido.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtApellido.setForeground(new Color(0, 0, 0));
        txtApellido.setMinimumSize(new Dimension(68, 30));
        txtApellido.setPreferredSize(new Dimension(190, 30));

        lblNombre.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblNombre.setForeground(new Color(255, 255, 255));
        lblNombre.setText("Nombre");
        lblNombre.setPreferredSize(new Dimension(52, 20));

        txtNombre.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtNombre.setForeground(new Color(0, 0, 0));
        txtNombre.setPreferredSize(new Dimension(190, 30));

        lblFecha.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblFecha.setForeground(new Color(255, 255, 255));
        lblFecha.setText("Fec. Nac.");
        lblFecha.setPreferredSize(new Dimension(60, 20));

        lblDatosPersonales.setFont(new Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDatosPersonales.setForeground(new Color(255, 255, 255));
        lblDatosPersonales.setText("Datos Personales");
        lblDatosPersonales.setPreferredSize(new Dimension(190, 30));

        txtBarrio.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtBarrio.setForeground(new Color(0, 0, 0));
        txtBarrio.setPreferredSize(new Dimension(200, 30));

        lblBarrio.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblBarrio.setForeground(new Color(255, 255, 255));
        lblBarrio.setText("Barrio");
        lblBarrio.setPreferredSize(new Dimension(40, 20));

        lblCalle.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblCalle.setForeground(new Color(255, 255, 255));
        lblCalle.setText("Calle");
        lblCalle.setPreferredSize(new Dimension(34, 20));

        txtCalle.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtCalle.setForeground(new Color(0, 0, 0));
        txtCalle.setPreferredSize(new Dimension(200, 30));

        lblDomicilio.setFont(new Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDomicilio.setForeground(new Color(255, 255, 255));
        lblDomicilio.setText("Domicilio");
        lblDomicilio.setPreferredSize(new Dimension(102, 30));

        calendarioNac.setForeground(new Color(0, 0, 0));
        calendarioNac.setFont(new Font("Roboto", Font.BOLD, 12)); // NOI18N
        calendarioNac.setOpaque(false);
        calendarioNac.setPreferredSize(new Dimension(190, 30));

        GroupLayout panelDatosPersonalesLayout = new GroupLayout(
                panelDatosPersonales);
        panelDatosPersonales.setLayout(panelDatosPersonalesLayout);
        panelDatosPersonalesLayout.setHorizontalGroup(
                panelDatosPersonalesLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelDatosPersonalesLayout.createSequentialGroup().addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING).addComponent(
                                                        lblFecha,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblNombre,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblApellido,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)).addGap(
                                                6, 6, 6).addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING).addGroup(
                                                        panelDatosPersonalesLayout.createParallelGroup(
                                                                GroupLayout.Alignment.TRAILING,
                                                                false).addComponent(
                                                                txtApellido,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE).addComponent(
                                                                txtNombre,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)).addComponent(
                                                        calendarioNac,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        130,
                                                        GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addGap(
                                                6, 6, 6).addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        GroupLayout.Alignment.CENTER).addComponent(
                                                        lblCalle,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblBarrio,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                                LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        GroupLayout.Alignment.CENTER).addComponent(
                                                        txtCalle,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        txtBarrio,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addComponent(
                                                lblDomicilio,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addComponent(
                                                lblDatosPersonales,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE))).addContainerGap(
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        panelDatosPersonalesLayout.setVerticalGroup(
                panelDatosPersonalesLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelDatosPersonalesLayout.createSequentialGroup().addGap(
                                12, 12, 12).addComponent(lblDatosPersonales,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addGap(20, 20,
                                20).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblApellido, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtApellido, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(30,
                                30, 30).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblNombre, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtNombre, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(30,
                                30, 30).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblFecha, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        calendarioNac,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(18,
                                18, 18).addComponent(lblDomicilio,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addGap(20, 20,
                                20).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblCalle, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtCalle, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(30,
                                30, 30).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblBarrio, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtBarrio, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addContainerGap(
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        panelDomicilio.setOpaque(false);

        lblDatosPersonales1.setFont(
                new Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDatosPersonales1.setForeground(new Color(255, 255, 255));
        lblDatosPersonales1.setText("Membresia");
        lblDatosPersonales1.setPreferredSize(new Dimension(122, 30));

        lblMembresia.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMembresia.setForeground(new Color(255, 255, 255));
        lblMembresia.setText("Tipo");
        lblMembresia.setPreferredSize(new Dimension(30, 20));

        cbxMembresia.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        cbxMembresia.setForeground(new Color(0, 0, 0));
        cbxMembresia.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cbxMembresia.setPreferredSize(new Dimension(150, 30));

        lblMonto.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMonto.setForeground(new Color(255, 255, 255));
        lblMonto.setText("Monto");
        lblMonto.setPreferredSize(new Dimension(42, 20));

        txtMonto.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtMonto.setForeground(new Color(0, 0, 0));
        txtMonto.setPreferredSize(new Dimension(170, 30));

        lblMembresia1.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMembresia1.setForeground(new Color(255, 255, 255));
        lblMembresia1.setText("Inicia el");
        lblMembresia1.setPreferredSize(new Dimension(50, 20));

        calendarioInicio.setForeground(new Color(0, 0, 0));
        calendarioInicio.setFont(new Font("Roboto", Font.BOLD, 12)); // NOI18N
        calendarioInicio.setOpaque(false);
        calendarioInicio.setPreferredSize(new Dimension(170, 30));

        lblMembresia2.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMembresia2.setForeground(new Color(255, 255, 255));
        lblMembresia2.setText("Finaliza el");
        lblMembresia2.setPreferredSize(new Dimension(66, 20));

        calendarioFin.setForeground(new Color(0, 0, 0));
        calendarioFin.setFont(new Font("Roboto", Font.BOLD, 12)); // NOI18N
        calendarioFin.setOpaque(false);
        calendarioFin.setPreferredSize(new Dimension(170, 30));

        lblContacto.setFont(new Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblContacto.setForeground(new Color(255, 255, 255));
        lblContacto.setText("Contacto");
        lblContacto.setPreferredSize(new Dimension(100, 30));

        lblEmail.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblEmail.setForeground(new Color(255, 255, 255));
        lblEmail.setText("Email");
        lblEmail.setPreferredSize(new Dimension(36, 20));

        txtEmail.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtEmail.setForeground(new Color(0, 0, 0));
        txtEmail.setPreferredSize(new Dimension(220, 30));

        lblTelefono.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblTelefono.setForeground(new Color(255, 255, 255));
        lblTelefono.setText("Telefono");
        lblTelefono.setPreferredSize(new Dimension(58, 20));

        txtTelefono.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtTelefono.setForeground(new Color(0, 0, 0));
        txtTelefono.setPreferredSize(new Dimension(220, 30));

        GroupLayout panelDomicilioLayout = new GroupLayout(panelDomicilio);
        panelDomicilio.setLayout(panelDomicilioLayout);
        panelDomicilioLayout.setHorizontalGroup(
                panelDomicilioLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelDomicilioLayout.createSequentialGroup().addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING).addGroup(
                                        panelDomicilioLayout.createSequentialGroup().addGap(
                                                6, 6, 6).addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING).addComponent(
                                                        lblContacto,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblDatosPersonales1,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDomicilioLayout.createSequentialGroup().addContainerGap().addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        GroupLayout.Alignment.CENTER).addComponent(
                                                        lblEmail,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblTelefono,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                                LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING).addComponent(
                                                        txtEmail,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        txtTelefono,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        150,
                                                        GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDomicilioLayout.createSequentialGroup().addGap(
                                                6, 6, 6).addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        GroupLayout.Alignment.CENTER).addComponent(
                                                        lblMembresia,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblMembresia1,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblMembresia2,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblMonto,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                                LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        GroupLayout.Alignment.TRAILING).addComponent(
                                                        cbxMembresia,
                                                        GroupLayout.Alignment.LEADING,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addGroup(
                                                        GroupLayout.Alignment.LEADING,
                                                        panelDomicilioLayout.createParallelGroup(
                                                                GroupLayout.Alignment.TRAILING,
                                                                false).addComponent(
                                                                txtMonto,
                                                                GroupLayout.Alignment.LEADING,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                130,
                                                                Short.MAX_VALUE).addComponent(
                                                                calendarioFin,
                                                                GroupLayout.Alignment.LEADING,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                0,
                                                                Short.MAX_VALUE).addComponent(
                                                                calendarioInicio,
                                                                GroupLayout.Alignment.LEADING,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                0,
                                                                Short.MAX_VALUE))))).addContainerGap(
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        panelDomicilioLayout.setVerticalGroup(
                panelDomicilioLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelDomicilioLayout.createSequentialGroup().addGap(12,
                                12, 12).addComponent(lblContacto,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addGap(20, 20,
                                20).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblEmail, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtEmail, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(30,
                                30, 30).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblTelefono, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtTelefono, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(18,
                                18, 18).addComponent(lblDatosPersonales1,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addGap(20, 20,
                                20).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblMembresia,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        cbxMembresia,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(30,
                                30, 30).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblMembresia1,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        calendarioInicio,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(30,
                                30, 30).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblMembresia2,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        calendarioFin,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(30,
                                30, 30).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblMonto, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtMonto, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addContainerGap(
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        btnGuardar.setBackground(new Color(0, 0, 0, 0));
        btnGuardar.setForeground(new Color(255, 255, 255));
        btnGuardar.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                         "Java\\Netbeans\\GYM\\src\\icon" +
                                         "\\guardar.png")); // NOI18N
        btnGuardar.setToolTipText("Guardar datos");
        btnGuardar.setColor(new Color(0, 0, 0, 0));
        btnGuardar.setColorClick(new Color(10, 193, 18));
        btnGuardar.setColorOver(new Color(15, 225, 24));
        btnGuardar.setFont(
                new Font("Roboto", Font.BOLD | Font.ITALIC, 14)); // NOI18N
        btnGuardar.setHorizontalTextPosition(SwingConstants.CENTER);
        btnGuardar.setIconTextGap(10);
        btnGuardar.setMargin(new Insets(2, 14, 2, 14));
        btnGuardar.setMaximumSize(new Dimension(50, 50));
        btnGuardar.setMinimumSize(new Dimension(50, 50));
        btnGuardar.setPreferredSize(new Dimension(50, 50));

        btnLimpiar.setBackground(new Color(0, 0, 0, 0));
        btnLimpiar.setForeground(new Color(255, 255, 255));
        btnLimpiar.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                         "Java\\Netbeans\\GYM\\src\\icon" +
                                         "\\limpiar.png")); // NOI18N
        btnLimpiar.setToolTipText("Limpiar");
        btnLimpiar.setColor(new Color(0, 0, 0, 0));
        btnLimpiar.setColorClick(new Color(177, 34, 219));
        btnLimpiar.setColorOver(new Color(196, 49, 240));
        btnLimpiar.setFont(
                new Font("Roboto", Font.BOLD | Font.ITALIC, 14)); // NOI18N
        btnLimpiar.setHorizontalTextPosition(SwingConstants.CENTER);
        btnLimpiar.setIconTextGap(10);
        btnLimpiar.setMargin(new Insets(2, 14, 2, 14));
        btnLimpiar.setMaximumSize(new Dimension(50, 50));
        btnLimpiar.setMinimumSize(new Dimension(50, 50));
        btnLimpiar.setPreferredSize(new Dimension(50, 50));

        GroupLayout panelEdicionLayout = new GroupLayout(panelEdicion);
        panelEdicion.setLayout(panelEdicionLayout);
        panelEdicionLayout.setHorizontalGroup(
                panelEdicionLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelEdicionLayout.createSequentialGroup().addGap(70,
                                70, 70).addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING,
                                        false).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                btnGuardar,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                30, 30, 30).addComponent(
                                                btnLimpiar,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)).addComponent(
                                        panelDatosPersonales,
                                        GroupLayout.PREFERRED_SIZE, 268,
                                        GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED, 96,
                                Short.MAX_VALUE).addComponent(panelDomicilio,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addGap(70, 70,
                                70)));
        panelEdicionLayout.setVerticalGroup(
                panelEdicionLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelEdicionLayout.createSequentialGroup().addContainerGap().addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                panelDomicilio,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addContainerGap(
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                panelDatosPersonales,
                                                GroupLayout.PREFERRED_SIZE, 376,
                                                GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                                LayoutStyle.ComponentPlacement.RELATED,
                                                38, Short.MAX_VALUE).addGroup(
                                                panelEdicionLayout.createParallelGroup(
                                                        GroupLayout.Alignment.CENTER).addComponent(
                                                        btnGuardar,
                                                        GroupLayout.Alignment.LEADING,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        btnLimpiar,
                                                        GroupLayout.Alignment.LEADING,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)).addGap(
                                                30, 30, 30)))));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelEdicion,
                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelEdicion,
                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                GroupLayout.PREFERRED_SIZE));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private es.xalpha.gym.vista.BotonRedondeado btnGuardar;
    private es.xalpha.gym.vista.BotonRedondeado btnLimpiar;
    private com.toedter.calendar.JDateChooser calendarioFin;
    private com.toedter.calendar.JDateChooser calendarioInicio;
    private com.toedter.calendar.JDateChooser calendarioNac;
    private JComboBox<String> cbxMembresia;
    private JLabel lblApellido;
    private JLabel lblBarrio;
    private JLabel lblCalle;
    private JLabel lblContacto;
    private JLabel lblDatosPersonales;
    private JLabel lblDatosPersonales1;
    private JLabel lblDomicilio;
    private JLabel lblEmail;
    private JLabel lblFecha;
    private JLabel lblMembresia;
    private JLabel lblMembresia1;
    private JLabel lblMembresia2;
    private JLabel lblMonto;
    private JLabel lblNombre;
    private JLabel lblTelefono;
    private JPanel panelDatosPersonales;
    private JPanel panelDomicilio;
    private JPanel panelEdicion;
    private JTextField txtApellido;
    private JTextField txtBarrio;
    private JTextField txtCalle;
    private JTextField txtEmail;
    private JTextField txtMonto;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
