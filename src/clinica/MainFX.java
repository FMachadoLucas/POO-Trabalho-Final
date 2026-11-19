package clinica;

import clinica.exception.ClinicaException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.*;
import util.GerenciadorArquivo;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MainFX extends Application {

    // ===== dados na memória (equivalente às listas do Main antigo) =====
    private final ObservableList<Tutor> tutores = FXCollections.observableArrayList();
    private final ObservableList<Animal> animais = FXCollections.observableArrayList();
    private final ObservableList<Veterinario> veterinarios = FXCollections.observableArrayList();
    private final ObservableList<Servico> servicos = FXCollections.observableArrayList();
    private final ObservableList<Agendamento> agendamentos = FXCollections.observableArrayList();
    private final ObservableList<Pagamento> pagamentos = FXCollections.observableArrayList();
    private final Map<Animal, Tutor> tutorDoAnimal = new HashMap<>();

    private final FilteredList<Agendamento> agendamentosPendentes =
            new FilteredList<>(agendamentos, a -> "Agendado".equals(a.getStatus()));

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ===== cores do tema amarelinho e laranja =====
    private static final String LARANJA = "#ff9f1c";
    private static final String LARANJA_ESCURO = "#f57c00";
    private static final String LARANJA_CLARO = "#ffd8a1";
    private static final String AMARELO_BEBE = "#fff0bf";
    private static final String AMARELO = "#ffe08a";
    private static final String FUNDO = "#fffaeb";
    private static final String TEXTO = "#7a5230";

    private int contadorPlaceholder = 0;
    private final MusicaFofa musica = new MusicaFofa();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        //carrega os dados salvos no disco antes de montar a tela
        carregarDadosIniciais();

        //montagem normal das abas da interface
        TabPane abas = new TabPane(
                abaTutorAnimal(),
                abaVeterinario(),
                abaServico(),
                abaAgendamento(),
                abaPagamento(),
                abaRegistroProntuario(),
                abaVisualizarProntuario(),
                abaRelatorio()
        );
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        estilizarAbas(abas);

        Label titulo = new Label("Clínica Veterinária Hina e Francisco");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;"
                + "-fx-effect: dropshadow(gaussian, rgba(120,70,0,0.35), 4, 0, 0, 1);");
        Label subtitulo = new Label("Cuidando com carinho de cada pet");
        subtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #fffde7;");
        VBox textos = new VBox(2, titulo, subtitulo);
        textos.setAlignment(Pos.CENTER_LEFT);
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        Button btnMusica = new Button("\u266A Música: ligada");
        String estiloMusica = "-fx-background-color: white; -fx-background-insets: 0; -fx-background-radius: 20;"
                + "-fx-text-fill: #f57c00; -fx-font-weight: bold; -fx-padding: 6 14; -fx-cursor: hand;";
        btnMusica.setStyle(estiloMusica);
        btnMusica.setOnMouseEntered(e -> btnMusica.setStyle(estiloMusica.replace("white", "#fffde7")));
        btnMusica.setOnMouseExited(e -> btnMusica.setStyle(estiloMusica));
        btnMusica.setOnAction(e -> {
            if (musica.estaTocando()) {
                musica.pausar();
                btnMusica.setText("\u266A Música: desligada");
            } else if (musica.iniciar()) {
                btnMusica.setText("\u266A Música: ligada");
            }
        });

        HBox topo = new HBox(10, carinhaGato(58), carinhaCachorro(58), textos, espaco, btnMusica,
                carinhaCachorro(58), carinhaGato(58));
        topo.setAlignment(Pos.CENTER_LEFT);
        topo.setPadding(new Insets(10, 20, 10, 20));
        topo.setStyle("-fx-background-color: linear-gradient(to right, #ffd54f, #ff9800);");

        BorderPane raiz = new BorderPane();
        raiz.setTop(topo);
        raiz.setCenter(abas);
        raiz.setStyle("-fx-background-color: " + FUNDO + ";"
                + "-fx-font-family: 'Segoe UI', 'Verdana', sans-serif; -fx-font-size: 14px;"
                + "-fx-accent: " + LARANJA + "; -fx-focus-color: " + LARANJA + "; -fx-faint-focus-color: transparent;"
                + "-fx-selection-bar: " + LARANJA + "; -fx-selection-bar-non-focused: " + LARANJA_CLARO + ";");

        stage.setTitle("Clínica Veterinária Hina e Francisco");
        stage.setScene(new Scene(raiz, 1100, 740));
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        stage.setOnCloseRequest(e -> musica.parar());
        stage.show();
        if (!musica.iniciar()) {
            btnMusica.setText("\u266A sem áudio");
            btnMusica.setDisable(true);
        }
    }

    //carregar os dados do disco
    private void carregarDadosIniciais() {
        //carregar os Tutores
        List<String> linhasTutores = GerenciadorArquivo.carregarDados("tutores.txt");
        for(String linha : linhasTutores) {
            Tutor t = Tutor.fromLinha(linha);
            if(t != null) tutores.add(t);
        }

        //carregar os Animais do disco
        List<String> linhasAnimais = GerenciadorArquivo.carregarDados("animais.txt");
        for(int i=0; i<linhasAnimais.size(); i++){
            Animal animal = Animal.fromLinha(linhasAnimais.get(i));
            if(animal != null){
                animais.add(animal);
                //associa o animal ao tutor cadastrado na mesma ordem na tela
                if(i < tutores.size()) {
                    tutorDoAnimal.put(animal, tutores.get(i));
                }else if(!tutores.isEmpty()) {
                    tutorDoAnimal.put(animal, tutores.get(0));//vinculo de seguranca
                }
            }
        }

        List<String> linhasVinculos = GerenciadorArquivo.carregarDados("vinculos_tutor_animal.txt");
        for (String linha : linhasVinculos) {
            String[] dados = linha.split(";", 2);
            if (dados.length == 2) {
                Animal animalVinculado = buscarAnimalPorNome(dados[0]);
                Tutor tutorVinculado = buscarTutorPorCpf(dados[1]);

                if (animalVinculado != null && tutorVinculado != null) {
                    tutorDoAnimal.put(animalVinculado, tutorVinculado);
                }
            }
        }

        List<String> linhasProntuarios = GerenciadorArquivo.carregarDados("prontuarios.txt");
        for (String linha : linhasProntuarios) {
            String[] dados = linha.split(";", 2);
            if (dados.length == 2) {
                String nomeAnimal = dados[0];
                String registro = dados[1];

                for (Animal animal : animais) {
                    if (animal.getNome().equals(nomeAnimal)) {
                        animal.getProntuario().adicionarRegistro(registro);
                        break;
                    }
                }
            }
        }

        //carregar os Veterinarios do disco
        List<String> linhasVeterinarios = GerenciadorArquivo.carregarDados("veterinarios.txt");
        for(String linha : linhasVeterinarios) {
            Veterinario vet = Veterinario.fromLinha(linha);
            if(vet != null) veterinarios.add(vet);
        }

        //carregar os Servicos do disco
        List<String> linhasServicos = GerenciadorArquivo.carregarDados("servicos.txt");
        for(String linha : linhasServicos){
            Servico servico = null;
            String[] dados = linha.split(";", -1);

            if (dados.length > 0) {
                switch (dados[0]) {
                    case "Consulta":
                        servico = Consulta.fromLinha(linha);
                        break;
                    case "Vacinacao":
                        servico = Vacinacao.fromLinha(linha);
                        break;
                    case "Exame":
                        servico = Exame.fromLinha(linha);
                        break;
                    case "BanhoETosa":
                        servico = BanhoETosa.fromLinha(linha);
                        break;
                    default:
                        servico = null;
                        break;
                }
            }

            if(servico != null) servicos.add(servico);
        }

        List<String> linhasAgendamentos = GerenciadorArquivo.carregarDados("agendamentos.txt");
        for (String linha : linhasAgendamentos) {
            Agendamento agendamento = Agendamento.fromLinha(
                    linha,
                    animais,
                    tutorDoAnimal,
                    veterinarios,
                    servicos
            );
            if (agendamento != null) {
                agendamentos.add(agendamento);
            }
        }

        List<String> linhasPagamentos = GerenciadorArquivo.carregarDados("pagamentos.txt");
        for (String linha : linhasPagamentos) {
            Pagamento pagamento = Pagamento.fromLinha(linha, agendamentos);
            if (pagamento != null) {
                pagamentos.add(pagamento);
            }
        }
    }



    private Tab abaTutorAnimal() {
        TextField tNome = campo("Nome do tutor");
        TextField tCpf = campo("CPF");
        TextField tTel = campo("Telefone");
        TextField tEnd = campo("Endereço");

        TextField aNome = campo("Nome do animal");
        TextField aEsp = campo("Espécie (ex: Cachorro)");
        TextField aRaca = campo("Raça");
        TextField aIdade = campo("Idade (anos)");
        TextField aPeso = campo("Peso (kg)");

        GridPane form = novoGrid();
        form.addRow(0, new Label("Nome:"), tNome, new Label("Nome:"), aNome);
        form.addRow(1, new Label("CPF:"), tCpf, new Label("Espécie:"), aEsp);
        form.addRow(2, new Label("Telefone:"), tTel, new Label("Raça:"), aRaca);
        form.addRow(3, new Label("Endereço:"), tEnd, new Label("Idade:"), aIdade);
        form.add(new Label("Peso:"), 2, 4);
        form.add(aPeso, 3, 4);

        TitledPane boxForm = new TitledPane("Dados do tutor (esquerda) e do animal (direita)", form);
        boxForm.setCollapsible(false);

        Button btn = new Button("Cadastrar Tutor e Animal");
        btn.setOnAction(e -> executar(() -> {
            exigir(tNome, "nome do tutor");
            validarCpf(tCpf);
            exigir(aNome, "nome do animal");
            exigir(aEsp, "espécie");
            int idade = lerInt(aIdade, "idade");
            double peso = lerDouble(aPeso, "peso");

            if (existeTutorComCpf(tCpf.getText().trim())) {
                throw new ClinicaException("Já existe um tutor cadastrado com este CPF.");
            }

            Tutor tutor = new Tutor(tNome.getText().trim(), tCpf.getText().trim(),
                    tTel.getText().trim(), tEnd.getText().trim());
            Animal animal = new Animal(aNome.getText().trim(), aEsp.getText().trim(),
                    aRaca.getText().trim(), idade, peso);

            tutores.add(tutor);
            animais.add(animal);
            tutorDoAnimal.put(animal, tutor);

            //persistencia dos arquivos no disco
            GerenciadorArquivo.anexarLinha("tutores.txt", tutor.toLinha());
            GerenciadorArquivo.anexarLinha("animais.txt", animal.toLinha());
            GerenciadorArquivo.anexarLinha("vinculos_tutor_animal.txt", animal.getNome() + ";" + tutor.getCpf());

            limpar(tNome, tCpf, tTel, tEnd, aNome, aEsp, aRaca, aIdade, aPeso);
            info("Tutor e animal cadastrados com sucesso!");
        }));

        TableView<Animal> tabela = new TableView<>(animais);
        tabela.getColumns().add(coluna("ID", a -> String.valueOf(a.getId()), 50));
        tabela.getColumns().add(coluna("Animal", Animal::getNome, 130));
        tabela.getColumns().add(coluna("Espécie", Animal::getEspecie, 100));
        tabela.getColumns().add(coluna("Raça", Animal::getRaca, 110));
        tabela.getColumns().add(coluna("Idade", a -> String.valueOf(a.getIdade()), 60));
        tabela.getColumns().add(coluna("Peso (kg)", a -> String.format("%.1f", a.getPeso()), 80));
        tabela.getColumns().add(coluna("Tutor", a -> {
            Tutor tutor = tutorDoAnimal.get(a);
            return tutor == null ? "Tutor não vinculado" : tutor.getNome();
        }, 160));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return criarAba("1. Tutor e Animal", boxForm, btn, tabela);
    }

    // =====================================================================
    // 2. Veterinário
    // =====================================================================
    private Tab abaVeterinario() {
        TextField nome = campo("Nome");
        TextField cpf = campo("CPF");
        TextField tel = campo("Telefone");
        TextField end = campo("Endereço");
        TextField crmv = campo("CRMV");
        TextField esp = campo("Especialidade");

        GridPane form = novoGrid();
        form.addRow(0, new Label("Nome:"), nome, new Label("CRMV:"), crmv);
        form.addRow(1, new Label("CPF:"), cpf, new Label("Especialidade:"), esp);
        form.addRow(2, new Label("Telefone:"), tel);
        form.addRow(3, new Label("Endereço:"), end);

        Button btn = new Button("Cadastrar Veterinário");
        btn.setOnAction(e -> executar(() -> {
            exigir(nome, "nome");
            validarCpf(cpf);
            exigir(crmv, "CRMV");

            if (existeVeterinarioComCrmv(crmv.getText().trim())) {
                throw new ClinicaException("Já existe um veterinário cadastrado com este CRMV.");
            }

//Cria a instancia do Veterinario com os dados digitados na tela
            Veterinario vet = new Veterinario(nome.getText().trim(), cpf.getText().trim(), tel.getText().trim(), end.getText().trim(), crmv.getText().trim(), esp.getText().trim());

            //Adiciona na lista em memoria RAM para atualizar a tabela do JavaFX
            veterinarios.add(vet);

            // Anexar a linha do arquivo texto (dados/veterinarios.txt)
            GerenciadorArquivo.anexarLinha("veterinarios.txt", vet.toLinha());

            //limpa a tela e exibe a confirmacao
            limpar(nome, cpf, tel, end, crmv, esp);
            info("Veterinário cadastrado com sucesso!");
        }));

        TableView<Veterinario> tabela = new TableView<>(veterinarios);
        tabela.getColumns().add(coluna("ID", v -> String.valueOf(v.getId()), 50));
        tabela.getColumns().add(coluna("Nome", Veterinario::getNome, 160));
        tabela.getColumns().add(coluna("CRMV", Veterinario::getCrmv, 100));
        tabela.getColumns().add(coluna("Especialidade", Veterinario::getEspecialidade, 140));
        tabela.getColumns().add(coluna("Telefone", Veterinario::getTelefone, 110));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return criarAba("2. Veterinário", form, btn, tabela);
    }

    // =====================================================================
    // 3. Serviço
    // =====================================================================
    private Tab abaServico() {
        ComboBox<String> tipo = new ComboBox<>(FXCollections.observableArrayList(
                "Consulta", "Vacinação", "Exame", "Banho e Tosa"));
        tipo.getSelectionModel().selectFirst();

        TextField nome = campo("Nome do serviço");
        TextField valor = campo("Valor base (R$)");
        Label lbl1 = new Label();
        Label lbl2 = new Label();
        TextField extra1 = new TextField();
        TextField extra2 = new TextField();
        CheckBox tosaHigienica = new CheckBox("Incluir tosa higiênica");

        Runnable atualizarCampos = () -> {
            extra1.clear();
            extra2.clear();
            tosaHigienica.setVisible(false);
            switch (tipo.getValue()) {
                case "Consulta":
                    lbl1.setText("Motivo:");
                    lbl2.setText("Diagnóstico:");
                    break;
                case "Vacinação":
                    lbl1.setText("Nome da vacina:");
                    lbl2.setText("Custo da dose (R$):");
                    break;
                case "Exame":
                    lbl1.setText("Tipo de exame:");
                    lbl2.setText("Taxa laboratorial (R$):");
                    break;
                default:
                    lbl1.setText("Porte do animal:");
                    lbl2.setText("Taxa de tosa (R$):");
                    tosaHigienica.setVisible(true);
                    break;
            }
        };
        tipo.setOnAction(e -> atualizarCampos.run());
        atualizarCampos.run();

        GridPane form = novoGrid();
        form.addRow(0, new Label("Tipo:"), tipo);
        form.addRow(1, new Label("Nome:"), nome);
        form.addRow(2, new Label("Valor base:"), valor);
        form.addRow(3, lbl1, extra1);
        form.addRow(4, lbl2, extra2);
        form.add(tosaHigienica, 1, 5);

        Button btn = new Button("Cadastrar Serviço");
        btn.setOnAction(e -> executar(() -> {
            exigir(nome, "nome do serviço");
            double base = lerDouble(valor, "valor base");
            String t = tipo.getValue();
            Servico s;
            switch (t) {
                case "Consulta":
                    s = new Consulta(nome.getText().trim(), base, extra1.getText().trim(), extra2.getText().trim());
                    break;
                case "Vacinação":
                    s = new Vacinacao(nome.getText().trim(), base, extra1.getText().trim(),
                            lerDouble(extra2, "custo da dose"));
                    break;
                case "Exame":
                    s = new Exame(nome.getText().trim(), base, extra1.getText().trim(),
                            lerDouble(extra2, "taxa laboratorial"));
                    break;
                default:
                    double taxaTosa = 0;

                    if (tosaHigienica.isSelected()) {
                        taxaTosa = lerDouble(extra2, "taxa de tosa");
                    }

                    s = new BanhoETosa(nome.getText().trim(), base, extra1.getText().trim(),
                            tosaHigienica.isSelected(), taxaTosa);
                    break;
            }
            //Adiciona o serviço na lista em memória RAM
            servicos.add(s);

            //Anexa a linha no arquivo de texto (dados/servicos.txt)
            GerenciadorArquivo.anexarLinha("servicos.txt", s.toLinha());

            //Limpa os campos da tela
            limpar(nome, valor, extra1, extra2);
            tosaHigienica.setSelected(false);
            info("Serviço cadastrado com sucesso!");
        }));

        TableView<Servico> tabela = new TableView<>(servicos);
        tabela.getColumns().add(coluna("ID", s -> String.valueOf(s.getId()), 50));
        tabela.getColumns().add(coluna("Tipo", s -> s.getClass().getSimpleName(), 110));
        tabela.getColumns().add(coluna("Nome", Servico::getNomeServico, 180));
        tabela.getColumns().add(coluna("Valor base", s -> moeda(s.getValorBase()), 90));
        tabela.getColumns().add(coluna("Valor final", s -> moeda(s.calcularValorFinal()), 90));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return criarAba("3. Serviço", form, btn, tabela);
    }

    private void salvarAgendamentos() throws ClinicaException {
        StringBuilder sb = new StringBuilder();
        for (Agendamento agendamento : agendamentos) {
            sb.append(agendamento.toLinha()).append(System.lineSeparator());
        }
        GerenciadorArquivo.salvarDados("agendamentos.txt", sb.toString());
    }

    // =====================================================================
    // 4. Agendamento
    // =====================================================================
    private Tab abaAgendamento() {
        ComboBox<Animal> cbAnimal = combo(animais, a -> {
            Tutor tutor = tutorDoAnimal.get(a);
            String nomeTutor = tutor == null ? "sem tutor" : tutor.getNome();
            return "#" + a.getId() + " - " + a.getNome() + " (" + a.getEspecie() + ") - Tutor: " + nomeTutor;
        });
        ComboBox<Veterinario> cbVet = combo(veterinarios, Veterinario::getNome);
        ComboBox<Servico> cbServ = combo(servicos,
                s -> s.getNomeServico() + " - " + moeda(s.calcularValorFinal()));
        Label lblTutor = new Label("-");
        cbAnimal.setOnAction(e -> {
            Animal a = cbAnimal.getValue();
            if (a == null) {
                lblTutor.setText("-");
            }else{
                Tutor tutor = tutorDoAnimal.get(a);
                lblTutor.setText(tutor == null ? "Tutor não vinculado" : tutor.getNome());
            }
        });

        DatePicker data = new DatePicker(LocalDate.now());
        TextField hora = campo("HH:mm");
        hora.setText("09:00");

        GridPane form = novoGrid();
        form.addRow(0, new Label("Animal:"), cbAnimal);
        form.addRow(1, new Label("Tutor:"), lblTutor);
        form.addRow(2, new Label("Veterinário:"), cbVet);
        form.addRow(3, new Label("Serviço:"), cbServ);
        form.addRow(4, new Label("Data:"), data);
        form.addRow(5, new Label("Hora:"), hora);

        TableView<Agendamento> tabela = new TableView<>(agendamentos);

        Button btn = new Button("Realizar Agendamento");
        btn.setOnAction(e -> executar(() -> {
            if (cbAnimal.getValue() == null || cbVet.getValue() == null || cbServ.getValue() == null) {
                throw new ClinicaException("Selecione animal, veterinário e serviço!");
            }
            if (data.getEditor().getText() == null || data.getEditor().getText().trim().isEmpty()) {
                throw new ClinicaException("Informe a data!");
            }

            try {
                data.setValue(data.getConverter().fromString(data.getEditor().getText()));
            } catch (Exception ex) {
                throw new ClinicaException("Data inválida! Corrija a data antes de agendar.");
            }

            if (data.getValue() == null) {
                throw new ClinicaException("Data inválida! Corrija a data antes de agendar.");
            }

            LocalTime h;
            String textoHora = hora.getText().trim();

            if (!textoHora.matches("\\d{2}:\\d{2}")) {
                throw new ClinicaException("Hora inválida! Use exatamente o formato HH:mm (ex: 14:30).");
            }

            try {
                h = LocalTime.parse(textoHora);
            } catch (Exception ex) {
                throw new ClinicaException("Hora inválida! Use exatamente o formato HH:mm (ex: 14:30).");
            }
            LocalDateTime dataHora = LocalDateTime.of(data.getValue(), h);
            if (dataHora.isBefore(LocalDateTime.now())) {
                throw new ClinicaException("Não é possível agendar para uma data/hora no passado.");
            }

            Animal animal = cbAnimal.getValue();
            Veterinario vet = cbVet.getValue();
            Tutor tutor = tutorDoAnimal.get(animal);

            if(tutor == null){
                throw new ClinicaException("Animal sem tutor vinculado. Verifique o cadastro de tutor e animal.");
            }

            Agendamento novo = new Agendamento(dataHora, animal, tutor, vet, cbServ.getValue());
            novo.verificarDisponibilidade(dataHora, vet);

            for (Agendamento a : agendamentos) {
                if (a.getVeterinario().getCrmv().equalsIgnoreCase(vet.getCrmv())
                        && a.getDataHora().equals(dataHora)
                        && "Agendado".equals(a.getStatus())) {
                    throw new ClinicaException("O veterinário já tem um agendamento nesse horário!");
                }

                if (a.getAnimal() == animal
                        && a.getDataHora().equals(dataHora)
                        && "Agendado".equals(a.getStatus())) {
                    throw new ClinicaException("Este animal já possui um agendamento nesse horário!");
                }
            }
            agendamentos.add(novo);
            GerenciadorArquivo.anexarLinha("agendamentos.txt", novo.toLinha());
            info("Agendamento #" + novo.getId() + " realizado com sucesso!");
        }));

        Button btnCancelar = new Button("Cancelar selecionado");
        btnCancelar.setOnAction(e -> executar(() -> {
            Agendamento sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) throw new ClinicaException("Selecione um agendamento na tabela!");
            if (!"Agendado".equals(sel.getStatus()))
                throw new ClinicaException("Só é possível cancelar agendamentos com status 'Agendado'.");
            sel.cancelar();
            salvarAgendamentos();
            atualizar(tabela);
            agendamentosPendentes.setPredicate(a -> "Agendado".equals(a.getStatus()));
        }));

        tabela.getColumns().add(coluna("ID", a -> String.valueOf(a.getId()), 40));
        tabela.getColumns().add(coluna("Data/Hora", a -> a.getDataHora().format(FMT), 120));
        tabela.getColumns().add(coluna("Animal", a -> a.getAnimal().getNome(), 100));
        tabela.getColumns().add(coluna("Tutor", a -> a.getTutor() == null ? "Tutor não vinculado" : a.getTutor().getNome(), 120));
        tabela.getColumns().add(coluna("Veterinário", a -> a.getVeterinario().getNome(), 120));
        tabela.getColumns().add(coluna("Serviço", a -> a.getServico().getNomeServico(), 120));
        tabela.getColumns().add(coluna("Status", Agendamento::getStatus, 80));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return criarAba("4. Agendamento", form, new HBox(10, btn, btnCancelar), tabela);
    }

    // =====================================================================
    // 5. Pagamento
    // =====================================================================
    private Tab abaPagamento() {
        ComboBox<Agendamento> cbAg = combo(agendamentosPendentes,
                a -> "#" + a.getId() + " - " + a.getAnimal().getNome() + " - "
                        + a.getServico().getNomeServico() + " - " + a.getDataHora().format(FMT));
        Label lblValor = new Label("-");
        lblValor.setStyle("-fx-font-weight: bold;");
        cbAg.setOnAction(e -> {
            Agendamento a = cbAg.getValue();
            lblValor.setText(a == null ? "-" : moeda(a.getServico().calcularValorFinal()));
        });

        ComboBox<String> cbForma = new ComboBox<>(FXCollections.observableArrayList(
                "Dinheiro", "Pix", "Cartão de crédito", "Cartão de débito"));
        cbForma.getSelectionModel().selectFirst();

        GridPane form = novoGrid();
        form.addRow(0, new Label("Agendamento:"), cbAg);
        form.addRow(1, new Label("Valor a pagar:"), lblValor);
        form.addRow(2, new Label("Forma de pagamento:"), cbForma);

        Button btn = new Button("Processar Pagamento");
        btn.setOnAction(e -> executar(() -> {
            Agendamento ag = cbAg.getValue();
            if (ag == null) throw new ClinicaException("Selecione um agendamento pendente!");

            if (ag.getDataHora().isAfter(LocalDateTime.now())) {
                throw new ClinicaException("Não é possível processar pagamento antes da data/hora do atendimento.");
            }

            Pagamento p = new Pagamento(ag, cbForma.getValue());
            p.processarPagamento();
            pagamentos.add(p);
            GerenciadorArquivo.anexarLinha("pagamentos.txt", p.toLinha());
            salvarAgendamentos();
            agendamentosPendentes.setPredicate(a -> "Agendado".equals(a.getStatus()));
            cbAg.getSelectionModel().clearSelection();
            lblValor.setText("-");
            info("Pagamento de " + moeda(p.getValorPago()) + " processado! Agendamento concluído.");
        }));

        TableView<Pagamento> tabela = new TableView<>(pagamentos);
        tabela.getColumns().add(coluna("ID", p -> String.valueOf(p.getId()), 40));
        tabela.getColumns().add(coluna("Animal", p -> p.getAgendamento().getAnimal().getNome(), 120));
        tabela.getColumns().add(coluna("Serviço", p -> p.getAgendamento().getServico().getNomeServico(), 150));
        tabela.getColumns().add(coluna("Valor", p -> moeda(p.getValorPago()), 90));
        tabela.getColumns().add(coluna("Forma", Pagamento::getFormaPagamento, 120));
        tabela.getColumns().add(coluna("Data", p -> p.getDataPagamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 90));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return criarAba("5. Pagamento", form, btn, tabela);
    }

    // =====================================================================
    // 6. Registrar no prontuário
    // =====================================================================
    private Tab abaRegistroProntuario() {
        ComboBox<Animal> cbAnimal = combo(animais, a -> {
            Tutor tutor = tutorDoAnimal.get(a);
            String nomeTutor = tutor == null ? "sem tutor" : tutor.getNome();
            return "#" + a.getId() + " - " + a.getNome() + " (" + a.getEspecie() + ") - Tutor: " + nomeTutor;
        });
        TextArea texto = new TextArea();
        texto.setPromptText("Digite a anotação clínica...");
        texto.setPrefRowCount(6);
        texto.setWrapText(true);

        GridPane form = novoGrid();
        form.addRow(0, new Label("Animal:"), cbAnimal);
        form.addRow(1, new Label("Anotação:"), texto);

        Button btn = new Button("Registrar no Prontuário");
        btn.setOnAction(e -> executar(() -> {
            if (cbAnimal.getValue() == null) throw new ClinicaException("Selecione um animal!");
            if (texto.getText().trim().isEmpty()) throw new ClinicaException("Digite a anotação!");
            String registro = "[" + LocalDateTime.now().format(FMT) + "] " + texto.getText().trim();
            Animal animal = cbAnimal.getValue();
            animal.getProntuario().adicionarRegistro(registro);
            GerenciadorArquivo.anexarLinha("prontuarios.txt", animal.getNome() + ";" + registro);
            texto.clear();
            info("Registro adicionado ao prontuário de " + animal.getNome() + ".");
        }));

        return criarAba("6. Registrar Prontuário", form, btn);
    }

    // =====================================================================
    // 7. Visualizar prontuário
    // =====================================================================
    private Tab abaVisualizarProntuario() {
        ComboBox<Animal> cbAnimal = combo(animais, a -> {
            Tutor tutor = tutorDoAnimal.get(a);
            String nomeTutor = tutor == null ? "sem tutor" : tutor.getNome();
            return "#" + a.getId() + " - " + a.getNome() + " (" + a.getEspecie() + ") - Tutor: " + nomeTutor;
        });
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        VBox.setVgrow(area, Priority.ALWAYS);

        Runnable mostrar = () -> {
            Animal a = cbAnimal.getValue();
            if (a == null) {
                area.clear();
            } else {
                area.setText("Prontuário #" + a.getProntuario().getId() + " - " + a.getNome() + "\n"
                        + "------------------------------------------\n"
                        + a.getProntuario().listarHistorico());
            }
        };
        cbAnimal.setOnAction(e -> mostrar.run());

        Button btn = new Button("Atualizar");
        btn.setOnAction(e -> mostrar.run());

        HBox topo = new HBox(10, new Label("Animal:"), cbAnimal, btn);
        topo.setAlignment(Pos.CENTER_LEFT);

        return criarAba("7. Ver Prontuário", topo, area);
    }

    // =====================================================================
    // 8. Relatório
    // =====================================================================
    private Tab abaRelatorio() {
        TextArea area = new TextArea();
        area.setEditable(false);
        VBox.setVgrow(area, Priority.ALWAYS);

        Button btnGerar = new Button("Gerar prévia");
        btnGerar.setOnAction(e -> area.setText(montarRelatorio()));

        Button btnSalvar = new Button("Salvar em arquivo (.txt)");
        btnSalvar.setOnAction(e -> executar(() -> {
            String rel = montarRelatorio();
            area.setText(rel);
            GerenciadorArquivo.salvarDados("relatorio.txt", rel);
            info("Relatório salvo em dados/relatorio.txt");
        }));

        HBox botoes = new HBox(10, btnGerar, btnSalvar);
        return criarAba("8. Relatório", botoes, area);
    }

    private String montarRelatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO - CLÍNICA VETERINÁRIA HINA E FRANCISCO ===\n");
        sb.append("Gerado em: ").append(LocalDateTime.now().format(FMT)).append("\n\n");

        sb.append("--- TUTORES (").append(tutores.size()).append(") ---\n");
        for (Tutor t : tutores) {
            sb.append(t.getId()).append(" | ").append(t.getNome()).append(" | CPF: ").append(t.getCpf())
                    .append(" | Tel: ").append(t.getTelefone()).append("\n");
        }

        sb.append("\n--- ANIMAIS (").append(animais.size()).append(") ---\n");
        for (Animal a : animais) {
            Tutor tutor = tutorDoAnimal.get(a);
            sb.append(a.getId()).append(" | ").append(a.getNome()).append(" | ").append(a.getEspecie())
                    .append(" | ").append(a.getRaca()).append(" | Tutor: ")
                    .append(tutor == null ? "Tutor não vinculado" : tutor.getNome()).append("\n");
        }

        sb.append("\n--- VETERINÁRIOS (").append(veterinarios.size()).append(") ---\n");
        for (Veterinario v : veterinarios) {
            sb.append(v.getId()).append(" | ").append(v.getNome()).append(" | CRMV: ").append(v.getCrmv())
                    .append(" | ").append(v.getEspecialidade()).append("\n");
        }

        sb.append("\n--- SERVIÇOS (").append(servicos.size()).append(") ---\n");
        for (Servico s : servicos) {
            sb.append(s.getId()).append(" | ").append(s.getClass().getSimpleName()).append(" | ")
                    .append(s.getNomeServico()).append(" | ").append(moeda(s.calcularValorFinal())).append("\n");
        }

        sb.append("\n--- AGENDAMENTOS (").append(agendamentos.size()).append(") ---\n");
        for (Agendamento a : agendamentos) {
            sb.append("#").append(a.getId()).append(" | ").append(a.getDataHora().format(FMT))
                    .append(" | ").append(a.getAnimal().getNome()).append(" | ")
                    .append(a.getVeterinario().getNome()).append(" | ")
                    .append(a.getServico().getNomeServico()).append(" | ").append(a.getStatus()).append("\n");
        }

        double total = 0;
        sb.append("\n--- PAGAMENTOS (").append(pagamentos.size()).append(") ---\n");
        for (Pagamento p : pagamentos) {
            double valorArredondado = Math.round(p.getValorPago() * 100.0) / 100.0;
            total += valorArredondado;

            sb.append("#").append(p.getId()).append(" | ").append(p.getAgendamento().getAnimal().getNome())
                    .append(" | ").append(moeda(valorArredondado)).append(" | ")
                    .append(p.getFormaPagamento()).append("\n");
        }
        sb.append("TOTAL ARRECADADO: ").append(moeda(total)).append("\n");

        sb.append("\n--- PRONTUÁRIOS ---\n");
        for (Animal a : animais) {
            sb.append(a.getNome()).append(":\n").append(a.getProntuario().listarHistorico()).append("\n\n");
        }
        return sb.toString();
    }



    // =====================================================================
