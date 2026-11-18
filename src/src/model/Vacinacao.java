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

}
