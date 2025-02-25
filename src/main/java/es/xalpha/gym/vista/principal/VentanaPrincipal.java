package es.xalpha.gym.vista.principal;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import es.xalpha.gym.logica.util.ManipularArchivo;
import es.xalpha.gym.logica.util.gui.BotonRedondeado;
import es.xalpha.gym.vista.gestion.gym.GestorDeConfiguracionDeGym;
import es.xalpha.gym.vista.visualizacion.dato.VerClientes;
import es.xalpha.gym.vista.visualizacion.dato.VerFacturas;
import es.xalpha.gym.vista.gestion.cliente.EditarDatosCliente;
import es.xalpha.gym.vista.gestion.cliente.RegistrarCliente;

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

public class VentanaPrincipal extends JFrame {

    private int ejeX;
    private int ejeY;
    private JPanel panelEditarDatos;
    private JPanel panelRegistro;
    private JPanel panelVerClientes;
    private JPanel panelVerFacturas;
    private JPanel panelConfiguracion;
    private EditarDatosCliente editarDatos;
    private VerClientes verClientes;
    private VerFacturas verFacturas;
    private GestorDeConfiguracionDeGym verConfiguracion;
    private RegistrarCliente registrarCliente;
    private final ManipularArchivo archivo = new ManipularArchivo();
    private final List<JPanel> paneles = new ArrayList<>();

    public VentanaPrincipal() {
        inicioWindow();
        estilo();
        initComponents();
        inicializarPaneles();
        agregarPanel(panelRegistro);
        agregarPanel(panelEditarDatos);
        agregarPanel(panelVerClientes);
        agregarPanel(panelVerFacturas);
        agregarPanel(panelConfiguracion);
        establecerPanelesCentrales();
        setLabel(lblFondo, "src/image/gimnasio.png");
        setIconImage(new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/image/wilson.png"))).getImage());
        gestionarPanelSup();
        btnListener();
    }

    private void inicioWindow() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                crearArchivo();
                verClientes.cargarDatosEnSegundoPlano();
                verFacturas.cargarDatosEnSegundoPlano();
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

    private void inicializarPaneles() {
        verConfiguracion = new GestorDeConfiguracionDeGym();
        registrarCliente = new RegistrarCliente(this);
        verClientes = new VerClientes(this);
        editarDatos = new EditarDatosCliente(this);
        verFacturas = new VerFacturas(this);
        panelRegistro = registrarCliente.getPanelEdicion();
        panelEditarDatos = editarDatos.getPanelEdicion();
        panelVerClientes = verClientes.getPanelEdicion();
        panelVerFacturas = verFacturas.getPanelEdicion();
        panelConfiguracion = verConfiguracion.getPanelEdicion();
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
            registrarCliente.setComboBoxMembresia(getListaMembresia());
            verPanel(panelRegistro, paneles);
        });

        btnClientes.addActionListener(_ -> {
            verPanel(panelVerClientes, paneles);
            cargarDatosCliente();
        });

