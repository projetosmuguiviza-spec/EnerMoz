package controller;

import java.time.LocalDate;
import java.util.ArrayList;

import exceptions.DadosInvalidosException;
import model.dispositivos.Dispositivo;
import model.energia.CategoriaEnergetica;
import model.energia.HistoricoConsumo;
import model.utilizador.Utilizador;
import repositories.DispositivoRepository;
import repositories.HistoricoRepository;
import services.AlertaService;
import services.DashboardService;
import services.FacturaService;
import services.RankingService;
import services.RelatorioService;
import services.ScoreEnergeticoService;
import services.SimuladorService;
import services.SugestaoService;
import utils.PotenciaEstimator;

/**
 * Orquestra a aplicacao, separando a view de consola das regras de negocio.
 */
public class EnerMozController {
    private final Utilizador utilizador;
    private final DispositivoRepository dispositivoRepository;
    private final HistoricoRepository historicoRepository;
    private final FacturaService facturaService;
    private final DashboardService dashboardService;
    private final RelatorioService relatorioService;
    private final AlertaService alertaService;
    private final SugestaoService sugestaoService;
    private final RankingService rankingService;
    private final SimuladorService simuladorService;

    public EnerMozController(String nomeUtilizador) throws DadosInvalidosException {
        this.utilizador = new Utilizador(nomeUtilizador);
        this.dispositivoRepository = new DispositivoRepository();
        this.historicoRepository = new HistoricoRepository();
        this.facturaService = new FacturaService();
        ScoreEnergeticoService scoreService = new ScoreEnergeticoService();
        this.dashboardService = new DashboardService(facturaService, scoreService);
        this.relatorioService = new RelatorioService(facturaService, scoreService);
        this.alertaService = new AlertaService();
        this.sugestaoService = new SugestaoService();
        this.rankingService = new RankingService();
        this.simuladorService = new SimuladorService(facturaService);
        carregarDados();
    }

    public Dispositivo criarDispositivo(String nome, CategoriaEnergetica categoria, double horasUso,
            double potenciaWatts, int quantidade, String fabricante, String modelo, String localizacao,
            String observacoes, String eficiencia) throws DadosInvalidosException {
        Dispositivo dispositivo = new Dispositivo(String.valueOf(System.currentTimeMillis()), nome, categoria, horasUso,
                potenciaWatts);
        dispositivo.setQuantidade(quantidade);
        dispositivo.setFabricante(fabricante);
        dispositivo.setModelo(modelo);
        dispositivo.setLocalizacao(localizacao);
        dispositivo.setObservacoes(observacoes);
        dispositivo.setEficienciaEnergetica(eficiencia);
        utilizador.adicionarDispositivo(dispositivo);
        guardarDados();
        return dispositivo;
    }

    public void editarDispositivo(int indice, String nome, CategoriaEnergetica categoria, double horasUso,
            double potenciaWatts, int quantidade, String fabricante, String modelo, String localizacao,
            String observacoes, String eficiencia) throws DadosInvalidosException {
        Dispositivo dispositivo = utilizador.obterDispositivo(indice);
        dispositivo.setNome(nome);
        dispositivo.setCategoria(categoria);
        dispositivo.setHorasUsoDiario(horasUso);
        dispositivo.setPotenciaWatts(potenciaWatts > 0 ? potenciaWatts : PotenciaEstimator.estimar(nome, categoria));
        dispositivo.setQuantidade(quantidade);
        dispositivo.setFabricante(fabricante);
        dispositivo.setModelo(modelo);
        dispositivo.setLocalizacao(localizacao);
        dispositivo.setObservacoes(observacoes);
        dispositivo.setEficienciaEnergetica(eficiencia);
        guardarDados();
    }

    public void removerDispositivo(int indice) throws DadosInvalidosException {
        utilizador.removerDispositivo(indice);
        guardarDados();
    }

    public void registarHistoricoActual() {
        double consumo = utilizador.calcularConsumoTotalMensal();
        utilizador.adicionarHistorico(new HistoricoConsumo(LocalDate.now(), consumo, facturaService.calcularFactura(consumo)));
        guardarHistorico();
    }

    private void carregarDados() throws DadosInvalidosException {
        for (Dispositivo dispositivo : dispositivoRepository.carregar()) {
            utilizador.adicionarDispositivo(dispositivo);
        }
        for (HistoricoConsumo registo : historicoRepository.carregar()) {
            utilizador.adicionarHistorico(registo);
        }
    }

    public void guardarDados() {
        dispositivoRepository.guardarTodos(utilizador.getDispositivos());
    }

    public void guardarHistorico() {
        historicoRepository.guardarTodos(utilizador.getHistorico());
    }

    public Utilizador getUtilizador() {
        return utilizador;
    }

    public String gerarDashboard() {
        return dashboardService.gerarDashboard(utilizador);
    }

    public String gerarRelatorio() {
        return relatorioService.gerarRelatorio(utilizador);
    }

    public ArrayList<String> gerarAlertas() {
        return alertaService.gerarAlertas(utilizador);
    }

    public ArrayList<String> gerarSugestoes() {
        return sugestaoService.gerarSugestoes(utilizador);
    }

    public ArrayList<Dispositivo> gerarRanking() {
        return rankingService.ordenarPorConsumo(utilizador);
    }

    public String simularPoupanca(double percentagem) {
        return simuladorService.simular(utilizador.calcularConsumoTotalMensal(), percentagem);
    }
}
