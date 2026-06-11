package view;

import java.util.ArrayList;
import java.util.Scanner;

import controller.EnerMozController;
import exceptions.DadosInvalidosException;
import model.dispositivos.Dispositivo;
import model.energia.CategoriaEnergetica;
import model.energia.HistoricoConsumo;

/**
 * Interface de consola. Mantem entrada/saida separada das regras de negocio.
 */
public class MenuConsole {
    private final Scanner scanner;
    private EnerMozController controller;

    public MenuConsole() {
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        try {
            System.out.print("Nome do utilizador: ");
            controller = new EnerMozController(scanner.nextLine());
            int opcao;
            do {
                mostrarMenu();
                opcao = lerInteiro("Escolha: ");
                executarOpcao(opcao);
            } while (opcao != 0);
        } catch (DadosInvalidosException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void executarOpcao(int opcao) {
        try {
            switch (opcao) {
                case 1 -> System.out.println("\n" + controller.gerarDashboard());
                case 2 -> adicionarDispositivo();
                case 3 -> listarDispositivos();
                case 4 -> editarDispositivo();
                case 5 -> removerDispositivo();
                case 6 -> System.out.println("\n" + controller.gerarRelatorio());
                case 7 -> imprimirLista("ALERTAS INTELIGENTES", controller.gerarAlertas());
                case 8 -> imprimirLista("SUGESTOES DE POUPANCA", controller.gerarSugestoes());
                case 9 -> mostrarRanking();
                case 10 -> simularPoupanca();
                case 11 -> registarHistorico();
                case 12 -> listarHistorico();
                case 0 -> System.out.println("\nObrigado por usar o EnerMoz.");
                default -> System.out.println("Opcao invalida.");
            }
        } catch (DadosInvalidosException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void mostrarMenu() {
        System.out.println("\n================ ENERMOZ ================");
        System.out.println("1. Dashboard");
        System.out.println("2. Adicionar aparelho");
        System.out.println("3. Listar aparelhos");
        System.out.println("4. Editar aparelho");
        System.out.println("5. Remover aparelho");
        System.out.println("6. Relatorio energetico");
        System.out.println("7. Alertas inteligentes");
        System.out.println("8. Sugestoes de poupanca");
        System.out.println("9. Ranking energetico");
        System.out.println("10. Simulador de poupanca");
        System.out.println("11. Registar historico actual");
        System.out.println("12. Ver historico");
        System.out.println("0. Sair");
    }

    private void adicionarDispositivo() throws DadosInvalidosException {
        System.out.println("\nNovo aparelho");
        String nome = lerTexto("Nome: ");
        CategoriaEnergetica categoria = escolherCategoria();
        double horas = lerDouble("Horas de uso por dia: ");
        double potencia = lerDouble("Potencia em Watts (0 para estimar): ");
        int quantidade = lerInteiro("Quantidade: ");
        String fabricante = lerTextoOpcional("Fabricante (opcional): ");
        String modelo = lerTextoOpcional("Modelo (opcional): ");
        String localizacao = lerTextoOpcional("Localizacao (opcional): ");
        String observacoes = lerTextoOpcional("Observacoes (opcional): ");
        String eficiencia = lerTextoOpcional("Eficiencia energetica (opcional): ");

        Dispositivo dispositivo = controller.criarDispositivo(nome, categoria, horas, potencia, quantidade,
                fabricante, modelo, localizacao, observacoes, eficiencia);
        System.out.printf("Aparelho adicionado. Potencia usada: %.1f W%n", dispositivo.getPotenciaWatts());
    }

    private void editarDispositivo() throws DadosInvalidosException {
        listarDispositivos();
        int indice = lerInteiro("Numero do aparelho: ") - 1;
        String nome = lerTexto("Novo nome: ");
        CategoriaEnergetica categoria = escolherCategoria();
        double horas = lerDouble("Novas horas de uso por dia: ");
        double potencia = lerDouble("Nova potencia em Watts: ");
        int quantidade = lerInteiro("Nova quantidade: ");
        String fabricante = lerTextoOpcional("Fabricante: ");
        String modelo = lerTextoOpcional("Modelo: ");
        String localizacao = lerTextoOpcional("Localizacao: ");
        String observacoes = lerTextoOpcional("Observacoes: ");
        String eficiencia = lerTextoOpcional("Eficiencia energetica: ");

        controller.editarDispositivo(indice, nome, categoria, horas, potencia, quantidade, fabricante, modelo,
                localizacao, observacoes, eficiencia);
        System.out.println("Aparelho actualizado.");
    }

    private void removerDispositivo() throws DadosInvalidosException {
        listarDispositivos();
        int indice = lerInteiro("Numero do aparelho: ") - 1;
        controller.removerDispositivo(indice);
        System.out.println("Aparelho removido.");
    }

    private void listarDispositivos() {
        ArrayList<Dispositivo> dispositivos = controller.getUtilizador().getDispositivos();
        if (dispositivos.isEmpty()) {
            System.out.println("Nenhum aparelho cadastrado.");
            return;
        }

        System.out.println("\nAPARELHOS");
        for (int i = 0; i < dispositivos.size(); i++) {
            System.out.println((i + 1) + ". " + dispositivos.get(i).gerarResumo());
        }
    }

    private void mostrarRanking() {
        System.out.println("\nRANKING ENERGETICO");
        int posicao = 1;
        for (Dispositivo dispositivo : controller.gerarRanking()) {
            System.out.printf("%d. %s - %.2f kWh/mes%n", posicao++, dispositivo.getNome(),
                    dispositivo.calcularConsumoMensal());
        }
    }

    private void simularPoupanca() {
        double percentagem = lerDouble("Percentagem de reducao desejada: ");
        System.out.println("\n" + controller.simularPoupanca(percentagem));
    }

    private void registarHistorico() {
        controller.registarHistoricoActual();
        System.out.println("Historico registado com a data actual.");
    }

    private void listarHistorico() {
        ArrayList<HistoricoConsumo> historico = controller.getUtilizador().getHistorico();
        if (historico.isEmpty()) {
            System.out.println("Nenhum historico registado.");
            return;
        }
        System.out.println("\nHISTORICO");
        for (HistoricoConsumo registo : historico) {
            System.out.printf("%s | %.2f kWh | %.2f MT%n", registo.getData(), registo.getConsumoKwh(),
                    registo.getFacturaMt());
        }
    }

    private CategoriaEnergetica escolherCategoria() {
        System.out.println("Categorias:");
        CategoriaEnergetica[] categorias = CategoriaEnergetica.values();
        for (int i = 0; i < categorias.length; i++) {
            System.out.println((i + 1) + ". " + categorias[i].getDescricao());
        }
        return CategoriaEnergetica.porOpcao(lerInteiro("Categoria: "));
    }

    private void imprimirLista(String titulo, ArrayList<String> itens) {
        System.out.println("\n" + titulo);
        for (String item : itens) {
            System.out.println("- " + item);
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    private String lerTextoOpcional(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero inteiro valido.");
            }
        }
    }

    private double lerDouble(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Digite um valor numerico valido.");
            }
        }
    }
}
