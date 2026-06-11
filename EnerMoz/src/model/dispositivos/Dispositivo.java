package model.dispositivos;

import exceptions.DadosInvalidosException;
import model.energia.CategoriaEnergetica;
import model.energia.EntidadeEnergetica;
import utils.PotenciaEstimator;
import utils.TextUtils;

/**
 * Representa qualquer aparelho electrico cadastrado pelo utilizador.
 * A categoria vem de um enum, evitando criar uma classe para cada aparelho.
 */
public class Dispositivo extends EntidadeEnergetica {
    private String nome;
    private CategoriaEnergetica categoria;
    private double horasUsoDiario;
    private int quantidade;
    private double potenciaWatts;
    private String fabricante;
    private String modelo;
    private String localizacao;
    private String observacoes;
    private String eficienciaEnergetica;

    public Dispositivo(String id, String nome, CategoriaEnergetica categoria, double horasUsoDiario, double potenciaWatts)
            throws DadosInvalidosException {
        super(id);
        setNome(nome);
        setCategoria(categoria);
        setHorasUsoDiario(horasUsoDiario);
        setQuantidade(1);
        setPotenciaWatts(potenciaWatts > 0 ? potenciaWatts : PotenciaEstimator.estimar(nome, categoria));
        this.fabricante = "Nao informado";
        this.modelo = "Nao informado";
        this.localizacao = "Nao definida";
        this.observacoes = "";
        this.eficienciaEnergetica = "Moderada";
    }

    /**
     * Consumo diario em kWh = potencia em W x horas x quantidade / 1000.
     */
    @Override
    public double calcularConsumoDiario() {
        return (potenciaWatts * horasUsoDiario * quantidade) / 1000.0;
    }

    @Override
    public double calcularConsumoMensal() {
        return calcularConsumoDiario() * 30;
    }

    @Override
    public String gerarEstadoConsumo() {
        double consumo = calcularConsumoMensal();
        if (consumo < 50) {
            return "Baixo consumo";
        }
        if (consumo < 150) {
            return "Consumo moderado";
        }
        return "Consumo elevado";
    }

    @Override
    public String emitirAlerta() {
        if (calcularConsumoMensal() >= 150) {
            return "Aparelho com elevado gasto energetico: " + nome;
        }
        if (horasUsoDiario > 12) {
            return "Uso prolongado detectado em " + nome + ".";
        }
        return "Sem alerta critico.";
    }

    @Override
    public String gerarSugestao() {
        if (calcularConsumoMensal() >= 150) {
            return "Reduzir horas de utilizacao ou substituir por equipamento mais eficiente.";
        }
        if (horasUsoDiario >= 8) {
            return "Avaliar desligar o aparelho quando nao estiver em uso.";
        }
        return "Manter bons habitos de utilizacao.";
    }

    @Override
    public String classificarEficiencia() {
        double consumo = calcularConsumoMensal();
        if (consumo < 50) {
            return "Eficiente";
        }
        if (consumo < 150) {
            return "Moderado";
        }
        return "Critico";
    }

    @Override
    public String gerarResumo() {
        return String.format("%s | %s | %.1f W | %.1f h/dia | %.2f kWh/mes | %s",
                nome, categoria.getDescricao(), potenciaWatts, horasUsoDiario, calcularConsumoMensal(),
                classificarEficiencia());
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadosInvalidosException {
        if (TextUtils.isBlank(nome)) {
            throw new DadosInvalidosException("O nome do aparelho e obrigatorio.");
        }
        this.nome = nome.trim();
    }

    public CategoriaEnergetica getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaEnergetica categoria) {
        this.categoria = categoria == null ? CategoriaEnergetica.OUTRO : categoria;
    }

    public double getHorasUsoDiario() {
        return horasUsoDiario;
    }

    public void setHorasUsoDiario(double horasUsoDiario) throws DadosInvalidosException {
        if (horasUsoDiario < 0 || horasUsoDiario > 24) {
            throw new DadosInvalidosException("As horas de uso devem estar entre 0 e 24.");
        }
        this.horasUsoDiario = horasUsoDiario;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) throws DadosInvalidosException {
        if (quantidade <= 0) {
            throw new DadosInvalidosException("A quantidade deve ser maior que zero.");
        }
        this.quantidade = quantidade;
    }

    public double getPotenciaWatts() {
        return potenciaWatts;
    }

    public void setPotenciaWatts(double potenciaWatts) throws DadosInvalidosException {
        if (potenciaWatts <= 0) {
            throw new DadosInvalidosException("A potencia deve ser maior que zero.");
        }
        this.potenciaWatts = potenciaWatts;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = TextUtils.valorOuPadrao(fabricante, "Nao informado");
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = TextUtils.valorOuPadrao(modelo, "Nao informado");
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = TextUtils.valorOuPadrao(localizacao, "Nao definida");
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = TextUtils.valorOuPadrao(observacoes, "");
    }

    public String getEficienciaEnergetica() {
        return eficienciaEnergetica;
    }

    public void setEficienciaEnergetica(String eficienciaEnergetica) {
        this.eficienciaEnergetica = TextUtils.valorOuPadrao(eficienciaEnergetica, "Moderada");
    }
}
