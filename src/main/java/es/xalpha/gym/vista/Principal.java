package es.xalpha.gym.vista;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import es.xalpha.gym.contoladora.ControladoraLogica;
import es.xalpha.gym.logica.util.ManejoArchivo;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Principal extends JFrame {

    private int ejeX;
    private int ejeY;
    private final ControladoraLogica controller;
    private final JPanel panelEditarDatos;
    private final JPanel panelRegistro;
    private final JPanel panelVerClientes;
    private final JPanel panelVerFacturas;
    private final JPanel panelConfiguracion;
    private final List<JPanel> paneles = new ArrayList<>();
    private final EditarDatos editarDatos;
    private final VerClientes verClientes;
    private final VerFacturas verFacturas;
    private final VerConfiguracion verConfiguracion;
    private final RegistrarCliente registrarCliente;
    private final ManejoArchivo archivo;

    public Principal() {
        inicioWindow();
        estilo();
        initComponents();
        this.controller = new ControladoraLogica();
        archivo = new ManejoArchivo();
        verConfiguracion = new VerConfiguracion(this);
        registrarCliente = new RegistrarCliente(controller, this);
        verClientes = new VerClientes(controller, this);
        editarDatos = new EditarDatos(controller, this);
        verFacturas = new VerFacturas(controller, this);
        panelRegistro = registrarCliente.getPanelEdicion();
        panelEditarDatos = editarDatos.getPanelEdicion();
        panelVerClientes = verClientes.getPanelEdicion();
        panelVerFacturas = verFacturas.getPanelEdicion();
        panelConfiguracion = verConfiguracion.getPanelEdicion();
        agregarPanel(panelRegistro);
        agregarPanel(panelEditarDatos);
        agregarPanel(panelVerClientes);
        agregarPanel(panelVerFacturas);
        agregarPanel(panelConfiguracion);
        establecerPanelesCentrales();
        setLabel(lblFondo, "src/image/gimnasio.png");
        setIconImage(new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/wilson.png"))).getImage());
        gestionarPanelSup();
        btnListener();
    }

    private void inicioWindow() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                crearArchivo();
                cargarDatosCliente();
                cargarDatosFactura();
            }
        });
    }

    private void crearArchivo() {
        File file;
        try {
            file = archivo.crearArchivo();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cargarJSON(file);
    }

    public ManejoArchivo getArchivo() {
        return archivo;
    }

    public static void estilo() {
        FlatMacLightLaf.setup();
        UIManager.put("Button.arc", 40);
        UIManager.put("Component.arc", 40);
        UIManager.put("TextComponent.arc", 40);
        UIManager.put("ToolTip.font",
                new FontUIResource("Roboto", Font.ITALIC, 14));
        UIManager.put("PopupMenu.background", new Color(124, 252, 229));
    }

    private void gestionarPanelSup() {
        panelSupListener();
        gestionarLabel();
    }

    private void panelSupListener() {
        panelSup.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ejeX = e.getX();
                ejeY = e.getY();
            }
        });

        panelSup.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x - ejeX, y - ejeY);
            }
        });
    }

    private void gestionarLabel() {
        lblX.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    System.exit(0);
                }
            }
        });
        labelListener(lblX, panelX, Color.red, Color.black);
        lblMinimizar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    setState(ICONIFIED);
                }
            }
        });
        labelListener(lblMinimizar, panelGuion, new Color(50, 50, 50),
                Color.black);
    }

    private void labelListener(JLabel label, JPanel panel, Color entrada,
                               Color salida) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(entrada);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(salida);
            }
        });
    }

    private void btnListener() {
        btnInicio.addActionListener(_ -> {
            ocultarPaneles(paneles);
            panelCentral.setVisible(false);
            panelCapaCentral.setVisible(false);
        });

        btnRegistro.addActionListener(_ -> {
            registrarCliente.setComboBox(getListaMembresia());
            verPanel(panelRegistro, paneles);
        });

        btnClientes.addActionListener(_ -> {
            verPanel(panelVerClientes, paneles);
            cargarDatosCliente();
        });

        btnFacturas.addActionListener(_ -> {
            verPanel(panelVerFacturas, paneles);
            cargarDatosFactura();
        });

        btnConfig.addActionListener(_ -> verPanel(panelConfiguracion, paneles));
    }

    private void setLabel(JLabel label, String ruta) {
        ImageIcon image = new ImageIcon(ruta);
        Icon icon = new ImageIcon(
                image.getImage().getScaledInstance(label.getWidth(),
                        label.getHeight(), Image.SCALE_DEFAULT));
        label.setIcon(icon);
        repaint();
    }

    public void verPanel(JPanel panel, List<JPanel> paneles) {
        if (!panel.isVisible()) {
            ocultarPaneles(paneles);
            panel.setVisible(true);
            panelCapaCentral.setVisible(true);
            panelCentral.setVisible(true);
        }
    }

    public static void ocultarPaneles(List<JPanel> paneles) {
        paneles.stream().filter(Component::isVisible).forEach(
                panel -> panel.setVisible(false));
    }

    private void establecerPanelesCentrales() {
        int width = panelCentral.getWidth();
        int height = panelCentral.getHeight();
        paneles.forEach(panel -> {
            panel.setSize(width, height);
            panel.setVisible(false);
            panelCentral.add(panel);
        });
        panelCentral.setVisible(false);
        panelCapaCentral.setVisible(false);
    }

    public void cargarDatosCliente() {
        verClientes.cargarDatosCliente(controller.getListaClientes());
    }

    public void cargarDatosFactura() {
        verFacturas.cargarDatosFactura(controller.getListaFacturas());
    }

    private void agregarPanel(JPanel panel) {
        paneles.add(panel);
    }

    public EditarDatos getEditarDatos() {
        return editarDatos;
    }

    public JPanel getPanelVerClientes() {
        return panelVerClientes;
    }

    public List<JPanel> getPaneles() {
        return paneles;
    }

    private void cargarJSON(File file) {
        verConfiguracion.cargarJSON(file);
    }

    public VerConfiguracion getVerConfiguracion() {
        return verConfiguracion;
    }

    public String getNombreLocal() {
        return verConfiguracion.getNombreLocal();
    }

    public List<String> getListaMembresia() {
        return verConfiguracion.getListaMembresia();
    }

    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new JPanel();
        panelCentral = new JPanel();
        panelCapaCentral = new JPanel();
        panelSup = new JPanel();
        panelGuion = new JPanel();
        lblMinimizar = new JLabel();
        panelX = new JPanel();
        lblX = new JLabel();
        panelMenu = new JPanel();
        btnInicio = new es.xalpha.gym.vista.BotonRedondeado();
        btnRegistro = new es.xalpha.gym.vista.BotonRedondeado();
        btnClientes = new es.xalpha.gym.vista.BotonRedondeado();
        btnFacturas = new es.xalpha.gym.vista.BotonRedondeado();
        btnConfig = new es.xalpha.gym.vista.BotonRedondeado();
        panelCapaMenu = new JPanel();
        lblFondo = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N
        setUndecorated(true);
        setResizable(false);

        panelPrincipal.setBackground(new Color(0, 0, 0));
        panelPrincipal.setName("panelPrincipal"); // NOI18N
        panelPrincipal.setOpaque(false);
        panelPrincipal.setPreferredSize(new Dimension(1000, 550));
        panelPrincipal.setLayout(
                new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCentral.setName(""); // NOI18N
        panelCentral.setOpaque(false);
        panelCentral.setPreferredSize(new Dimension(800, 500));

        GroupLayout panelCentralLayout = new GroupLayout(panelCentral);
        panelCentral.setLayout(panelCentralLayout);
        panelCentralLayout.setHorizontalGroup(
                panelCentralLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGap(0, 800,
                        Short.MAX_VALUE));
        panelCentralLayout.setVerticalGroup(
                panelCentralLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGap(0, 500,
                        Short.MAX_VALUE));

        panelPrincipal.add(panelCentral,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 800,
                        500));

        panelCapaCentral.setBackground(new Color(0, 0, 0, 175));
        panelCapaCentral.setName("panelCapaCentral"); // NOI18N
        panelCapaCentral.setPreferredSize(new Dimension(600, 500));

        GroupLayout panelCapaCentralLayout = new GroupLayout(panelCapaCentral);
        panelCapaCentral.setLayout(panelCapaCentralLayout);
        panelCapaCentralLayout.setHorizontalGroup(
                panelCapaCentralLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGap(0, 790,
                        Short.MAX_VALUE));
        panelCapaCentralLayout.setVerticalGroup(
                panelCapaCentralLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGap(0, 500,
                        Short.MAX_VALUE));

        panelPrincipal.add(panelCapaCentral,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 790,
                        -1));

        panelSup.setBackground(new Color(0, 0, 0));
        panelSup.setForeground(new Color(0, 0, 0));
        panelSup.setName("panelSup"); // NOI18N
        panelSup.setPreferredSize(new Dimension(1000, 30));

        panelGuion.setBackground(new Color(0, 0, 0, 175));
        panelGuion.setMaximumSize(new Dimension(18, 18));
        panelGuion.setMinimumSize(new Dimension(18, 18));
        panelGuion.setName("panelGuion"); // NOI18N
        panelGuion.setPreferredSize(new Dimension(18, 18));

        lblMinimizar.setBackground(new Color(255, 255, 255));
        lblMinimizar.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                           "Java\\Netbeans\\GYM\\src\\icon" +
                                           "\\minimizar.png")); // NOI18N
        lblMinimizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblMinimizar.setName("lblMinimizar"); // NOI18N

        GroupLayout panelGuionLayout = new GroupLayout(panelGuion);
        panelGuion.setLayout(panelGuionLayout);
        panelGuionLayout.setHorizontalGroup(
                panelGuionLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGroup(
                        GroupLayout.Alignment.TRAILING,
                        panelGuionLayout.createSequentialGroup().addGap(0, 0,
                                Short.MAX_VALUE).addComponent(lblMinimizar)));
        panelGuionLayout.setVerticalGroup(panelGuionLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                GroupLayout.Alignment.TRAILING,
                panelGuionLayout.createSequentialGroup().addGap(0, 0,
                        Short.MAX_VALUE).addComponent(lblMinimizar)));

        panelX.setBackground(new Color(0, 0, 0, 175));
        panelX.setForeground(new Color(255, 255, 255));
        panelX.setMaximumSize(new Dimension(18, 18));
        panelX.setMinimumSize(new Dimension(18, 18));
        panelX.setName("panelX"); // NOI18N
        panelX.setPreferredSize(new Dimension(18, 18));

        lblX.setIcon(new ImageIcon(
                "D:\\Ale\\Mis Cursos\\Curso " + "Java\\Netbeans\\GYM\\src" +
                "\\icon\\cerrar.png")); // NOI18N
        lblX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblX.setName("lblX"); // NOI18N

        GroupLayout panelXLayout = new GroupLayout(panelX);
        panelX.setLayout(panelXLayout);
        panelXLayout.setHorizontalGroup(panelXLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                panelXLayout.createSequentialGroup().addComponent(lblX,
                        GroupLayout.PREFERRED_SIZE, 18,
                        GroupLayout.PREFERRED_SIZE).addGap(0, 0,
                        Short.MAX_VALUE)));
        panelXLayout.setVerticalGroup(panelXLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                panelXLayout.createSequentialGroup().addComponent(lblX).addGap(
                        0, 0, Short.MAX_VALUE)));

        GroupLayout panelSupLayout = new GroupLayout(panelSup);
        panelSup.setLayout(panelSupLayout);
        panelSupLayout.setHorizontalGroup(panelSupLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                GroupLayout.Alignment.TRAILING,
                panelSupLayout.createSequentialGroup().addContainerGap(946,
                        Short.MAX_VALUE).addComponent(panelGuion,
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE).addPreferredGap(
                        LayoutStyle.ComponentPlacement.UNRELATED).addComponent(
                        panelX, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE).addContainerGap()));
        panelSupLayout.setVerticalGroup(panelSupLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                GroupLayout.Alignment.TRAILING,
                panelSupLayout.createSequentialGroup().addContainerGap(
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
                        panelSupLayout.createParallelGroup(
                                GroupLayout.Alignment.LEADING).addComponent(
                                panelX, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addComponent(
                                panelGuion, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)).addGap(125, 125,
                        125)));

        panelPrincipal.add(panelSup,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1,
                        -1));

        panelMenu.setBackground(new Color(80, 80, 80));
        panelMenu.setName("panelMenu"); // NOI18N
        panelMenu.setOpaque(false);
        panelMenu.setPreferredSize(new Dimension(172, 400));

        btnInicio.setBackground(new Color(0, 0, 0, 0));
        btnInicio.setForeground(new Color(255, 255, 255));
        btnInicio.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                        "Java\\Netbeans\\GYM\\src\\icon\\Home" +
                                        ".png")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(
                "es/xalpha/gym/vista/Bundle"); // NOI18N
        btnInicio.setText(
                bundle.getString("Principal.btnInicio.text")); // NOI18N
        btnInicio.setColor(new Color(0, 0, 0, 0));
        btnInicio.setColorClick(new Color(228, 205, 59));
        btnInicio.setColorOver(new Color(244, 219, 64));
        btnInicio.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        btnInicio.setHorizontalAlignment(SwingConstants.LEFT);
        btnInicio.setIconTextGap(10);
        btnInicio.setMargin(new Insets(2, 14, 2, 14));
        btnInicio.setMaximumSize(new Dimension(100, 40));
        btnInicio.setMinimumSize(new Dimension(170, 40));
        btnInicio.setName("btnInicio"); // NOI18N
        btnInicio.setPreferredSize(new Dimension(170, 40));

        btnRegistro.setBackground(new Color(0, 0, 0, 0));
        btnRegistro.setForeground(new Color(255, 255, 255));
        btnRegistro.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                          "Java\\Netbeans\\GYM\\src\\icon" +
                                          "\\registrar.png")); // NOI18N
        btnRegistro.setText(
                bundle.getString("Principal.btnRegistro.text")); // NOI18N
        btnRegistro.setColor(new Color(0, 0, 0, 0));
        btnRegistro.setColorClick(new Color(10, 193, 18));
        btnRegistro.setColorOver(new Color(15, 225, 24));
        btnRegistro.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        btnRegistro.setHorizontalAlignment(SwingConstants.LEFT);
        btnRegistro.setIconTextGap(10);
        btnRegistro.setMargin(new Insets(2, 14, 2, 14));
        btnRegistro.setMaximumSize(new Dimension(100, 40));
        btnRegistro.setMinimumSize(new Dimension(170, 40));
        btnRegistro.setName("btnRegistro"); // NOI18N
        btnRegistro.setPreferredSize(new Dimension(170, 40));

        btnClientes.setBackground(new Color(0, 0, 0, 0));
        btnClientes.setForeground(new Color(255, 255, 255));
        btnClientes.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                          "Java\\Netbeans\\GYM\\src\\icon" +
                                          "\\ver.png")); // NOI18N
        btnClientes.setText(
                bundle.getString("Principal.btnClientes.text")); // NOI18N
        btnClientes.setColor(new Color(0, 0, 0, 0));
        btnClientes.setColorClick(new Color(183, 51, 216));
        btnClientes.setColorOver(new Color(207, 60, 243));
        btnClientes.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        btnClientes.setHorizontalAlignment(SwingConstants.LEFT);
        btnClientes.setIconTextGap(10);
        btnClientes.setMargin(new Insets(2, 14, 2, 14));
        btnClientes.setMaximumSize(new Dimension(100, 40));
        btnClientes.setMinimumSize(new Dimension(170, 40));
        btnClientes.setName("btnClientes"); // NOI18N
        btnClientes.setPreferredSize(new Dimension(170, 40));

        btnFacturas.setBackground(new Color(0, 0, 0, 0));
        btnFacturas.setForeground(new Color(255, 255, 255));
        btnFacturas.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                          "Java\\Netbeans\\GYM\\src\\icon" +
                                          "\\factura.png")); // NOI18N
        btnFacturas.setText(
                bundle.getString("Principal.btnFacturas.text")); // NOI18N
        btnFacturas.setColor(new Color(0, 0, 0, 0));
        btnFacturas.setColorClick(new Color(232, 163, 42));
        btnFacturas.setColorOver(new Color(249, 175, 45));
        btnFacturas.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        btnFacturas.setHorizontalAlignment(SwingConstants.LEFT);
        btnFacturas.setIconTextGap(10);
        btnFacturas.setMargin(new Insets(2, 14, 2, 14));
        btnFacturas.setMaximumSize(new Dimension(100, 40));
        btnFacturas.setMinimumSize(new Dimension(170, 40));
        btnFacturas.setName("btnFacturas"); // NOI18N
        btnFacturas.setPreferredSize(new Dimension(170, 40));

        btnConfig.setBackground(new Color(0, 0, 0, 0));
        btnConfig.setForeground(new Color(255, 255, 255));
        btnConfig.setIcon(new ImageIcon("D:\\Ale\\Mis Cursos\\Curso " +
                                        "Java\\Netbeans\\GYM\\src\\icon" +
                                        "\\configuracion.png")); // NOI18N
        btnConfig.setText(
                bundle.getString("Principal.btnConfig.text")); // NOI18N
        btnConfig.setColor(new Color(0, 0, 0, 0));
        btnConfig.setColorClick(new Color(60, 72, 219));
        btnConfig.setColorOver(new Color(70, 83, 246));
        btnConfig.setFont(new Font("Roboto", Font.BOLD, 14)); // NOI18N
        btnConfig.setHorizontalAlignment(SwingConstants.LEFT);
        btnConfig.setIconTextGap(10);
        btnConfig.setMargin(new Insets(2, 14, 2, 14));
        btnConfig.setMaximumSize(new Dimension(100, 40));
        btnConfig.setMinimumSize(new Dimension(170, 40));
        btnConfig.setName("btnConfig"); // NOI18N
        btnConfig.setPreferredSize(new Dimension(170, 40));

        GroupLayout panelMenuLayout = new GroupLayout(panelMenu);
        panelMenu.setLayout(panelMenuLayout);
        panelMenuLayout.setHorizontalGroup(panelMenuLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                panelMenuLayout.createParallelGroup(
                        GroupLayout.Alignment.CENTER).addComponent(btnInicio,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(btnRegistro,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(btnClientes,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(btnFacturas,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(btnConfig,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)));
        panelMenuLayout.setVerticalGroup(panelMenuLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                panelMenuLayout.createSequentialGroup().addGap(134, 134,
                        134).addComponent(btnInicio, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE).addPreferredGap(
                        LayoutStyle.ComponentPlacement.RELATED).addComponent(
                        btnRegistro, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE).addPreferredGap(
                        LayoutStyle.ComponentPlacement.RELATED).addComponent(
                        btnClientes, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE).addPreferredGap(
                        LayoutStyle.ComponentPlacement.RELATED).addComponent(
                        btnFacturas, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE).addPreferredGap(
                        LayoutStyle.ComponentPlacement.RELATED).addComponent(
                        btnConfig, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE).addContainerGap(162,
                        Short.MAX_VALUE)));

        panelPrincipal.add(panelMenu,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1,
                        520));

        panelCapaMenu.setBackground(new Color(0, 0, 0, 175));
        panelCapaMenu.setName("panelCapaMenu"); // NOI18N
        panelCapaMenu.setPreferredSize(new Dimension(172, 400));

        GroupLayout panelCapaMenuLayout = new GroupLayout(panelCapaMenu);
        panelCapaMenu.setLayout(panelCapaMenuLayout);
        panelCapaMenuLayout.setHorizontalGroup(
                panelCapaMenuLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGap(0, 172,
                        Short.MAX_VALUE));
        panelCapaMenuLayout.setVerticalGroup(
                panelCapaMenuLayout.createParallelGroup(
                        GroupLayout.Alignment.LEADING).addGap(0, 520,
                        Short.MAX_VALUE));

        panelPrincipal.add(panelCapaMenu,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1,
                        520));

        lblFondo.setName("lblFondo"); // NOI18N
        lblFondo.setPreferredSize(new Dimension(1000, 550));
        panelPrincipal.add(lblFondo,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1,
                        -1));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelPrincipal,
                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(panelPrincipal,
                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private es.xalpha.gym.vista.BotonRedondeado btnClientes;
    private es.xalpha.gym.vista.BotonRedondeado btnConfig;
    private es.xalpha.gym.vista.BotonRedondeado btnFacturas;
    private es.xalpha.gym.vista.BotonRedondeado btnInicio;
    private es.xalpha.gym.vista.BotonRedondeado btnRegistro;
    private JLabel lblFondo;
    private JLabel lblMinimizar;
    private JLabel lblX;
    private JPanel panelCapaCentral;
    private JPanel panelCapaMenu;
    private JPanel panelCentral;
    private JPanel panelGuion;
    private JPanel panelMenu;
    private JPanel panelPrincipal;
    private JPanel panelSup;
    private JPanel panelX;
    // End of variables declaration//GEN-END:variables
}
