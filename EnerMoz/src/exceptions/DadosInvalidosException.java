package exceptions;

/**
 * Representa entradas invalidas informadas pelo utilizador.
 */
public class DadosInvalidosException extends EnerMozException {
    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
