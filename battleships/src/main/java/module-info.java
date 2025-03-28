module battleships {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires io.github.cdimascio.dotenv.java;

    opens battleships to javafx.fxml;
    exports battleships;
}
