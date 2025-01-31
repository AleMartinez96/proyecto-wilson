package es.xalpha.gym.vista;

import es.xalpha.gym.contoladora.ControladoraLogica;
import es.xalpha.gym.logica.entidad.Cliente;
import es.xalpha.gym.persistencia.exceptions.NonexistentEntityException;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.xalpha.gym.logica.util.Utils.*;
import static es.xalpha.gym.vista.RegistrarCliente.*;

public class EditarDatos extends JPanel {

    private final ControladoraLogica controller;
    private final Principal frame;
    private Cliente cliente;

    public EditarDatos(ControladoraLogica controller, Principal frame) {
        initComponents();
        this.controller = controller;
        this.frame = frame;
        setCalendario(calendarioNac);
        setCalendario(calendarioInicio);
        setCalendario(calendarioFin);
        btnListener();
        txtListener();
    }

    private void btnListener() {
        btnActualizar.addActionListener(_ -> {
            setComboBox(frame.getListaMembresia());
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

        if (datosValidos(components, txtEmail.getText())) {
            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            String calle = txtCalle.getText();
            String barrio = txtBarrio.getText();
            String email = txtEmail.getText();
            String tel = txtTelefono.getText();
            String tipo = cbxMembresia.getSelectedItem() ==
                          null ? "" : (String) cbxMembresia.getSelectedItem();
            Double monto = Double.parseDouble(txtMonto.getText());
            LocalDate nacimiento = obtenerLocalDate(calendarioNac.getDate());
            LocalDate fechaInicio = obtenerLocalDate(
                    calendarioInicio.getDate());
            LocalDate fechaFin = obtenerLocalDate(calendarioFin.getDate());
            try {
                controller.editarCliente(cliente, nombre, apellido, calle,
                        barrio, email, tel, tipo, monto, nacimiento,
                        fechaInicio, fechaFin);
                mensaje("Los datos se han actualizado correctamente.",
                        "Actualizado", JOptionPane.INFORMATION_MESSAGE);
                frame.verPanel(frame.getPanelVerClientes(), frame.getPaneles());
                frame.cargarDatosCliente();
            } catch (NonexistentEntityException e) {
                mensaje("Ocurrió un error al intentar actualizar los " +
                        "datos. Inténtelo nuevamente.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            limpiar();
        } else {
            cambiarColor(components, txtEmail);
            mensaje("Algunos campos no están completos. Por favor, revise " +
                    "antes de proceder.", "Cuidado",
                    JOptionPane.WARNING_MESSAGE);
        }
        restaurarColor(components, txtEmail);
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

    public JPanel getPanelEdicion() {
        return panelEdicion;
    }

    public void setFormulario(Long id) {
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
        List<String> lista = frame.getListaMembresia();
        String tipo = cliente.getMembresia().getTipo().toLowerCase();
        int index = lista.indexOf(tipo);
        setComboBox(lista);
        cbxMembresia.setSelectedIndex(index);
    }

    public void setComboBox(java.util.List<String> lista) {
        cbxMembresia.removeAllItems();
        lista.forEach(cbxMembresia::addItem);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated
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
        btnActualizar = new es.xalpha.gym.vista.BotonRedondeado();
        btnLimpiar = new es.xalpha.gym.vista.BotonRedondeado();

        setName("Form"); // NOI18N
        setPreferredSize(new Dimension(800, 500));

        panelEdicion.setName("panelEdicion"); // NOI18N
        panelEdicion.setOpaque(false);
        panelEdicion.setPreferredSize(new Dimension(800, 500));

        panelDatosPersonales.setName("panelDatosPersonales"); // NOI18N
        panelDatosPersonales.setOpaque(false);
        panelDatosPersonales.setPreferredSize(new Dimension(290, 300));

        lblApellido.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblApellido.setForeground(new Color(255, 255, 255));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(
                "es/xalpha/gym/vista/Bundle"); // NOI18N
        lblApellido.setText(
                bundle.getString("EditarDatos.lblApellido.text")); // NOI18N
        lblApellido.setName("lblApellido"); // NOI18N
        lblApellido.setPreferredSize(new Dimension(54, 20));

        txtApellido.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtApellido.setForeground(new Color(0, 0, 0));
        txtApellido.setMinimumSize(new Dimension(68, 30));
        txtApellido.setName("txtApellido"); // NOI18N
        txtApellido.setPreferredSize(new Dimension(190, 30));

        lblNombre.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblNombre.setForeground(new Color(255, 255, 255));
        lblNombre.setText(
                bundle.getString("EditarDatos.lblNombre.text")); // NOI18N
        lblNombre.setMaximumSize(new Dimension(52, 20));
        lblNombre.setName("lblNombre"); // NOI18N
        lblNombre.setPreferredSize(new Dimension(52, 20));

        txtNombre.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtNombre.setForeground(new Color(0, 0, 0));
        txtNombre.setName("txtNombre"); // NOI18N
        txtNombre.setPreferredSize(new Dimension(190, 30));

        lblFecha.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblFecha.setForeground(new Color(255, 255, 255));
        lblFecha.setText(
                bundle.getString("EditarDatos.lblFecha.text")); // NOI18N
        lblFecha.setName("lblFecha"); // NOI18N
        lblFecha.setPreferredSize(new Dimension(60, 20));

        lblDatosPersonales.setFont(new Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDatosPersonales.setForeground(new Color(255, 255, 255));
        lblDatosPersonales.setText(bundle.getString(
                "EditarDatos.lblDatosPersonales.text")); // NOI18N
        lblDatosPersonales.setName("lblDatosPersonales"); // NOI18N
        lblDatosPersonales.setPreferredSize(new Dimension(190, 30));

        txtBarrio.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtBarrio.setForeground(new Color(0, 0, 0));
        txtBarrio.setName("txtBarrio"); // NOI18N
        txtBarrio.setPreferredSize(new Dimension(200, 30));

        lblBarrio.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblBarrio.setForeground(new Color(255, 255, 255));
        lblBarrio.setText(
                bundle.getString("EditarDatos.lblBarrio.text")); // NOI18N
        lblBarrio.setName("lblBarrio"); // NOI18N
        lblBarrio.setPreferredSize(new Dimension(40, 20));

        lblCalle.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblCalle.setForeground(new Color(255, 255, 255));
        lblCalle.setText(
                bundle.getString("EditarDatos.lblCalle.text")); // NOI18N
        lblCalle.setName("lblCalle"); // NOI18N
        lblCalle.setPreferredSize(new Dimension(34, 20));

        txtCalle.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtCalle.setForeground(new Color(0, 0, 0));
        txtCalle.setName("txtCalle"); // NOI18N
        txtCalle.setPreferredSize(new Dimension(200, 30));

        lblDomicilio.setFont(new Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDomicilio.setForeground(new Color(255, 255, 255));
        lblDomicilio.setText(
                bundle.getString("EditarDatos.lblDomicilio.text")); // NOI18N
        lblDomicilio.setName("lblDomicilio"); // NOI18N
        lblDomicilio.setPreferredSize(new Dimension(102, 30));

        calendarioNac.setForeground(new Color(0, 0, 0));
        calendarioNac.setFont(new Font("Roboto", Font.BOLD, 12)); // NOI18N
        calendarioNac.setName("calendarioNac"); // NOI18N
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
                                                        lblDomicilio,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        lblDatosPersonales,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE))).addGroup(
                                        panelDatosPersonalesLayout.createSequentialGroup().addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING).addGroup(
                                                        panelDatosPersonalesLayout.createSequentialGroup().addGap(
                                                                6, 6,
                                                                6).addComponent(
                                                                lblFecha,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)).addGroup(
                                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addComponent(
                                                                lblNombre,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)).addGroup(
                                                        panelDatosPersonalesLayout.createSequentialGroup().addContainerGap().addComponent(
                                                                lblApellido,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE))).addGap(
                                                6, 6, 6).addGroup(
                                                panelDatosPersonalesLayout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING).addGroup(
                                                        panelDatosPersonalesLayout.createParallelGroup(
                                                                GroupLayout.Alignment.LEADING,
                                                                false).addComponent(
                                                                txtNombre,
                                                                GroupLayout.Alignment.TRAILING,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE).addComponent(
                                                                txtApellido,
                                                                GroupLayout.Alignment.TRAILING,
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
                                                        GroupLayout.PREFERRED_SIZE)))).addContainerGap(
                                8, Short.MAX_VALUE)));
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

