package model.energia;

/**
 * Enum que substitui subclasses como Televisao, Geleira ou Ventoinha.
 * Assim qualquer aparelho pode ser cadastrado manualmente sem criar nova classe.
 */
public enum CategoriaEnergetica {
    ELETRONICO("Electronico", 120),
    ELETRODOMESTICO("Electrodomestico", 250),
    ILUMINACAO("Iluminacao", 15),
    CLIMATIZACAO("Climatizacao", 900),
    INFORMATICA("Informatica", 180),
    COMUNICACAO("Comunicacao", 75),
    ENTRETENIMENTO("Entretenimento", 160),
    ENERGIA_SOLAR("Energia Solar", 300),
    BOMBEAMENTO("Bombeamento", 750),
    OUTRO("Outro", 100);

    private final String descricao;
    private final double potenciaMediaWatts;

    CategoriaEnergetica(String descricao, double potenciaMediaWatts) {
        this.descricao = descricao;
        this.potenciaMediaWatts = potenciaMediaWatts;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPotenciaMediaWatts() {
        return potenciaMediaWatts;
    }

    public static CategoriaEnergetica porOpcao(int opcao) {
        CategoriaEnergetica[] categorias = values();
        if (opcao < 1 || opcao > categorias.length) {
            return OUTRO;
        }
        return categorias[opcao - 1];
    }

    public static CategoriaEnergetica porNome(String nome) {
        for (CategoriaEnergetica categoria : values()) {
            if (categoria.name().equalsIgnoreCase(nome) || categoria.descricao.equalsIgnoreCase(nome)) {
                return categoria;
            }
        }
        return OUTRO;
    }
}
