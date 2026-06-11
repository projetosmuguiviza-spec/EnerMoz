package services;

import model.dispositivos.Dispositivo;
import model.utilizador.Utilizador;

/**
 * Gera relatorios textuais para consola ou futura interface grafica.
 */
public class RelatorioService {
    private final FacturaService facturaService;
    private final ScoreEnergeticoService scoreService;

    public RelatorioService(FacturaService facturaService, ScoreEnergeticoService scoreService) {
        this.facturaService = facturaService;
        this.scoreService = scoreService;
    }

    public String gerarRelatorio(Utilizador utilizador) {
        StringBuilder relatorio = new StringBuilder();
        double consumo = utilizador.calcularConsumoTotalMensal();
        relatorio.append("RELATORIO ENERGETICO\n");
        relatorio.append(String.format("Consumo total: %.2f kWh/mes%n", consumo));
        relatorio.append(String.format("Factura estimada: %.2f MT%n", facturaService.calcularFactura(consumo)));
        relatorio.append("Classificacao energetica: ").append(scoreService.classificarCasa(consumo)).append("\n\n");
        relatorio.append("Ranking de consumo:\n");

        int posicao = 1;
        for (Dispositivo dispositivo : new RankingService().ordenarPorConsumo(utilizador)) {
            relatorio.append(String.format("%d. %s - %.2f kWh/mes - %s%n",
                    posicao++, dispositivo.getNome(), dispositivo.calcularConsumoMensal(),
                    dispositivo.classificarEficiencia()));
        }

        return relatorio.toString();
    }
}
