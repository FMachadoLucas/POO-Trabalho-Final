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


    public String toLinha(){
        return getNomeServico() + ";" + getValorBase() + ";" + getPorteAnimal() + ";" + isIncluirTosaHigienica() + ";" + getTaxaTosa();
    }

    public static BanhoETosa fromLinha(String linha){
        if(linha == null || linha.trim().isEmpty()){
            return null;
        }

        String[] dados = linha.split(";");
        if(dados.length != 5){
            return null;
        }

        try {
            String nomeServico = dados[0];
            double valorBase = Double.parseDouble(dados[1]);
            String porteAnimal = dados[2];
            boolean incluirTosaHigienica = Boolean.parseBoolean(dados[3]);
            double taxaTosa = Double.parseDouble(dados[4]);

            return new BanhoETosa(nomeServico, valorBase, porteAnimal, incluirTosaHigienica, taxaTosa);
        } catch(NumberFormatException e) {
            return null;
        }
    }
}
