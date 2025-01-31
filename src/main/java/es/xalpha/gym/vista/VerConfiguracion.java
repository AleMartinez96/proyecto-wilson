package es.xalpha.gym.vista;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.xalpha.gym.logica.entidad.Contacto;
import es.xalpha.gym.logica.entidad.Domicilio;
import es.xalpha.gym.logica.util.Configuracion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static es.xalpha.gym.logica.util.Utils.esEmailValido;
import static es.xalpha.gym.logica.util.Utils.mensaje;
import static es.xalpha.gym.vista.RegistrarCliente.cambiarColor;
import static es.xalpha.gym.vista.RegistrarCliente.restaurarColor;

public class VerConfiguracion extends JPanel {

    private static Configuracion configuracion = new Configuracion();
    private final Principal frame;
    private final List<String> listaMembresia = new ArrayList<>();

    public VerConfiguracion(Principal frame) {
        initComponents();
        this.frame = frame;
        lblImagen.setSize(250, 250);
        setLabel(lblImagen, "src/image/wilson.png");
        configurarBotones();
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

    private void configurarBotones() {
        List<JButton> buttons = List.of(btnActualizar, btnReubicar, btnMas,
                btnMenos);
        List<String> tips = List.of("Guardar cambios", "Cambiar ubicación",
                "Añadir", "Quitar");
        colocarToolTip(buttons, tips);
        btnListener();
    }

    public static void colocarToolTip(List<JButton> buttons,
                                      List<String> tips) {
        IntStream.range(0, buttons.size()).forEach(
                i -> buttons.get(i).setToolTipText(tips.get(i)));
    }

    private void btnListener() {
        btnMas.addActionListener(_ -> agregarMembresia());

        btnActualizar.addActionListener(_ -> actualizarJSON());

        btnMenos.addActionListener(_ -> eliminarMembresia());

        btnReubicar.addActionListener(_ -> cambiarUbicacionJSON());
    }

    private void actualizarJSON() {
        List<JComponent> components = List.of(txtCalle, txtNombre, txtTelefono,
                cbxMembresia);
        String email = txtEmail.getText();
        if (esEmailValido(email)) {
            String nombre = txtNombre.getText();
            String direccion = txtCalle.getText();
            String telefono = txtTelefono.getText();

            Contacto contacto = configuracion.getContacto();
            Domicilio domicilio = configuracion.getDomicilio();

            domicilio.setCalle(direccion);
            contacto.setEmail(email);
            contacto.setTelefono(telefono);
            configuracion.setNombre(nombre);
            configuracion.setContacto(contacto);
            configuracion.setDomicilio(domicilio);
            configuracion.setMembresias(listaMembresia);

            guardarJSON(configuracion);
            cargarJSON(frame.getArchivo().getFile());
        } else {
            cambiarColor(components, txtEmail);
            mensaje("Algunos campos no están completos. Por favor, revise " +
                    "antes de proceder.", "Cuidado",
                    JOptionPane.WARNING_MESSAGE);
        }
        restaurarColor(components, txtEmail);
    }

    private void guardarJSON(Configuracion configuracion) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(frame.getArchivo().getFile(), configuracion);
            mensaje("Los datos se han guardado correctamente.",
                    "Actualizacion exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            mensaje("Error al guardar la configuración: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarJSON(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (file != null && file.length() > 0) {
                configuracion = mapper.readValue(file, Configuracion.class);
                txtNombre.setText(configuracion.getNombre());
                txtCalle.setText(configuracion.getDomicilio().getCalle());
                txtEmail.setText(configuracion.getContacto().getEmail());
                txtTelefono.setText(configuracion.getContacto().getTelefono());
                listaMembresia.clear();
                listaMembresia.addAll(configuracion.getMembresias());
                setComboBox();
            }
        } catch (IOException e) {
            mensaje("No se encontró el archivo de configuración: " +
                    e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarUbicacionJSON() {
        try {
            verificarUbicacion();
            cargarJSON(frame.getArchivo().getFile());
        } catch (IOException e) {
            mensaje("No se pudo cambiar la ubicación del archivo.",
                    "Error al actualizar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verificarUbicacion() throws IOException {
        String anterior = frame.getArchivo().getPath();
        frame.getArchivo().actualizarArchivo(anterior, "Json (*.json)",
                ".json");
        String nuevo = frame.getArchivo().getPath();
        String[] pathAnterior = anterior.split("\\\\");
        String[] pathNuevo = nuevo.split("\\\\");
        if (pathAnterior.length != pathNuevo.length) {
            mensaje("El archivo ha sido movido con éxito.",
                    "Ubicación actualizada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mensaje("La ubicación del archivo no ha cambiado.", "Sin cambios",
                    JOptionPane.INFORMATION_MESSAGE);
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
            mensaje("No se ha proporcionado una membresía válida.", "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void agregarMembresia(String membresia) {
        if (!listaMembresia.contains(membresia)) {
            listaMembresia.add(membresia);
            cbxMembresia.addItem(membresia);
            mensaje("La nueva membresía ha sido agregada correctamente.",
                    "Membresía agregada", JOptionPane.PLAIN_MESSAGE);
        } else {
            mensaje("La membresía ingresada ya existe en la lista.",
                    "Membresía existente", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarMembresia() {
        String membresia = (String) cbxMembresia.getSelectedItem();
        if (listaMembresia.isEmpty()) {
            mensaje("No hay membresías para eliminar.", "Lista vacia",
                    JOptionPane.WARNING_MESSAGE);
        } else if (listaMembresia.remove(membresia)) {
            cbxMembresia.removeItem(membresia);
            mensaje("La membresía se ha eliminado correctamente.",
                    "Membresía eliminada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mensaje("La membresía seleccionada no existe en la lista.",
                    "Membresía no encontrada", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setComboBox() {
        cbxMembresia.removeAllItems();
        listaMembresia.forEach(cbxMembresia::addItem);
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

    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEditable = new javax.swing.JPanel();
        btnActualizar = new es.xalpha.gym.vista.BotonRedondeado();
        btnReubicar = new es.xalpha.gym.vista.BotonRedondeado();
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
        btnMas = new es.xalpha.gym.vista.BotonRedondeado();
        btnMenos = new es.xalpha.gym.vista.BotonRedondeado();
        lblMembresia = new javax.swing.JLabel();
        lblImagen = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(800, 500));
        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(800, 500));

        panelEditable.setMaximumSize(new java.awt.Dimension(800, 500));
        panelEditable.setMinimumSize(new java.awt.Dimension(800, 500));
        panelEditable.setOpaque(false);
        panelEditable.setPreferredSize(new java.awt.Dimension(800, 500));

        btnActualizar.setBackground(new Color(0, 0, 0, 0));
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\guardar.png")); // NOI18N
        btnActualizar.setToolTipText("");
        btnActualizar.setColor(new Color(0, 0, 0, 0));
        btnActualizar.setColorClick(new java.awt.Color(10, 193, 18));
        btnActualizar.setColorOver(new java.awt.Color(15, 225, 24));
        btnActualizar.setFont(new java.awt.Font("Roboto",
                Font.BOLD | Font.ITALIC, 12)); // NOI18N
        btnActualizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnActualizar.setIconTextGap(2);
        btnActualizar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnActualizar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnActualizar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnActualizar.setPreferredSize(new java.awt.Dimension(50, 50));

        btnReubicar.setBackground(new Color(0, 0, 0, 0));
        btnReubicar.setForeground(new java.awt.Color(255, 255, 255));
        btnReubicar.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\transferir.png")); // NOI18N
        btnReubicar.setToolTipText("");
        btnReubicar.setColor(new Color(0, 0, 0, 0));
        btnReubicar.setColorClick(new java.awt.Color(139, 49, 210));
        btnReubicar.setColorOver(new java.awt.Color(160, 58, 240));
        btnReubicar.setFont(new java.awt.Font("Roboto", Font.BOLD | Font.ITALIC, 12)); // NOI18N
        btnReubicar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReubicar.setIconTextGap(2);
        btnReubicar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnReubicar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnReubicar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnReubicar.setPreferredSize(new java.awt.Dimension(50, 50));

        panelFormulario.setOpaque(false);

        lblNombre.setFont(new java.awt.Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(255, 255, 255));
        lblNombre.setText("Nombre del local");

        txtNombre.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre.setMinimumSize(new java.awt.Dimension(70, 30));
        txtNombre.setPreferredSize(new java.awt.Dimension(70, 30));

        lblCalle.setFont(new java.awt.Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblCalle.setForeground(new java.awt.Color(255, 255, 255));
        lblCalle.setText("Direccion");

        txtCalle.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtCalle.setForeground(new java.awt.Color(0, 0, 0));
        txtCalle.setMinimumSize(new java.awt.Dimension(70, 30));
        txtCalle.setPreferredSize(new java.awt.Dimension(70, 30));

        lblEmail.setFont(new java.awt.Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(255, 255, 255));
        lblEmail.setText("Email");

        txtEmail.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtEmail.setMinimumSize(new java.awt.Dimension(70, 30));
        txtEmail.setPreferredSize(new java.awt.Dimension(70, 30));

        lblTelefono.setFont(new java.awt.Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(255, 255, 255));
        lblTelefono.setText("Telefono");

        txtTelefono.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtTelefono.setForeground(new java.awt.Color(0, 0, 0));
        txtTelefono.setMinimumSize(new java.awt.Dimension(70, 30));
        txtTelefono.setPreferredSize(new java.awt.Dimension(70, 30));

        cbxMembresia.setFont(new java.awt.Font("Roboto", Font.BOLD, 14)); // NOI18N
        cbxMembresia.setMinimumSize(new java.awt.Dimension(80, 30));
        cbxMembresia.setPreferredSize(new java.awt.Dimension(80, 30));

        btnMas.setBackground(new Color(0, 0, 0, 0));
        btnMas.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\mas.png")); // NOI18N
        btnMas.setToolTipText("");
        btnMas.setColor(new Color(0, 0, 0, 0));
        btnMas.setColorClick(new java.awt.Color(10, 193, 18));
        btnMas.setColorOver(new java.awt.Color(15, 225, 24));
        btnMas.setPreferredSize(new java.awt.Dimension(40, 40));

        btnMenos.setBackground(new Color(0, 0, 0, 0));
        btnMenos.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\menos.png")); // NOI18N
        btnMenos.setToolTipText("");
        btnMenos.setColor(new Color(0, 0, 0, 0));
        btnMenos.setColorClick(new java.awt.Color(220, 51, 51));
        btnMenos.setColorOver(new java.awt.Color(240, 56, 56));
        btnMenos.setPreferredSize(new java.awt.Dimension(40, 40));

        lblMembresia.setFont(new java.awt.Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblMembresia.setForeground(new java.awt.Color(255, 255, 255));
        lblMembresia.setText("Membresia");

        javax.swing.GroupLayout panelFormularioLayout = new javax.swing.GroupLayout(panelFormulario);
        panelFormulario.setLayout(panelFormularioLayout);
        panelFormularioLayout.setHorizontalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCalle)
                    .addComponent(lblEmail)
                    .addComponent(lblTelefono)
                    .addComponent(lblMembresia)
                    .addComponent(lblNombre))
                .addGap(4, 4, 4)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelFormularioLayout.createSequentialGroup()
                        .addComponent(cbxMembresia, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(btnMas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(btnMenos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFormularioLayout.setVerticalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombre))
                .addGap(18, 18, 18)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCalle)
                    .addComponent(txtCalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTelefono)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbxMembresia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMenos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMembresia))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblImagen.setPreferredSize(new java.awt.Dimension(250, 250));

        javax.swing.GroupLayout panelEditableLayout = new javax.swing.GroupLayout(panelEditable);
        panelEditable.setLayout(panelEditableLayout);
        panelEditableLayout.setHorizontalGroup(
            panelEditableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEditableLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(panelEditableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEditableLayout.createSequentialGroup()
                        .addComponent(panelFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                        .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50))
                    .addGroup(panelEditableLayout.createSequentialGroup()
                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(btnReubicar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        panelEditableLayout.setVerticalGroup(
            panelEditableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEditableLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(panelEditableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(74, 74, 74)
                .addGroup(panelEditableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReubicar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(74, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelEditable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelEditable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private es.xalpha.gym.vista.BotonRedondeado btnActualizar;
    private es.xalpha.gym.vista.BotonRedondeado btnMas;
    private es.xalpha.gym.vista.BotonRedondeado btnMenos;
    private es.xalpha.gym.vista.BotonRedondeado btnReubicar;
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
