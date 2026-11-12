package model;

public class Recepcionista extends Pessoa {

    private String turno;
    private double salario;

    public Recepcionista(String nome, String cpf, String telefone, String endereco, String turno, double salario){
        super(nome, cpf, telefone, endereco);
        this.turno = turno;
        this.salario = salario;
    }

    public String getTurno() {
        return turno;
    }
    public void setTurno(String turno) {
        this.turno = turno;
    }
    public double getSalario() {
        return salario;
    }
    public void setSalario(double salario) {
        this.salario = salario;
    }

}
