package utils;

/**
 * Pequenas operacoes de texto usadas em varias camadas.
 */
public final class TextUtils {
    private TextUtils() {
    }

    public static boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static String valorOuPadrao(String valor, String padrao) {
        return isBlank(valor) ? padrao : valor.trim();
    }

    public static String escaparCampo(String valor) {
        return valorOuPadrao(valor, "").replace("|", "/").replace("\n", " ");
    }
}
