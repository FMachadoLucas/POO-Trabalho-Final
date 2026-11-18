package model;

public class Veterinario extends Pessoa{

    private String crmv;
    private String especialidade;

    public Veterinario(String nome, String cpf, String telefone, String endereco, String crmv, String especialidade){
        super(nome, cpf, telefone, endereco);
        this.crmv = crmv;
        this.especialidade = especialidade;
    }

    public String getCrmv() {
        return crmv;
    }
    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }
    public String getEspecialidade() {
        return especialidade;
    }
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    //passar os dados do Veterinario em uma linha formatada e delimitada por ";"
    public String toLinha(){
        return getNome() + ";" + getCpf() + ";" + getTelefone() + ";" + getEndereco() + ";" + getCrmv() + ";" + getEspecialidade();
    }

    //reconstruir um objeto Veterinario a partir de uma linha formatada e delimitada por ";"
    public static Veterinario fromLinha(String linha){
        if(linha == null || linha.trim().isEmpty()){
            return null;
        }

        String[] dados = linha.split(";", -1);
        if(dados.length != 6){
            return null;
        }

        return new Veterinario(dados[0], dados[1], dados[2], dados[3], dados[4], dados[5]);
    }

}
