package es.xalpha.gym.vista;

import es.xalpha.gym.contoladora.ControladoraLogica;
import es.xalpha.gym.logica.entidad.Cliente;
import es.xalpha.gym.logica.util.ControladoraLogicaSingleton;
import es.xalpha.gym.logica.util.UtilGUI;
import es.xalpha.gym.persistencia.exceptions.NonexistentEntityException;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.xalpha.gym.logica.util.UtilLogica.*;

public class EditarDatos extends RegistrarCliente {

    private Cliente cliente;

    public EditarDatos() {
        initComponents();
        UtilGUI.setCalendario(calendarioNac);
        UtilGUI.setCalendario(calendarioInicio);
        UtilGUI.setCalendario(calendarioFin);
        btnListener();
        txtListener();
    }

    private void btnListener() {
        btnActualizar.addActionListener(_ -> {
            setComboBoxMembresia(getPrincipal().getListaMembresia());
            actualizarDatos();
        });
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

    private void actualizarDatos() {
        List<JComponent> components = List.of(txtApellido, txtNombre, txtCalle,
                txtBarrio, txtTelefono, calendarioNac, calendarioFin,
                calendarioInicio, txtMonto);

        if (datosValidos(components, txtEmail.getText(),
                txtTelefono.getText())) {
            try {
                editarCliente();
                UtilGUI.mensaje("Los datos se han actualizado correctamente.",
                        "Actualizado", JOptionPane.INFORMATION_MESSAGE);
                getPrincipal().verPanel(getPrincipal().getPanelVerClientes(),
                        getPrincipal().getPaneles());
                getPrincipal().cargarDatosCliente();
            } catch (NonexistentEntityException e) {
                UtilGUI.mensaje("Ocurrió un error al intentar actualizar los " +
                                "datos. Inténtelo nuevamente.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            limpiar();
        } else {
            resaltarComponentesVaciosOIncorrectos(components, txtEmail,
                    txtTelefono);
            UtilGUI.mensaje(
                    "Algunos campos no están completos. Por favor, revise " +
                    "antes de proceder.", "Cuidado",
                    JOptionPane.WARNING_MESSAGE);
            restaurarColorComponentes(components, txtEmail, txtTelefono);
        }
    }

    private void editarCliente() throws NonexistentEntityException {
        ControladoraLogica controller =
                ControladoraLogicaSingleton.INSTANCIA.getController();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String calle = txtCalle.getText();
        String barrio = txtBarrio.getText();
        String email = txtEmail.getText();
        String tel = txtTelefono.getText();
        String tipo = (String) cbxMembresia.getSelectedItem();
        Double monto = Double.parseDouble(txtMonto.getText());
        LocalDate nacimiento = obtenerLocalDate(calendarioNac.getDate());
        LocalDate fechaInicio = obtenerLocalDate(calendarioInicio.getDate());
        LocalDate fechaFin = obtenerLocalDate(calendarioFin.getDate());
        controller.editarCliente(cliente, nombre, apellido, calle, barrio,
                email, tel, tipo, monto, nacimiento, fechaInicio, fechaFin);
    }

    public JPanel getPanelEdicion() {
        return panelEdicion;
    }

    public void setFormulario(Long id) {
        ControladoraLogica controller =
                ControladoraLogicaSingleton.INSTANCIA.getController();
        cliente = controller.getCliente(id);
        txtNombre.setText(cliente.getNombre());
        txtApellido.setText(cliente.getApellido());
        txtCalle.setText(cliente.getDomicilio().getCalle());
        txtBarrio.setText(cliente.getDomicilio().getBarrio());
        txtEmail.setText(cliente.getContacto().getEmail());
        txtTelefono.setText(cliente.getContacto().getTelefono());
        txtMonto.setText(String.valueOf(cliente.getMembresia().getMonto()));
        calendarioNac.setDate(obtenerDate(cliente.getFechaNac()));
        calendarioInicio.setDate(
                obtenerDate(cliente.getMembresia().getFechaInicio()));
        calendarioFin.setDate(
                obtenerDate(cliente.getMembresia().getFechaFin()));
        seleccionarCombo();
    }

    private void seleccionarCombo() {
        List<String> lista = getPrincipal().getListaMembresia();
        String tipo = cliente.getMembresia().getTipo().toLowerCase();
        int index = lista.indexOf(tipo);
        setComboBoxMembresia(lista);
        cbxMembresia.setSelectedIndex(index);
    }

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
        btnActualizar = new es.xalpha.gym.vista.BotonRedondeado();
        btnLimpiar = new es.xalpha.gym.vista.BotonRedondeado();

        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(800, 500));

        panelEdicion.setName("panelEdicion"); // NOI18N
        panelEdicion.setOpaque(false);
        panelEdicion.setPreferredSize(new java.awt.Dimension(800, 500));

        panelDatosPersonales.setName("panelDatosPersonales"); // NOI18N
        panelDatosPersonales.setOpaque(false);
        panelDatosPersonales.setPreferredSize(new java.awt.Dimension(290, 300));

        lblApellido.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblApellido.setForeground(new java.awt.Color(255, 255, 255));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(
                "es/xalpha/gym/vista/Bundle"); // NOI18N
        lblApellido.setText(
                bundle.getString("EditarDatos.lblApellido.text")); // NOI18N
        lblApellido.setName("lblApellido"); // NOI18N
        lblApellido.setPreferredSize(new java.awt.Dimension(54, 20));

        txtApellido.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtApellido.setForeground(new java.awt.Color(0, 0, 0));
        txtApellido.setMinimumSize(new java.awt.Dimension(68, 30));
        txtApellido.setName("txtApellido"); // NOI18N
        txtApellido.setPreferredSize(new java.awt.Dimension(190, 30));

        lblNombre.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(255, 255, 255));
        lblNombre.setText(
                bundle.getString("EditarDatos.lblNombre.text")); // NOI18N
        lblNombre.setMaximumSize(new java.awt.Dimension(52, 20));
        lblNombre.setName("lblNombre"); // NOI18N
        lblNombre.setPreferredSize(new java.awt.Dimension(52, 20));

        txtNombre.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre.setName("txtNombre"); // NOI18N
        txtNombre.setPreferredSize(new java.awt.Dimension(190, 30));

        lblFecha.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblFecha.setForeground(new java.awt.Color(255, 255, 255));
        lblFecha.setText(
                bundle.getString("EditarDatos.lblFecha.text")); // NOI18N
        lblFecha.setName("lblFecha"); // NOI18N
        lblFecha.setPreferredSize(new java.awt.Dimension(60, 20));

        lblDatosPersonales.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDatosPersonales.setForeground(new java.awt.Color(255, 255, 255));
        lblDatosPersonales.setText(bundle.getString(
                "EditarDatos.lblDatosPersonales.text")); // NOI18N
        lblDatosPersonales.setName("lblDatosPersonales"); // NOI18N
        lblDatosPersonales.setPreferredSize(new java.awt.Dimension(190, 30));

        txtBarrio.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtBarrio.setForeground(new java.awt.Color(0, 0, 0));
        txtBarrio.setName("txtBarrio"); // NOI18N
        txtBarrio.setPreferredSize(new java.awt.Dimension(200, 30));

        lblBarrio.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblBarrio.setForeground(new java.awt.Color(255, 255, 255));
        lblBarrio.setText(
                bundle.getString("EditarDatos.lblBarrio.text")); // NOI18N
        lblBarrio.setName("lblBarrio"); // NOI18N
        lblBarrio.setPreferredSize(new java.awt.Dimension(40, 20));

        lblCalle.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblCalle.setForeground(new java.awt.Color(255, 255, 255));
        lblCalle.setText(
                bundle.getString("EditarDatos.lblCalle.text")); // NOI18N
        lblCalle.setName("lblCalle"); // NOI18N
        lblCalle.setPreferredSize(new java.awt.Dimension(34, 20));

        txtCalle.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtCalle.setForeground(new java.awt.Color(0, 0, 0));
        txtCalle.setName("txtCalle"); // NOI18N
        txtCalle.setPreferredSize(new java.awt.Dimension(200, 30));

        lblDomicilio.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDomicilio.setForeground(new java.awt.Color(255, 255, 255));
        lblDomicilio.setText(
                bundle.getString("EditarDatos.lblDomicilio.text")); // NOI18N
        lblDomicilio.setName("lblDomicilio"); // NOI18N
        lblDomicilio.setPreferredSize(new java.awt.Dimension(102, 30));

        calendarioNac.setForeground(new java.awt.Color(0, 0, 0));
        calendarioNac.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        calendarioNac.setName("calendarioNac"); // NOI18N
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
                                                        lblDomicilio,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        116,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblDatosPersonales,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        206,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                                        panelDatosPersonalesLayout.createSequentialGroup().addGap(
                                                                6, 6,
                                                                6).addComponent(
                                                                lblFecha,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addComponent(
                                                                lblNombre,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addComponent(
                                                                lblApellido,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))).addGap(
                                                6, 6, 6).addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                                        panelDatosPersonalesLayout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                false).addComponent(
                                                                txtNombre,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE).addComponent(
                                                                txtApellido,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
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
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))).addContainerGap(
                                8, Short.MAX_VALUE)));
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
                                20, 20, 20).addComponent(lblDomicilio,
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

        panelDomicilio.setName("panelDomicilio"); // NOI18N
        panelDomicilio.setOpaque(false);

        lblDatosPersonales1.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDatosPersonales1.setForeground(new java.awt.Color(255, 255, 255));
        lblDatosPersonales1.setText(bundle.getString(
                "EditarDatos.lblDatosPersonales1.text")); // NOI18N
        lblDatosPersonales1.setName("lblDatosPersonales1"); // NOI18N
        lblDatosPersonales1.setPreferredSize(new java.awt.Dimension(122, 30));

        lblMembresia.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblMembresia.setForeground(new java.awt.Color(255, 255, 255));
        lblMembresia.setText(
                bundle.getString("EditarDatos.lblMembresia.text")); // NOI18N
        lblMembresia.setName("lblMembresia"); // NOI18N
        lblMembresia.setPreferredSize(new java.awt.Dimension(30, 20));

        cbxMembresia.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        cbxMembresia.setForeground(new java.awt.Color(0, 0, 0));
        cbxMembresia.setCursor(
                new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cbxMembresia.setName("cbxMembresia"); // NOI18N
        cbxMembresia.setPreferredSize(new java.awt.Dimension(150, 30));

        lblMonto.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblMonto.setForeground(new java.awt.Color(255, 255, 255));
        lblMonto.setText(
                bundle.getString("EditarDatos.lblMonto.text")); // NOI18N
        lblMonto.setName("lblMonto"); // NOI18N
        lblMonto.setPreferredSize(new java.awt.Dimension(42, 20));

        txtMonto.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtMonto.setForeground(new java.awt.Color(0, 0, 0));
        txtMonto.setName("txtMonto"); // NOI18N
        txtMonto.setPreferredSize(new java.awt.Dimension(170, 30));

        lblMembresia1.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblMembresia1.setForeground(new java.awt.Color(255, 255, 255));
        lblMembresia1.setText(
                bundle.getString("EditarDatos.lblMembresia1.text")); // NOI18N
        lblMembresia1.setName("lblMembresia1"); // NOI18N
        lblMembresia1.setPreferredSize(new java.awt.Dimension(50, 20));

        calendarioInicio.setForeground(new java.awt.Color(0, 0, 0));
        calendarioInicio.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        calendarioInicio.setName("calendarioInicio"); // NOI18N
        calendarioInicio.setOpaque(false);
        calendarioInicio.setPreferredSize(new java.awt.Dimension(170, 30));

        lblMembresia2.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblMembresia2.setForeground(new java.awt.Color(255, 255, 255));
        lblMembresia2.setText(
                bundle.getString("EditarDatos.lblMembresia2.text")); // NOI18N
        lblMembresia2.setName("lblMembresia2"); // NOI18N
        lblMembresia2.setPreferredSize(new java.awt.Dimension(66, 20));

        calendarioFin.setForeground(new java.awt.Color(0, 0, 0));
        calendarioFin.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        calendarioFin.setName("calendarioFin"); // NOI18N
        calendarioFin.setOpaque(false);
        calendarioFin.setPreferredSize(new java.awt.Dimension(150, 30));

        lblContacto.setFont(
                new java.awt.Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblContacto.setForeground(new java.awt.Color(255, 255, 255));
        lblContacto.setText(
                bundle.getString("EditarDatos.lblContacto.text")); // NOI18N
        lblContacto.setName("lblContacto"); // NOI18N
        lblContacto.setPreferredSize(new java.awt.Dimension(100, 30));

        lblEmail.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(255, 255, 255));
        lblEmail.setText(
                bundle.getString("EditarDatos.lblEmail.text")); // NOI18N
        lblEmail.setName("lblEmail"); // NOI18N
        lblEmail.setPreferredSize(new java.awt.Dimension(36, 20));

        txtEmail.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtEmail.setMinimumSize(new java.awt.Dimension(68, 30));
        txtEmail.setName("txtEmail"); // NOI18N
        txtEmail.setPreferredSize(new java.awt.Dimension(220, 30));

        lblTelefono.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(255, 255, 255));
        lblTelefono.setText(
                bundle.getString("EditarDatos.lblTelefono.text")); // NOI18N
        lblTelefono.setName("lblTelefono"); // NOI18N
        lblTelefono.setPreferredSize(new java.awt.Dimension(58, 20));

        txtTelefono.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtTelefono.setForeground(new java.awt.Color(0, 0, 0));
        txtTelefono.setMinimumSize(new java.awt.Dimension(68, 30));
        txtTelefono.setName("txtTelefono"); // NOI18N
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
                                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                                        lblEmail,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblTelefono,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                                6, 6, 6).addGroup(
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
                                        panelDomicilioLayout.createSequentialGroup().addContainerGap().addGroup(
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
                                                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                        cbxMembresia,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                                                        panelDomicilioLayout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                false).addComponent(
                                                                txtMonto,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                0,
                                                                Short.MAX_VALUE).addComponent(
                                                                calendarioFin,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                130,
                                                                Short.MAX_VALUE).addComponent(
                                                                calendarioInicio,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                0,
                                                                Short.MAX_VALUE)))).addGroup(
                                        panelDomicilioLayout.createSequentialGroup().addGap(
                                                6, 6, 6).addGroup(
                                                panelDomicilioLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                        lblContacto,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        114,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblDatosPersonales1,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        136,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))).addContainerGap(
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
                                20, 20, 20).addComponent(lblDatosPersonales1,
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

        btnActualizar.setBackground(new Color(0, 0, 0, 0));
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\actualizar.png")); // NOI18N
        btnActualizar.setText(
                bundle.getString("EditarDatos.btnLimpiar.text")); // NOI18N
        btnActualizar.setToolTipText("Actualizar datos");
        btnActualizar.setColor(new Color(0, 0, 0, 0));
        btnActualizar.setColorClick(new java.awt.Color(68, 75, 217));
        btnActualizar.setColorOver(new java.awt.Color(79, 87, 248));
        btnActualizar.setFont(
                new java.awt.Font("Roboto", Font.ITALIC, 14)); // NOI18N
        btnActualizar.setIconTextGap(0);
        btnActualizar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnActualizar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnActualizar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnActualizar.setName("btnActualizar"); // NOI18N
        btnActualizar.setPreferredSize(new java.awt.Dimension(50, 50));

        btnLimpiar.setBackground(new Color(0, 0, 0, 0));
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\limpiar.png")); // NOI18N
        btnLimpiar.setText(
                bundle.getString("EditarDatos.btnLimpiar.text")); // NOI18N
        btnLimpiar.setToolTipText("Limpiar");
        btnLimpiar.setColor(new Color(0, 0, 0, 0));
        btnLimpiar.setColorClick(new java.awt.Color(177, 34, 219));
        btnLimpiar.setColorOver(new java.awt.Color(196, 49, 240));
        btnLimpiar.setFont(
                new java.awt.Font("Roboto", Font.ITALIC, 14)); // NOI18N
        btnLimpiar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnLimpiar.setIconTextGap(0);
        btnLimpiar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnLimpiar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnLimpiar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnLimpiar.setName("btnLimpiar"); // NOI18N
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
                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                btnActualizar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                30, 30, 30).addComponent(
                                                btnLimpiar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(
                                        panelDatosPersonales,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        270,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                94, Short.MAX_VALUE).addComponent(
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
                                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                        panelDomicilio,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                panelDatosPersonales,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                376,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                38, 38, 38).addGroup(
                                                panelEdicionLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                                        btnActualizar,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        btnLimpiar,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))).addContainerGap(
                                30, Short.MAX_VALUE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 800,
                Short.MAX_VALUE).addGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panelEdicion, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 500,
                Short.MAX_VALUE).addGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panelEdicion, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private es.xalpha.gym.vista.BotonRedondeado btnActualizar;
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
