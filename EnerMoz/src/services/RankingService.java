package services;

import java.util.ArrayList;

import model.dispositivos.Dispositivo;
import model.utilizador.Utilizador;

/**
 * Ordena aparelhos pelo consumo mensal, do maior para o menor.
 */
public class RankingService {
    public ArrayList<Dispositivo> ordenarPorConsumo(Utilizador utilizador) {
        ArrayList<Dispositivo> ranking = new ArrayList<>(utilizador.getDispositivos());
        ranking.sort((a, b) -> Double.compare(b.calcularConsumoMensal(), a.calcularConsumoMensal()));
        return ranking;
    }
}
