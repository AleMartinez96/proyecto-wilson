package es.xalpha.gym.vista.gestion.gym;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.xalpha.gym.logica.entidad.Contacto;
import es.xalpha.gym.logica.entidad.Domicilio;
import es.xalpha.gym.logica.util.*;
import es.xalpha.gym.logica.util.gui.BotonRedondeado;
import es.xalpha.gym.logica.util.gui.UtilGUI;
import es.xalpha.gym.logica.util.gui.PanelBase;
import es.xalpha.gym.logica.util.interfaces.ControladorDeValidacionYComponentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestorDeConfiguracionDeGym extends PanelBase implements ControladorDeValidacionYComponentes {

    private static Configuracion configuracion = new Configuracion();
    private final List<String> listaMembresia = new ArrayList<>(
            List.of("Sin membresia"));

    public GestorDeConfiguracionDeGym() {
        initComponents();
        setComboBoxMembresia();
        lblImagen.setSize(250, 250);
        setLabel(lblImagen, "src/image/wilson.png");
        btnListener();
        txtListener();
    }

    private void setLabel(JLabel label, String ruta) {
        ImageIcon image = new ImageIcon(ruta);
        Icon icon = new ImageIcon(
                image.getImage().getScaledInstance(label.getWidth(),
                        label.getHeight(), Image.SCALE_DEFAULT));
        label.setIcon(icon);
        repaint();
    }

    private void btnListener() {
        btnMas.addActionListener(_ -> agregarMembresia());
        btnGuardar.addActionListener(_ -> guardarConfiguracionComoJSON());
        btnMenos.addActionListener(_ -> eliminarMembresia());
        btnReubicar.addActionListener(_ -> cambiarUbicacionDelArchivo());
    }

    private void guardarConfiguracionComoJSON() {
        List<JComponent> components = List.of(txtNombre, txtCalle);
        if (datosValidos(components, txtEmail.getText(),
                txtTelefono.getText())) {
            guardarConfiguracionEnArchivo();
        } else {
            UtilGUI.mensaje(
                    "Algunos campos no están completos. Por favor, revise " +
                    "antes de proceder.", "Cuidado",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public boolean datosValidos(List<JComponent> components, String email,
                                String numTelefono) {
        List<JTextField> lista = components.stream().filter(
                component -> component instanceof JTextField).map(
                component -> (JTextField) component).toList();
        return UtilGUI.sonTextFieldsVacios(lista) &&
               UtilLogica.esEmailValido(email) &&
               UtilLogica.esNumeroDeTelValido(numTelefono);
    }

    @Override
    public void resaltarComponentesVaciosOIncorrectos(List<JComponent> components, JTextField email, JTextField telefono) {
        SwingUtilities.invokeLater(() -> {
            components.forEach(component -> component.setBackground(Color.red));
            resaltarSiEsInvalido(email, UtilLogica::esEmailValido, Color.red);
            resaltarSiEsInvalido(telefono, UtilLogica::esNumeroDeTelValido,
                    Color.red);
        });
    }

    @Override
    public void restaurarColorComponentes(List<JComponent> components,
                                          JTextField email,
                                          JTextField telefono) {
        SwingUtilities.invokeLater(() -> {
            components.forEach(
                    component -> component.setBackground(Color.white));
            email.setBackground(Color.white);
            telefono.setBackground(Color.white);
        });
    }

    private void guardarConfiguracionEnArchivo() {
        String email = txtEmail.getText();
        String telefono = txtTelefono.getText();
        String nombre = txtNombre.getText();
        String direccion = txtCalle.getText();

        Contacto contacto = configuracion.getContacto();
        Domicilio domicilio = configuracion.getDomicilio();

        domicilio.setCalle(direccion);
        contacto.setEmail(email);
        contacto.setTelefono(telefono);
        configuracion.setNombre(nombre);
        configuracion.setContacto(contacto);
        configuracion.setDomicilio(domicilio);
        configuracion.setMembresias(listaMembresia);

        ManipularArchivo.guardarArchivo(configuracion);
        cargarArchivoJSON(ManipularArchivo.getFile());
    }

    public void cargarArchivoJSON(File file) {
        try {
            cargarConfiguracionDesdeArchivo(file);
        } catch (IOException e) {
            UtilGUI.mensaje("No se encontró el archivo de configuración: " +
                            e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarConfiguracionDesdeArchivo(File file) throws IOException {
        if (file != null && file.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            configuracion = mapper.readValue(file, Configuracion.class);
            txtNombre.setText(configuracion.getNombre());
            txtCalle.setText(configuracion.getDomicilio().getCalle());
            txtEmail.setText(configuracion.getContacto().getEmail());
            txtTelefono.setText(configuracion.getContacto().getTelefono());
            listaMembresia.clear();
            listaMembresia.addAll(configuracion.getMembresias());
            setComboBoxMembresia();
        }
    }

    private void cambiarUbicacionDelArchivo() {
        try {
            ManipularArchivo.verificarUbicacionDelArchivo();
            cargarArchivoJSON(ManipularArchivo.getFile());
        } catch (IOException e) {
            UtilGUI.mensaje("No se pudo cambiar la ubicación del archivo.",
                    "Error al actualizar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void txtListener() {
        txtTelefono.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) ||
                    txtTelefono.getText().length() > 15) {
                    e.consume();
                }
            }
        });
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    private void agregarMembresia() {
        String membresia = JOptionPane.showInputDialog(
                "Ingrese una nueva membresia");
        if (membresia != null && !membresia.isEmpty()) {
            agregarMembresia(membresia);
        } else {
            UtilGUI.mensaje("No se ha proporcionado una membresía válida.",
                    "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void agregarMembresia(String membresia) {
        if (membresia != null && !listaMembresia.contains(membresia)) {
            listaMembresia.add(membresia.toLowerCase());
            cbxMembresia.addItem(membresia.toLowerCase());
            UtilGUI.mensaje(
                    "La nueva membresía ha sido agregada correctamente.",
                    "Membresía agregada", JOptionPane.PLAIN_MESSAGE);
        } else {
            UtilGUI.mensaje("La membresía ingresada ya existe en la lista.",
                    "Membresía existente", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarMembresia() {
        String membresia = (String) cbxMembresia.getSelectedItem();
        if (listaMembresia.getFirst().equalsIgnoreCase(membresia)) {
            UtilGUI.mensaje(
                    "No es posible eliminar la membresia predeterminada.",
                    "Error", JOptionPane.WARNING_MESSAGE);
        } else if (listaMembresia.remove(membresia)) {
            cbxMembresia.removeItem(membresia);
            UtilGUI.mensaje("La membresía se ha eliminado correctamente.",
                    "Membresía eliminada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            UtilGUI.mensaje("La membresía seleccionada no existe en la lista.",
                    "Membresía no encontrada", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setComboBoxMembresia() {
        cbxMembresia.removeAllItems();
        listaMembresia.stream().map(String::toLowerCase).forEach(
                cbxMembresia::addItem);
    }

    public JPanel getPanelEdicion() {
        return panelEditable;
    }

    public String getNombreLocal() {
        return txtNombre.getText();
    }

    public List<String> getListaMembresia() {
        return listaMembresia;
    }

    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEditable = new javax.swing.JPanel();
        btnGuardar = new BotonRedondeado();
        btnReubicar = new BotonRedondeado();
        panelFormulario = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblCalle = new javax.swing.JLabel();
        txtCalle = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        cbxMembresia = new javax.swing.JComboBox<>();
        btnMas = new BotonRedondeado();
        btnMenos = new BotonRedondeado();
        lblMembresia = new javax.swing.JLabel();
        lblImagen = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(800, 500));
        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(800, 500));

        panelEditable.setMaximumSize(new java.awt.Dimension(800, 500));
        panelEditable.setMinimumSize(new java.awt.Dimension(800, 500));
        panelEditable.setOpaque(false);
        panelEditable.setPreferredSize(new java.awt.Dimension(800, 500));

        btnGuardar.setBackground(new Color(0, 0, 0, 0));
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\guardar.png")); // NOI18N
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.setColor(new Color(0, 0, 0, 0));
        btnGuardar.setColorClick(new java.awt.Color(10, 193, 18));
        btnGuardar.setColorOver(new java.awt.Color(15, 225, 24));
        btnGuardar.setFont(
                new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.setIconTextGap(2);
        btnGuardar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnGuardar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnGuardar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnGuardar.setPreferredSize(new java.awt.Dimension(50, 50));

        btnReubicar.setBackground(new Color(0, 0, 0, 0));
        btnReubicar.setForeground(new java.awt.Color(255, 255, 255));
        btnReubicar.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\transferir.png")); // NOI18N
        btnReubicar.setToolTipText("Mover Archivo");
        btnReubicar.setColor(new Color(0, 0, 0, 0));
        btnReubicar.setColorClick(new java.awt.Color(139, 49, 210));
        btnReubicar.setColorOver(new java.awt.Color(160, 58, 240));
        btnReubicar.setFont(
                new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnReubicar.setHorizontalTextPosition(
                javax.swing.SwingConstants.CENTER);
        btnReubicar.setIconTextGap(2);
        btnReubicar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnReubicar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnReubicar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnReubicar.setPreferredSize(new java.awt.Dimension(50, 50));

        panelFormulario.setOpaque(false);

        lblNombre.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 18)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(255, 255, 255));
        lblNombre.setText("Nombre del local");

        txtNombre.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre.setMinimumSize(new java.awt.Dimension(70, 30));
        txtNombre.setPreferredSize(new java.awt.Dimension(70, 30));

        lblCalle.setFont(new java.awt.Font("Roboto", Font.PLAIN, 18)); // NOI18N
        lblCalle.setForeground(new java.awt.Color(255, 255, 255));
        lblCalle.setText("Direccion");

        txtCalle.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtCalle.setForeground(new java.awt.Color(0, 0, 0));
        txtCalle.setMinimumSize(new java.awt.Dimension(70, 30));
        txtCalle.setPreferredSize(new java.awt.Dimension(70, 30));

        lblEmail.setFont(new java.awt.Font("Roboto", Font.PLAIN, 18)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(255, 255, 255));
        lblEmail.setText("Email");

        txtEmail.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtEmail.setMinimumSize(new java.awt.Dimension(70, 30));
        txtEmail.setPreferredSize(new java.awt.Dimension(70, 30));

        lblTelefono.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 18)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(255, 255, 255));
        lblTelefono.setText("Telefono");

        txtTelefono.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        txtTelefono.setForeground(new java.awt.Color(0, 0, 0));
        txtTelefono.setMinimumSize(new java.awt.Dimension(70, 30));
        txtTelefono.setPreferredSize(new java.awt.Dimension(70, 30));

        cbxMembresia.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        cbxMembresia.setMinimumSize(new java.awt.Dimension(80, 30));
        cbxMembresia.setPreferredSize(new java.awt.Dimension(80, 30));

        btnMas.setBackground(new Color(0, 0, 0, 0));
        btnMas.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\mas.png")); // NOI18N
        btnMas.setToolTipText("Agregar");
        btnMas.setColor(new Color(0, 0, 0, 0));
        btnMas.setColorClick(new java.awt.Color(10, 193, 18));
        btnMas.setColorOver(new java.awt.Color(15, 225, 24));
        btnMas.setFont(new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnMas.setPreferredSize(new java.awt.Dimension(40, 40));

        btnMenos.setBackground(new Color(0, 0, 0, 0));
        btnMenos.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\menos.png")); // NOI18N
        btnMenos.setToolTipText("Quitar");
        btnMenos.setColor(new Color(0, 0, 0, 0));
        btnMenos.setColorClick(new java.awt.Color(220, 51, 51));
        btnMenos.setColorOver(new java.awt.Color(240, 56, 56));
        btnMenos.setFont(
                new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnMenos.setPreferredSize(new java.awt.Dimension(40, 40));

        lblMembresia.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 18)); // NOI18N
        lblMembresia.setForeground(new java.awt.Color(255, 255, 255));
        lblMembresia.setText("Membresia");

        javax.swing.GroupLayout panelFormularioLayout =
                new javax.swing.GroupLayout(
                panelFormulario);
        panelFormulario.setLayout(panelFormularioLayout);
        panelFormularioLayout.setHorizontalGroup(
                panelFormularioLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelFormularioLayout.createSequentialGroup().addContainerGap().addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblCalle).addComponent(
                                        lblEmail).addComponent(
                                        lblTelefono).addComponent(
                                        lblMembresia).addComponent(
                                        lblNombre)).addGap(6, 6, 6).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING,
                                        false).addGroup(
                                        panelFormularioLayout.createSequentialGroup().addComponent(
                                                cbxMembresia,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                140,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(btnMas,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(btnMenos,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(
                                        txtTelefono,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        150,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtEmail,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE).addComponent(txtCalle,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE).addComponent(txtNombre,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)).addContainerGap(
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)));
        panelFormularioLayout.setVerticalGroup(
                panelFormularioLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelFormularioLayout.createSequentialGroup().addGap(18,
                                18, 18).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                        txtNombre,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        lblNombre)).addGap(18, 18, 18).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblCalle).addComponent(txtCalle,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                18, 18, 18).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                        lblEmail).addComponent(txtEmail,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                18, 18, 18).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblTelefono).addComponent(txtTelefono,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                18, 18, 18).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        cbxMembresia,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        btnMas,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        btnMenos,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        lblMembresia)).addContainerGap(
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)));

        lblImagen.setPreferredSize(new java.awt.Dimension(250, 250));

        javax.swing.GroupLayout panelEditableLayout =
                new javax.swing.GroupLayout(
                panelEditable);
        panelEditable.setLayout(panelEditableLayout);
        panelEditableLayout.setHorizontalGroup(
                panelEditableLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelEditableLayout.createSequentialGroup().addGap(50,
                                50, 50).addGroup(
                                panelEditableLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                        panelEditableLayout.createSequentialGroup().addComponent(
                                                panelFormulario,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                76,
                                                Short.MAX_VALUE).addComponent(
                                                lblImagen,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                250,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                50, 50, 50)).addGroup(
                                        panelEditableLayout.createSequentialGroup().addComponent(
                                                btnGuardar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                30, 30, 30).addComponent(
                                                btnReubicar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)))));
        panelEditableLayout.setVerticalGroup(
                panelEditableLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelEditableLayout.createSequentialGroup().addGap(40,
                                40, 40).addGroup(
                                panelEditableLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblImagen,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        250,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        panelFormulario,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                80, 80, 80).addGroup(
                                panelEditableLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.TRAILING).addComponent(
                                        btnGuardar,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        btnReubicar,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(
                                74, Short.MAX_VALUE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panelEditable, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panelEditable, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BotonRedondeado btnGuardar;
    private BotonRedondeado btnMas;
    private BotonRedondeado btnMenos;
    private BotonRedondeado btnReubicar;
    private javax.swing.JComboBox<String> cbxMembresia;
    private javax.swing.JLabel lblCalle;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JLabel lblMembresia;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JPanel panelEditable;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JTextField txtCalle;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
