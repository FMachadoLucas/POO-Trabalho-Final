package model;

import clinica.exception.ClinicaException;
import java.time.LocalDateTime;

public class Agendamento {//classe que conecta/relaciona as outras classes

    private static int proximoId = 1;
    private int id;
    private java.time.LocalDateTime dataHora;
    private Animal animal;
    private Tutor tutor;
    private Veterinario veterinario;
    private Servico servico;
    private String status;

    public Agendamento(java.time.LocalDateTime dataHora, Animal animal, Tutor tutor, Veterinario veterinario, Servico servico){
        this.id = proximoId++;
        this.dataHora = dataHora;
        this.animal = animal;
        this.tutor = tutor;
        this.veterinario = veterinario;
        this.servico = servico;
        this.status = "Agendado";
    }

    public boolean verificarDisponibilidade(LocalDateTime dataHora, Veterinario veterinario) throws ClinicaException{
        if(dataHora == null || veterinario == null){
            throw new ClinicaException("Data/hora e veterinário devem ser informados!");
        }
        return true;
    }

    public void concluir(){
        this.status = "Concluido";
    }
    public void cancelar(){
        this.status = "Cancelado";
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public java.time.LocalDateTime getDataHora(){
        return this.dataHora;
    }
    public void setDataHora(java.time.LocalDateTime dataHora){
        this.dataHora = dataHora;
    }
    public Animal getAnimal(){
        return this.animal;
    }
    public void setAnimal(Animal animal){
        this.animal = animal;
    }
    public Tutor getTutor(){
        return this.tutor;
    }
    public void setTutor(Tutor tutor){
        this.tutor = tutor;
    }
    public Veterinario getVeterinario(){
        return this.veterinario;
    }
    public void setVeterinario(Veterinario veterinario){
        this.veterinario = veterinario;
    }
    public Servico getServico(){
        return this.servico;
    }
    public void setServico(Servico servico){
        this.servico = servico;
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    public String toLinha(){
        return dataHora + ";"
                + animal.getNome() + ";"
                + tutor.getCpf() + ";"
                + veterinario.getCrmv() + ";"
                + servico.getNomeServico() + ";"
                + status;
    }

    public static Agendamento fromLinha(String linha,
                                        java.util.List<Animal> animais,
                                        java.util.Map<Animal, Tutor> tutorDoAnimal,
                                        java.util.List<Veterinario> veterinarios,
                                        java.util.List<Servico> servicos){
        if(linha == null || linha.trim().isEmpty()){
            return null;
        }

        String[] dados = linha.split(";");
        if(dados.length != 6){
            return null;
        }

        try {
            LocalDateTime dataHora = LocalDateTime.parse(dados[0]);
            String nomeAnimal = dados[1];
            String cpfTutor = dados[2];
            String crmvVet = dados[3];
            String nomeServico = dados[4];
            String status = dados[5];

            Animal animal = animais.stream()
                    .filter(a -> a.getNome().equals(nomeAnimal))
                    .findFirst()
                    .orElse(null);

            Tutor tutor = animal == null ? null : tutorDoAnimal.get(animal);

            Veterinario veterinario = veterinarios.stream()
                    .filter(v -> v.getCrmv().equals(crmvVet))
                    .findFirst()
                    .orElse(null);

            Servico servico = servicos.stream()
                    .filter(s -> s.getNomeServico().equals(nomeServico))
                    .findFirst()
                    .orElse(null);

            if(animal == null || tutor == null || veterinario == null || servico == null){
                return null;
            }

            Agendamento agendamento = new Agendamento(dataHora, animal, tutor, veterinario, servico);
            agendamento.setStatus(status);
            return agendamento;

        } catch (Exception e) {
            return null;
        }
    }

}
