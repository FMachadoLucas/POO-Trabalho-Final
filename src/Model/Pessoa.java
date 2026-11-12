package Model;

public abstract class Pessoa {

    //atributos
    private static int proximoId = 1;
    protected int id;
    protected String nome;
    protected String cpf;
    protected String telefone;
    protected String endereco;

    //construtor
    public Pessoa (String nome, String cpf, String telefone, String endereco) {
        this.id = proximoId++;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    //metodos getters e setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }


}
