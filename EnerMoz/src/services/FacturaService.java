package services;

/**
 * Calcula uma factura estimada em meticais.
 * Os valores sao simplificados para fins academicos e podem ser ajustados.
 */
public class FacturaService {
    private static final double TAXA_FIXA_MT = 257.97;

    public double calcularFactura(double consumoKwh) {
        double tarifa;
        if (consumoKwh <= 300) {
            tarifa = 6.63;
        } else if (consumoKwh <= 500) {
            tarifa = 9.39;
        } else {
            tarifa = 9.85;
        }
        return (consumoKwh * tarifa) + TAXA_FIXA_MT;
    }
}
