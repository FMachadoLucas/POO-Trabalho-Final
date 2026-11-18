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


    public String toLinha(){
        return getNomeServico() + ";" + getValorBase() + ";" + getTipoExame() + ";" + getTaxaLaboratorial();
    }

    public static Exame fromLinha(String linha){
        if(linha == null || linha.trim().isEmpty()){
            return null;
        }

        String[] dados = linha.split(";", -1);
        if(dados.length != 4){
            return null;
        }

        try {
            String nomeServico = dados[0];
            double valorBase = Double.parseDouble(dados[1]);
            String tipoExame = dados[2];
            double taxaLaboratorial = Double.parseDouble(dados[3]);

            return new Exame(nomeServico, valorBase, tipoExame, taxaLaboratorial);
        } catch(NumberFormatException e){
            return null;
        }
    }
}
