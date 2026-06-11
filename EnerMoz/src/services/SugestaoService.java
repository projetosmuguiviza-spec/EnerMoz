package services;

import java.util.ArrayList;

import model.dispositivos.Dispositivo;
import model.utilizador.Utilizador;

/**
 * Sugere accoes praticas para reduzir consumo e factura.
 */
public class SugestaoService {
    public ArrayList<String> gerarSugestoes(Utilizador utilizador) {
        ArrayList<String> sugestoes = new ArrayList<>();

        for (Dispositivo dispositivo : utilizador.getDispositivos()) {
            if (dispositivo.calcularConsumoMensal() > 120) {
                sugestoes.add(dispositivo.getNome() + ": reduzir horas de utilizacao.");
            }
            if ("Critico".equalsIgnoreCase(dispositivo.classificarEficiencia())) {
                sugestoes.add(dispositivo.getNome() + ": substituir por equipamento mais eficiente.");
            }
        }

        if (utilizador.calcularConsumoTotalMensal() > 180) {
            sugestoes.add("Desligar aparelhos em standby durante a noite.");
        }

        if (sugestoes.isEmpty()) {
            sugestoes.add("O consumo esta equilibrado. Continue a monitorar os aparelhos.");
        }
        return sugestoes;
    }
}
