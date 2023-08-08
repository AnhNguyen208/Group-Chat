module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.logging;

    opens com.example.client to javafx.fxml;
    exports com.example.client;
    exports com.example.client.handler;
    opens com.example.client.handler to javafx.fxml;
}