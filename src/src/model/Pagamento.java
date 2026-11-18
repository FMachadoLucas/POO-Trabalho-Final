package model;

public class Pagamento {

    private static int proximoId = 1;
    private int id;
    private Agendamento agendamento;
    private double valorPago;
    private String formaPagamento;
    private java.time.LocalDate dataPagamento;

    public Pagamento(Agendamento agendamento, String formaPagamento){
        this.id = proximoId++;
        this.dataPagamento = java.time.LocalDate.now();
        this.valorPago = agendamento.getServico().calcularValorFinal();
        this.agendamento = agendamento;
        this.formaPagamento = formaPagamento;
    }

    public void processarPagamento(){
        if(this.agendamento != null){
            agendamento.concluir();
        }
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }
    public Agendamento getAgendamento(){
        return this.agendamento;
    }
    public void setAgendamento(Agendamento agendamento){
        this.agendamento = agendamento;
    }
    public double getValorPago(){
        return this.valorPago;
    }
    public void setValorPago(double valorPago){
        this.valorPago = valorPago;
    }
    public String getFormaPagamento(){
        return this.formaPagamento;
    }
    public void setFormaPagamento(String formaPagamento){
        this.formaPagamento = formaPagamento;
    }
    public java.time.LocalDate getDataPagamento(){
        return this.dataPagamento;
    }
    public void setDataPagamento(java.time.LocalDate dataPagamento){
        this.dataPagamento = dataPagamento;
    }

}
