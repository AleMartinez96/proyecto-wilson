package es.xalpha.gym.logica.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    private static final Set<String> DOMINIOS_GLOBALES = Set.of("gmail.com",
            "hotmail.com", "hotmail.es", "outlook.com", "outlook.es",
            "live.com", "live.es", "msn.com", "yahoo.com", "yahoo.es",
            "ymail.com", "icloud.com", "me.com", "mac.com", "protonmail.com",
            "zoho.com", "aol.com", "mail.com", "gmx.com");

    public static String capitalizarNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return nombre;
        }
        return Arrays.stream(nombre.split("\\s+")).map(
                palabra -> palabra.substring(0, 1).toUpperCase() +
                           palabra.substring(1).toLowerCase()).collect(
                Collectors.joining(" "));
    }

    public static @NotNull String formatoFecha(LocalDate date, String formato) {
        if (date == null || formato == null || formato.isEmpty()) {
            throw new IllegalArgumentException("Fecha o patrón inválido");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
        return formatter.format(date);
    }

    public static LocalDate obtenerLocalDate(Date date) {
        return date == null ? LocalDate.now() : date.toInstant().atZone(
                ZoneId.systemDefault()).toLocalDate();
    }

    public static void mensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipoMensaje);
    }

    public static Integer opcion(String mensaje, String titulo,
                                 int tipoOpcion) {
        return JOptionPane.showConfirmDialog(null, mensaje, titulo, tipoOpcion);
    }

    public static Date obtenerDate(LocalDate localDate) {
        return localDate == null ? new Date() : Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static boolean esEmailValido(String email) {
        Pattern pattern = Pattern.compile(
                "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)" +
                "{1,3}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches() && email.length() >= 10 &&
               dominioValido(email);
    }

    private static boolean dominioValido(String email) {
        String dominio = email.substring(email.indexOf('@') + 1).toLowerCase();
        return DOMINIOS_GLOBALES.contains(dominio);
    }

}
