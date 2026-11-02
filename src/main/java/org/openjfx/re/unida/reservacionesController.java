package org.openjfx.re.unida;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador principal de la vista de reservaciones.
 *
 * @author rub3n
 */
public class reservacionesController {

    // Lista compartida en memoria para conservar las reservas durante la sesión
    private static final ObservableList<Reserva> DATOS_RESERVAS = FXCollections.observableArrayList();
    private static boolean datosInicializados = false;

    // --- Controles del Formulario (Pestaña 1) ---
    @FXML
    private TextField txtAnfitrion;
    @FXML
    private TextField txtEmpresa;
    @FXML
    private DatePicker pickerFecha;
    @FXML
    private Spinner<String> spinHora;
    @FXML
    private ComboBox<String> comboDuracion;
    @FXML
    private ComboBox<String> comboSala;
    @FXML
    private TextArea txtRequerimientos;
    @FXML
    private CheckBox chkCafe;
    @FXML
    private CheckBox chkProyector;
    @FXML
    private CheckBox chkMicrofonos;
    @FXML
    private CheckBox chkGrabacion;
    @FXML
    private Button btnCerrarSesion;

    // --- Controles de la Tabla (Pestaña 2) ---
    @FXML
    private TableView<Reserva> tablaReservas;
    @FXML
    private TableColumn<Reserva, String> colFecha;
    @FXML
    private TableColumn<Reserva, String> colHora;
    @FXML
    private TableColumn<Reserva, String> colDuracion;
    @FXML
    private TableColumn<Reserva, String> colSala;
    @FXML
    private TableColumn<Reserva, String> colAnfitrion;
    @FXML
    private TableColumn<Reserva, String> colEmpresa;
    @FXML
    private TableColumn<Reserva, String> colServicios;
    @FXML
    private TableColumn<Reserva, String> colEstado;
    @FXML
    private Label lblDetalleRequerimientos;
    @FXML
    private Label lblContadorReservas;

