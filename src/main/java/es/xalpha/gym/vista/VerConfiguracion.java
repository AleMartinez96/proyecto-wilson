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
import java.net.URI;
import java.net.URISyntaxException;
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
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEditable = new JPanel();
        btnActualizar = new es.xalpha.gym.vista.BotonRedondeado();
        btnReubicar = new es.xalpha.gym.vista.BotonRedondeado();
        panelFormulario = new JPanel();
        lblNombre = new JLabel();
        txtNombre = new JTextField();
        lblCalle = new JLabel();
        txtCalle = new JTextField();
        lblEmail = new JLabel();
        txtEmail = new JTextField();
        lblTelefono = new JLabel();
        txtTelefono = new JTextField();
        cbxMembresia = new JComboBox<>();
        btnMas = new es.xalpha.gym.vista.BotonRedondeado();
        btnMenos = new es.xalpha.gym.vista.BotonRedondeado();
        lblMembresia = new JLabel();
        lblImagen = new JLabel();

        setMaximumSize(new Dimension(800, 500));
        setMinimumSize(new Dimension(800, 500));
        setPreferredSize(new Dimension(800, 500));

        panelEditable.setMaximumSize(new Dimension(800, 500));
        panelEditable.setMinimumSize(new Dimension(800, 500));
        panelEditable.setOpaque(false);
        panelEditable.setPreferredSize(new Dimension(800, 500));

        btnActualizar.setBackground(new Color(0, 0, 0, 0));
        btnActualizar.setForeground(new Color(255, 255, 255));
        btnActualizar.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                            "Java\\Netbeans\\GYM\\src\\icon" +
                                            "\\guardar.png")); // NOI18N
        btnActualizar.setToolTipText("");
        btnActualizar.setColor(new Color(0, 0, 0, 0));
        btnActualizar.setColorClick(new Color(10, 193, 18));
        btnActualizar.setColorOver(new Color(15, 225, 24));
        btnActualizar.setFont(
                new Font("Roboto", Font.BOLD | Font.ITALIC, 12)); // NOI18N
        btnActualizar.setHorizontalTextPosition(SwingConstants.CENTER);
        btnActualizar.setIconTextGap(2);
        btnActualizar.setMargin(new Insets(2, 14, 2, 14));
        btnActualizar.setMaximumSize(new Dimension(50, 50));
        btnActualizar.setMinimumSize(new Dimension(50, 50));
        btnActualizar.setPreferredSize(new Dimension(50, 50));

        btnReubicar.setBackground(new Color(0, 0, 0, 0));
        btnReubicar.setForeground(new Color(255, 255, 255));
        btnReubicar.setIcon(new JLabel() {
            public Icon getIcon() {
                try {
                    return new ImageIcon(
                            new URI("file:/D:/Ale/Mis%20Cursos/Curso%20Java" +
                                    "/Netbeans" +
                                    "/GYM/src/icon/transferir.png").toURL());
                } catch (java.net.MalformedURLException _) {
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        }.getIcon());
        btnReubicar.setToolTipText("");
        btnReubicar.setColor(new Color(0, 0, 0, 0));
        btnReubicar.setColorClick(new Color(139, 49, 210));
        btnReubicar.setColorOver(new Color(160, 58, 240));
        btnReubicar.setFont(
                new Font("Roboto", Font.BOLD | Font.ITALIC, 12)); // NOI18N
        btnReubicar.setHorizontalTextPosition(SwingConstants.CENTER);
        btnReubicar.setIconTextGap(2);
        btnReubicar.setMargin(new Insets(2, 14, 2, 14));
        btnReubicar.setMaximumSize(new Dimension(50, 50));
        btnReubicar.setMinimumSize(new Dimension(50, 50));
        btnReubicar.setPreferredSize(new Dimension(50, 50));

        panelFormulario.setOpaque(false);

        lblNombre.setFont(new Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblNombre.setForeground(new Color(255, 255, 255));
        lblNombre.setText("Nombre del local");

        txtNombre.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtNombre.setForeground(new Color(0, 0, 0));
        txtNombre.setMinimumSize(new Dimension(70, 30));
        txtNombre.setPreferredSize(new Dimension(70, 30));

        lblCalle.setFont(new Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblCalle.setForeground(new Color(255, 255, 255));
        lblCalle.setText("Direccion");

        txtCalle.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtCalle.setForeground(new Color(0, 0, 0));
        txtCalle.setMinimumSize(new Dimension(70, 30));
        txtCalle.setPreferredSize(new Dimension(70, 30));

        lblEmail.setFont(new Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblEmail.setForeground(new Color(255, 255, 255));
        lblEmail.setText("Email");

        txtEmail.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtEmail.setForeground(new Color(0, 0, 0));
        txtEmail.setMinimumSize(new Dimension(70, 30));
        txtEmail.setPreferredSize(new Dimension(70, 30));

        lblTelefono.setFont(new Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblTelefono.setForeground(new Color(255, 255, 255));
        lblTelefono.setText("Telefono");

        txtTelefono.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        txtTelefono.setForeground(new Color(0, 0, 0));
        txtTelefono.setMinimumSize(new Dimension(70, 30));
        txtTelefono.setPreferredSize(new Dimension(70, 30));

        cbxMembresia.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        cbxMembresia.setMinimumSize(new Dimension(80, 30));
        cbxMembresia.setPreferredSize(new Dimension(80, 30));

        btnMas.setBackground(new Color(0, 0, 0, 0));
        btnMas.setIcon(new ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso " + "Java\\Netbeans\\GYM\\src" +
                "\\icon\\mas.png")); // NOI18N
        btnMas.setToolTipText("");
        btnMas.setColor(new Color(0, 0, 0, 0));
        btnMas.setColorClick(new Color(10, 193, 18));
        btnMas.setColorOver(new Color(15, 225, 24));
        btnMas.setPreferredSize(new Dimension(40, 40));

        btnMenos.setBackground(new Color(0, 0, 0, 0));
        btnMenos.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                       "Java\\Netbeans\\GYM\\src\\icon\\menos" +
                                       ".png")); // NOI18N
        btnMenos.setToolTipText("");
        btnMenos.setColor(new Color(0, 0, 0, 0));
        btnMenos.setColorClick(new Color(220, 51, 51));
        btnMenos.setColorOver(new Color(240, 56, 56));
        btnMenos.setPreferredSize(new Dimension(40, 40));

        lblMembresia.setFont(new Font("Roboto", Font.BOLD, 18)); // NOI18N
        lblMembresia.setForeground(new Color(255, 255, 255));
        lblMembresia.setText("Membresia");

        GroupLayout panelFormularioLayout = new GroupLayout(panelFormulario);
        panelFormulario.setLayout(panelFormularioLayout);
        panelFormularioLayout.setHorizontalGroup(
                panelFormularioLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelFormularioLayout.createSequentialGroup().addContainerGap().addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblCalle).addComponent(
                                        lblEmail).addComponent(
                                        lblTelefono).addComponent(
                                        lblMembresia).addComponent(
                                        lblNombre)).addGap(4, 4, 4).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING,
                                        false).addGroup(
                                        panelFormularioLayout.createSequentialGroup().addComponent(
                                                cbxMembresia,
                                                GroupLayout.PREFERRED_SIZE, 140,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(btnMas,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(btnMenos,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)).addComponent(
                                        txtTelefono, GroupLayout.PREFERRED_SIZE,
                                        150,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtEmail, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE).addComponent(txtCalle,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE).addComponent(txtNombre,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)).addContainerGap(
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        panelFormularioLayout.setVerticalGroup(
                panelFormularioLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelFormularioLayout.createSequentialGroup().addGap(24,
                                24, 24).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE).addComponent(
                                        txtNombre, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        lblNombre)).addGap(18, 18, 18).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblCalle).addComponent(txtCalle,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(18,
                                18, 18).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE).addComponent(
                                        lblEmail).addComponent(txtEmail,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(18,
                                18, 18).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblTelefono).addComponent(txtTelefono,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(18,
                                18, 18).addGroup(
                                panelFormularioLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        cbxMembresia,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        btnMas, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        btnMenos, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        lblMembresia)).addContainerGap(
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        lblImagen.setPreferredSize(new Dimension(250, 250));

        GroupLayout panelEditableLayout = new GroupLayout(panelEditable);
        panelEditable.setLayout(panelEditableLayout);
        panelEditableLayout.setHorizontalGroup(
                panelEditableLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelEditableLayout.createSequentialGroup().addGap(50,
                                50, 50).addGroup(
                                panelEditableLayout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING).addGroup(
                                        panelEditableLayout.createSequentialGroup().addComponent(
                                                panelFormulario,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                                LayoutStyle.ComponentPlacement.RELATED,
                                                74,
                                                Short.MAX_VALUE).addComponent(
                                                lblImagen,
                                                GroupLayout.PREFERRED_SIZE, 250,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                50, 50, 50)).addGroup(
                                        panelEditableLayout.createSequentialGroup().addComponent(
                                                btnActualizar,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                30, 30, 30).addComponent(
                                                btnReubicar,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addContainerGap(
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)))));
        panelEditableLayout.setVerticalGroup(
                panelEditableLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelEditableLayout.createSequentialGroup().addGap(40,
                                40, 40).addGroup(
                                panelEditableLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblImagen, GroupLayout.PREFERRED_SIZE,
                                        250,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        panelFormulario,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(74,
                                74, 74).addGroup(
                                panelEditableLayout.createParallelGroup(
                                        GroupLayout.Alignment.TRAILING).addComponent(
                                        btnActualizar,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        btnReubicar, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addContainerGap(
                                74, Short.MAX_VALUE)));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelEditable,
                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelEditable,
                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private es.xalpha.gym.vista.BotonRedondeado btnActualizar;
    private es.xalpha.gym.vista.BotonRedondeado btnMas;
    private es.xalpha.gym.vista.BotonRedondeado btnMenos;
    private es.xalpha.gym.vista.BotonRedondeado btnReubicar;
    private JComboBox<String> cbxMembresia;
    private JLabel lblCalle;
    private JLabel lblEmail;
    private JLabel lblImagen;
    private JLabel lblMembresia;
    private JLabel lblNombre;
    private JLabel lblTelefono;
    private JPanel panelEditable;
    private JPanel panelFormulario;
    private JTextField txtCalle;
    private JTextField txtEmail;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
