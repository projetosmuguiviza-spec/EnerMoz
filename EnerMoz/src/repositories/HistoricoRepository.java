package repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import model.energia.HistoricoConsumo;

/**
 * Guarda snapshots de consumo e factura para consulta futura.
 */
public class HistoricoRepository {
    private static final String CAMINHO = "src/data/historico.txt";

    public ArrayList<HistoricoConsumo> carregar() {
        ArrayList<HistoricoConsumo> historico = new ArrayList<>();
        File ficheiro = new File(CAMINHO);
        if (!ficheiro.exists()) {
            return historico;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ficheiro))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.split("\\|", -1);
                if (campos.length == 3) {
                    historico.add(new HistoricoConsumo(
                            LocalDate.parse(campos[0]),
                            Double.parseDouble(campos[1]),
                            Double.parseDouble(campos[2])));
                }
            }
        } catch (Exception e) {
            System.out.println("Nao foi possivel carregar historico: " + e.getMessage());
        }
        return historico;
    }

    public void guardarTodos(ArrayList<HistoricoConsumo> historico) {
        File ficheiro = new File(CAMINHO);
        File pasta = ficheiro.getParentFile();
        if (pasta != null) {
            pasta.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ficheiro, false))) {
            for (HistoricoConsumo registo : historico) {
                writer.write(registo.getData() + "|" + registo.getConsumoKwh() + "|" + registo.getFacturaMt());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Nao foi possivel guardar historico: " + e.getMessage());
        }
    }
}
