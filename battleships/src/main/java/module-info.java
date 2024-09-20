module battleships {
    requires javafx.controls;
    requires javafx.fxml;

    opens battleships to javafx.fxml;
    exports battleships;
}
