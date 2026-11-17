package model;

public class Vacinacao extends Servico{

    private String nomeVacina;
    private double custoDose;

    public Vacinacao(String nomeServico, double valorBase, String nomeVacina, double custoDose){
        super(nomeServico, valorBase);
        this.nomeVacina = nomeVacina;
        this.custoDose = custoDose;
    }


    public String getNomeVacina() {
        return nomeVacina;
    }
    public void setNomeVacina(String nomeVacina) {
        this.nomeVacina = nomeVacina;
    }
    public double getCustoDose() {
        return custoDose;
    }
    public void setCustoDose(double custoDose) {
        this.custoDose = custoDose;
    }

    @Override
    public double calcularValorFinal() {
        return this.valorBase + this.custoDose;
    }


    public String toLinha(){
        return getNomeServico() + ";" + getValorBase() + ";" + getNomeVacina() + ";" + getCustoDose();
    }

    public static Vacinacao fromLinha(String linha){
        if(linha == null || linha.trim().isEmpty()){
            return null;
        }

        String[] dados = linha.split(";");
        if(dados.length != 4){
            return null;
        }

        try {
            String nomeServico = dados[0];
            double valorBase = Double.parseDouble(dados[1]);
            String nomeVacina = dados[2];
            double custoDose = Double.parseDouble(dados[3]);

            return new Vacinacao(nomeServico, valorBase, nomeVacina, custoDose);
        } catch(NumberFormatException e){
            return null;
        }
    }
}
