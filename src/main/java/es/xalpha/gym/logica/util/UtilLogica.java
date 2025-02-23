package es.xalpha.gym.logica.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UtilLogica {

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

    public static String formatoFecha(LocalDate date, String formato) {
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

    public static Date obtenerDate(LocalDate localDate) {
        return localDate == null ? new Date() : Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static boolean esEmailValido(String email) {
        Pattern pattern = Pattern.compile(
                "^([\\w._%+-]+)@([\\w._]+)\\.([a-zA-Z]{2,4})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches() && dominioValido(email);
    }

    public static boolean esNumeroDeTelValido(String telefono) {
        Pattern pattern = Pattern.compile(
                "^(\\+\\d{1,3}\\s?)?(\\d{9,10})$");
        Matcher matcher = pattern.matcher(telefono);
        return matcher.matches();
    }

    private static boolean dominioValido(String email) {
        String dominio = email.substring(email.indexOf('@') + 1).toLowerCase();
        return DOMINIOS_GLOBALES.contains(dominio);
    }

}
