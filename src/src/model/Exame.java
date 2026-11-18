package model;

public class Exame extends Servico{

    private String tipoExame;
    private double taxaLaboratorial;

    public Exame(String nomeServico, double valorBase, String tipoExame, double taxaLaboratorial){
        super(nomeServico, valorBase);
        this.tipoExame = tipoExame;
        this.taxaLaboratorial = taxaLaboratorial;
    }

    public String getTipoExame(){
        return tipoExame;
    }
    public void setTipoExame(String tipoExame){
        this.tipoExame = tipoExame;
    }
    public double getTaxaLaboratorial(){
        return taxaLaboratorial;
    }
    public void setTaxaLaboratorial(double taxaLaboratorial){
        this.taxaLaboratorial = taxaLaboratorial;
    }

    @Override
    public double calcularValorFinal() {
        return this.valorBase + this.taxaLaboratorial;
    }

}
