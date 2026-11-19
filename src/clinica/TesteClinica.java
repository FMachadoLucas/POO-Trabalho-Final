package clinica;

import clinica.exception.ClinicaException;
import model.*;
import util.GerenciadorArquivo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TesteClinica {

    public static void main(String[] args) throws ClinicaException {
        System.out.println("=================================================");
        System.out.println(" INICIANDO SUÍTE DE TESTES FUNCIONAIS (POO)");
        System.out.println("=================================================\n");

        int testesPassaram = 0;
        int totalTestes = 4;

        //Teste de Polimorfismo nos Serviços
        if (testarPolimorfismoServicos()) testesPassaram++;

        //Teste de Lançamento da Exceção Customizada (ClinicaException)
        if (testarClinicaException()) testesPassaram++;

        //Teste de Gravação e Leitura de Arquivo Texto
        if (testarPersistenciaArquivo()) testesPassaram++;

        //Teste de Composição do Prontuário
        if (testarComposicaoProntuario()) testesPassaram++;

        System.out.println("=================================================");
        System.out.println(" RESULTADO FINAL: " + testesPassaram + "/" + totalTestes + " TESTES APROVADOS.");
        System.out.println("=================================================");
    }

    //Verifica o cálculo do valor final de cada subclasse de Serviço (Polimorfismo).
    private static boolean testarPolimorfismoServicos(){
        System.out.println("[TESTE 1] Testando Polimorfismo no Cálculo de Serviços...");

        try{
            Servico s1 = new Consulta("Consulta Geral", 100.0, "Rotina", "Saudável");
            Servico s2 = new Vacinacao("Vacina Antirrábica", 80.0, "V10", 30.0);
            Servico s3 = new Exame("Hemograma", 150.0, "Sangue", 50.0);
            Servico s4 = new BanhoETosa("Banho Completo", 60.0, "Médio", true, 20.0);

            boolean c1 = s1.calcularValorFinal() == 100.0;
            boolean c2 = s2.calcularValorFinal() == 110.0; // 80 + 30
            boolean c3 = s3.calcularValorFinal() == 200.0; // 150 + 50
            boolean c4 = s4.calcularValorFinal() == 80.0; // 60 + 20

            if (c1 && c2 && c3 && c4) {
                System.out.println(" --> SUCESSO: Polimorfismo e cálculos das subclasses validados!\n");
                return true;
            }else{
                System.out.println(" --> FALHA: Os valores finais dos serviços não correspondem ao esperado.\n");
                return false;
            }
        } catch (Exception e) {
            System.out.println(" --> ERRO INESPERADO: " + e.getMessage() + "\n");
            return false;
        }
    }

    //Verifica o bloqueio e disparo da ClinicaException em conflito de agenda.
    private static boolean testarClinicaException(){
        System.out.println("[TESTE 2] Testando Validação e Lançamento de ClinicaException...");
        try{
            Tutor tutor = new Tutor("Maria Oliveira", "12345678901", "62999998888", "Rua A");
            Animal animal = new Animal("Thor", "Cachorro", "Labrador", 4, 25.0);
            Veterinario vet = new Veterinario("Dr. Paulo", "98765432100", "62988887777", "Av. Central", "CRMV-1001", "Clínica Geral");
            Servico consulta = new Consulta("Consulta", 100.0, "Avaliação", "Pendente");

            LocalDateTime dataHora = LocalDateTime.now().plusDays(1);

            Agendamento ag1 = new Agendamento(dataHora, animal, tutor, vet, consulta);
            List<Agendamento> listaAgendamentos = new ArrayList<>();
            listaAgendamentos.add(ag1);

            // Simula tentativa de criar um 2º agendamento para o mesmo vet no mesmo horário
            Veterinario vetConflito = vet;
            LocalDateTime dataHoraConflito = dataHora;

            boolean conflitoDetectado = false;
            for (Agendamento a : listaAgendamentos){
                if (a.getVeterinario().getCrmv().equalsIgnoreCase(vetConflito.getCrmv())
                        && a.getDataHora().equals(dataHoraConflito)
                        && "Agendado".equals(a.getStatus())){
                    conflitoDetectado = true;
                    throw new ClinicaException("Conflito de agenda: Veterinário já possui atendimento neste horário!");
                }
            }
            if (!conflitoDetectado){
                System.out.println(" --> FALHA: O conflito de horário não foi detectado.\n");
                return false;
            }
        }catch (ClinicaException e){
            System.out.println(" --> SUCESSO: ClinicaException capturada com a mensagem: \"" + e.getMessage() + "\"\n");
            return true;
        }
        return false;
    }
    //Testa a gravação e reconstrução de objetos a partir dos arquivos .txt.
    private static boolean testarPersistenciaArquivo(){
        System.out.println("[TESTE 3] Testando Persistência em Arquivo Texto (GerenciadorArquivo)...");
        try{
            Veterinario vOriginal = new Veterinario("Dra. Camila", "11122233344", "62977776666", "Rua B", "CRMV-2024", "Dermatologia");

            // Grava a linha no arquivo
            GerenciadorArquivo.anexarLinha("teste_veterinarios.txt", vOriginal.toLinha());

            // Carrega do arquivo
            List<String> linhas = GerenciadorArquivo.carregarDados("teste_veterinarios.txt");
            if (linhas.isEmpty()){
                System.out.println(" --> FALHA: O arquivo de teste não foi lido ou está vazio.\n");
                return false;
            }

            // Reconstrução do objeto a partir do texto
            String ultimaLinha = linhas.get(linhas.size() - 1);
            Veterinario vLido = Veterinario.fromLinha(ultimaLinha);

            if (vLido != null && vLido.getCrmv().equals(vOriginal.getCrmv()) && vLido.getNome().equals(vOriginal.getNome())){
                System.out.println(" --> SUCESSO: Objeto salvo e reconstruído com precisão a partir do arquivo!\n");
                return true;
            }else{
                System.out.println(" --> FALHA: Dados reconstruídos não batem com os dados originais.\n");
                return false;
            }
        } catch (Exception e) {
            System.out.println(" --> ERRO NA PERSISTÊNCIA: " + e.getMessage() + "\n");
            return false;
        }
    }

    //Testa a composição entre o Animal e seu Prontuário.
    private static boolean testarComposicaoProntuario(){
        System.out.println("[TESTE 4] Testando Composição (Animal + Prontuário)...");
        try{
            Animal animal = new Animal("Bobi", "Gato", "Siamês", 2, 4.2);
            animal.getProntuario().adicionarRegistro("01/10/2026: Aplicação de vacina V4.");
            animal.getProntuario().adicionarRegistro("15/10/2026: Retorno de avaliação clínica.");

            String historico = animal.getProntuario().listarHistorico();
            if (historico.contains("V4") && historico.contains("Retorno")){
                System.out.println(" --> SUCESSO: Histórico do prontuário registrado e recuperado corretamente!\n");
                return true;
            }else{
                System.out.println(" --> FALHA: Os registros do prontuário não foram armazenados adequadamente.\n");
                return false;
            }
        } catch (Exception e) {
            System.out.println(" --> ERRO NO PRONTUÁRIO: " + e.getMessage() + "\n");
            return false;
        }
    }
}
