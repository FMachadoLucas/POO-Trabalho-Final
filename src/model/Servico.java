package model;

public abstract class Servico {

    //atributos
    private static int proximoId = 1;
    protected int id;
    protected String nomeServico;
    protected double valorBase;

    //construtor
    public  Servico(String nomeServico, double valorBase){
        this.id = proximoId++;
        this.nomeServico = nomeServico;
        this.valorBase = valorBase;
    }

    //setters e getters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNomeServico() {
        return nomeServico;
    }
    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }
    public double getValorBase() {
        return valorBase;
    }
    public void setValorBase(double valorBase) {
        this.valorBase = valorBase;
    }

    public abstract double calcularValorFinal();
}