        panelDomicilio.setName("panelDomicilio"); // NOI18N
        panelDomicilio.setOpaque(false);

        lblDatosPersonales1.setFont(
                new Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblDatosPersonales1.setForeground(new Color(255, 255, 255));
        lblDatosPersonales1.setText(bundle.getString(
                "EditarDatos.lblDatosPersonales1.text")); // NOI18N
        lblDatosPersonales1.setName("lblDatosPersonales1"); // NOI18N
        lblDatosPersonales1.setPreferredSize(new Dimension(122, 30));

        lblMembresia.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMembresia.setForeground(new Color(255, 255, 255));
        lblMembresia.setText(
                bundle.getString("EditarDatos.lblMembresia.text")); // NOI18N
        lblMembresia.setName("lblMembresia"); // NOI18N
        lblMembresia.setPreferredSize(new Dimension(30, 20));

        cbxMembresia.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        cbxMembresia.setForeground(new Color(0, 0, 0));
        cbxMembresia.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cbxMembresia.setName("cbxMembresia"); // NOI18N
        cbxMembresia.setPreferredSize(new Dimension(150, 30));

        lblMonto.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMonto.setForeground(new Color(255, 255, 255));
        lblMonto.setText(
                bundle.getString("EditarDatos.lblMonto.text")); // NOI18N
        lblMonto.setName("lblMonto"); // NOI18N
        lblMonto.setPreferredSize(new Dimension(42, 20));

        txtMonto.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtMonto.setForeground(new Color(0, 0, 0));
        txtMonto.setName("txtMonto"); // NOI18N
        txtMonto.setPreferredSize(new Dimension(170, 30));

        lblMembresia1.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMembresia1.setForeground(new Color(255, 255, 255));
        lblMembresia1.setText(
                bundle.getString("EditarDatos.lblMembresia1.text")); // NOI18N
        lblMembresia1.setName("lblMembresia1"); // NOI18N
        lblMembresia1.setPreferredSize(new Dimension(50, 20));

        calendarioInicio.setForeground(new Color(0, 0, 0));
        calendarioInicio.setFont(new Font("Roboto", Font.BOLD, 12)); // NOI18N
        calendarioInicio.setName("calendarioInicio"); // NOI18N
        calendarioInicio.setOpaque(false);
        calendarioInicio.setPreferredSize(new Dimension(170, 30));

        lblMembresia2.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblMembresia2.setForeground(new Color(255, 255, 255));
        lblMembresia2.setText(
                bundle.getString("EditarDatos.lblMembresia2.text")); // NOI18N
        lblMembresia2.setName("lblMembresia2"); // NOI18N
        lblMembresia2.setPreferredSize(new Dimension(66, 20));

        calendarioFin.setForeground(new Color(0, 0, 0));
        calendarioFin.setFont(new Font("Roboto", Font.BOLD, 12)); // NOI18N
        calendarioFin.setName("calendarioFin"); // NOI18N
        calendarioFin.setOpaque(false);
        calendarioFin.setPreferredSize(new Dimension(150, 30));

        lblContacto.setFont(new Font("Roboto", Font.BOLD, 24)); // NOI18N
        lblContacto.setForeground(new Color(255, 255, 255));
        lblContacto.setText(
                bundle.getString("EditarDatos.lblContacto.text")); // NOI18N
        lblContacto.setName("lblContacto"); // NOI18N
        lblContacto.setPreferredSize(new Dimension(100, 30));

        lblEmail.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblEmail.setForeground(new Color(255, 255, 255));
        lblEmail.setText(
                bundle.getString("EditarDatos.lblEmail.text")); // NOI18N
        lblEmail.setName("lblEmail"); // NOI18N
        lblEmail.setPreferredSize(new Dimension(36, 20));

        txtEmail.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtEmail.setForeground(new Color(0, 0, 0));
        txtEmail.setMinimumSize(new Dimension(68, 30));
        txtEmail.setName("txtEmail"); // NOI18N
        txtEmail.setPreferredSize(new Dimension(220, 30));

        lblTelefono.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        lblTelefono.setForeground(new Color(255, 255, 255));
        lblTelefono.setText(
                bundle.getString("EditarDatos.lblTelefono.text")); // NOI18N
        lblTelefono.setName("lblTelefono"); // NOI18N
        lblTelefono.setPreferredSize(new Dimension(58, 20));

        txtTelefono.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtTelefono.setForeground(new Color(0, 0, 0));
        txtTelefono.setMinimumSize(new Dimension(68, 30));
        txtTelefono.setName("txtTelefono"); // NOI18N
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
                                        panelDomicilioLayout.createSequentialGroup().addContainerGap().addGroup(
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
                                                        GroupLayout.Alignment.LEADING).addComponent(
                                                        cbxMembresia,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addGroup(
                                                        panelDomicilioLayout.createParallelGroup(
                                                                GroupLayout.Alignment.TRAILING,
                                                                false).addComponent(
                                                                txtMonto,
                                                                GroupLayout.Alignment.LEADING,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                0,
                                                                Short.MAX_VALUE).addComponent(
                                                                calendarioFin,
                                                                GroupLayout.Alignment.LEADING,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                130,
                                                                Short.MAX_VALUE).addComponent(
                                                                calendarioInicio,
                                                                GroupLayout.Alignment.LEADING,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                0,
                                                                Short.MAX_VALUE)))).addGroup(
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
                                                        GroupLayout.PREFERRED_SIZE)))).addContainerGap(
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

        btnActualizar.setBackground(new Color(0, 0, 0, 0));
        btnActualizar.setForeground(new Color(255, 255, 255));
        btnActualizar.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                            "Java\\Netbeans\\GYM\\src\\icon" +
                                            "\\actualizar.png")); // NOI18N
        btnActualizar.setText(
                bundle.getString("EditarDatos.btnLimpiar.text")); // NOI18N
        btnActualizar.setToolTipText("Actualizar datos");
        btnActualizar.setColor(new Color(0, 0, 0, 0));
        btnActualizar.setColorClick(new Color(68, 75, 217));
        btnActualizar.setColorOver(new Color(79, 87, 248));
        btnActualizar.setFont(
                new Font("Roboto", Font.BOLD | Font.ITALIC, 14)); // NOI18N
        btnActualizar.setIconTextGap(0);
        btnActualizar.setMargin(new Insets(2, 14, 2, 14));
        btnActualizar.setMaximumSize(new Dimension(50, 50));
        btnActualizar.setMinimumSize(new Dimension(50, 50));
        btnActualizar.setName("btnActualizar"); // NOI18N
        btnActualizar.setPreferredSize(new Dimension(50, 50));

        btnLimpiar.setBackground(new Color(0, 0, 0, 0));
        btnLimpiar.setForeground(new Color(255, 255, 255));
        btnLimpiar.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                         "Java\\Netbeans\\GYM\\src\\icon" +
                                         "\\limpiar.png")); // NOI18N
        btnLimpiar.setText(
                bundle.getString("EditarDatos.btnLimpiar.text")); // NOI18N
        btnLimpiar.setToolTipText("Limpiar");
        btnLimpiar.setColor(new Color(0, 0, 0, 0));
        btnLimpiar.setColorClick(new Color(177, 34, 219));
        btnLimpiar.setColorOver(new Color(196, 49, 240));
        btnLimpiar.setFont(
                new Font("Roboto", Font.BOLD | Font.ITALIC, 14)); // NOI18N
        btnLimpiar.setHorizontalTextPosition(SwingConstants.LEFT);
        btnLimpiar.setIconTextGap(0);
        btnLimpiar.setMargin(new Insets(2, 14, 2, 14));
        btnLimpiar.setMaximumSize(new Dimension(50, 50));
        btnLimpiar.setMinimumSize(new Dimension(50, 50));
        btnLimpiar.setName("btnLimpiar"); // NOI18N
        btnLimpiar.setPreferredSize(new Dimension(50, 50));

        GroupLayout panelEdicionLayout = new GroupLayout(panelEdicion);
        panelEdicion.setLayout(panelEdicionLayout);
        panelEdicionLayout.setHorizontalGroup(
                panelEdicionLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelEdicionLayout.createSequentialGroup().addGap(70,
                                70, 70).addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                btnActualizar,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                30, 30, 30).addComponent(
                                                btnLimpiar,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)).addComponent(
                                        panelDatosPersonales,
                                        GroupLayout.PREFERRED_SIZE, 270,
                                        GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED, 94,
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
                                        GroupLayout.Alignment.LEADING).addComponent(
                                        panelDomicilio,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                panelDatosPersonales,
                                                GroupLayout.PREFERRED_SIZE, 376,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                38, 38, 38).addGroup(
                                                panelEdicionLayout.createParallelGroup(
                                                        GroupLayout.Alignment.BASELINE).addComponent(
                                                        btnActualizar,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        btnLimpiar,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)))).addContainerGap(
                                30, Short.MAX_VALUE)));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 800,
                Short.MAX_VALUE).addGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelEdicion,
                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 500,
                Short.MAX_VALUE).addGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelEdicion,
                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private es.xalpha.gym.vista.BotonRedondeado btnActualizar;
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
