package model;

public class Animal {

    private static int proximoId = 1;
    private int id;
    private String nome;
    private String especie;
    private String raca;
    private int idade;
    private double peso;
    private Prontuario prontuario;//composicao

    public Animal(String nome, String especie, String raca, int idade, double peso){
        this.id = proximoId++;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.peso = peso;
        this.prontuario = new Prontuario();
    }


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
    public String getEspecie() {
        return especie;
    }
    public void setEspecie(String especie) {
        this.especie = especie;
    }
    public String getRaca() {
        return raca;
    }
    public void setRaca(String raca) {
        this.raca = raca;
    }
    public int getIdade() {
        return idade;
    }
    public void setIdade(int idade) {
        this.idade = idade;
    }
    public double getPeso() {
        return peso;
    }
    public void setPeso(double peso) {
        this.peso = peso;
    }
    public Prontuario getProntuario() {
        return prontuario;
    }


    public String toLinha(){
        return this.nome + ";" + this.especie + ";" + this.raca + ";" + this.idade + ";" + this.peso;
    }

    public static Animal fromLinha(String linha){
        if(linha == null || linha.trim().isEmpty()){
            return null;
        }

        String[] dados = linha.split(";");
        if(dados.length != 5){
            return null;
        }

        //reconstruir o objeto Animal na memoria
        try {
            String nome = dados[0];
            String especie = dados[1];
            String raca = dados[2];
            int idade = Integer.parseInt(dados[3]);
            double peso = Double.parseDouble(dados[4]);

            return new Animal(nome, especie, raca, idade, peso);

        }catch (NumberFormatException e){
            return null;//se ocorrer erro de conversao
        }
    }
}
