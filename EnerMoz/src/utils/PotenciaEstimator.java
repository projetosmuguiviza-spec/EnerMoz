package utils;

import model.energia.CategoriaEnergetica;

/**
 * Estima potencia quando o utilizador nao sabe informar o valor em Watts.
 * As referencias sao aproximacoes comuns para contexto domestico.
 */
public final class PotenciaEstimator {
    private PotenciaEstimator() {
    }

    public static double estimar(String nome, CategoriaEnergetica categoria) {
        String texto = nome == null ? "" : nome.toLowerCase();
        if (texto.contains("starlink")) {
            return 75;
        }
        if (texto.contains("router")) {
            return 12;
        }
        if (texto.contains("tv") || texto.contains("televis")) {
            return 120;
        }
        if (texto.contains("geleira") || texto.contains("frigor")) {
            return 250;
        }
        if (texto.contains("ventoinha")) {
            return 75;
        }
        if (texto.contains("computador") || texto.contains("desktop")) {
            return 200;
        }
        if (texto.contains("ups")) {
            return 100;
        }
        if (texto.contains("xbox") || texto.contains("playstation")) {
            return 180;
        }
        if (texto.contains("impressora")) {
            return 60;
        }
        if (texto.contains("painel solar")) {
            return 300;
        }
        if (texto.contains("bomba")) {
            return 750;
        }
        return categoria == null ? CategoriaEnergetica.OUTRO.getPotenciaMediaWatts() : categoria.getPotenciaMediaWatts();
    }
}
