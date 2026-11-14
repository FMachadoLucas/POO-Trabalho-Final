package clinica;

import clinica.exception.ClinicaException;
import model.*;//importa todas as classes do pacote model
import util.GerenciadorArquivo;

import javax.swing.JOptionPane;//se for usar essa interface
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    //dados na memoria
    private static List<Tutor> tutores = new ArrayList<>();
    private static List<Animal> animais = new ArrayList<>();
    private static List<Veterinario> veterinarios = new ArrayList<>();
    private static List<Servico> servicos = new ArrayList<>();
    private static List<Agendamento> agendamentos = new ArrayList<>();
    private static List<Pagamento> pagamentos = new ArrayList<>();

    public static void main(String[] args){

        //formatando a data e hora
        DateTimeFormatter formatoDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dataHora = LocalDateTime.now();
        String dataHoraFormatada = dataHora.format(formatoDataHora);

        boolean executando = true;

        while(executando){
            String menu = "===SISTEMA DE GERENCIAMENTO DE CLINICA VETERINARIA===\n\n"
                    + "1. Cadastrar Tutor e Animal\n"
                    + "2. Cadastrar Veterinário\n"
                    + "3. Cadastrar Serviço (Consulta / Vacinação / Exame / Banho e Tosa)\n"
                    + "4. Realizar Agendamento\n"
                    + "5. Processar Pagamento\n"
                    + "6. Registrar no Prontuário do Animal\n"
                    + "7. Visualizar Prontuário do Animal\n"
                    + "8. Salvar Relatório em Arquivo (.txt)\n"
                    + "0. Sair do Sistema\n\n"
                    + "Escolha uma opção:";

            String opcaoStr = JOptionPane.showInputDialog(null, menu, "Clínica Veterinária", JOptionPane.QUESTION_MESSAGE);

            if(opcaoStr == null || opcaoStr.equals("0")){
                executando = false;
                JOptionPane.showMessageDialog(null, "Encerrando o sistema...");
                break;
            }

            try{
                //linha temporaria para conseguir compilar
                if (false) throw new ClinicaException("");

                switch(opcaoStr){
                    case "1":

                        break;
                    case "2":

                        break;
                    case "3":

                        break;
                    case "4":

                        break;
                    case "5":

                        break;
                    case "6":

                        break;
                    case "7":

                        break;
                    case "8":

                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Opção inválida!");
                        break;
                }
            }catch(ClinicaException e){
                //captura excecoes personalizada de negocio
                JOptionPane.showMessageDialog(null, "ERRO DE NEGÓCIO: " + e.getMessage(), "Alerta da Clínica", JOptionPane.WARNING_MESSAGE);
            }catch(Exception e){
                //captura excecoes genericas de formato ou digitacao
                JOptionPane.showMessageDialog(null, "ERRO DE ENTRADA: Verifique os dados digitados! (" + e.getMessage() + ")", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
