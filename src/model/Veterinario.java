package model;

public class Veterinario extends Pessoa{

    private String crmv;
    private String especialidade;

    public Veterinario(String nome, String cpf, String telefone, String endereco, String crmv, String especialidade){
        super(nome, cpf, telefone, endereco);
        this.crmv = crmv;
        this.especialidade = especialidade;
    }

    public String crmv() {
        return crmv;
    }
    public void crmv(String crmv) {
        this.crmv = crmv;
    }
    public String getEspecialidade() {
        return especialidade;
    }
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

}
