package model;

public class Pagamento {

    private static int proximoId = 1;
    private int id;
    private Agendamento agendamento;
    private double valorPago;
    private String formaPagamento;
    private java.time.LocalDate dataPagamento;

    public Pagamento(Agendamento agendamento, String formaPagamento){
        if (agendamento == null) {
            throw new IllegalArgumentException("Agendamento não pode ser nulo.");
        }

        if (formaPagamento == null || formaPagamento.trim().isEmpty()) {
            throw new IllegalArgumentException("Forma de pagamento é obrigatória.");
        }

        double valor = agendamento.getServico().calcularValorFinal();

        if (valor < 0 || Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new IllegalArgumentException("Valor do pagamento inválido.");
        }

        this.id = proximoId++;
        this.dataPagamento = java.time.LocalDate.now();
        this.valorPago = valor;
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

    public String toLinha(){
        return agendamento.getId() + ";"
                + valorPago + ";"
                + formaPagamento + ";"
                + dataPagamento;
    }

    public static Pagamento fromLinha(String linha, java.util.List<Agendamento> agendamentos) {
        if (linha == null || linha.trim().isEmpty()) {
            return null;
        }

        String[] dados = linha.split(";", -1);
        if (dados.length != 4) {
            return null;
        }

        try {
            int idAgendamento = Integer.parseInt(dados[0]);
            double valorPago = Double.parseDouble(dados[1]);
            String formaPagamento = dados[2];
            java.time.LocalDate dataPagamento = java.time.LocalDate.parse(dados[3]);

            Agendamento agendamento = agendamentos.stream()
                    .filter(a -> a.getId() == idAgendamento)
                    .findFirst()
                    .orElse(null);

            if (agendamento == null) {
                return null;
            }

            Pagamento pagamento = new Pagamento(agendamento, formaPagamento);
            pagamento.setValorPago(valorPago);
            pagamento.setDataPagamento(dataPagamento);
            return pagamento;

        } catch (Exception e) {
            return null;
        }
    }
}
