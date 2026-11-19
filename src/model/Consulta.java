package model;

public class Consulta extends Servico{

    private String motivo;
    private String diagnostico;

    public Consulta(String nomeServico, double valorBase, String motivo, String diagnostico){
        super(nomeServico, valorBase);
        this.motivo = motivo;
        this.diagnostico = diagnostico;
    }


    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    public String getDiagnostico() {
        return diagnostico;
    }
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    @Override
    public double calcularValorFinal() {
        return this.valorBase;
    }


    public String toLinha(){
        return "Consulta;" + getNomeServico() + ";" + getValorBase() + ";" + getMotivo() + ";" + getDiagnostico();
    }

    public static Consulta fromLinha(String linha){
        if(linha == null || linha.trim().isEmpty()){
            return null;
        }

        String[] dados = linha.split(";", -1);
        if(dados.length != 5 || !"Consulta".equals(dados[0])){
            return null;
        }

        try {
            String nomeServico = dados[1];
            double valorBase = Double.parseDouble(dados[2]);
            String motivo = dados[3];
            String diagnostico = dados[4];

            return new Consulta(nomeServico, valorBase, motivo, diagnostico);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
