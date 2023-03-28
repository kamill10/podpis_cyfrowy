module com.example.widok {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.example;

    opens com.example.widok to javafx.fxml;
    exports com.example.widok;
}