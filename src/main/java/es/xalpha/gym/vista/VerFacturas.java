package es.xalpha.gym.vista;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import es.xalpha.gym.contoladora.ControladoraLogica;
import es.xalpha.gym.logica.entidad.Factura;
import es.xalpha.gym.logica.util.Configuracion;
import es.xalpha.gym.logica.util.ManejoArchivo;
import es.xalpha.gym.logica.util.Utils;

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

import static es.xalpha.gym.logica.util.Utils.mensaje;
import static es.xalpha.gym.vista.VerClientes.*;

public class VerFacturas extends JPanel {

    private final ControladoraLogica controller;
    private final Principal frame;
    private static List<Factura> facturas = new ArrayList<>();
    private static final Map<String, String> FILTRO = Map.of("id factura",
            "idFactura", "nro factura", "nroFactura", "fecha de emision",
            "fechaEmision");

    public VerFacturas(ControladoraLogica controller, Principal frame) {
        initComponents();
        this.frame = frame;
        this.controller = controller;
        List<String> items = FILTRO.keySet().stream().toList();
        setComboBox(items);
        btnListener();
        gestionarTxt(txtBuscar);
        filtrarFacturas(txtBuscar);
    }

    private void btnListener() {
        btnImprimir.addActionListener(_ -> obtenerPDF());
        btnAsc.addActionListener(this::ordenarFacturasPor);
        btnDesc.addActionListener(this::ordenarFacturasPor);
        btnBuscar.addActionListener(_ -> aplicarFiltro(txtBuscar));
    }

