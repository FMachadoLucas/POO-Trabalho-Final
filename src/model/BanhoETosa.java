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
        return "BanhoETosa;" + getNomeServico() + ";" + getValorBase() + ";" + getPorteAnimal() + ";" + isIncluirTosaHigienica() + ";" + getTaxaTosa();
    }

    public static BanhoETosa fromLinha(String linha){
        if(linha == null || linha.trim().isEmpty()){
            return null;
        }

        String[] dados = linha.split(";", -1);
        if(dados.length != 6 || !"BanhoETosa".equals(dados[0])){
            return null;
        }

        try {
            String nomeServico = dados[1];
            double valorBase = Double.parseDouble(dados[2]);
            String porteAnimal = dados[3];
            boolean incluirTosaHigienica = Boolean.parseBoolean(dados[4]);
            double taxaTosa = Double.parseDouble(dados[5]);

            return new BanhoETosa(nomeServico, valorBase, porteAnimal, incluirTosaHigienica, taxaTosa);
        } catch(NumberFormatException e) {
            return null;
        }
    }
}
