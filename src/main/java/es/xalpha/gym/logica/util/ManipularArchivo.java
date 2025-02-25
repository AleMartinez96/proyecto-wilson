package es.xalpha.gym.logica.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.xalpha.gym.logica.util.gui.UtilGUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class ManipularArchivo {

    private static String path;
    private static final String PROPERTIES_FILE = "config.properties";
    private static final Properties properties = new Properties();
    private static String PATH_DEFAULT;

    public ManipularArchivo() {
        PATH_DEFAULT = pathDefault("config", ".json");
        path = cargarPathDeProperties();
    }

    public static String cargarPathDeProperties() {
        File archivo = new File(PROPERTIES_FILE);
        try {
            if (!archivo.exists() && archivo.createNewFile()) {
                path = PATH_DEFAULT;
                properties.setProperty("path", PATH_DEFAULT);
                guardarPathEnProperties();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return leerProperties();
    }

    private static String leerProperties() {
        try (FileInputStream inputStream = new FileInputStream(
                PROPERTIES_FILE)) {
            properties.load(inputStream);
            return properties.getProperty("path", PATH_DEFAULT);
        } catch (IOException e) {
            return PATH_DEFAULT;
        }
    }

    public static void setPath(String path) {
        ManipularArchivo.path = path;
    }

    private static void guardarPathEnProperties() {
        properties.setProperty("path", path);
        try (FileOutputStream outputStream = new FileOutputStream(
                PROPERTIES_FILE)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void actualizarArchivo(String path, String tipoArchivo,
                                          String extension) throws IOException {
        if (path == null || path.isEmpty()) {
            path = PATH_DEFAULT;
        }
        String nuevoPath = obtenerPath(path, tipoArchivo, extension);
        if (!nuevoPath.trim().isEmpty() && !nuevoPath.equals(extension)) {
            Files.move(Paths.get(path), Paths.get(nuevoPath),
                    StandardCopyOption.REPLACE_EXISTING);
            setPath(nuevoPath);
            guardarPathEnProperties();
        }
    }

    public static void guardarArchivo(Configuracion configuracion) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(ManipularArchivo.getFile(), configuracion);
            UtilGUI.mensaje("Los datos se han guardado correctamente.",
                    "Actualizacion exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            UtilGUI.mensaje(
                    "Error al guardar la configuración: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String obtenerPath(String ruta, String tipoArchivo,
                                     String extension) {
        String nuevoPath = "";
        JFileChooser chooser = abrirJFileChooser(ruta, tipoArchivo, extension);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            nuevoPath = chooser.getSelectedFile().getAbsolutePath();
        }
        return !nuevoPath.endsWith(extension) ?
                nuevoPath + extension : nuevoPath;
    }

    public static void verificarUbicacionDelArchivo() throws IOException {
        String anterior = path;
        actualizarArchivo(anterior, "Json (*.json)", ".json");
        String nuevo = path;
        String[] pathAnterior = anterior.split("\\\\");
        String[] pathNuevo = nuevo.split("\\\\");
        if (pathAnterior.length != pathNuevo.length) {
            UtilGUI.mensaje("El archivo ha sido movido con éxito.",
                    "Ubicación actualizada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            UtilGUI.mensaje("La ubicación del archivo no ha cambiado.",
                    "Sin cambios", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static JFileChooser abrirJFileChooser(String ruta,
                                                  String tipoArchivo,
                                                  String extension) {
        String texto = ruta != null && !ruta.isEmpty() ? ruta :
                "nuevo archivo" + extension;
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos " + tipoArchivo, extension);
        chooser.setDialogTitle("Especificar ubicación para guardar el archivo");
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(new File(texto));
        return chooser;
    }

    public static File getFile() {
        Path ruta = Paths.get(path);
        return ruta.toFile();
    }

    public File crearArchivo() throws IOException {
        Path ruta = Paths.get(path);
        if (!Files.exists(ruta)) {
            return Files.createFile(ruta).toFile();
        }
        return ruta.toFile();
    }

    public static String pathDefault(String nombre, String extension) {
        String path;
        String userHome = System.getProperty("user.home");
        String oneDrive = System.getenv("OneDrive");
        String nombreArchivo = nombre + extension;
        if (oneDrive == null || oneDrive.isEmpty()) {
            path = userHome + "\\Desktop";
        } else {
            path = oneDrive + "\\Desktop";
        }
        return path + "\\" + nombreArchivo;
    }
}