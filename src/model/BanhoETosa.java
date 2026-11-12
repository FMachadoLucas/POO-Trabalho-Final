package model;

public class BanhoETosa extends Servico{

    private String porteAnimal;
    private boolean incluirTosaHigienica;
    private double taxaTosa;

    public BanhoETosa(String nomeServico, double valorBase, String porteAnimal, boolean incluirTosaHigienica, double taxaTosa){
        super(nomeServico, valorBase);
        this.porteAnimal = porteAnimal;
        this.incluirTosaHigienica = incluirTosaHigienica;
        this.taxaTosa = taxaTosa;
    }

    public String getPorteAnimal() {
        return porteAnimal;
    }
    public void setPorteAnimal(String porteAnimal) {
        this.porteAnimal = porteAnimal;
    }
    public boolean isIncluirTosaHigienica() {
        return incluirTosaHigienica;
    }
    public void setIncluirTosaHigienica(boolean incluirTosaHigienica) {
        this.incluirTosaHigienica = incluirTosaHigienica;
    }
    public double getTaxaTosa() {
        return taxaTosa;
    }
    public void setTaxaTosa(double taxaTosa) {
        this.taxaTosa = taxaTosa;
    }

    @Override
    public double calcularValorFinal() {
        if(this.incluirTosaHigienica){
            return this.valorBase + this.taxaTosa;
        }else{
            return this.valorBase;
        }
    }

}
