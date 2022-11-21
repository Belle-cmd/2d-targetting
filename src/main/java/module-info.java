module com.example.asn4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.asn4 to javafx.fxml;
    exports com.example.asn4;
}