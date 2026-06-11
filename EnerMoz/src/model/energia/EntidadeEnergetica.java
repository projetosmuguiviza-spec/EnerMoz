package model.energia;

/**
 * Classe abstracta comum aos elementos monitorados pelo EnerMoz.
 * Demonstra abstraccao e permite polimorfismo sobre consumo energetico.
 */
public abstract class EntidadeEnergetica implements Consumivel, Monitoravel, Classificavel {
    private final String id;

    protected EntidadeEnergetica(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract String gerarResumo();
}
