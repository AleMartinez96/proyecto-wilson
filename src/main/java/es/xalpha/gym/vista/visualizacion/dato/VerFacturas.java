package es.xalpha.gym.vista.visualizacion.dato;

import es.xalpha.gym.controladora.ControladoraFactura;
import es.xalpha.gym.logica.entidad.Factura;
import es.xalpha.gym.logica.util.*;
import es.xalpha.gym.logica.util.enums.ControladoraFacturaSingleton;
import es.xalpha.gym.logica.util.gui.BotonRedondeado;
import es.xalpha.gym.logica.util.gui.UtilGUI;
import es.xalpha.gym.vista.principal.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class VerFacturas extends JPanel {

    private static List<Factura> facturas = new ArrayList<>();
    private final VentanaPrincipal principal;
    private static final Map<String, String> FILTRO = Map.of("id factura",
            "idFactura", "nro factura", "nroFactura", "fecha de emision",
            "fechaEmision");

    public VerFacturas(VentanaPrincipal principal) {
        initComponents();
        this.principal = principal;
        List<String> items = FILTRO.keySet().stream().toList();
        setComboBoxFiltro(items);
        btnListener();
        VerClientes.gestionarTxt(txtBuscar);
        filtrarFacturas(txtBuscar);
    }

    private void btnListener() {
        btnImprimir.addActionListener(_ -> generarPDF());
        btnAsc.addActionListener(this::ordenarFacturasPor);
        btnDesc.addActionListener(this::ordenarFacturasPor);
        btnBuscar.addActionListener(_ -> aplicarFiltro(txtBuscar));
        btnActualizar.addActionListener(_ -> actualizarFactura());
    }

    private void actualizarFactura() {
        ControladoraFactura controller =
                ControladoraFacturaSingleton.INSTANCIA.getControllerFactura();
        String nombre = principal.getVerConfiguracion().getNombreLocal();
        controller.actualizarNombreDeLocalEnFactura(nombre);
        cargarDatosFactura(controller.getListaFacturas());
    }

    private void filtrarFacturas(JTextField textField) {
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
        ControladoraFactura controller =
                ControladoraFacturaSingleton.INSTANCIA.getControllerFactura();
        cargarDatosFactura(controller.filtrarFacturas(filtro));
    }

    private void generarPDF() {
        if (tabla.getRowCount() == 0) {
            UtilGUI.mensaje("No hay datos disponibles para mostrar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (tabla.getSelectedRow() != -1) {
            try {
                String valor = String.valueOf(
                        tabla.getValueAt(tabla.getSelectedRow(), 0));
                Long idFactura = Long.parseLong(valor);
                crearPDF(idFactura);
            } catch (IOException e) {
                UtilGUI.mensaje("Surgio un error al crear el archivo: " +
                                e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            UtilGUI.mensaje(
                    "Debe seleccionar una fila para realizar esta acción.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearPDF(Long idFactura) throws IOException {
        CreadorPDFDeFactura.crearPDF(obtenerPDFDeBaseDeDatos(idFactura),
                principal.getVerConfiguracion().getConfiguracion());
        UtilGUI.mensaje("El archivo PDF se ha generado correctamente.",
                "PDF generado", JOptionPane.PLAIN_MESSAGE);
    }

    private Factura obtenerPDFDeBaseDeDatos(Long idFactura) {
        ControladoraFactura controller =
                ControladoraFacturaSingleton.INSTANCIA.getControllerFactura();
        return controller.getFactura(idFactura);
    }

    public void cargarDatosEnSegundoPlano() {
        new SwingWorker<List<Factura>, Void>() {

            @Override
            protected List<Factura> doInBackground() {
                ControladoraFactura controller =
                        ControladoraFacturaSingleton.INSTANCIA.getControllerFactura();
                return controller.getListaFacturas();
            }

            @Override
            protected void done() {
                try {
                    List<Factura> facturas = get();
                    cargarDatosFactura(facturas);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }

    private void cargarDatosFactura(List<Factura> listaFacturas) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] titulos = {"id_factura", "nro_factura", "fecha_de_emision",
                "monto", "local", "id_cliente", "apellido", "nombre"};
        modelo.setColumnIdentifiers(titulos);
        facturas = listaFacturas;
        facturas.forEach(factura -> {
            Object[] objects = {factura.getIdFactura(), factura.getNroFactura(),
                    UtilLogica.formatoFecha(factura.getFechaEmision(),
                            "dd-MM-yyyy"), factura.getMonto(),
                    factura.getNomLocal(), factura.getCliente().getIdCliente(),
                    factura.getCliente().getApellido(),
                    factura.getCliente().getNombre()};
            modelo.addRow(objects);
        });
        tabla.setModel(modelo);
    }

    private void ordenarFacturasPor(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        String texto = button.getToolTipText();
        String seleccion = ((String) Objects.requireNonNull(
                cbxOrden.getSelectedItem())).toLowerCase();
        facturas = getFacturas();
        cargarDatosFactura(getListaOrdenada(texto, seleccion));
    }

    private List<Factura> getListaOrdenada(String texto, String seleccion) {
        ControladoraFactura controller =
                ControladoraFacturaSingleton.INSTANCIA.getControllerFactura();
        if (entradaValida(texto, seleccion) && texto.equalsIgnoreCase("asc")) {
            facturas = controller.getListaOrdenadaFactura(true,
                    FILTRO.get(seleccion));
        } else if (entradaValida(texto, seleccion)) {
            facturas = controller.getListaOrdenadaFactura(false,
                    FILTRO.get(seleccion));
        }
        return facturas;
    }

    private boolean entradaValida(String texto, String seleccion) {
        return (texto != null && !texto.isEmpty()) &&
               (seleccion != null && !seleccion.isEmpty());
    }

    public void setComboBoxFiltro(List<String> items) {
        items.forEach(cbxOrden::addItem);
    }

    public static List<Factura> getFacturas() {
        return facturas;
    }

    public JPanel getPanelEdicion() {
        return panelEdicion;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEdicion = new javax.swing.JPanel();
        btnImprimir = new BotonRedondeado();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        lblOrden = new javax.swing.JLabel();
        cbxOrden = new javax.swing.JComboBox<>();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new BotonRedondeado();
        btnAsc = new BotonRedondeado();
        btnDesc = new BotonRedondeado();
        btnActualizar = new BotonRedondeado();

        panelEdicion.setOpaque(false);
        panelEdicion.setPreferredSize(new java.awt.Dimension(800, 500));

        btnImprimir.setBackground(new Color(0, 0, 0, 0));
        btnImprimir.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimir.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\PDF.png")); // NOI18N
        btnImprimir.setToolTipText("Generar PDF");
        btnImprimir.setColor(new Color(0, 0, 0, 0));
        btnImprimir.setColorClick(new java.awt.Color(221, 59, 221));
        btnImprimir.setColorOver(new java.awt.Color(238, 62, 238));
        btnImprimir.setFont(
                new java.awt.Font("Roboto", Font.ITALIC, 14)); // NOI18N
        btnImprimir.setHorizontalTextPosition(
                javax.swing.SwingConstants.CENTER);
        btnImprimir.setIconTextGap(10);
        btnImprimir.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnImprimir.setMaximumSize(new java.awt.Dimension(50, 50));
        btnImprimir.setMinimumSize(new java.awt.Dimension(50, 50));
        btnImprimir.setPreferredSize(new java.awt.Dimension(50, 50));

        jScrollPane1.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 12)); // NOI18N
        jScrollPane1.setOpaque(true);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(750, 310));

        tabla.setFont(new java.awt.Font("Roboto", Font.PLAIN, 12)); // NOI18N
        tabla.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{{}, {}, {}, {}}, new String[]{

        }));
        tabla.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla.setRowHeight(30);
        tabla.setRowMargin(1);
        jScrollPane1.setViewportView(tabla);

        lblOrden.setFont(new java.awt.Font("Roboto", Font.PLAIN, 16)); // NOI18N
        lblOrden.setForeground(new java.awt.Color(255, 255, 255));
        lblOrden.setText("Ordenar por");
        lblOrden.setPreferredSize(new java.awt.Dimension(90, 20));

        cbxOrden.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        cbxOrden.setForeground(new java.awt.Color(0, 0, 0));
        cbxOrden.setPreferredSize(new java.awt.Dimension(80, 30));

        txtBuscar.setFont(
                new java.awt.Font("Roboto", Font.PLAIN, 12)); // NOI18N
        txtBuscar.setMinimumSize(new java.awt.Dimension(68, 30));
        txtBuscar.setPreferredSize(new java.awt.Dimension(200, 30));

        btnBuscar.setBackground(new Color(0, 0, 0, 0));
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\buscar.png")); // NOI18N
        btnBuscar.setToolTipText("Buscar");
        btnBuscar.setColor(new Color(0, 0, 0, 0));
        btnBuscar.setColorClick(new java.awt.Color(65, 72, 213));
        btnBuscar.setColorOver(new java.awt.Color(71, 78, 231));
        btnBuscar.setFont(
                new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnBuscar.setPreferredSize(new java.awt.Dimension(40, 40));

        btnAsc.setBackground(new Color(0, 0, 0, 0));
        btnAsc.setForeground(new java.awt.Color(255, 255, 255));
        btnAsc.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\orden asc.png")); // NOI18N
        btnAsc.setToolTipText("Asc");
        btnAsc.setColor(new Color(0, 0, 0, 0));
        btnAsc.setColorClick(new java.awt.Color(65, 72, 213));
        btnAsc.setColorOver(new java.awt.Color(71, 78, 231));
        btnAsc.setFont(new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnAsc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAsc.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnAsc.setMaximumSize(new java.awt.Dimension(30, 30));
        btnAsc.setMinimumSize(new java.awt.Dimension(30, 30));
        btnAsc.setPreferredSize(new java.awt.Dimension(30, 30));

        btnDesc.setBackground(new Color(0, 0, 0, 0));
        btnDesc.setForeground(new java.awt.Color(255, 255, 255));
        btnDesc.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\orden desc.png")); // NOI18N
        btnDesc.setToolTipText("Desc");
        btnDesc.setColor(new Color(0, 0, 0, 0));
        btnDesc.setColorClick(new java.awt.Color(65, 72, 213));
        btnDesc.setColorOver(new java.awt.Color(71, 78, 231));
        btnDesc.setFont(new java.awt.Font("Roboto", Font.ITALIC, 12)); // NOI18N
        btnDesc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDesc.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnDesc.setMaximumSize(new java.awt.Dimension(30, 30));
        btnDesc.setMinimumSize(new java.awt.Dimension(30, 30));
        btnDesc.setPreferredSize(new java.awt.Dimension(30, 30));

        btnActualizar.setBackground(new Color(0, 0, 0, 0));
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setIcon(new javax.swing.ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson " +
                "Gimnasio\\src\\icon\\actualizar.png")); // NOI18N
        btnActualizar.setToolTipText("Actualizar");
        btnActualizar.setColor(new Color(0, 0, 0, 0));
        btnActualizar.setColorClick(new java.awt.Color(68, 75, 217));
        btnActualizar.setColorOver(new java.awt.Color(79, 87, 248));
        btnActualizar.setFont(
                new java.awt.Font("Roboto", Font.ITALIC, 14)); // NOI18N
        btnActualizar.setHorizontalTextPosition(
                javax.swing.SwingConstants.CENTER);
        btnActualizar.setIconTextGap(10);
        btnActualizar.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnActualizar.setMaximumSize(new java.awt.Dimension(50, 50));
        btnActualizar.setMinimumSize(new java.awt.Dimension(50, 50));
        btnActualizar.setPreferredSize(new java.awt.Dimension(50, 50));

        javax.swing.GroupLayout panelEdicionLayout =
                new javax.swing.GroupLayout(
                panelEdicion);
        panelEdicion.setLayout(panelEdicionLayout);
        panelEdicionLayout.setHorizontalGroup(
                panelEdicionLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelEdicionLayout.createSequentialGroup().addGap(20,
                                20, 20).addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                btnBuscar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(txtBuscar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                200,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                220, 220, 220).addComponent(
                                                lblOrden,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(cbxOrden,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                130,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addGroup(
                                                panelEdicionLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                        btnAsc,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                        btnDesc,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                                32, 67,
                                                Short.MAX_VALUE)).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addGroup(
                                                panelEdicionLayout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                                btnImprimir,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                                30, 30,
                                                                30).addComponent(
                                                                btnActualizar,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(
                                                        jScrollPane1,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        760,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                                0, 0, Short.MAX_VALUE)))));
        panelEdicionLayout.setVerticalGroup(
                panelEdicionLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        panelEdicionLayout.createSequentialGroup().addGap(18,
                                18, 18).addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        lblOrden,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        cbxOrden,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                btnAsc,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(btnDesc,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(
                                        btnBuscar,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtBuscar,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(
                                18, 18, 18).addComponent(jScrollPane1,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                37, Short.MAX_VALUE).addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                        btnImprimir,
                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                        btnActualizar,
                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 800,
                Short.MAX_VALUE).addGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panelEdicion, javax.swing.GroupLayout.Alignment.TRAILING,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 500,
                Short.MAX_VALUE).addGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panelEdicion, javax.swing.GroupLayout.Alignment.TRAILING,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BotonRedondeado btnActualizar;
    private BotonRedondeado btnAsc;
    private BotonRedondeado btnBuscar;
    private BotonRedondeado btnDesc;
    private BotonRedondeado btnImprimir;
    private javax.swing.JComboBox<String> cbxOrden;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblOrden;
    private javax.swing.JPanel panelEdicion;
    private javax.swing.JTable tabla;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
