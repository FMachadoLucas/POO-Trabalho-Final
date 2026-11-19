package clinica;

import clinica.exception.ClinicaException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import model.*;
import util.GerenciadorArquivo;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class MainFX extends Application {

    private ArrayList<Tutor> tutores = new ArrayList<>();
    private ArrayList<Animal> animais = new ArrayList<>();
    private ArrayList<Veterinario> veterinarios = new ArrayList<>();
    private ArrayList<Servico> servicos = new ArrayList<>();
    private ArrayList<Agendamento> agendamentos = new ArrayList<>();
    private ArrayList<Pagamento> pagamentos = new ArrayList<>();
    private ArrayList<Agendamento> pendentes = new ArrayList<>(); // ainda não pagos
    // tutor e animal são cadastrados juntos: o tutor do animal i está na posição i de tutores

    private DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private String LARANJA = "#ff9f1c";

    private ListView<String> listaAnimais = new ListView<>(), listaVets = new ListView<>(),
            listaServicos = new ListView<>(), listaAgendamentos = new ListView<>(),
            listaPagamentos = new ListView<>();
    private ComboBox<String> comboAnimalAgenda = new ComboBox<>(), comboAnimalPront = new ComboBox<>(),
            comboVet = new ComboBox<>(), comboServico = new ComboBox<>(), comboPendentes = new ComboBox<>();
    private Button btnMusica = new Button("♪ Música: ligada");
    private Clip musica;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        TabPane abas = new TabPane(criarAbaTutorAnimal(), criarAbaVeterinario(), criarAbaServico(),
                criarAbaAgendamento(), criarAbaPagamento(), criarAbaProntuario(), criarAbaRelatorio());
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane raiz = new BorderPane();
        raiz.setTop(criarCabecalho());
        raiz.setCenter(abas);
        raiz.setStyle("-fx-background-color: #fffaeb; -fx-accent: " + LARANJA + ";");

        stage.setTitle("Clínica Veterinária Hina e Francisco");
        stage.setScene(new Scene(raiz, 1000, 700));
        stage.show();
    }

    @Override
    public void stop() {
        if (musica != null) {
            musica.close();
        }
    }

    private HBox criarCabecalho() {
        Label titulo = new Label("Clínica Veterinária Hina e Francisco");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subtitulo = new Label("Cuidando com carinho de cada pet");
        subtitulo.setStyle("-fx-text-fill: white;");

        // música: toca o arquivo musica.wav, que fica dentro da pasta src (junto com o projeto)
        try {
            musica = AudioSystem.getClip();
            musica.open(AudioSystem.getAudioInputStream(MainFX.class.getResource("/musica.wav")));
            musica.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ex) {
            System.out.println("Não foi possível tocar a música: " + ex.getMessage());
            btnMusica.setText("♪ sem música");
        }
        btnMusica.setStyle("-fx-background-color: white; -fx-text-fill: " + LARANJA + "; -fx-background-radius: 15;");
        btnMusica.setOnAction(e -> {
            if (musica == null) {
                return;
            }
            if (musica.isRunning()) {
                musica.stop();
                btnMusica.setText("♪ Música: desligada");
            } else {
                musica.loop(Clip.LOOP_CONTINUOUSLY);
                btnMusica.setText("♪ Música: ligada");
            }
        });

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        HBox cabecalho = new HBox(10, criarGatinho(), criarCachorrinho(), new VBox(2, titulo, subtitulo),
                espaco, btnMusica, criarCachorrinho(), criarGatinho());
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(10, 20, 10, 20));
        cabecalho.setStyle("-fx-background-color: linear-gradient(to right, #ffd54f, " + LARANJA + ");");
        return cabecalho;
    }

    private Group criarGatinho() {
        Polygon orelhaEsq = new Polygon(8, 20, 10, 2, 22, 12);
        Polygon orelhaDir = new Polygon(52, 20, 50, 2, 38, 12);
        orelhaEsq.setFill(Color.web("#ffb74d"));
        orelhaDir.setFill(Color.web("#ffb74d"));
        return new Group(orelhaEsq, orelhaDir, new Circle(30, 32, 22, Color.web("#fff3e0")),
                new Circle(22, 28, 3), new Circle(38, 28, 3), new Circle(30, 38, 3, Color.PINK));
    }

    private Group criarCachorrinho() {
        Ellipse orelhaEsq = new Ellipse(8, 28, 7, 15);
        Ellipse orelhaDir = new Ellipse(52, 28, 7, 15);
        orelhaEsq.setFill(Color.SADDLEBROWN);
        orelhaDir.setFill(Color.SADDLEBROWN);
        return new Group(orelhaEsq, orelhaDir, new Circle(30, 32, 22, Color.web("#f3d3a0")),
                new Circle(22, 28, 3), new Circle(38, 28, 3), new Ellipse(30, 40, 5, 3.5));
    }

    // ---------- abas ----------

    private Tab criarAbaTutorAnimal() {
        TextField nomeTutor = new TextField(), cpf = new TextField(), telefone = new TextField(),
                endereco = new TextField();
        TextField nomeAnimal = new TextField(), especie = new TextField(), raca = new TextField(),
                idade = new TextField(), peso = new TextField();

        HBox formularios = new HBox(40,
                formulario(new String[]{"Nome do tutor:", "CPF:", "Telefone:", "Endereço:"},
                        new Control[]{nomeTutor, cpf, telefone, endereco}),
                formulario(new String[]{"Nome do animal:", "Espécie:", "Raça:", "Idade:", "Peso (kg):"},
                        new Control[]{nomeAnimal, especie, raca, idade, peso}));

        Button btn = botao("Cadastrar tutor e animal");
        btn.setOnAction(e -> {
            try {
                if (nomeTutor.getText().isEmpty() || cpf.getText().isEmpty() || nomeAnimal.getText().isEmpty()) {
                    throw new ClinicaException("Preencha o nome e o CPF do tutor e o nome do animal!");
                }
                int idadeAnimal = Integer.parseInt(idade.getText());
                double pesoAnimal = Double.parseDouble(peso.getText().replace(",", "."));
                tutores.add(new Tutor(nomeTutor.getText(), cpf.getText(), telefone.getText(), endereco.getText()));
                animais.add(new Animal(nomeAnimal.getText(), especie.getText(), raca.getText(), idadeAnimal, pesoAnimal));
                atualizarTudo();
                limpar(nomeTutor, cpf, telefone, endereco, nomeAnimal, especie, raca, idade, peso);
                aviso("Tutor e animal cadastrados!");
            } catch (NumberFormatException ex) {
                erro("Idade e peso precisam ser números!");
            } catch (ClinicaException ex) {
                erro(ex.getMessage());
            }
        });
        return criarAba("1. Tutor e Animal", formularios, btn, listaAnimais);
    }

    private Tab criarAbaVeterinario() {
        TextField nome = new TextField(), cpf = new TextField(), telefone = new TextField(),
                endereco = new TextField(), crmv = new TextField(), especialidade = new TextField();

        GridPane form = formulario(new String[]{"Nome:", "CPF:", "Telefone:", "Endereço:", "CRMV:", "Especialidade:"},
                new Control[]{nome, cpf, telefone, endereco, crmv, especialidade});

        Button btn = botao("Cadastrar veterinário");
        btn.setOnAction(e -> {
            try {
                if (nome.getText().isEmpty() || cpf.getText().isEmpty() || crmv.getText().isEmpty()) {
                    throw new ClinicaException("Preencha pelo menos o nome, o CPF e o CRMV!");
                }
                veterinarios.add(new Veterinario(nome.getText(), cpf.getText(), telefone.getText(),
                        endereco.getText(), crmv.getText(), especialidade.getText()));
                atualizarTudo();
                limpar(nome, cpf, telefone, endereco, crmv, especialidade);
                aviso("Veterinário cadastrado!");
            } catch (ClinicaException ex) {
                erro(ex.getMessage());
            }
        });
        return criarAba("2. Veterinário", form, btn, listaVets);
    }

    private Tab criarAbaServico() {
        ComboBox<String> tipo = new ComboBox<>();
        tipo.getItems().addAll("Consulta", "Vacinação", "Exame", "Banho e Tosa");
        tipo.setValue("Consulta");
        TextField nome = new TextField(), valor = new TextField(), detalhe1 = new TextField(),
                detalhe2 = new TextField();
        CheckBox tosa = new CheckBox("Incluir (só para Banho e Tosa)");

        // o texto de ajuda dos campos muda conforme o tipo
        detalhe1.setPromptText("Motivo");
        detalhe2.setPromptText("Diagnóstico");
        tipo.setOnAction(e -> {
            if (tipo.getValue().equals("Consulta")) {
                detalhe1.setPromptText("Motivo");
                detalhe2.setPromptText("Diagnóstico");
            } else if (tipo.getValue().equals("Vacinação")) {
                detalhe1.setPromptText("Nome da vacina");
                detalhe2.setPromptText("Custo da dose (R$)");
            } else if (tipo.getValue().equals("Exame")) {
                detalhe1.setPromptText("Tipo de exame");
                detalhe2.setPromptText("Taxa laboratorial (R$)");
            } else {
                detalhe1.setPromptText("Porte do animal");
                detalhe2.setPromptText("Taxa de tosa (R$)");
            }
        });

        GridPane form = formulario(
                new String[]{"Tipo:", "Nome do serviço:", "Valor base (R$):", "Detalhe 1:", "Detalhe 2:", "Tosa higiênica:"},
                new Control[]{tipo, nome, valor, detalhe1, detalhe2, tosa});

        Button btn = botao("Cadastrar serviço");
        btn.setOnAction(e -> {
            try {
                if (nome.getText().isEmpty()) {
                    throw new ClinicaException("Digite o nome do serviço!");
                }
                double base = Double.parseDouble(valor.getText().replace(",", "."));
                if (tipo.getValue().equals("Consulta")) {
                    servicos.add(new Consulta(nome.getText(), base, detalhe1.getText(), detalhe2.getText()));
                } else {
                    double extra = Double.parseDouble(detalhe2.getText().replace(",", "."));
                    if (tipo.getValue().equals("Vacinação")) {
                        servicos.add(new Vacinacao(nome.getText(), base, detalhe1.getText(), extra));
                    } else if (tipo.getValue().equals("Exame")) {
                        servicos.add(new Exame(nome.getText(), base, detalhe1.getText(), extra));
                    } else {
                        servicos.add(new BanhoETosa(nome.getText(), base, detalhe1.getText(), tosa.isSelected(), extra));
                    }
                }
                atualizarTudo();
                limpar(nome, valor, detalhe1, detalhe2);
                tosa.setSelected(false);
                aviso("Serviço cadastrado!");
            } catch (NumberFormatException ex) {
                erro("Os valores em R$ precisam ser números!");
            } catch (ClinicaException ex) {
                erro(ex.getMessage());
            }
        });
        return criarAba("3. Serviço", form, btn, listaServicos);
    }

    private Tab criarAbaAgendamento() {
        DatePicker data = new DatePicker();
        TextField hora = new TextField("09:00");

        GridPane form = formulario(new String[]{"Animal:", "Veterinário:", "Serviço:", "Data:", "Hora (HH:mm):"},
                new Control[]{comboAnimalAgenda, comboVet, comboServico, data, hora});

        Button btn = botao("Realizar agendamento");
        btn.setOnAction(e -> {
            try {
                int a = comboAnimalAgenda.getSelectionModel().getSelectedIndex();
                int v = comboVet.getSelectionModel().getSelectedIndex();
                int s = comboServico.getSelectionModel().getSelectedIndex();
                if (a < 0 || v < 0 || s < 0 || data.getValue() == null) {
                    throw new ClinicaException("Escolha o animal, o veterinário, o serviço e a data!");
                }
                Veterinario vet = veterinarios.get(v);
                LocalDateTime dataHora = LocalDateTime.of(data.getValue(), LocalTime.parse(hora.getText()));

                for (Agendamento outro : agendamentos) {
                    if (outro.getVeterinario() == vet && outro.getDataHora().equals(dataHora)
                            && outro.getStatus().equals("Agendado")) {
                        throw new ClinicaException("Esse veterinário já tem agendamento nesse horário!");
                    }
                }
                Agendamento ag = new Agendamento(dataHora, animais.get(a), tutores.get(a), vet, servicos.get(s));
                ag.verificarDisponibilidade(dataHora, vet);
                agendamentos.add(ag);
                atualizarTudo();
                aviso("Agendamento realizado!");
            } catch (DateTimeParseException ex) {
                erro("Hora inválida! Use o formato HH:mm, por exemplo 14:30");
            } catch (ClinicaException ex) {
                erro(ex.getMessage());
            }
        });
        return criarAba("4. Agendamento", form, btn, listaAgendamentos);
    }

    private Tab criarAbaPagamento() {
        ComboBox<String> forma = new ComboBox<>();
        forma.getItems().addAll("Dinheiro", "Pix", "Cartão de crédito", "Cartão de débito");
        forma.setValue("Pix");
        comboPendentes.setPrefWidth(500);

        GridPane form = formulario(new String[]{"Agendamento:", "Forma de pagamento:"},
                new Control[]{comboPendentes, forma});

        Button btn = botao("Processar pagamento");
        btn.setOnAction(e -> {
            try {
                int p = comboPendentes.getSelectionModel().getSelectedIndex();
                if (p < 0) {
                    throw new ClinicaException("Escolha um agendamento para pagar!");
                }
                Pagamento pagamento = new Pagamento(pendentes.get(p), forma.getValue());
                pagamento.processarPagamento(); // marca o agendamento como concluído
                pagamentos.add(pagamento);
                atualizarTudo();
                aviso("Pagamento realizado!");
            } catch (ClinicaException ex) {
                erro(ex.getMessage());
            }
        });
        return criarAba("5. Pagamento", form, btn, listaPagamentos);
    }

    private Tab criarAbaProntuario() {
        TextArea historico = new TextArea(), nota = new TextArea();
        historico.setEditable(false);
        historico.setWrapText(true);
        nota.setPrefRowCount(3);
        nota.setPromptText("Digite a nova anotação...");
        VBox.setVgrow(historico, Priority.ALWAYS);

        comboAnimalPront.setOnAction(e -> {
            int pos = comboAnimalPront.getSelectionModel().getSelectedIndex();
            if (pos >= 0) {
                historico.setText(animais.get(pos).getProntuario().listarHistorico());
            } else {
                historico.clear();
            }
        });

        Button btn = botao("Registrar no prontuário");
        btn.setOnAction(e -> {
            try {
                int pos = comboAnimalPront.getSelectionModel().getSelectedIndex();
                if (pos < 0 || nota.getText().trim().isEmpty()) {
                    throw new ClinicaException("Escolha o animal e digite a anotação!");
                }
                Prontuario prontuario = animais.get(pos).getProntuario();
                prontuario.adicionarRegisto("[" + LocalDateTime.now().format(formato) + "] " + nota.getText());
                historico.setText(prontuario.listarHistorico());
                nota.clear();
                aviso("Anotação registrada!");
            } catch (ClinicaException ex) {
                erro(ex.getMessage());
            }
        });
        return criarAba("6. Prontuário", new HBox(10, new Label("Animal:"), comboAnimalPront), historico, nota, btn);
    }

    private Tab criarAbaRelatorio() {
        TextArea area = new TextArea();
        area.setEditable(false);
        VBox.setVgrow(area, Priority.ALWAYS);

        Button btn = botao("Gerar e salvar relatório");
        btn.setOnAction(e -> {
            double total = 0;
            for (Pagamento p : pagamentos) {
                total += p.getValorPago();
            }
            String texto = "RELATÓRIO - CLÍNICA VETERINÁRIA HINA E FRANCISCO\n"
                    + "Gerado em: " + LocalDateTime.now().format(formato)
                    + "\n\nANIMAIS\n" + String.join("\n", listaAnimais.getItems())
                    + "\n\nVETERINÁRIOS\n" + String.join("\n", listaVets.getItems())
                    + "\n\nSERVIÇOS\n" + String.join("\n", listaServicos.getItems())
                    + "\n\nAGENDAMENTOS\n" + String.join("\n", listaAgendamentos.getItems())
                    + "\n\nPAGAMENTOS\n" + String.join("\n", listaPagamentos.getItems())
                    + "\nTotal arrecadado: " + String.format("R$ %.2f", total) + "\n\nPRONTUÁRIOS\n";
            for (Animal a : animais) {
                texto += a.getNome() + ":\n" + a.getProntuario().listarHistorico() + "\n\n";
            }
            area.setText(texto);
            GerenciadorArquivo.salvarDados("relatorio.txt", texto);
            aviso("Relatório salvo em dados/relatorio.txt");
        });
        return criarAba("7. Relatório", btn, area);
    }

    // ---------- atualiza todas as listas e caixas de seleção ----------

    private void atualizarTudo() {
        ArrayList<String> textos = new ArrayList<>();
        for (int i = 0; i < animais.size(); i++) {
            Animal a = animais.get(i);
            textos.add(a.getId() + " - " + a.getNome() + " (" + a.getEspecie() + ") - Tutor: " + tutores.get(i).getNome());
        }
        listaAnimais.getItems().setAll(textos);
        comboAnimalAgenda.getItems().setAll(textos);
        comboAnimalPront.getItems().setAll(textos);

        textos = new ArrayList<>();
        for (Veterinario v : veterinarios) {
            textos.add(v.getId() + " - " + v.getNome() + " - CRMV: " + v.crmv() + " - " + v.getEspecialidade());
        }
        listaVets.getItems().setAll(textos);
        comboVet.getItems().setAll(textos);

        textos = new ArrayList<>();
        for (Servico s : servicos) {
            textos.add(s.getId() + " - " + s.getClass().getSimpleName() + " - " + s.getNomeServico()
                    + " - " + String.format("R$ %.2f", s.calcularValorFinal()));
        }
        listaServicos.getItems().setAll(textos);
        comboServico.getItems().setAll(textos);

        textos = new ArrayList<>();
        ArrayList<String> textosPendentes = new ArrayList<>();
        pendentes.clear();
        for (Agendamento ag : agendamentos) {
            String linha = "#" + ag.getId() + " | " + ag.getDataHora().format(formato) + " | "
                    + ag.getAnimal().getNome() + " | " + ag.getVeterinario().getNome() + " | "
                    + ag.getServico().getNomeServico();
            textos.add(linha + " | " + ag.getStatus());
            if (ag.getStatus().equals("Agendado")) {
                pendentes.add(ag);
                textosPendentes.add(linha + " | " + String.format("R$ %.2f", ag.getServico().calcularValorFinal()));
            }
        }
        listaAgendamentos.getItems().setAll(textos);
        comboPendentes.getItems().setAll(textosPendentes);

        textos = new ArrayList<>();
        for (Pagamento p : pagamentos) {
            textos.add("#" + p.getId() + " | " + p.getAgendamento().getAnimal().getNome() + " | "
                    + p.getAgendamento().getServico().getNomeServico() + " | "
                    + String.format("R$ %.2f", p.getValorPago()) + " | " + p.getFormaPagamento());
        }
        listaPagamentos.getItems().setAll(textos);
    }

    // ---------- métodos auxiliares ----------

    private Tab criarAba(String titulo, Node... itens) {
        VBox caixa = new VBox(12, itens);
        caixa.setPadding(new Insets(15));
        for (Node item : itens) {
            if (item instanceof ListView) {
                VBox.setVgrow(item, Priority.ALWAYS);
            }
        }
        return new Tab(titulo, caixa);
    }

    private GridPane formulario(String[] rotulos, Control[] campos) {
        GridPane grade = new GridPane();
        grade.setHgap(10);
        grade.setVgap(8);
        for (int i = 0; i < rotulos.length; i++) {
            grade.add(new Label(rotulos[i]), 0, i);
            grade.add(campos[i], 1, i);
        }
        return grade;
    }

    private Button botao(String texto) {
        Button b = new Button(texto);
        b.setStyle("-fx-background-color: " + LARANJA + "; -fx-text-fill: white; -fx-font-weight: bold;"
                + " -fx-background-radius: 15;");
        return b;
    }

    private void limpar(TextField... campos) {
        for (TextField c : campos) {
            c.clear();
        }
    }

    private void aviso(String texto) {
        Alert alerta = new Alert(AlertType.INFORMATION, texto);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void erro(String texto) {
        Alert alerta = new Alert(AlertType.WARNING, texto);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
