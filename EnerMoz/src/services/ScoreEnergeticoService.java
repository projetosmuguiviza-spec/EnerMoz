package services;

import model.dispositivos.Dispositivo;
import model.utilizador.Utilizador;

/**
 * Calcula um score de 0 a 100, penalizando consumo alto e equipamentos ineficientes.
 */
public class ScoreEnergeticoService {
    public int calcularScore(Utilizador utilizador) {
        double consumo = utilizador.calcularConsumoTotalMensal();
        int score = 100;

        score -= (int) Math.min(55, consumo / 8);
        for (Dispositivo dispositivo : utilizador.getDispositivos()) {
            if ("Critico".equalsIgnoreCase(dispositivo.classificarEficiencia())) {
                score -= 6;
            }
        }

        if (score < 0) {
            return 0;
        }
        return Math.min(score, 100);
    }

    public String classificarCasa(double consumo) {
        if (consumo < 100) {
            return "Eficiente";
        }
        if (consumo < 250) {
            return "Moderado";
        }
        return "Critico";
    }
}