// Métodos auxiliares
// =====================================================================

    private Animal buscarAnimalPorNome(String nomeAnimal) {
        for (Animal animal : animais) {
            if (animal.getNome().equals(nomeAnimal)) {
                return animal;
            }
        }
        return null;
    }

    private Tutor buscarTutorPorCpf(String cpfTutor) {
        for (Tutor tutor : tutores) {
            if (tutor.getCpf().equals(cpfTutor)) {
                return tutor;
            }
        }
        return null;
    }

    private boolean existeTutorComCpf(String cpf) {
        return buscarTutorPorCpf(cpf) != null;
    }

    private boolean existeVeterinarioComCrmv(String crmv) {
        for (Veterinario veterinario : veterinarios) {
            if (veterinario.getCrmv().equalsIgnoreCase(crmv)) {
                return true;
            }
        }
        return false;
    }

    /** Executa uma ação tratando as exceções igual ao antigo try/catch do Main. */
    private void executar(Acao acao) {
        try {
            acao.executar();
        } catch (ClinicaException ex) {
            alerta(Alert.AlertType.WARNING, "Alerta da Clínica", "ERRO DE NEGÓCIO: " + ex.getMessage());
        } catch (Exception ex) {
            alerta(Alert.AlertType.ERROR, "Erro", "ERRO DE ENTRADA: Verifique os dados digitados! (" + ex.getMessage() + ")");
        }
    }

    @FunctionalInterface
    private interface Acao {
        void executar() throws Exception;
    }

    private void info(String msg) {
        alerta(Alert.AlertType.INFORMATION, "Sucesso", msg);
    }

    private void alerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: " + FUNDO + "; -fx-font-size: 14px;");
        for (ButtonType bt : a.getDialogPane().getButtonTypes()) {
            Node botao = a.getDialogPane().lookupButton(bt);
            if (botao instanceof Button) estilizarBotao((Button) botao);
        }
        a.showAndWait();
    }

    private TextField campo(String prompt) {
        TextField t = new TextField();
        t.setPromptText(prompt);
        return t;
    }

    private void exigir(TextField campo, String nome) throws ClinicaException {
        if (campo.getText() == null || campo.getText().trim().isEmpty()) {
            throw new ClinicaException("Preencha o campo: " + nome + "!");
        }
    }

    private void validarCpf(TextField campo) throws ClinicaException {
        exigir(campo, "CPF");

        String cpf = campo.getText().trim();

        if (!cpf.matches("\\d{11}")) {
            throw new ClinicaException("CPF inválido! Informe exatamente 11 números.");
        }
    }

    private int lerInt(TextField campo, String nome) throws ClinicaException {
        try {
            int v = Integer.parseInt(campo.getText().trim());
            if (v < 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            throw new ClinicaException("Valor inválido no campo: " + nome + "!");
        }
    }

    private double lerDouble(TextField campo, String nome) throws ClinicaException {
        try {
            double v = Double.parseDouble(campo.getText().trim().replace(",", "."));

            if (v < 0 || Double.isNaN(v) || Double.isInfinite(v)) {
                throw new NumberFormatException();
            }

            return v;
        } catch (NumberFormatException e) {
            throw new ClinicaException("Valor inválido no campo: " + nome + "! Informe um número finito e maior ou igual a zero.");
        }
    }

    private void limpar(TextField... campos) {
        for (TextField c : campos) c.clear();
    }

    private String moeda(double v) {
        return String.format("R$ %.2f", v);
    }

    private GridPane novoGrid() {
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        g.setPadding(new Insets(10));
        return g;
    }

    private <T> ComboBox<T> combo(ObservableList<T> itens, Function<T, String> texto) {
        ComboBox<T> cb = new ComboBox<>(itens);
        cb.setPrefWidth(320);
        cb.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T obj) {
                return obj == null ? "" : texto.apply(obj);
            }

            @Override
            public T fromString(String s) {
                return null;
            }
        });
        return cb;
    }

    private <T> TableColumn<T, String> coluna(String titulo, Function<T, String> valor, double largura) {
        TableColumn<T, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(cell -> new SimpleStringProperty(valor.apply(cell.getValue())));
        c.setPrefWidth(largura);
        return c;
    }

    /** Força a tabela a redesenhar (usado quando só o status de um objeto muda). */
    private <T> void atualizar(TableView<T> tabela) {
        tabela.refresh();
    }

    private Tab criarAba(String titulo, Node... conteudo) {
        VBox box = new VBox(12, conteudo);
        box.setPadding(new Insets(15));
        for (Node n : conteudo) {
            if (n instanceof TableView) VBox.setVgrow(n, Priority.ALWAYS);
        }
        estilizar(box);
        return new Tab(titulo, box);
    }

    // =====================================================================
    // Estilo fofinho feito só com código Java (setStyle)
    // =====================================================================

    /** Percorre os componentes e aplica o visual fofinho em cada um. */
    private void estilizar(Node n) {
        if (n instanceof Button) {
            estilizarBotao((Button) n);
        } else if (n instanceof TextArea) {
            estilizarTextArea((TextArea) n);
        } else if (n instanceof TextField) {
            estilizarCampo((Control) n, true);
        } else if (n instanceof ComboBoxBase) {
            estilizarCampo((Control) n, false);
            aoTerSkin((Control) n, () -> {
                Node seta = n.lookup(".arrow-button");
                if (seta != null) seta.setStyle("-fx-background-color: transparent;");
            });
        } else if (n instanceof CheckBox) {
            n.setStyle("-fx-text-fill: " + TEXTO + "; -fx-mark-color: " + LARANJA_ESCURO + ";");
        } else if (n instanceof TableView) {
            estilizarTabela((TableView<?>) n);
        } else if (n instanceof TitledPane) {
            estilizarPainel((TitledPane) n);
        } else if (n instanceof Label) {
            n.setStyle("-fx-text-fill: " + TEXTO + ";");
        } else if (n instanceof Pane) {
            for (Node filho : ((Pane) n).getChildren()) estilizar(filho);
        }
    }

    private void estilizarBotao(Button b) {
        String normal = "-fx-background-color: " + LARANJA + "; -fx-background-insets: 0; -fx-background-radius: 20;"
                + "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, rgba(255,152,0,0.45), 8, 0, 0, 2);";
        String hover = normal.replace(LARANJA, LARANJA_ESCURO);
        b.setStyle(normal);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e -> b.setStyle(normal));
    }

    private void estilizarCampo(Control c, boolean comPadding) {
        String base = "-fx-background-color: white; -fx-background-insets: 0; -fx-background-radius: 14;"
                + "-fx-border-radius: 14; -fx-border-width: 1.2; -fx-text-fill: " + TEXTO + ";"
                + "-fx-prompt-text-fill: #d9bf8c;" + (comPadding ? "-fx-padding: 6 10;" : "");
        String normal = base + "-fx-border-color: " + LARANJA_CLARO + ";";
        String focado = base + "-fx-border-color: " + LARANJA + ";";
        c.setStyle(normal);
        c.focusedProperty().addListener((o, antes, agora) -> c.setStyle(agora ? focado : normal));
    }

    private void estilizarTextArea(TextArea ta) {
        estilizarCampo(ta, false);
        aoTerSkin(ta, () -> {
            Node conteudo = ta.lookup(".content");
            if (conteudo != null) conteudo.setStyle("-fx-background-color: white; -fx-background-radius: 14;");
        });
    }

    private void estilizarPainel(TitledPane tp) {
        aoTerSkin(tp, () -> {
            Node titulo = tp.lookup(".title");
            if (titulo != null) titulo.setStyle("-fx-background-color: " + AMARELO + "; -fx-background-radius: 14 14 0 0;");
            Node texto = tp.lookup(".title .text");
            if (texto != null) texto.setStyle("-fx-fill: #8a6200; -fx-font-weight: bold;");
            Node corpo = tp.lookup(".content");
            if (corpo != null) corpo.setStyle("-fx-background-color: white; -fx-border-color: " + AMARELO
                    + "; -fx-border-radius: 0 0 14 14; -fx-background-radius: 0 0 14 14;");
        });
        if (tp.getContent() != null) estilizar(tp.getContent());
    }

    private <T> void estilizarTabela(TableView<T> tv) {
        tv.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: " + LARANJA_CLARO
                + "; -fx-border-radius: 16; -fx-padding: 2;");
        Label vazio = new Label("Nada por aqui ainda \u2661");
        vazio.setStyle("-fx-text-fill: #d9ad6a; -fx-font-size: 15px;");
        VBox placeholder = new VBox(8, contadorPlaceholder++ % 2 == 0 ? carinhaCachorro(80) : carinhaGato(80), vazio);
        placeholder.setAlignment(Pos.CENTER);
        tv.setPlaceholder(placeholder);

        // linhas alternadas em rosa clarinho
        tv.setRowFactory(t -> {
            TableRow<T> linha = new TableRow<>();
            Runnable pintar = () -> {
                if (linha.isEmpty() || linha.isSelected()) {
                    linha.setStyle("");
                } else {
                    linha.setStyle("-fx-background-color: " + (linha.getIndex() % 2 == 0 ? "white" : "#fff6dc") + ";");
                }
            };
            linha.indexProperty().addListener((o, a, b) -> pintar.run());
            linha.emptyProperty().addListener((o, a, b) -> pintar.run());
            linha.selectedProperty().addListener((o, a, b) -> pintar.run());
            pintar.run();
            return linha;
        });

        // cabecalho da tabela
        Runnable cabecalho = () -> {
            Node fundo = tv.lookup(".column-header-background");
            if (fundo != null) fundo.setStyle("-fx-background-color: #ffe9a8; -fx-background-radius: 14 14 0 0;");
            for (Node h : tv.lookupAll(".column-header")) {
                h.setStyle("-fx-background-color: #ffe9a8; -fx-border-color: transparent;");
            }
            for (Node l : tv.lookupAll(".column-header .label")) {
                l.setStyle("-fx-text-fill: #8a5a00; -fx-font-weight: bold;");
            }
            for (Node f : tv.lookupAll(".filler")) {
                f.setStyle("-fx-background-color: #ffe9a8;");
            }
        };
        aoTerSkin(tv, cabecalho);
        tv.widthProperty().addListener((o, a, b) -> Platform.runLater(cabecalho));
    }

    private void estilizarAbas(TabPane tp) {
        for (Tab t : tp.getTabs()) {
            Runnable pintar = () -> t.setStyle(
                    "-fx-background-color: " + (t.isSelected() ? LARANJA : AMARELO_BEBE) + "; -fx-background-insets: 0;"
                    + "-fx-background-radius: 14 14 0 0; -fx-padding: 4 8; -fx-cursor: hand;"
                    + "-fx-font-size: 13px; -fx-font-weight: bold;"
                    + "-fx-text-base-color: " + (t.isSelected() ? "white" : "#8a6a30") + ";"
                    + "-fx-focus-color: " + (t.isSelected() ? "white" : "#8a6a30") + ";");
            t.selectedProperty().addListener((o, a, b) -> pintar.run());
            pintar.run();
        }
        tp.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        Runnable fundos = () -> {
            Node cab = tp.lookup(".tab-header-background");
            if (cab != null) cab.setStyle("-fx-background-color: transparent;");
            Node area = tp.lookup(".tab-content-area");
            if (area != null) area.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        };
        aoTerSkin(tp, fundos);
    }

    // =====================================================================
    // Bichinhos desenhados com formas do JavaFX (sem precisar de imagens)
    // =====================================================================

    private Node carinhaGato(double tamanho) {
        double k = tamanho / 100.0;
        Group g = new Group();
        g.getChildren().addAll(
                caixa(k),
                // orelhas
                poligono(k, "#ff9f43", 14, 44, 18, 4, 46, 24),
                poligono(k, "#ffc4c4", 21, 36, 23, 14, 38, 26),
                poligono(k, "#ff9f43", 86, 44, 82, 4, 54, 24),
                poligono(k, "#ffc4c4", 79, 36, 77, 14, 62, 26),
                // cabeca
                elipse(k, 50, 60, 40, 33, "#ffb85c", "#e08a2e"),
                // listras da testa
                linha(k, 50, 30, 50, 40, "#e08a2e", 3),
                linha(k, 40, 32, 43, 40, "#e08a2e", 3),
                linha(k, 60, 32, 57, 40, "#e08a2e", 3),
                // olhos
                elipse(k, 35, 56, 6, 7, "#2b2118", null),
                elipse(k, 65, 56, 6, 7, "#2b2118", null),
                elipse(k, 37, 53, 2, 2, "white", null),
                elipse(k, 67, 53, 2, 2, "white", null),
                // bochechas
                elipse(k, 26, 68, 6, 4, "#ffa8a0", null),
                elipse(k, 74, 68, 6, 4, "#ffa8a0", null),
                // nariz e boca
                poligono(k, "#ff7b8a", 46, 65, 54, 65, 50, 70),
                curva(k, 50, 70, 50, 77, 43, 76, "#5a3a22", 2),
                curva(k, 50, 70, 50, 77, 57, 76, "#5a3a22", 2),
                // bigodes
                linha(k, 22, 64, 4, 60, "#fff3dc", 1.5),
                linha(k, 22, 69, 4, 73, "#fff3dc", 1.5),
                linha(k, 78, 64, 96, 60, "#fff3dc", 1.5),
                linha(k, 78, 69, 96, 73, "#fff3dc", 1.5)
        );
        return g;
    }

    private Node carinhaCachorro(double tamanho) {
        double k = tamanho / 100.0;
        Ellipse orelhaE = elipse(k, 17, 48, 13, 30, "#a0633a", "#7a4526");
        orelhaE.setRotate(15);
        Ellipse orelhaD = elipse(k, 83, 48, 13, 30, "#a0633a", "#7a4526");
        orelhaD.setRotate(-15);

        Group g = new Group();
        g.getChildren().addAll(
                caixa(k),
                orelhaE, orelhaD,
                // cabeca
                elipse(k, 50, 54, 34, 37, "#f3d3a0", "#c9a26a"),
                // mancha no olho
                elipse(k, 36, 46, 10, 11, "#c98f5a", null),
                // olhos
                elipse(k, 37, 46, 5, 6, "#2b2118", null),
                elipse(k, 63, 46, 5, 6, "#2b2118", null),
                elipse(k, 39, 43, 2, 2, "white", null),
                elipse(k, 65, 43, 2, 2, "white", null),
                // focinho
                elipse(k, 50, 69, 19, 15, "#fff4e0", null),
                // lingua
                elipse(k, 50, 82, 6, 7, "#ff8a80", null),
                // bochechas
                elipse(k, 27, 62, 6, 4, "#ffb199", null),
                elipse(k, 73, 62, 6, 4, "#ffb199", null),
                // nariz e boca
                elipse(k, 50, 61, 8, 5.5, "#3b2a20", null),
                linha(k, 50, 66, 50, 73, "#5a3a22", 2),
                curva(k, 50, 73, 50, 79, 41, 77, "#5a3a22", 2),
                curva(k, 50, 73, 50, 79, 59, 77, "#5a3a22", 2)
        );
        return g;
    }

    /** Quadrado invisivel que garante o tamanho do desenho (100 x 100 escalado). */
    private Rectangle caixa(double k) {
        Rectangle r = new Rectangle(100 * k, 100 * k);
        r.setFill(Color.TRANSPARENT);
        return r;
    }

    private Polygon poligono(double k, String cor, double... pontos) {
        Polygon p = new Polygon();
        for (double v : pontos) p.getPoints().add(v * k);
        p.setFill(Color.web(cor));
        return p;
    }

    private Ellipse elipse(double k, double cx, double cy, double rx, double ry, String cor, String borda) {
        Ellipse e = new Ellipse(cx * k, cy * k, rx * k, ry * k);
        e.setFill(Color.web(cor));
        if (borda != null) {
            e.setStroke(Color.web(borda));
            e.setStrokeWidth(2 * k);
        }
        return e;
    }

    private Line linha(double k, double x1, double y1, double x2, double y2, String cor, double largura) {
        Line l = new Line(x1 * k, y1 * k, x2 * k, y2 * k);
        l.setStroke(Color.web(cor));
        l.setStrokeWidth(largura * k);
        l.setStrokeLineCap(StrokeLineCap.ROUND);
        return l;
    }

    private QuadCurve curva(double k, double x1, double y1, double cx, double cy, double x2, double y2,
                            String cor, double largura) {
        QuadCurve c = new QuadCurve(x1 * k, y1 * k, cx * k, cy * k, x2 * k, y2 * k);
        c.setFill(null);
        c.setStroke(Color.web(cor));
        c.setStrokeWidth(largura * k);
        c.setStrokeLineCap(StrokeLineCap.ROUND);
        return c;
    }

    // =====================================================================
    // Musiquinha de caixinha de musica, composta e gerada por codigo
    // =====================================================================
    static class MusicaFofa {
        private Clip clip;
        private boolean tocando = false;

        boolean estaTocando() {
            return tocando;
        }

        /** Comeca (ou continua) a musica em loop. Devolve false se o computador nao tiver audio. */
        boolean iniciar() {
            try {
                if (clip == null) {
                    byte[] dados = gerar();
                    AudioFormat formato = new AudioFormat(44100f, 16, 1, true, false);
                    clip = AudioSystem.getClip();
                    clip.open(formato, dados, 0, dados.length);
                    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-10f); // volume baixinho
                    }
                }
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                tocando = true;
                return true;
            } catch (Exception e) {
                tocando = false;
                return false;
            }
        }

        void pausar() {
            if (clip != null) clip.stop();
            tocando = false;
        }

        void parar() {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
            tocando = false;
        }

        /** Monta a melodia (8 compassos) + acompanhamento e devolve o audio em 16 bits. */
        static byte[] gerar() {
            final int taxa = 44100;
            final double batida = 60.0 / 96.0;      // 96 batidas por minuto
            final int n = (int) Math.round(32 * batida * taxa); // 8 compassos de 4 batidas
            double[] audio = new double[n];

            // melodia: {nota MIDI, duracao em batidas}
            int[][] melodia = {
                    {76, 1}, {79, 1}, {81, 1}, {79, 1},
                    {76, 1}, {74, 1}, {72, 2},
                    {74, 1}, {76, 1}, {79, 1}, {76, 1},
                    {74, 1}, {72, 1}, {74, 2},
                    {76, 1}, {79, 1}, {81, 1}, {84, 1},
                    {81, 1}, {79, 1}, {76, 2},
                    {79, 1}, {76, 1}, {74, 1}, {72, 1},
                    {74, 2}, {72, 2}
            };
            double pos = 0;
            for (int[] nota : melodia) {
                somarNota(audio, taxa, pos * batida, nota[0], 2.2, 0.55, 2.8);
                pos += nota[1];
            }

            // acompanhamento: um acorde por compasso tocado em arpejo suave
            int[][] acordes = {
                    {48, 52, 55}, {48, 52, 55}, {43, 47, 50}, {43, 47, 50},
                    {41, 45, 48}, {41, 45, 48}, {43, 47, 50}, {48, 52, 55}
            };
            int[] ordem = {0, 1, 2, 1};
            for (int c = 0; c < acordes.length; c++) {
                for (int b = 0; b < 4; b++) {
                    somarNota(audio, taxa, (c * 4 + b) * batida, acordes[c][ordem[b]], 1.8, 0.28, 2.0);
                }
            }

            // eco suave (circular, assim o loop emenda sem estalo)
            double[] eco = new double[n];
            int atraso = (int) (0.3 * taxa);
            for (int i = 0; i < n; i++) eco[(i + atraso) % n] = audio[i] * 0.25;
            for (int i = 0; i < n; i++) audio[i] += eco[i];

            // normaliza e converte para bytes
            double pico = 0;
            for (double v : audio) pico = Math.max(pico, Math.abs(v));
            byte[] saida = new byte[n * 2];
            for (int i = 0; i < n; i++) {
                short v = (short) Math.round(audio[i] / pico * 0.6 * 32767);
                saida[2 * i] = (byte) (v & 0xff);
                saida[2 * i + 1] = (byte) ((v >> 8) & 0xff);
            }
            return saida;
        }

        /** Soma uma nota com som de caixinha de musica (ataque rapido e decaimento suave). */
        private static void somarNota(double[] audio, int taxa, double inicioSeg, int midi,
                                      double duracaoSeg, double volume, double decaimento) {
            double freq = 440.0 * Math.pow(2, (midi - 69) / 12.0);
            int inicio = (int) Math.round(inicioSeg * taxa);
            int tam = (int) (duracaoSeg * taxa);
            for (int i = 0; i < tam; i++) {
                double t = i / (double) taxa;
                double envelope = (1 - Math.exp(-t * 300)) * Math.exp(-t * decaimento);
                double onda = Math.sin(2 * Math.PI * freq * t)
                        + 0.35 * Math.sin(4 * Math.PI * freq * t) * Math.exp(-t * 4)
                        + 0.12 * Math.sin(6 * Math.PI * freq * t) * Math.exp(-t * 6);
                audio[(inicio + i) % audio.length] += volume * envelope * onda;
            }
        }
    }

    /** Executa a acao quando o componente ja tem o visual (skin) criado. */
    private void aoTerSkin(Control c, Runnable acao) {
        if (c.getSkin() != null) {
            Platform.runLater(acao);
        } else {
            c.skinProperty().addListener((o, antes, skin) -> {
                if (skin != null) Platform.runLater(acao);
            });
        }
    }
}
