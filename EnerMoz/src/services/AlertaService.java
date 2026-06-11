package services;

import java.util.ArrayList;

import model.dispositivos.Dispositivo;
import model.utilizador.Utilizador;

/**
 * Produz alertas inteligentes com base em regras simples e transparentes.
 */
public class AlertaService {
    public ArrayList<String> gerarAlertas(Utilizador utilizador) {
        ArrayList<String> alertas = new ArrayList<>();
        double consumoTotal = utilizador.calcularConsumoTotalMensal();

        if (consumoTotal > 250) {
            alertas.add("Consumo elevado detectado na residencia.");
        }

        for (Dispositivo dispositivo : utilizador.getDispositivos()) {
            if (dispositivo.calcularConsumoMensal() >= 150) {
                alertas.add(dispositivo.emitirAlerta());
            }
            if (dispositivo.getPotenciaWatts() >= 1000) {
                alertas.add("Potencia muito alta detectada em " + dispositivo.getNome() + ".");
            }
        }

        if (alertas.isEmpty()) {
            alertas.add("Nenhum alerta critico detectado.");
        }
        return alertas;
    }
}
