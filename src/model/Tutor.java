package model;

public class Tutor extends Pessoa{

    public Tutor(String nome, String cpf, String telefone, String endereco){
        super(nome, cpf, telefone, endereco);
    }

    //passar os dados do Tutor em uma linha formatada e delimitada por ";"
    public String toLinha() {
        return getNome() + ";" + getCpf() + ";" + getTelefone() + ";" + getEndereco();
    }

    //reconstruir um objeto Tutor a partir de uma linha formatada e delimitada por ";"
    public static Tutor fromLinha(String linha) {
        if(linha == null || linha.trim().isEmpty()) {
            return null;
        }

        //separar os dados da linha por ";"
        String[] dados = linha.split(";");
        if(dados.length != 4) {
            return new Tutor(dados[0], dados[1], dados[2], dados[3]);
        }
        return null;//conferir essa parte do codigo se da para melhorar
    }

}
