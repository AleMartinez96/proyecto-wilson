package es.xalpha.gym.logica.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class ManejoArchivo {

    private String path;
    private static final String PROPERTIES_FILE = "config.properties";
    private final Properties properties;
    private final String PATH_DEFAULT;

    public ManejoArchivo() {
        this.properties = new Properties();
        PATH_DEFAULT = pathDefault("config", ".json");
        path = cargarPathDeProperties();
    }

    public String cargarPathDeProperties() {
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

    private String leerProperties() {
        try (FileInputStream inputStream = new FileInputStream(
                PROPERTIES_FILE)) {
            properties.load(inputStream);
            return properties.getProperty("path", PATH_DEFAULT);
        } catch (IOException e) {
            return PATH_DEFAULT;
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void guardarPathEnProperties() {
        properties.setProperty("path", path);
        try (FileOutputStream outputStream = new FileOutputStream(
                PROPERTIES_FILE)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPath() {
        return path;
    }

    public void actualizarArchivo(String path, String tipoArchivo,
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

    public static String obtenerPath(String ruta, String tipoArchivo,
                                     String extension) {
        JFileChooser chooser = new JFileChooser();
        String nuevoPath = "";
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos " + tipoArchivo, extension);
        chooser.setDialogTitle("Especificar ubicación para guardar el archivo");
        chooser.setFileFilter(filter);
        String texto = ruta != null && !ruta.isEmpty() ? ruta :
                "nuevo archivo" + extension;
        chooser.setSelectedFile(new File(texto));
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            nuevoPath = chooser.getSelectedFile().getAbsolutePath();
        }
        return !nuevoPath.endsWith(extension) ?
                nuevoPath + extension : nuevoPath;
    }

    public File getFile() {
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

    public static @NotNull String pathDefault(String nombre, String extension) {
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