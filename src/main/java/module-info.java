
module com.medialab.hangman {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.json;

    opens com.medialab.hangman to javafx.fxml;
    exports com.medialab.hangman;
}