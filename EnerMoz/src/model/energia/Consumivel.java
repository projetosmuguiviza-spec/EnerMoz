package model.energia;

/**
 * Interface para qualquer entidade que consiga calcular consumo electrico.
 */
public interface Consumivel {
    double calcularConsumoDiario();

    double calcularConsumoMensal();
}
