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

}
