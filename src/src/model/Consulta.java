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

}
