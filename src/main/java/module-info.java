module org.openjfx.re.unida {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.openjfx.re.unida to javafx.fxml;
    exports org.openjfx.re.unida;
}
