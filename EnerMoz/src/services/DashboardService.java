package services;

import model.dispositivos.Dispositivo;
import model.utilizador.Utilizador;

/**
 * Junta os principais indicadores para uma leitura rapida do consumo.
 */
public class DashboardService {
    private final FacturaService facturaService;
    private final ScoreEnergeticoService scoreService;

    public DashboardService(FacturaService facturaService, ScoreEnergeticoService scoreService) {
        this.facturaService = facturaService;
        this.scoreService = scoreService;
    }

    public String gerarDashboard(Utilizador utilizador) {
        double consumo = utilizador.calcularConsumoTotalMensal();
        double factura = facturaService.calcularFactura(consumo);
        Dispositivo maior = obterMaiorConsumidor(utilizador);

        String maiorNome = maior == null ? "Sem aparelhos" : maior.getNome();
        return String.format(
                "Utilizador: %s%nAparelhos: %d%nConsumo total: %.2f kWh/mes%nFactura estimada: %.2f MT%nScore energetico: %d/100%nMaior consumidor: %s%nClassificacao: %s",
                utilizador.getNome(),
                utilizador.getDispositivos().size(),
                consumo,
                factura,
                scoreService.calcularScore(utilizador),
                maiorNome,
                scoreService.classificarCasa(consumo));
    }

    public Dispositivo obterMaiorConsumidor(Utilizador utilizador) {
        if (utilizador.getDispositivos().isEmpty()) {
            return null;
        }

        Dispositivo maior = utilizador.getDispositivos().get(0);
        for (Dispositivo dispositivo : utilizador.getDispositivos()) {
            if (dispositivo.calcularConsumoMensal() > maior.calcularConsumoMensal()) {
                maior = dispositivo;
            }
        }
        return maior;
    }
}
