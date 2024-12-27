package battleships;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Ship {

    private final int SHIP_SIZE;
    private int startRow;
    private int startCol;
    private boolean isVertical;
    private Rectangle shipVisuals;

    public Ship(int SIZE, boolean isVertical) {
        this.SHIP_SIZE = SIZE;
        this.isVertical = isVertical;
    }

    public int getSize() {
        return SHIP_SIZE;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    @SuppressWarnings("exports")
    public Rectangle getShipVisuals() {
        return shipVisuals;
    }

    public void setStartRow(int row) {
        this.startRow = row;
    }

    public void setStartCol(int col) {
        this.startCol = col;
    }


    public boolean isVertical() {
        return isVertical;
    }

    /**
     * Erstellt die visuelle Darstellung des Schiffs als Rectangle.
     *
     * @param cellSize Die Größe einer Zelle im Grid.
     * @param isVertical Gibt an, ob das Schiff vertikal oder horizontal ist.
     * @return Das erstellte Rectangle-Objekt.
     */
    @SuppressWarnings("exports")
    public Rectangle createShip(int cellSize, boolean isVertical) {
        shipVisuals = new Rectangle(
                isVertical ? cellSize : cellSize * SHIP_SIZE,
                isVertical ? cellSize * SHIP_SIZE : cellSize
        );
        shipVisuals.setFill(Color.GRAY);

        // Drag-and-Drop-Ereignis für das Schiff
        shipVisuals.setOnDragDetected(event -> {
            shipVisuals.setMouseTransparent(true);
            Dragboard db = shipVisuals.startDragAndDrop(TransferMode.MOVE);
            // Schiffgröße als String speichern
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(getSize())); // Platzhalter, damit etwas übertragen wird
            db.setContent(content);
            event.consume();
        });

        shipVisuals.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                shipVisuals.setVisible(false); // Schiff verschwindet, wenn es platziert wurde
            }
            shipVisuals.setMouseTransparent(false);
            event.consume();
        });

        return shipVisuals;
    }

    /**
     * Aktualisiert die Ausrichtung des Schiffs und passt die visuelle
     * Darstellung an.
     *
     * @param isVertical Gibt an, ob das Schiff vertikal oder horizontal sein
     * soll.
     */
    public void updateOrientation(boolean isVertical) {
        this.isVertical = isVertical;
        if (shipVisuals != null) {
            if (this.isVertical) {
                shipVisuals.setWidth(50);
                shipVisuals.setHeight(50 * SHIP_SIZE); // Vertikale Ausrichtung
            } else {
                shipVisuals.setWidth(50 * SHIP_SIZE);
                shipVisuals.setHeight(50); // Horizontale Ausrichtung
            }
        }
    }
}
