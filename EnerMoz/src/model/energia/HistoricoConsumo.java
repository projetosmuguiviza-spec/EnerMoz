package model.energia;

import java.time.LocalDate;

/**
 * Registo mensal simplificado para acompanhar evolucao de consumo e factura.
 */
public class HistoricoConsumo {
    private LocalDate data;
    private double consumoKwh;
    private double facturaMt;

    public HistoricoConsumo(LocalDate data, double consumoKwh, double facturaMt) {
        this.data = data;
        this.consumoKwh = consumoKwh;
        this.facturaMt = facturaMt;
    }

    public LocalDate getData() {
        return data;
    }

    public double getConsumoKwh() {
        return consumoKwh;
    }

    public double getFacturaMt() {
        return facturaMt;
    }
}
