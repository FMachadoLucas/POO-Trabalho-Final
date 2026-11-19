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
        return "Vacinacao;" + getNomeServico() + ";" + getValorBase() + ";" + getNomeVacina() + ";" + getCustoDose();
    }

    public static Vacinacao fromLinha(String linha){
        if(linha == null || linha.trim().isEmpty()){
            return null;
        }

        String[] dados = linha.split(";", -1);
        if(dados.length != 5 || !"Vacinacao".equals(dados[0])){
            return null;
        }

        try {
            String nomeServico = dados[1];
            double valorBase = Double.parseDouble(dados[2]);
            String nomeVacina = dados[3];
            double custoDose = Double.parseDouble(dados[4]);

            return new Vacinacao(nomeServico, valorBase, nomeVacina, custoDose);
        } catch(NumberFormatException e){
            return null;
        }
    }
}
