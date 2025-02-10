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

    private void limpiar() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtCalle.setText("");
        txtBarrio.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        txtMonto.setText("");
        calendarioNac.setDate(null);
        calendarioInicio.setDate(null);
        calendarioFin.setDate(null);
        System.out.println(cbxMembresia.getSelectedIndex());
        if (cbxMembresia.getSelectedIndex() > 0) {
            cbxMembresia.setSelectedIndex(0);
        }
    }

    public void setComboBox(List<String> lista) {
        cbxMembresia.removeAllItems();
        lista.forEach(cbxMembresia::addItem);
    }

    public JPanel getPanelEdicion() {
        return panelEdicion;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEdicion = new javax.swing.JPanel();
        panelDatosPersonales = new javax.swing.JPanel();
        lblApellido = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblFecha = new javax.swing.JLabel();
        lblDatosPersonales = new javax.swing.JLabel();
        txtBarrio = new javax.swing.JTextField();
        lblBarrio = new javax.swing.JLabel();
        lblCalle = new javax.swing.JLabel();
        txtCalle = new javax.swing.JTextField();
        lblDomicilio = new javax.swing.JLabel();
        calendarioNac = new com.toedter.calendar.JDateChooser();
        panelDomicilio = new javax.swing.JPanel();
        lblDatosPersonales1 = new javax.swing.JLabel();
        lblMembresia = new javax.swing.JLabel();
        cbxMembresia = new javax.swing.JComboBox<>();
        lblMonto = new javax.swing.JLabel();
        txtMonto = new javax.swing.JTextField();
        lblMembresia1 = new javax.swing.JLabel();
        calendarioInicio = new com.toedter.calendar.JDateChooser();
        lblMembresia2 = new javax.swing.JLabel();
        calendarioFin = new com.toedter.calendar.JDateChooser();
        lblContacto = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        btnGuardar = new es.xalpha.gym.vista.BotonRedondeado();
        btnLimpiar = new es.xalpha.gym.vista.BotonRedondeado();

        setPreferredSize(new java.awt.Dimension(800, 500));

        panelEdicion.setOpaque(false);
        panelEdicion.setPreferredSize(new java.awt.Dimension(800, 500));

        panelDatosPersonales.setOpaque(false);
        panelDatosPersonales.setPreferredSize(new java.awt.Dimension(290, 300));

        lblApellido.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblApellido.setForeground(new java.awt.Color(255, 255, 255));
        lblApellido.setText("Apellido");
        lblApellido.setPreferredSize(new java.awt.Dimension(54, 20));

        txtApellido.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtApellido.setForeground(new java.awt.Color(0, 0, 0));
        txtApellido.setMinimumSize(new java.awt.Dimension(68, 30));
        txtApellido.setPreferredSize(new java.awt.Dimension(190, 30));

        lblNombre.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(255, 255, 255));
        lblNombre.setText("Nombre");
        lblNombre.setPreferredSize(new java.awt.Dimension(52, 20));

        txtNombre.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre.setPreferredSize(new java.awt.Dimension(190, 30));

        lblFecha.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblFecha.setForeground(new java.awt.Color(255, 255, 255));
        lblFecha.setText("Fec. Nac.");
        lblFecha.setPreferredSize(new java.awt.Dimension(60, 20));

        lblDatosPersonales.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDatosPersonales.setForeground(new java.awt.Color(255, 255, 255));
        lblDatosPersonales.setText("Datos Personales");
        lblDatosPersonales.setPreferredSize(new java.awt.Dimension(190, 30));

        txtBarrio.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtBarrio.setForeground(new java.awt.Color(0, 0, 0));
        txtBarrio.setPreferredSize(new java.awt.Dimension(200, 30));

        lblBarrio.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblBarrio.setForeground(new java.awt.Color(255, 255, 255));
        lblBarrio.setText("Barrio");
        lblBarrio.setPreferredSize(new java.awt.Dimension(40, 20));

        lblCalle.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblCalle.setForeground(new java.awt.Color(255, 255, 255));
        lblCalle.setText("Calle");
        lblCalle.setPreferredSize(new java.awt.Dimension(34, 20));

        txtCalle.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtCalle.setForeground(new java.awt.Color(0, 0, 0));
        txtCalle.setPreferredSize(new java.awt.Dimension(200, 30));

        lblDomicilio.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDomicilio.setForeground(new java.awt.Color(255, 255, 255));
        lblDomicilio.setText("Domicilio");
        lblDomicilio.setPreferredSize(new java.awt.Dimension(102, 30));

        calendarioNac.setForeground(new java.awt.Color(0, 0, 0));
        calendarioNac.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 12)); // NOI18N
        calendarioNac.setOpaque(false);
        calendarioNac.setPreferredSize(new java.awt.Dimension(190, 30));

        javax.swing.GroupLayout panelDatosPersonalesLayout =
                new javax.swing.GroupLayout(
                panelDatosPersonales);
        panelDatosPersonales.setLayout(panelDatosPersonalesLayout);
        panelDatosPersonalesLayout.setHorizontalGroup(
                panelDatosPersonalesLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelDatosPersonalesLayout.createSequentialGroup().addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                        lblFecha,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblNombre,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblApellido,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                                6, 6, 6).addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                                        panelDatosPersonalesLayout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                false).addComponent(
                                                                txtApellido,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE).addComponent(
                                                                txtNombre,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)).addComponent(
                                                        calendarioNac,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        130,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addGap(
                                                6, 6, 6).addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                                        lblCalle,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblBarrio,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                                        txtCalle,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        txtBarrio,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addComponent(
                                                lblDomicilio,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addComponent(
                                                lblDatosPersonales,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap(
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)));
        panelDatosPersonalesLayout.setVerticalGroup(
                panelDatosPersonalesLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelDatosPersonalesLayout.createSequentialGroup().addGap(
                                12, 12, 12).addComponent(lblDatosPersonales,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                20, 20, 20).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblApellido,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtApellido,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                30, 30, 30).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblNombre,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtNombre,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                30, 30, 30).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblFecha,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        calendarioNac,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                18, 18, 18).addComponent(lblDomicilio,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                20, 20, 20).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblCalle,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtCalle,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                30, 30, 30).addGroup(
                                panelDatosPersonalesLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblBarrio,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtBarrio,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)));

        panelDomicilio.setOpaque(false);

        lblDatosPersonales1.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDatosPersonales1.setForeground(new java.awt.Color(255, 255, 255));
        lblDatosPersonales1.setText("Membresia");
        lblDatosPersonales1.setPreferredSize(new java.awt.Dimension(122, 30));

        lblMembresia.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMembresia.setForeground(new java.awt.Color(255, 255, 255));
        lblMembresia.setText("Tipo");
        lblMembresia.setPreferredSize(new java.awt.Dimension(30, 20));

        cbxMembresia.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        cbxMembresia.setForeground(new java.awt.Color(0, 0, 0));
        cbxMembresia.setCursor(
                new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cbxMembresia.setPreferredSize(new java.awt.Dimension(150, 30));

        lblMonto.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMonto.setForeground(new java.awt.Color(255, 255, 255));
        lblMonto.setText("Monto");
        lblMonto.setPreferredSize(new java.awt.Dimension(42, 20));

        txtMonto.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtMonto.setForeground(new java.awt.Color(0, 0, 0));
        txtMonto.setPreferredSize(new java.awt.Dimension(170, 30));

        lblMembresia1.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMembresia1.setForeground(new java.awt.Color(255, 255, 255));
        lblMembresia1.setText("Inicia el");
        lblMembresia1.setPreferredSize(new java.awt.Dimension(50, 20));

        calendarioInicio.setForeground(new java.awt.Color(0, 0, 0));
        calendarioInicio.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 12)); // NOI18N
        calendarioInicio.setOpaque(false);
        calendarioInicio.setPreferredSize(new java.awt.Dimension(170, 30));

        lblMembresia2.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMembresia2.setForeground(new java.awt.Color(255, 255, 255));
        lblMembresia2.setText("Finaliza el");
        lblMembresia2.setPreferredSize(new java.awt.Dimension(66, 20));

        calendarioFin.setForeground(new java.awt.Color(0, 0, 0));
        calendarioFin.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 12)); // NOI18N
        calendarioFin.setOpaque(false);
        calendarioFin.setPreferredSize(new java.awt.Dimension(170, 30));

        lblContacto.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblContacto.setForeground(new java.awt.Color(255, 255, 255));
        lblContacto.setText("Contacto");
        lblContacto.setPreferredSize(new java.awt.Dimension(100, 30));

        lblEmail.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(255, 255, 255));
        lblEmail.setText("Email");
        lblEmail.setPreferredSize(new java.awt.Dimension(36, 20));

        txtEmail.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtEmail.setPreferredSize(new java.awt.Dimension(220, 30));

        lblTelefono.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(255, 255, 255));
        lblTelefono.setText("Telefono");
        lblTelefono.setPreferredSize(new java.awt.Dimension(58, 20));

        txtTelefono.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtTelefono.setForeground(new java.awt.Color(0, 0, 0));
        txtTelefono.setPreferredSize(new java.awt.Dimension(220, 30));

        javax.swing.GroupLayout panelDomicilioLayout =
                new javax.swing.GroupLayout(
                panelDomicilio);
        panelDomicilio.setLayout(panelDomicilioLayout);
        panelDomicilioLayout.setHorizontalGroup(
                panelDomicilioLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelDomicilioLayout.createSequentialGroup().addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                        panelDomicilioLayout.createSequentialGroup().addGap(
                                                6, 6, 6).addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                        lblContacto,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblDatosPersonales1,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDomicilioLayout.createSequentialGroup().addContainerGap().addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                                        lblEmail,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblTelefono,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                        txtEmail,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        txtTelefono,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        150,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDomicilioLayout.createSequentialGroup().addGap(
                                                6, 6, 6).addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                                        lblMembresia,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblMembresia1,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblMembresia2,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblMonto,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.TRAILING).addComponent(
                                                        cbxMembresia,
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        panelDomicilioLayout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                false).addComponent(
                                                                txtMonto,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                130,
                                                                Short.MAX_VALUE).addComponent(
                                                                calendarioFin,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                0,
                                                                Short.MAX_VALUE).addComponent(
                                                                calendarioInicio,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                0,
                                                                Short.MAX_VALUE))))).addContainerGap(
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)));
        panelDomicilioLayout.setVerticalGroup(
                panelDomicilioLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelDomicilioLayout.createSequentialGroup().addGap(12,
                                12, 12).addComponent(lblContacto,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                20, 20, 20).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblEmail,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtEmail,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                30, 30, 30).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblTelefono,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtTelefono,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                18, 18, 18).addComponent(lblDatosPersonales1,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                20, 20, 20).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblMembresia,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        cbxMembresia,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                30, 30, 30).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblMembresia1,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        calendarioInicio,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                30, 30, 30).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblMembresia2,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        calendarioFin,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                30, 30, 30).addGroup(
                                panelDomicilioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblMonto,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtMonto,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)));

        btnGuardar.setBackground(new Color(0, 0, 0, 0));
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\guardar.png")); // NOI18N
        btnGuardar.setToolTipText("Guardar datos");
        btnGuardar.setColor(new Color(0, 0, 0, 0));
        btnGuardar.setColorClick(new java.awt.Color(10, 193, 18));
        btnGuardar.setColorOver(new java.awt.Color(15, 225, 24));
        btnGuardar.setFont(new java.awt.Font("Roboto", Font.BOLD | Font.ITALIC,
                14)); // NOI18N
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.setIconTextGap(10);
        btnGuardar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnGuardar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnGuardar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnGuardar.setPreferredSize(new java.awt.Dimension(50, 50));

        btnLimpiar.setBackground(new Color(0, 0, 0, 0));
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\limpiar.png")); // NOI18N
        btnLimpiar.setToolTipText("Limpiar");
        btnLimpiar.setColor(new Color(0, 0, 0, 0));
        btnLimpiar.setColorClick(new java.awt.Color(177, 34, 219));
        btnLimpiar.setColorOver(new java.awt.Color(196, 49, 240));
        btnLimpiar.setFont(new java.awt.Font("Roboto", Font.BOLD | Font.ITALIC,
                14)); // NOI18N
        btnLimpiar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLimpiar.setIconTextGap(10);
        btnLimpiar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnLimpiar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnLimpiar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnLimpiar.setPreferredSize(new java.awt.Dimension(50, 50));

        javax.swing.GroupLayout panelEdicionLayout =
                new javax.swing.GroupLayout(
                panelEdicion);
        panelEdicion.setLayout(panelEdicionLayout);
        panelEdicionLayout.setHorizontalGroup(
                panelEdicionLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelEdicionLayout.createSequentialGroup().addGap(70,
                                70, 70).addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING,
                                        false).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                btnGuardar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                30, 30, 30).addComponent(
                                                btnLimpiar,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(
                                        panelDatosPersonales,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        268,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                96, Short.MAX_VALUE).addComponent(
                                panelDomicilio,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                70, 70, 70)));
        panelEdicionLayout.setVerticalGroup(
                panelEdicionLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelEdicionLayout.createSequentialGroup().addContainerGap().addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                panelDomicilio,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                panelDatosPersonales,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                376,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                38, Short.MAX_VALUE).addGroup(
                                                panelEdicionLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                                        btnGuardar,
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        btnLimpiar,
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                                30, 30, 30)))));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panelEdicion, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panelEdicion, javax.swing.GroupLayout.PREFERRED_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.PREFERRED_SIZE));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private es.xalpha.gym.vista.BotonRedondeado btnGuardar;
    private es.xalpha.gym.vista.BotonRedondeado btnLimpiar;
    private com.toedter.calendar.JDateChooser calendarioFin;
    private com.toedter.calendar.JDateChooser calendarioInicio;
    private com.toedter.calendar.JDateChooser calendarioNac;
    private javax.swing.JComboBox<String> cbxMembresia;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblBarrio;
    private javax.swing.JLabel lblCalle;
    private javax.swing.JLabel lblContacto;
    private javax.swing.JLabel lblDatosPersonales;
    private javax.swing.JLabel lblDatosPersonales1;
    private javax.swing.JLabel lblDomicilio;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblMembresia;
    private javax.swing.JLabel lblMembresia1;
    private javax.swing.JLabel lblMembresia2;
    private javax.swing.JLabel lblMonto;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JPanel panelDatosPersonales;
    private javax.swing.JPanel panelDomicilio;
    private javax.swing.JPanel panelEdicion;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtBarrio;
    private javax.swing.JTextField txtCalle;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtMonto;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