    private void filtrarFacturas(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    aplicarFiltro(textField);
                }
            }
        });
    }

    private void aplicarFiltro(JTextField textField) {
        String filtro = textField.getText();
        cargarDatosFactura(controller.filtrarFacturas(filtro));
    }

    private void obtenerPDF() {
        if (tabla.getRowCount() == 0) {
            mensaje("No hay datos disponibles para mostrar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (tabla.getSelectedRow() != -1) {
            String valor = String.valueOf(
                    tabla.getValueAt(tabla.getSelectedRow(), 0));
            Long idFactura = Long.parseLong(valor);
            Factura factura = controller.getFactura(idFactura);
            try {
                crearPDF(factura);
                mensaje("El archivo PDF se ha generado correctamente.",
                        "PDF generado", JOptionPane.PLAIN_MESSAGE);
            } catch (IOException e) {
                mensaje("Surgio un error al crear el archivo: " +
                        e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            mensaje("Debe seleccionar una fila para realizar esta acción.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearPDF(Factura factura) throws IOException {
        try (PdfWriter pdfWriter = new PdfWriter(obtenerPath())) {
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);
            darEstilo(document, factura);
            document.close();
        }
    }

    private String obtenerPath() {
        String path = ManejoArchivo.obtenerPath("", "PDF (*.pdf)", ".pdf");
        if (path != null && (path.trim().isEmpty() || path.equals(".pdf"))) {
            path = ManejoArchivo.pathDefault("nuevo documento pdf", ".pdf");
        }
        return path;
    }

    private void darEstilo(Document document, Factura factura) throws IOException {
        Configuracion config = frame.getVerConfiguracion().getConfiguracion();
        PdfFont boldFont = PdfFontFactory.createFont(
                StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(
                StandardFonts.HELVETICA);

        Paragraph header = new Paragraph("***** FACTURA *****").setFont(
                boldFont).setFontSize(24).setTextAlignment(
                TextAlignment.CENTER).setFontColor(
                ColorConstants.BLUE).setMarginBottom(15);

        document.add(header);

        document.add(new LineSeparator(new SolidLine(2f)).setMarginBottom(15));

        document.add(tituloSeccion("Información del Local", boldFont));

        document.add(
                parrafoConBorde("Nombre del local: ", factura.getNomLocal(),
                        boldFont, regularFont));
        document.add(
                parrafoConBorde("Direccion: ", config.getDomicilio().getCalle(),
                        boldFont, regularFont));

        document.add(parrafoConBorde("Email: ", config.getContacto().getEmail(),
                boldFont, regularFont));

        document.add(parrafoConBorde("Teléfono: ",
                config.getContacto().getTelefono(), boldFont, regularFont));

        document.add(tituloSeccion("Información de la Factura", boldFont));

        document.add(parrafoConBorde("Nro. de factura: ",
                "" + factura.getNroFactura(), boldFont, regularFont));

        document.add(
                parrafoConBorde("Monto: ", "" + factura.getMonto(), boldFont,
                        regularFont));

        document.add(parrafoConBorde("Cliente: ",
                factura.getCliente().nombreCompleto(), boldFont, regularFont));

        document.add(parrafoConBorde("Fecha de emisión: ",
                Utils.formatoFecha(factura.getFechaEmision(), "dd-MM-yyyy"),
                boldFont, regularFont));

        document.add(new LineSeparator(new SolidLine(1f)).setMarginTop(
                10).setMarginBottom(20));

        Paragraph footer = new Paragraph(
                "***** ¡Muchas gracias por elegirnos! *****").setFont(
                boldFont).setFontSize(14).setTextAlignment(
                TextAlignment.CENTER).setFontColor(
                ColorConstants.BLUE).setMarginTop(30);

        document.add(footer);
    }

    private Paragraph tituloSeccion(String titulo, PdfFont font) {
        return new Paragraph(titulo).setFont(font).setFontSize(16).setFontColor(
                ColorConstants.WHITE).setBackgroundColor(
                ColorConstants.DARK_GRAY).setTextAlignment(
                TextAlignment.CENTER).setPadding(5).setMarginBottom(10);
    }

    private Paragraph parrafoConBorde(String label, String valor,
                                      PdfFont boldFont, PdfFont regularFont) {
        if (label == null) {
            label = "";
        }
        if (valor == null) {
            valor = "";
        }

        Text labelText = new Text(label).setFont(regularFont);

        Text valueText = new Text(valor).setFont(boldFont);

        return new Paragraph().add(labelText).add(valueText).setFontSize(
                12).setFontColor(ColorConstants.DARK_GRAY).setBorderBottom(
                new SolidBorder(0.5f)).setPaddingBottom(5).setMarginBottom(10);
    }

    public void cargarDatosFactura(List<Factura> listaFacturas) {
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
                    Utils.formatoFecha(factura.getFechaEmision(), "dd-MM-yyyy"),
                    factura.getMonto(), factura.getNomLocal(),
                    factura.getCliente().getIdCliente(),
                    factura.getCliente().getApellido(),
                    factura.getCliente().getNombre()};
            modelo.addRow(objects);
        });
        tabla.setModel(modelo);
    }

    private void ordenarFacturasPor(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        String texto = button.getToolTipText();
        String filtro = filtroValido(txtBuscar);
        String seleccion = ((String) Objects.requireNonNull(
                cbxOrden.getSelectedItem())).toLowerCase();
        facturas = getFacturas();
        if (texto.equalsIgnoreCase("asc") && !seleccion.isEmpty()) {
            facturas = controller.getListaOrdenadaFactura(true,
                    FILTRO.get(seleccion), filtro);
        } else if (texto.equalsIgnoreCase("desc") && !seleccion.isEmpty()) {
            facturas = controller.getListaOrdenadaFactura(false,
                    FILTRO.get(seleccion), filtro);
        }
        cargarDatosFactura(facturas);
    }

    public void setComboBox(List<String> items) {
        items.forEach(cbxOrden::addItem);
    }

    public static List<Factura> getFacturas() {
        return facturas;
    }

    public JPanel getPanelEdicion() {
        return panelEdicion;
    }

    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEdicion = new JPanel();
        btnImprimir = new es.xalpha.gym.vista.BotonRedondeado();
        jScrollPane1 = new JScrollPane();
        tabla = new JTable();
        lblOrden = new JLabel();
        cbxOrden = new JComboBox<>();
        txtBuscar = new JTextField();
        btnBuscar = new es.xalpha.gym.vista.BotonRedondeado();
        btnAsc = new es.xalpha.gym.vista.BotonRedondeado();
        btnDesc = new es.xalpha.gym.vista.BotonRedondeado();

        panelEdicion.setOpaque(false);
        panelEdicion.setPreferredSize(new Dimension(800, 500));

        btnImprimir.setBackground(new Color(0, 0, 0, 0));
        btnImprimir.setForeground(new Color(255, 255, 255));
        btnImprimir.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                          "Java\\Netbeans\\GYM\\src\\icon" +
                                          "\\PDF.png")); // NOI18N
        btnImprimir.setToolTipText("Generar PDF");
        btnImprimir.setColor(new Color(0, 0, 0, 0));
        btnImprimir.setColorClick(new Color(221, 59, 221));
        btnImprimir.setColorOver(new Color(238, 62, 238));
        btnImprimir.setFont(
                new Font("Roboto", Font.BOLD | Font.ITALIC, 14)); // NOI18N
        btnImprimir.setHorizontalTextPosition(SwingConstants.CENTER);
        btnImprimir.setIconTextGap(10);
        btnImprimir.setMargin(new Insets(2, 14, 2, 14));
        btnImprimir.setMaximumSize(new Dimension(50, 50));
        btnImprimir.setMinimumSize(new Dimension(50, 50));
        btnImprimir.setPreferredSize(new Dimension(50, 50));

        jScrollPane1.setFont(new Font("Roboto", Font.PLAIN, 12)); // NOI18N
        jScrollPane1.setOpaque(true);
        jScrollPane1.setPreferredSize(new Dimension(750, 310));

        tabla.setFont(new Font("Roboto", Font.PLAIN, 12)); // NOI18N
        tabla.setModel(new DefaultTableModel(new Object[][]{{}, {}, {}, {}},
                new String[]{

                }));
        tabla.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tabla.setRowHeight(30);
        tabla.setRowMargin(1);
        jScrollPane1.setViewportView(tabla);

        lblOrden.setFont(new Font("Roboto", Font.BOLD, 16)); // NOI18N
        lblOrden.setForeground(new Color(255, 255, 255));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(
                "es/xalpha/gym/vista/Bundle"); // NOI18N
        lblOrden.setText(
                bundle.getString("VerClientes.lblOrden.text")); // NOI18N
        lblOrden.setPreferredSize(new Dimension(90, 20));

        cbxOrden.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        cbxOrden.setForeground(new Color(0, 0, 0));
        cbxOrden.setPreferredSize(new Dimension(80, 30));

        txtBuscar.setMinimumSize(new Dimension(68, 30));
        txtBuscar.setPreferredSize(new Dimension(200, 30));

        btnBuscar.setBackground(new Color(0, 0, 0, 0));
        btnBuscar.setForeground(new Color(255, 255, 255));
        btnBuscar.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                        "Java\\Netbeans\\GYM\\src\\icon" +
                                        "\\buscar.png")); // NOI18N
        btnBuscar.setToolTipText("Buscar");
        btnBuscar.setColor(new Color(0, 0, 0, 0));
        btnBuscar.setColorClick(new Color(65, 72, 213));
        btnBuscar.setColorOver(new Color(71, 78, 231));
        btnBuscar.setPreferredSize(new Dimension(40, 40));

        btnAsc.setBackground(new Color(0, 0, 0, 0));
        btnAsc.setForeground(new Color(255, 255, 255));
        btnAsc.setIcon(new ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso " + "Java\\Netbeans\\GYM\\src" +
                "\\icon\\orden asc.png"));
        // NOI18N
        btnAsc.setToolTipText("Asc");
        btnAsc.setColor(new Color(0, 0, 0, 0));
        btnAsc.setColorClick(new Color(65, 72, 213));
        btnAsc.setColorOver(new Color(71, 78, 231));
        btnAsc.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAsc.setMargin(new Insets(2, 14, 2, 14));
        btnAsc.setMaximumSize(new Dimension(30, 30));
        btnAsc.setMinimumSize(new Dimension(30, 30));
        btnAsc.setPreferredSize(new Dimension(30, 30));

        btnDesc.setBackground(new Color(0, 0, 0, 0));
        btnDesc.setForeground(new Color(255, 255, 255));
        btnDesc.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                      "Java\\Netbeans\\GYM\\src\\icon\\orden " +
                                      "desc.png")); // NOI18N
        btnDesc.setToolTipText("Desc");
        btnDesc.setColor(new Color(0, 0, 0, 0));
        btnDesc.setColorClick(new Color(65, 72, 213));
        btnDesc.setColorOver(new Color(71, 78, 231));
        btnDesc.setHorizontalTextPosition(SwingConstants.CENTER);
        btnDesc.setMargin(new Insets(2, 14, 2, 14));
        btnDesc.setMaximumSize(new Dimension(30, 30));
        btnDesc.setMinimumSize(new Dimension(30, 30));
        btnDesc.setPreferredSize(new Dimension(30, 30));

        GroupLayout panelEdicionLayout = new GroupLayout(panelEdicion);
        panelEdicion.setLayout(panelEdicionLayout);
        panelEdicionLayout.setHorizontalGroup(
                panelEdicionLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelEdicionLayout.createSequentialGroup().addGap(20,
                                20, 20).addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        GroupLayout.Alignment.LEADING).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                btnBuscar,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(txtBuscar,
                                                GroupLayout.PREFERRED_SIZE, 200,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                220, 220, 220).addComponent(
                                                lblOrden,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(cbxOrden,
                                                GroupLayout.PREFERRED_SIZE, 130,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addGroup(
                                                panelEdicionLayout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING).addComponent(
                                                        btnAsc,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        btnDesc,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)).addGap(
                                                32, 67,
                                                Short.MAX_VALUE)).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addGroup(
                                                panelEdicionLayout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING).addComponent(
                                                        btnImprimir,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                                        jScrollPane1,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        760,
                                                        GroupLayout.PREFERRED_SIZE)).addGap(
                                                0, 0, Short.MAX_VALUE)))));
        panelEdicionLayout.setVerticalGroup(
                panelEdicionLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        panelEdicionLayout.createSequentialGroup().addGap(18,
                                18, 18).addGroup(
                                panelEdicionLayout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER).addComponent(
                                        lblOrden, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        cbxOrden, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addGroup(
                                        panelEdicionLayout.createSequentialGroup().addComponent(
                                                btnAsc,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE).addGap(
                                                1, 1, 1).addComponent(btnDesc,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)).addComponent(
                                        btnBuscar, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).addComponent(
                                        txtBuscar, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addGap(18,
                                18, 18).addComponent(jScrollPane1,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED, 38,
                                Short.MAX_VALUE).addComponent(btnImprimir,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addContainerGap()));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 800,
                Short.MAX_VALUE).addGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelEdicion,
                GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 500,
                Short.MAX_VALUE).addGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelEdicion,
                GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private es.xalpha.gym.vista.BotonRedondeado btnAsc;
    private es.xalpha.gym.vista.BotonRedondeado btnBuscar;
    private es.xalpha.gym.vista.BotonRedondeado btnDesc;
    private es.xalpha.gym.vista.BotonRedondeado btnImprimir;
    private JComboBox<String> cbxOrden;
    private JScrollPane jScrollPane1;
    private JLabel lblOrden;
    private JPanel panelEdicion;
    private JTable tabla;
    private JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
