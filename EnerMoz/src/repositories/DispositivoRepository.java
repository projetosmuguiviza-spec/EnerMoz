package repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.DadosInvalidosException;
import model.dispositivos.Dispositivo;
import model.energia.CategoriaEnergetica;
import utils.TextUtils;

/**
 * Persistencia em TXT usando campos separados por pipe.
 * Foi mantida simples para cumprir as restricoes academicas sem bibliotecas externas.
 */
public class DispositivoRepository {
    private static final String CAMINHO = "src/data/dispositivos.txt";

    public ArrayList<Dispositivo> carregar() {
        ArrayList<Dispositivo> dispositivos = new ArrayList<>();
        File ficheiro = new File(CAMINHO);
        if (!ficheiro.exists()) {
            return dispositivos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ficheiro))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                Dispositivo dispositivo = converterLinha(linha);
                if (dispositivo != null) {
                    dispositivos.add(dispositivo);
                }
            }
        } catch (IOException e) {
            System.out.println("Nao foi possivel carregar dispositivos: " + e.getMessage());
        }
        return dispositivos;
    }

    public void guardarTodos(ArrayList<Dispositivo> dispositivos) {
        File ficheiro = new File(CAMINHO);
        File pasta = ficheiro.getParentFile();
        if (pasta != null) {
            pasta.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ficheiro, false))) {
            for (Dispositivo dispositivo : dispositivos) {
                writer.write(converterDispositivo(dispositivo));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Nao foi possivel guardar dispositivos: " + e.getMessage());
        }
    }

    private String converterDispositivo(Dispositivo dispositivo) {
        return String.join("|",
                TextUtils.escaparCampo(dispositivo.getId()),
                TextUtils.escaparCampo(dispositivo.getNome()),
                dispositivo.getCategoria().name(),
                String.valueOf(dispositivo.getHorasUsoDiario()),
                String.valueOf(dispositivo.getQuantidade()),
                String.valueOf(dispositivo.getPotenciaWatts()),
                TextUtils.escaparCampo(dispositivo.getFabricante()),
                TextUtils.escaparCampo(dispositivo.getModelo()),
                TextUtils.escaparCampo(dispositivo.getLocalizacao()),
                TextUtils.escaparCampo(dispositivo.getObservacoes()),
                TextUtils.escaparCampo(dispositivo.getEficienciaEnergetica()));
    }

    private Dispositivo converterLinha(String linha) {
        try {
            if (linha.contains("|")) {
                return converterLinhaActual(linha);
            }
            return converterLinhaAntiga(linha);
        } catch (Exception e) {
            return null;
        }
    }

    private Dispositivo converterLinhaActual(String linha) throws DadosInvalidosException {
        String[] campos = linha.split("\\|", -1);
        if (campos.length < 11) {
            return null;
        }

        Dispositivo dispositivo = new Dispositivo(
                campos[0],
                campos[1],
                CategoriaEnergetica.porNome(campos[2]),
                Double.parseDouble(campos[3]),
                Double.parseDouble(campos[5]));
        dispositivo.setQuantidade(Integer.parseInt(campos[4]));
        dispositivo.setFabricante(campos[6]);
        dispositivo.setModelo(campos[7]);
        dispositivo.setLocalizacao(campos[8]);
        dispositivo.setObservacoes(campos[9]);
        dispositivo.setEficienciaEnergetica(campos[10]);
        return dispositivo;
    }

    private Dispositivo converterLinhaAntiga(String linha) throws DadosInvalidosException {
        String[] campos = linha.split(";", -1);
        if (campos.length < 6) {
            return null;
        }

        Dispositivo dispositivo = new Dispositivo(
                String.valueOf(System.nanoTime()),
                campos[0],
                CategoriaEnergetica.porNome(campos[1]),
                Double.parseDouble(campos[3]),
                Double.parseDouble(campos[2]));
        dispositivo.setQuantidade(Integer.parseInt(campos[4]));
        dispositivo.setLocalizacao(campos[5]);
        return dispositivo;
    }
}