    @FXML
    public void initialize() {
        // 1. Configurar las columnas del TableView con los atributos de Reserva
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        colSala.setCellValueFactory(new PropertyValueFactory<>("sala"));
        colAnfitrion.setCellValueFactory(new PropertyValueFactory<>("anfitrion"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));
        colServicios.setCellValueFactory(new PropertyValueFactory<>("servicios"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Estilo visual en la columna Estado ("Programada" en verde, "Cancelada" en rojo)
        colEstado.setCellFactory(columna -> new TableCell<Reserva, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    if ("Cancelada".equalsIgnoreCase(estado)) {
                        setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: #1e8449; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        tablaReservas.setPlaceholder(new Label("No hay reuniones programadas actualmente."));
        tablaReservas.setItems(DATOS_RESERVAS);

        // Cargar ejemplos iniciales la primera vez para visualizar el diseño de la tabla
        if (!datosInicializados) {
            DATOS_RESERVAS.add(new Reserva(
                    LocalDate.now().toString(),
                    "09:00",
                    "1 hora",
                    "Sala A (12 pers.)",
                    "Roberto Valenzuela",
                    "TechSolutions S.A.",
                    "Café, Proyector",
                    "Se requiere adaptador HDMI y pizarrón limpio.",
                    "Programada"
            ));
            DATOS_RESERVAS.add(new Reserva(
                    LocalDate.now().plusDays(1).toString(),
                    "11:00",
                    "2 horas",
                    "Auditorio (100 pers.)",
                    "Laura Méndez",
                    "Grupo Financiero Norte",
                    "Café, Proyector, Micrófonos, Grabación",
                    "Conferencia trimestral con 60 invitados.",
                    "Programada"
            ));
            datosInicializados = true;
        }

        actualizarContador();
        DATOS_RESERVAS.addListener((ListChangeListener<Reserva>) change -> actualizarContador());

        // Mostrar requerimientos al seleccionar una fila en la tabla
        tablaReservas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                String req = (seleccionada.getRequerimientos() == null || seleccionada.getRequerimientos().isEmpty())
                        ? "Sin requerimientos técnicos especiales."
                        : seleccionada.getRequerimientos();
                lblDetalleRequerimientos.setText("[" + seleccionada.getSala() + " - " + seleccionada.getAnfitrion() + "] "
                        + req + " | Servicios: " + seleccionada.getServicios());
            } else {
                lblDetalleRequerimientos.setText("Selecciona una fila en la tabla para ver sus requerimientos técnicos.");
            }
        });

        // 2. Deshabilitar fechas anteriores a hoy en el DatePicker
        pickerFecha.setValue(LocalDate.now());
        pickerFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #b0b0b0;");
                }
            }
        });

        // 3. Cargar opciones en el ComboBox de Duración
        ObservableList<String> opcionesDuracion = FXCollections.observableArrayList(
                "30 minutos", "1 hora", "1 hora 30 min", "2 horas", "3 horas"
        );
        comboDuracion.setItems(opcionesDuracion);
        comboDuracion.getSelectionModel().select(1); // "1 hora" por defecto

        // 4. Cargar horas en el Spinner
        ObservableList<String> opcionesHoras = FXCollections.observableArrayList(
                "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"
        );
        SpinnerValueFactory<String> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(opcionesHoras);
        valueFactory.setValue("09:00");
        spinHora.setValueFactory(valueFactory);

        // 5. Cargar opciones en el ComboBox de Sala
        ObservableList<String> opcionesSalas = FXCollections.observableArrayList(
                "Sala A (12 pers.)", "Sala B (8 pers.)", "Sala de Juntas (20 pers.)", "Auditorio (100 pers.)"
        );
        comboSala.setItems(opcionesSalas);
        comboSala.getSelectionModel().selectFirst();
    }

    private void actualizarContador() {
        if (lblContadorReservas != null) {
            long activas = DATOS_RESERVAS.stream()
                    .filter(r -> !"Cancelada".equalsIgnoreCase(r.getEstado()))
                    .count();
            lblContadorReservas.setText("Total: " + DATOS_RESERVAS.size() + " registros (" + activas + " programadas)");
        }
    }

    @FXML
    public void comprobarConflictos() {
        LocalDate fechaSeleccionada = pickerFecha.getValue();
        String hora = spinHora.getValue();
        String duracion = comboDuracion.getValue();
        String sala = comboSala.getValue();

        if (fechaSeleccionada == null || hora == null || duracion == null || sala == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos",
                    "Selecciona primero la fecha, hora, duración y sala para comprobar disponibilidad.");
            return;
        }

        Reserva conflicto = buscarConflicto(fechaSeleccionada.toString(), hora, duracion, sala);
        if (conflicto != null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Conflicto de horario detectado",
                    "La sala '" + sala + "' ya está ocupada el " + fechaSeleccionada
                    + " a las " + conflicto.getHora() + " (" + conflicto.getDuracion() + ")\n"
                    + "Reservada por: " + conflicto.getAnfitrion() + " (" + conflicto.getEmpresa() + ").");
        } else {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Horario disponible",
                    "¡La sala '" + sala + "' está libre el " + fechaSeleccionada
                    + " a las " + hora + " (" + duracion + ")!");
        }
    }

    @FXML
    public void guardarReserva() {
        String host = (txtAnfitrion.getText() != null) ? txtAnfitrion.getText().trim() : "";
        String empresa = (txtEmpresa.getText() != null) ? txtEmpresa.getText().trim() : "";
        LocalDate fechaSeleccionada = pickerFecha.getValue();
        String hora = spinHora.getValue();
        String duracion = comboDuracion.getValue();
        String sala = comboSala.getValue();

        // 1. Validación de campos obligatorios
        List<String> faltantes = new ArrayList<>();
        if (host.isEmpty()) {
            faltantes.add("Nombre del anfitrión");
        }
        if (empresa.isEmpty()) {
            faltantes.add("Empresa del anfitrión");
        }
        if (fechaSeleccionada == null) {
            faltantes.add("Fecha de reunión");
        }
        if (hora == null || hora.isEmpty()) {
            faltantes.add("Hora de inicio");
        }
        if (duracion == null || duracion.isEmpty()) {
            faltantes.add("Duración estimada");
        }
        if (sala == null || sala.isEmpty()) {
            faltantes.add("Sala asignada");
        }

        if (!faltantes.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos obligatorios incompletos",
                    "Por favor completa los siguientes datos antes de guardar:\n• "
                    + String.join("\n• ", faltantes));
            return;
        }

        if (fechaSeleccionada.isBefore(LocalDate.now())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Fecha inválida",
                    "No se pueden programar reuniones en fechas pasadas.");
            return;
        }

        // 2. Verificar si existe conflicto de horario en la misma sala y fecha
        String fechaTexto = fechaSeleccionada.toString();
        Reserva conflicto = buscarConflicto(fechaTexto, hora, duracion, sala);
        if (conflicto != null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Horario no disponible",
                    "No se puede guardar la reserva porque se empalma con otra reunión:\n\n"
                    + "• Sala: " + conflicto.getSala() + "\n"
                    + "• Horario ocupado: " + conflicto.getHora() + " (" + conflicto.getDuracion() + ")\n"
                    + "• Anfitrión: " + conflicto.getAnfitrion() + " - " + conflicto.getEmpresa());
            return;
        }

        // 3. Recolectar los servicios seleccionados en los CheckBox
        List<String> listaServicios = new ArrayList<>();
        if (chkCafe != null && chkCafe.isSelected()) {
            listaServicios.add("Café");
        }
        if (chkProyector != null && chkProyector.isSelected()) {
            listaServicios.add("Proyector");
        }
        if (chkMicrofonos != null && chkMicrofonos.isSelected()) {
            listaServicios.add("Micrófonos");
        }
        if (chkGrabacion != null && chkGrabacion.isSelected()) {
            listaServicios.add("Grabación");
        }
        String serviciosTexto = listaServicios.isEmpty() ? "Ninguno" : String.join(", ", listaServicios);

        String requerimientosTexto = (txtRequerimientos != null && txtRequerimientos.getText() != null)
                ? txtRequerimientos.getText().trim()
                : "";

        // 4. Crear y agregar la nueva reserva a la tabla
        Reserva nuevaReserva = new Reserva(
                fechaTexto,
                hora,
                duracion,
                sala,
                host,
                empresa,
                serviciosTexto,
                requerimientosTexto,
                "Programada"
        );
        DATOS_RESERVAS.add(nuevaReserva);
        tablaReservas.getSelectionModel().select(nuevaReserva);

        limpiarFormulario();

        mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva registrada",
                "¡Reserva guardada con éxito para " + host + " en " + sala + "!\n"
                + "Puedes consultarla en la pestaña 'Reuniones Programadas'.");
    }

    @FXML
    public void limpiarFormulario() {
        txtAnfitrion.clear();
        txtEmpresa.clear();
        pickerFecha.setValue(LocalDate.now());

        if (comboDuracion.getItems() != null && !comboDuracion.getItems().isEmpty()) {
            comboDuracion.getSelectionModel().select(1);
        }
        if (comboSala.getItems() != null && !comboSala.getItems().isEmpty()) {
            comboSala.getSelectionModel().selectFirst();
        }
        if (spinHora.getValueFactory() != null) {
            spinHora.getValueFactory().setValue("09:00");
        }
        if (txtRequerimientos != null) {
            txtRequerimientos.clear();
        }
        if (chkCafe != null) {
            chkCafe.setSelected(false);
        }
        if (chkProyector != null) {
            chkProyector.setSelected(false);
        }
        if (chkMicrofonos != null) {
            chkMicrofonos.setSelected(false);
        }
        if (chkGrabacion != null) {
            chkGrabacion.setSelected(false);
        }
    }

    @FXML
    public void verDetalleReserva() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                    "Selecciona primero una reunión en la tabla para ver su ficha completa.");
            return;
        }

        String req = (seleccionada.getRequerimientos() == null || seleccionada.getRequerimientos().isEmpty())
                ? "Sin requerimientos adicionales."
                : seleccionada.getRequerimientos();

        String detalle = "Fecha: " + seleccionada.getFecha() + "\n"
                + "Hora de inicio: " + seleccionada.getHora() + " (" + seleccionada.getDuracion() + ")\n"
                + "Sala asignada: " + seleccionada.getSala() + "\n"
                + "Anfitrión: " + seleccionada.getAnfitrion() + "\n"
                + "Empresa: " + seleccionada.getEmpresa() + "\n"
                + "Servicios solicitados: " + seleccionada.getServicios() + "\n"
                + "Estado actual: " + seleccionada.getEstado() + "\n\n"
                + "Requerimientos técnicos:\n" + req;

        mostrarAlerta(Alert.AlertType.INFORMATION, "Detalle de la Reunión", detalle);
    }

    @FXML
    public void cancelarReserva() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                    "Selecciona una reservación en la tabla para cancelarla.");
            return;
        }

        if ("Cancelada".equalsIgnoreCase(seleccionada.getEstado())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva ya cancelada",
                    "Esta reservación ya se encuentra con estado 'Cancelada'.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas cancelar la reunión de " + seleccionada.getAnfitrion()
                + " en " + seleccionada.getSala() + " (" + seleccionada.getFecha() + " " + seleccionada.getHora() + ")?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            seleccionada.setEstado("Cancelada");
            tablaReservas.refresh();
            actualizarContador();
        }
    }

    @FXML
    public void eliminarReserva() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                    "Selecciona una fila de la tabla para eliminarla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar reservación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de eliminar permanentemente la reserva de "
                + seleccionada.getAnfitrion() + " de la tabla?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            DATOS_RESERVAS.remove(seleccionada);
        }
    }

    @FXML
    public void cerrarSesion() {
        try {
            App.setRoot("login");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Busca si existe alguna reserva activa ("Programada") en la misma sala y fecha cuyo horario se solape.
     */
    private Reserva buscarConflicto(String fecha, String horaInicioNueva, String duracionNueva, String sala) {
        LocalTime inicioA = LocalTime.parse(horaInicioNueva);
        LocalTime finA = inicioA.plusMinutes(convertirDuracionAMinutos(duracionNueva));

        for (Reserva existente : DATOS_RESERVAS) {
            if ("Cancelada".equalsIgnoreCase(existente.getEstado())) {
                continue;
            }
            if (existente.getFecha().equals(fecha) && existente.getSala().equals(sala)) {
                LocalTime inicioB = LocalTime.parse(existente.getHora());
                LocalTime finB = inicioB.plusMinutes(convertirDuracionAMinutos(existente.getDuracion()));
                if (inicioA.isBefore(finB) && inicioB.isBefore(finA)) {
                    return existente;
                }
            }
        }
        return null;
    }

    private int convertirDuracionAMinutos(String textoDuracion) {
        if (textoDuracion == null) {
            return 60;
        }
        switch (textoDuracion) {
            case "30 minutos":
                return 30;
            case "1 hora":
                return 60;
            case "1 hora 30 min":
                return 90;
            case "2 horas":
                return 120;
            case "3 horas":
                return 180;
            default:
                return 60;
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
