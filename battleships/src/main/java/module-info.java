module battleships {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens battleships to javafx.fxml;
    exports battleships;
}
