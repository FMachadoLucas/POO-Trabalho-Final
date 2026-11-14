package model;

import java.util.List;
import java.util.ArrayList;

public class Prontuario {

    private static int proximoId = 1;
    private int id;
    private List<String> historicoEntrada = new ArrayList<>();

    public Prontuario(){
        this.id = proximoId++;
        this.historicoEntrada = new ArrayList<>();
    }

    //metodos
    //adiciona uma nova anotacao clinica a lista
    public void adicionarRegisto(String registro){
        this.historicoEntrada.add(registro);
    }

    public List<String> getHistoricoEntrada(){
        return this.historicoEntrada;
    }

    //retorna todos os registros em texto
    public String listarHistorico(){
        if(this.historicoEntrada.isEmpty()){
            return "Nenhum registro no prontuário.";
        }
        return String.join("\n", this.historicoEntrada);
    }
    public int getId(){
        return this.id;
    }

}
