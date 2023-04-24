module com.example.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires org.json;
    opens com.example.chat to javafx.fxml;
    exports com.example.chat;
}