        btnFacturas.addActionListener(_ -> {
            verPanel(panelVerFacturas, paneles);
            cargarDatosFacturas();
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

    private void agregarPanel(JPanel panel) {
        paneles.add(panel);
    }

    public EditarDatosCliente getEditarDatos() {
        return editarDatos;
    }

    public JPanel getPanelVerClientes() {
        return panelVerClientes;
    }

    public List<JPanel> getPaneles() {
        return paneles;
    }

    public void cargarDatosCliente() {
        verClientes.cargarDatosEnSegundoPlano();
    }

    public void cargarDatosFacturas() {
        verFacturas.cargarDatosEnSegundoPlano();
    }

    private void cargarJSON(File file) {
        verConfiguracion.cargarArchivoJSON(file);
    }

    public GestorDeConfiguracionDeGym getVerConfiguracion() {
        return verConfiguracion;
    }

    public List<String> getListaMembresia() {
        return verConfiguracion.getListaMembresia();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JPanel();
        panelCentral = new javax.swing.JPanel();
        panelCapaCentral = new javax.swing.JPanel();
        panelSup = new javax.swing.JPanel();
        panelGuion = new javax.swing.JPanel();
        lblMinimizar = new javax.swing.JLabel();
        panelX = new javax.swing.JPanel();
        lblX = new javax.swing.JLabel();
        panelMenu = new javax.swing.JPanel();
        btnInicio = new BotonRedondeado();
        btnRegistro = new BotonRedondeado();
        btnClientes = new BotonRedondeado();
        btnFacturas = new BotonRedondeado();
        btnConfig = new BotonRedondeado();
        panelCapaMenu = new javax.swing.JPanel();
        lblFondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N
        setUndecorated(true);
        setResizable(false);

        panelPrincipal.setBackground(new java.awt.Color(0, 0, 0));
        panelPrincipal.setName("panelPrincipal"); // NOI18N
        panelPrincipal.setOpaque(false);
        panelPrincipal.setPreferredSize(new java.awt.Dimension(1000, 550));
        panelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCentral.setName(""); // NOI18N
        panelCentral.setOpaque(false);
        panelCentral.setPreferredSize(new java.awt.Dimension(800, 500));

        javax.swing.GroupLayout panelCentralLayout = new javax.swing.GroupLayout(panelCentral);
        panelCentral.setLayout(panelCentralLayout);
        panelCentralLayout.setHorizontalGroup(
            panelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        panelCentralLayout.setVerticalGroup(
            panelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        panelPrincipal.add(panelCentral, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 800, 500));

        panelCapaCentral.setBackground(new Color(0, 0, 0, 175));
        panelCapaCentral.setName("panelCapaCentral"); // NOI18N
        panelCapaCentral.setPreferredSize(new java.awt.Dimension(600, 500));

        javax.swing.GroupLayout panelCapaCentralLayout = new javax.swing.GroupLayout(panelCapaCentral);
        panelCapaCentral.setLayout(panelCapaCentralLayout);
        panelCapaCentralLayout.setHorizontalGroup(
            panelCapaCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 790, Short.MAX_VALUE)
        );
        panelCapaCentralLayout.setVerticalGroup(
            panelCapaCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        panelPrincipal.add(panelCapaCentral, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 790, -1));

        panelSup.setBackground(new java.awt.Color(0, 0, 0));
        panelSup.setForeground(new java.awt.Color(0, 0, 0));
        panelSup.setName("panelSup"); // NOI18N
        panelSup.setPreferredSize(new java.awt.Dimension(1000, 30));

        panelGuion.setBackground(new Color(0, 0, 0, 175));
        panelGuion.setMaximumSize(new java.awt.Dimension(18, 18));
        panelGuion.setMinimumSize(new java.awt.Dimension(18, 18));
        panelGuion.setName("panelGuion"); // NOI18N
        panelGuion.setPreferredSize(new java.awt.Dimension(18, 18));

        lblMinimizar.setBackground(new java.awt.Color(255, 255, 255));
        lblMinimizar.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\minimizar.png")); // NOI18N
        lblMinimizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblMinimizar.setName("lblMinimizar"); // NOI18N

        javax.swing.GroupLayout panelGuionLayout = new javax.swing.GroupLayout(panelGuion);
        panelGuion.setLayout(panelGuionLayout);
        panelGuionLayout.setHorizontalGroup(
            panelGuionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGuionLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblMinimizar))
        );
        panelGuionLayout.setVerticalGroup(
            panelGuionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGuionLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblMinimizar))
        );

        panelX.setBackground(new Color(0, 0, 0, 175));
        panelX.setForeground(new java.awt.Color(255, 255, 255));
        panelX.setMaximumSize(new java.awt.Dimension(18, 18));
        panelX.setMinimumSize(new java.awt.Dimension(18, 18));
        panelX.setName("panelX"); // NOI18N
        panelX.setPreferredSize(new java.awt.Dimension(18, 18));

        lblX.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\cerrar.png")); // NOI18N
        lblX.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblX.setName("lblX"); // NOI18N

        javax.swing.GroupLayout panelXLayout = new javax.swing.GroupLayout(panelX);
        panelX.setLayout(panelXLayout);
        panelXLayout.setHorizontalGroup(
            panelXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelXLayout.createSequentialGroup()
                .addComponent(lblX, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelXLayout.setVerticalGroup(
            panelXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelXLayout.createSequentialGroup()
                .addComponent(lblX)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelSupLayout = new javax.swing.GroupLayout(panelSup);
        panelSup.setLayout(panelSupLayout);
        panelSupLayout.setHorizontalGroup(
            panelSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSupLayout.createSequentialGroup()
                .addContainerGap(946, Short.MAX_VALUE)
                .addComponent(panelGuion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelSupLayout.setVerticalGroup(
            panelSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSupLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelGuion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(125, 125, 125))
        );

        panelPrincipal.add(panelSup, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        panelMenu.setBackground(new java.awt.Color(80, 80, 80));
        panelMenu.setName("panelMenu"); // NOI18N
        panelMenu.setOpaque(false);
        panelMenu.setPreferredSize(new java.awt.Dimension(172, 400));

        btnInicio.setBackground(new Color(0, 0, 0, 0));
        btnInicio.setForeground(new java.awt.Color(255, 255, 255));
        btnInicio.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\Home.png")); // NOI18N
        btnInicio.setText("Inicio");
        btnInicio.setColor(new Color(0, 0, 0, 0));
        btnInicio.setColorClick(new java.awt.Color(228, 205, 59));
        btnInicio.setColorOver(new java.awt.Color(244, 219, 64));
        btnInicio.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        btnInicio.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInicio.setIconTextGap(10);
        btnInicio.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnInicio.setMaximumSize(new java.awt.Dimension(100, 40));
        btnInicio.setMinimumSize(new java.awt.Dimension(170, 40));
        btnInicio.setName("btnInicio"); // NOI18N
        btnInicio.setPreferredSize(new java.awt.Dimension(170, 40));

        btnRegistro.setBackground(new Color(0, 0, 0, 0));
        btnRegistro.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistro.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\registrar.png")); // NOI18N
        btnRegistro.setText("Registrar");
        btnRegistro.setColor(new Color(0, 0, 0, 0));
        btnRegistro.setColorClick(new java.awt.Color(10, 193, 18));
        btnRegistro.setColorOver(new java.awt.Color(15, 225, 24));
        btnRegistro.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        btnRegistro.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRegistro.setIconTextGap(10);
        btnRegistro.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnRegistro.setMaximumSize(new java.awt.Dimension(100, 40));
        btnRegistro.setMinimumSize(new java.awt.Dimension(170, 40));
        btnRegistro.setName("btnRegistro"); // NOI18N
        btnRegistro.setPreferredSize(new java.awt.Dimension(170, 40));

        btnClientes.setBackground(new Color(0, 0, 0, 0));
        btnClientes.setForeground(new java.awt.Color(255, 255, 255));
        btnClientes.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\ver.png")); // NOI18N
        btnClientes.setText("Clientes");
        btnClientes.setColor(new Color(0, 0, 0, 0));
        btnClientes.setColorClick(new java.awt.Color(183, 51, 216));
        btnClientes.setColorOver(new java.awt.Color(207, 60, 243));
        btnClientes.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        btnClientes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnClientes.setIconTextGap(10);
        btnClientes.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnClientes.setMaximumSize(new java.awt.Dimension(100, 40));
        btnClientes.setMinimumSize(new java.awt.Dimension(170, 40));
        btnClientes.setName("btnClientes"); // NOI18N
        btnClientes.setPreferredSize(new java.awt.Dimension(170, 40));

        btnFacturas.setBackground(new Color(0, 0, 0, 0));
        btnFacturas.setForeground(new java.awt.Color(255, 255, 255));
        btnFacturas.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\factura.png")); // NOI18N
        btnFacturas.setText("Facturas");
        btnFacturas.setColor(new Color(0, 0, 0, 0));
        btnFacturas.setColorClick(new java.awt.Color(232, 163, 42));
        btnFacturas.setColorOver(new java.awt.Color(249, 175, 45));
        btnFacturas.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        btnFacturas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFacturas.setIconTextGap(10);
        btnFacturas.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnFacturas.setMaximumSize(new java.awt.Dimension(100, 40));
        btnFacturas.setMinimumSize(new java.awt.Dimension(170, 40));
        btnFacturas.setName("btnFacturas"); // NOI18N
        btnFacturas.setPreferredSize(new java.awt.Dimension(170, 40));

        btnConfig.setBackground(new Color(0, 0, 0, 0));
        btnConfig.setForeground(new java.awt.Color(255, 255, 255));
        btnConfig.setIcon(new javax.swing.ImageIcon("D:\\Ale\\Mis Cursos\\Curso Java\\Netbeans\\Proyecto Wilson Gimnasio\\src\\icon\\configuracion.png")); // NOI18N
        btnConfig.setText("Configuracion");
        btnConfig.setColor(new Color(0, 0, 0, 0));
        btnConfig.setColorClick(new java.awt.Color(60, 72, 219));
        btnConfig.setColorOver(new java.awt.Color(70, 83, 246));
        btnConfig.setFont(new java.awt.Font("Roboto", Font.PLAIN, 14)); // NOI18N
        btnConfig.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnConfig.setIconTextGap(10);
        btnConfig.setMargin(new java.awt.Insets(2, 14, 2, 14));
        btnConfig.setMaximumSize(new java.awt.Dimension(100, 40));
        btnConfig.setMinimumSize(new java.awt.Dimension(170, 40));
        btnConfig.setName("btnConfig"); // NOI18N
        btnConfig.setPreferredSize(new java.awt.Dimension(170, 40));

        javax.swing.GroupLayout panelMenuLayout = new javax.swing.GroupLayout(panelMenu);
        panelMenu.setLayout(panelMenuLayout);
        panelMenuLayout.setHorizontalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(btnInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConfig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelMenuLayout.setVerticalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createSequentialGroup()
                .addGap(134, 134, 134)
                .addComponent(btnInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(162, Short.MAX_VALUE))
        );

        panelPrincipal.add(panelMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1, 520));

        panelCapaMenu.setBackground(new Color(0, 0, 0, 175));
        panelCapaMenu.setName("panelCapaMenu"); // NOI18N
        panelCapaMenu.setPreferredSize(new java.awt.Dimension(172, 400));

        javax.swing.GroupLayout panelCapaMenuLayout = new javax.swing.GroupLayout(panelCapaMenu);
        panelCapaMenu.setLayout(panelCapaMenuLayout);
        panelCapaMenuLayout.setHorizontalGroup(
            panelCapaMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );
        panelCapaMenuLayout.setVerticalGroup(
            panelCapaMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 520, Short.MAX_VALUE)
        );

        panelPrincipal.add(panelCapaMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1, 520));

        lblFondo.setName("lblFondo"); // NOI18N
        lblFondo.setPreferredSize(new java.awt.Dimension(1000, 550));
        panelPrincipal.add(lblFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BotonRedondeado btnClientes;
    private BotonRedondeado btnConfig;
    private BotonRedondeado btnFacturas;
    private BotonRedondeado btnInicio;
    private BotonRedondeado btnRegistro;
    private javax.swing.JLabel lblFondo;
    private javax.swing.JLabel lblMinimizar;
    private javax.swing.JLabel lblX;
    private javax.swing.JPanel panelCapaCentral;
    private javax.swing.JPanel panelCapaMenu;
    private javax.swing.JPanel panelCentral;
    private javax.swing.JPanel panelGuion;
    private javax.swing.JPanel panelMenu;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelSup;
    private javax.swing.JPanel panelX;
    // End of variables declaration//GEN-END:variables
}
