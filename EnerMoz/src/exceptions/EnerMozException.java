package exceptions;

/**
 * Excepcao base do dominio EnerMoz.
 * Facilita tratar erros esperados da aplicacao sem misturar com falhas tecnicas.
 */
public class EnerMozException extends Exception {
    public EnerMozException(String mensagem) {
        super(mensagem);
    }
}
