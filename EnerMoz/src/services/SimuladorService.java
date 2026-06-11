package services;

/**
 * Simula poupanca financeira a partir de uma percentagem de reducao.
 */
public class SimuladorService {
    private final FacturaService facturaService;

    public SimuladorService(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    public String simular(double consumoActual, double percentagemReducao) {
        double reducao = Math.max(0, Math.min(percentagemReducao, 100)) / 100.0;
        double novoConsumo = consumoActual * (1 - reducao);
        double facturaActual = facturaService.calcularFactura(consumoActual);
        double novaFactura = facturaService.calcularFactura(novoConsumo);
        double poupancaMensal = facturaActual - novaFactura;

        return String.format(
                "Reducao: %.1f%%%nConsumo simulado: %.2f kWh/mes%nFactura simulada: %.2f MT%nPoupanca mensal: %.2f MT%nPoupanca anual: %.2f MT",
                percentagemReducao, novoConsumo, novaFactura, poupancaMensal, poupancaMensal * 12);
    }
}
