package util;

import clinica.exception.ClinicaException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorArquivo {

    //diretorio padrao onde os arquivos .txt serao armazenados
    private static final String DIRETORIO_PADRAO = "dados/";

    // Garante que a pasta 'dados/' exista no projeto antes de qualquer gravação
    private static void verificarDiretorio(){
        File diretorio = new File(DIRETORIO_PADRAO);
        if(!diretorio.exists()){
            diretorio.mkdirs();
        }
    }

    // Sobrescreve todo o conteudo do arquivo com um novo texto completo
    public static void salvarDados(String nomeArquivo, String conteudo) throws ClinicaException {
        verificarDiretorio();
        String caminhoCompleto = DIRETORIO_PADRAO + nomeArquivo;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoCompleto, false))){
            bw.write(conteudo);
        } catch (IOException e) {
            throw new ClinicaException("Erro ao salvar dados no arquivo " + nomeArquivo + ": " + e.getMessage());
        }
    }

    // Anexa uma nova linha ao final do arquivo sem apagar o conteúdo existente
    //linha: linha de texto para ser anexada
    public static void anexarLinha(String nomeArquivo, String linha) throws ClinicaException {
        verificarDiretorio();
        String caminhoCompleto = DIRETORIO_PADRAO + nomeArquivo;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoCompleto, true))){
            bw.write(linha);
            bw.newLine();
        } catch (IOException e) {
            throw new ClinicaException("Erro ao anexar linha ao arquivo " + nomeArquivo + ": " + e.getMessage());
        }
    }

    // Lê o arquivo linha por linha e retorna uma lista com todas as linhas em formato String
    public static List<String> carregarDados(String nomeArquivo){
        verificarDiretorio();

        List<String> linhas = new ArrayList<>();
        File arquivo = new File(DIRETORIO_PADRAO + nomeArquivo);

        // Se o arquivo ainda não existir no disco, retorna a lista vazia com segurança
        if(!arquivo.exists()){
            return linhas;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))){
            String linha;
            //le linha por linha ate EOF (readLine retorna null)
            while((linha = br.readLine()) != null){ //ignora as linhas vazias
                linhas.add(linha);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler dados do arquivo " + nomeArquivo + ": " + e.getMessage());
        }
        return linhas;
    }

}
