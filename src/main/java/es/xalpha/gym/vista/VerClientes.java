package es.xalpha.gym.vista;

import es.xalpha.gym.contoladora.ControladoraLogica;
import es.xalpha.gym.logica.entidad.Cliente;
import es.xalpha.gym.logica.util.ControladoraLogicaSingleton;
import es.xalpha.gym.logica.util.UtilGUI;
import es.xalpha.gym.logica.util.UtilLogica;
import es.xalpha.gym.persistencia.exceptions.NonexistentEntityException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class VerClientes extends JPanel {

    private static Timer timer;
    private final VentanaPrincipal principal;
    private static List<Cliente> clientes = new ArrayList<>();
    private static final Map<String, String> FILTRO = Map.of("id cliente",
            "idCliente", "nombre", "nombre", "apellido", "apellido");

    public VerClientes(VentanaPrincipal principal) {
        initComponents();
        this.principal = principal;
        setComboBox(FILTRO.keySet().stream().toList());
        btnListener();
        gestionarTxt(txtBuscar);
        filtrarClientes(txtBuscar);
    }

    private void btnListener() {
        btnEditar.addActionListener(_ -> editarDatos());
        btnEliminar.addActionListener(_ -> eliminarCliente());
        btnAsc.addActionListener(this::ordenarLista);
        btnDesc.addActionListener(this::ordenarLista);
        btnBuscar.addActionListener(_ -> aplicarFiltro(txtBuscar));
    }

    protected static void gestionarTxt(JTextField textField) {
        setTextField(textField, "buscar", Color.gray,
                new Font("Roboto", Font.ITALIC, 12));
        txtListener(textField, "buscar", Color.gray,
                new Font("Roboto", Font.ITALIC, 12), "", Color.black,
                new Font("Roboto", Font.PLAIN, 12));
    }

    private static void txtListener(JTextField textField, String txtDefault,
                                    Color colorDefault, Font fontDefault,
                                    String nuevo, Color color, Font font) {

        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (textField.getText().equals(txtDefault)) {
                    setTextField(textField, nuevo, color, font);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (textField.getText().isEmpty() ||
                    textField.getText().equals(txtDefault)) {
                    setTextField(textField, txtDefault, colorDefault,
                            fontDefault);
                }
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (textField.getText().equals(txtDefault) ||
                    textField.getText().isEmpty()) {
                    setTextField(textField, nuevo, color, font);
                }
            }
        });

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                timer = new Timer(500, _ -> SwingUtilities.invokeLater(() -> {
                    if (textField.getText().isEmpty()) {
                        setTextField(textField, "buscar", Color.gray,
                                new Font("Roboto", Font.ITALIC, 12));
                    } else {
                        timer.stop();
                    }
                }));
                timer.start();
            }
        });
    }

    private void filtrarClientes(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    aplicarFiltro(textField);
                }
                if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                    cargarDatosEnSegundoPlano();
                }
            }
        });
    }

    private void aplicarFiltro(JTextField textField) {
        String filtro = textField.getText();
        ControladoraLogica controller =
                ControladoraLogicaSingleton.INSTANCIA.getController();
        cargarDatosCliente(controller.filtrarClientes(filtro));
    }

    protected static void setTextField(JTextField textField, String texto,
                                       Color color, Font font) {
        textField.setText(texto);
        textField.setForeground(color);
        textField.setFont(font);
    }

    private void editarDatos() {
        if (tabla.getRowCount() == 0) {
            UtilGUI.mensaje("No hay datos disponibles para mostrar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (tabla.getSelectedRow() != -1) {
            String valor = String.valueOf(
                    tabla.getValueAt(tabla.getSelectedRow(), 0));
            Long idCliente = Long.parseLong(valor);
            EditarDatos editarDatos = principal.getEditarDatos();
            editarDatos.setFormulario(idCliente);
            principal.verPanel(editarDatos.getPanelEdicion(),
                    principal.getPaneles());
        } else {
            UtilGUI.mensaje(
                    "Debe seleccionar una fila para realizar esta acción.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        if (tabla.getRowCount() == 0) {
            UtilGUI.mensaje("No hay datos disponibles para mostrar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (tabla.getSelectedRow() != -1) {
            boolean opcion =
                    UtilGUI.opcion("¿Esta seguro de realizar esta accion?",
                            "Eliminacion", JOptionPane.YES_NO_OPTION) ==
                    JOptionPane.YES_OPTION;
            if (opcion) {
                eliminar();
            } else {
                UtilGUI.mensaje("La operación fue cancelada.", "Cancelado",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            UtilGUI.mensaje(
                    "Debe seleccionar una fila para realizar esta acción.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        String valor = String.valueOf(
                tabla.getValueAt(tabla.getSelectedRow(), 0));
        Long idCliente = Long.parseLong(valor);
        try {
            ControladoraLogica controller =
                    ControladoraLogicaSingleton.INSTANCIA.getController();
            controller.eliminarCliente(idCliente);
            UtilGUI.mensaje("Los datos fueron eliminados con exito", "Exito",
                    JOptionPane.INFORMATION_MESSAGE);
            principal.cargarDatosCliente();
        } catch (NonexistentEntityException e) {
            UtilGUI.mensaje(
                    "Ocurrió un error al intentar eliminar los datos. " +
                    "Inténtelo nuevamente.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarDatosEnSegundoPlano() {
        new SwingWorker<List<Cliente>, Void>() {

            @Override
            protected List<Cliente> doInBackground() {
                ControladoraLogica controller =
                        ControladoraLogicaSingleton.INSTANCIA.getController();
                return controller.getListaClientes();
            }

            @Override
            protected void done() {
                try {
                    List<Cliente> clientes = get();
                    cargarDatosCliente(clientes);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }

    private void cargarDatosCliente(List<Cliente> listaClientes) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] titulos = {"id_cliente", "apellido", "nombre", "tel.", "email",
                "membresia", "inicio_el", "finaliza_el"};
        modelo.setColumnIdentifiers(titulos);
        clientes = listaClientes;
        clientes.forEach(cliente -> {
            Object[] objects = {cliente.getIdCliente(), cliente.getApellido(),
                    cliente.getNombre(), cliente.getContacto().getTelefono(),
                    cliente.getContacto().getEmail(),
                    cliente.getMembresia().getTipo(), UtilLogica.formatoFecha(
                    cliente.getMembresia().getFechaInicio(), "dd-MM-yyyy"),
                    UtilLogica.formatoFecha(
                            cliente.getMembresia().getFechaFin(),
                            "dd-MM-yyyy")};
            modelo.addRow(objects);
        });
        tabla.setModel(modelo);
    }

    public void setComboBox(List<String> items) {
        items.forEach(cbxOrden::addItem);
    }

    private void ordenarLista(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        String texto = button.getToolTipText();
        String seleccion = ((String) Objects.requireNonNull(
                cbxOrden.getSelectedItem())).toLowerCase();
        clientes = getListaClientes();
        cargarDatosCliente(getListaOrdenada(texto, seleccion));
    }

    private List<Cliente> getListaOrdenada(String texto, String seleccion) {
        ControladoraLogica controller =
                ControladoraLogicaSingleton.INSTANCIA.getController();
        if (entradaValida(texto, seleccion) && texto.equalsIgnoreCase("asc")) {
            clientes = controller.getListaOrdenadaCliente(true,
                    FILTRO.get(seleccion));
        } else if (entradaValida(texto, seleccion)) {
            clientes = controller.getListaOrdenadaCliente(false,
                    FILTRO.get(seleccion));
        }
        return clientes;
    }

    private boolean entradaValida(String texto, String seleccion) {
        return (texto != null && !texto.isEmpty()) &&
               (seleccion != null && !seleccion.isEmpty());
    }

    private List<Cliente> getListaClientes() {
        return clientes;
    }

    public JPanel getPanelEdicion() {
        return panelEdicion;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEdicion = new javax.swing.JPanel();
        btnEliminar = new es.xalpha.gym.vista.BotonRedondeado();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        lblOrden = new javax.swing.JLabel();
        cbxOrden = new javax.swing.JComboBox<>();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new es.xalpha.gym.vista.BotonRedondeado();
        btnAsc = new es.xalpha.gym.vista.BotonRedondeado();
        btnDesc = new es.xalpha.gym.vista.BotonRedondeado();
        btnEditar = new es.xalpha.gym.vista.BotonRedondeado();

        setName("Form"); // NOI18N

        panelEdicion.setName("panelEdicion"); // NOI18N
        panelEdicion.setOpaque(false);
        panelEdicion.setPreferredSize(new java.awt.Dimension(800, 500));

        btnEliminar.setBackground(new Color(0, 0, 0, 0));
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\eliminar.png")); // NOI18N
        btnEliminar.setToolTipText("Eliminar");
        btnEliminar.setColor(new Color(0, 0, 0, 0));
        btnEliminar.setColorClick(new java.awt.Color(220, 51, 51));
        btnEliminar.setColorOver(new java.awt.Color(240, 56, 56));
        btnEliminar.setFont(new java.awt.Font("Roboto", Font.ITALIC, 14)); // NOI18N
        btnEliminar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminar.setIconTextGap(10);
        btnEliminar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnEliminar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnEliminar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnEliminar.setName("btnEliminar"); // NOI18N
        btnEliminar.setPreferredSize(new java.awt.Dimension(50, 50));

        jScrollPane1.setFont(new java.awt.Font("Roboto", Font.PLAIN, 12)); // NOI18N
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setOpaque(true);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(750, 310));

        tabla.setFont(new java.awt.Font("Roboto", Font.PLAIN, 12)); // NOI18N
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tabla.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla.setName("tabla"); // NOI18N
        tabla.setRowHeight(30);
        tabla.setRowMargin(1);
        jScrollPane1.setViewportView(tabla);

        lblOrden.setFont(new java.awt.Font("Roboto", Font.PLAIN, 16)); // NOI18N
        lblOrden.setForeground(new java.awt.Color(255, 255, 255));
        lblOrden.setText("Ordenar por");
        lblOrden.setName("lblOrden"); // NOI18N
        lblOrden.setPreferredSize(new java.awt.Dimension(90, 20));

        cbxOrden.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        cbxOrden.setForeground(new java.awt.Color(0, 0, 0));
        cbxOrden.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cbxOrden.setName("cbxOrden"); // NOI18N
        cbxOrden.setPreferredSize(new java.awt.Dimension(130, 30));

        txtBuscar.setFont(new java.awt.Font("Roboto", Font.PLAIN, 12)); // NOI18N
        txtBuscar.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtBuscar.setMinimumSize(new java.awt.Dimension(68, 30));
        txtBuscar.setName("txtBuscar"); // NOI18N
        txtBuscar.setPreferredSize(new java.awt.Dimension(200, 30));

        btnBuscar.setBackground(new Color(0, 0, 0, 0));
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\buscar.png")); // NOI18N
        btnBuscar.setToolTipText("Buscar");
        btnBuscar.setColor(new Color(0, 0, 0, 0));
        btnBuscar.setColorClick(new java.awt.Color(65, 72, 213));
        btnBuscar.setColorOver(new java.awt.Color(71, 78, 231));
        btnBuscar.setFont(new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnBuscar.setName("btnBuscar"); // NOI18N
        btnBuscar.setPreferredSize(new java.awt.Dimension(40, 40));

        btnAsc.setBackground(new Color(0, 0, 0, 0));
        btnAsc.setForeground(new java.awt.Color(255, 255, 255));
        btnAsc.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\orden asc.png")); // NOI18N
        btnAsc.setToolTipText("Asc");
        btnAsc.setColor(new Color(0, 0, 0, 0));
        btnAsc.setColorClick(new java.awt.Color(65, 72, 213));
        btnAsc.setColorOver(new java.awt.Color(71, 78, 231));
        btnAsc.setFont(new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnAsc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAsc.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnAsc.setMaximumSize(new java.awt.Dimension(30, 30));
        btnAsc.setMinimumSize(new java.awt.Dimension(30, 30));
        btnAsc.setName("btnAsc"); // NOI18N
        btnAsc.setPreferredSize(new java.awt.Dimension(30, 30));

        btnDesc.setBackground(new Color(0, 0, 0, 0));
        btnDesc.setForeground(new java.awt.Color(255, 255, 255));
        btnDesc.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\orden desc.png")); // NOI18N
        btnDesc.setToolTipText("Desc");
        btnDesc.setColor(new Color(0, 0, 0, 0));
        btnDesc.setColorClick(new java.awt.Color(65, 72, 213));
        btnDesc.setColorOver(new java.awt.Color(71, 78, 231));
        btnDesc.setFont(new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnDesc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDesc.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnDesc.setMaximumSize(new java.awt.Dimension(30, 30));
        btnDesc.setMinimumSize(new java.awt.Dimension(30, 30));
        btnDesc.setName("btnDesc"); // NOI18N
        btnDesc.setPreferredSize(new java.awt.Dimension(30, 30));

        btnEditar.setBackground(new Color(0, 0, 0, 0));
        btnEditar.setForeground(new java.awt.Color(255, 255, 255));
        btnEditar.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\editar.png")); // NOI18N
        btnEditar.setToolTipText("Editar");
        btnEditar.setColor(new Color(0, 0, 0, 0));
        btnEditar.setColorClick(new java.awt.Color(218, 148, 39));
        btnEditar.setColorOver(new java.awt.Color(236, 160, 44));
        btnEditar.setFont(new java.awt.Font("Roboto", Font.ITALIC, 14)); // NOI18N
        btnEditar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditar.setIconTextGap(10);
        btnEditar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnEditar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnEditar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnEditar.setName("btnEditar"); // NOI18N
        btnEditar.setPreferredSize(new java.awt.Dimension(50, 50));

        javax.swing.GroupLayout panelEdicionLayout = new javax.swing.GroupLayout(panelEdicion);
        panelEdicion.setLayout(panelEdicionLayout);
        panelEdicionLayout.setHorizontalGroup(
            panelEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEdicionLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelEdicionLayout.createSequentialGroup()
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(220, 220, 220)
                        .addComponent(lblOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(cbxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addGroup(panelEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btnAsc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelEdicionLayout.createSequentialGroup()
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        panelEdicionLayout.setVerticalGroup(
            panelEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEdicionLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panelEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(panelEdicionLayout.createSequentialGroup()
                        .addComponent(btnAsc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(btnDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cbxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(panelEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panelEdicion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panelEdicion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private es.xalpha.gym.vista.BotonRedondeado btnAsc;
    private es.xalpha.gym.vista.BotonRedondeado btnBuscar;
    private es.xalpha.gym.vista.BotonRedondeado btnDesc;
    private es.xalpha.gym.vista.BotonRedondeado btnEditar;
    private es.xalpha.gym.vista.BotonRedondeado btnEliminar;
    private javax.swing.JComboBox<String> cbxOrden;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblOrden;
    private javax.swing.JPanel panelEdicion;
    private javax.swing.JTable tabla;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
