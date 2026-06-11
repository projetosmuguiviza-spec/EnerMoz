package model.utilizador;

import java.util.ArrayList;

import exceptions.DadosInvalidosException;
import model.dispositivos.Dispositivo;
import model.energia.HistoricoConsumo;
import utils.TextUtils;

/**
 * Agrega os dispositivos e o historico de consumo de um utilizador.
 * Demonstra agregacao: os dispositivos pertencem a lista do utilizador.
 */
public class Utilizador {
    private String nome;
    private final ArrayList<Dispositivo> dispositivos;
    private final ArrayList<HistoricoConsumo> historico;

    public Utilizador(String nome) throws DadosInvalidosException {
        setNome(nome);
        this.dispositivos = new ArrayList<>();
        this.historico = new ArrayList<>();
    }

    public void adicionarDispositivo(Dispositivo dispositivo) throws DadosInvalidosException {
        if (dispositivo == null) {
            throw new DadosInvalidosException("Dispositivo invalido.");
        }
        dispositivos.add(dispositivo);
    }

    public Dispositivo obterDispositivo(int indice) throws DadosInvalidosException {
        if (indice < 0 || indice >= dispositivos.size()) {
            throw new DadosInvalidosException("Indice de aparelho invalido.");
        }
        return dispositivos.get(indice);
    }

    public void removerDispositivo(int indice) throws DadosInvalidosException {
        obterDispositivo(indice);
        dispositivos.remove(indice);
    }

    public double calcularConsumoTotalMensal() {
        double total = 0;
        for (Dispositivo dispositivo : dispositivos) {
            total += dispositivo.calcularConsumoMensal();
        }
        return total;
    }

    public void adicionarHistorico(HistoricoConsumo registo) {
        historico.add(registo);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadosInvalidosException {
        if (TextUtils.isBlank(nome)) {
            throw new DadosInvalidosException("O nome do utilizador e obrigatorio.");
        }
        this.nome = nome.trim();
    }

    public ArrayList<Dispositivo> getDispositivos() {
        return dispositivos;
    }

    public ArrayList<HistoricoConsumo> getHistorico() {
        return historico;
    }
}
