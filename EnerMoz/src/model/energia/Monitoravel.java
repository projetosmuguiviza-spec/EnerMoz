package model.energia;

/**
 * Interface para entidades que geram estado, alertas e sugestoes.
 */
public interface Monitoravel {
    String gerarEstadoConsumo();

    String emitirAlerta();

    String gerarSugestao();
}
