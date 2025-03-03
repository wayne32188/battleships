module battleships {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens battleships to javafx.fxml;
    exports battleships;
}
