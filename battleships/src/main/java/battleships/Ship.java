package battleships;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Ship {

    private final int SHIP_SIZE;
    private int[] positionOnGrid;
    private boolean isVertical;
    private Rectangle shipVisuals;
    private boolean isDestroyed = false;
    private boolean isHostShip = false;
    private final List<int[]> hitCells = new ArrayList<>(); // Liste für getroffene Zellen

    public Ship(int SIZE, boolean isVertical) {
        this.SHIP_SIZE = SIZE;
        this.isVertical = isVertical;
    }

    public int getSize() {
        return SHIP_SIZE;
    }

    public Rectangle getShipVisuals() {
        return shipVisuals;
    }

    public void setPosition(int[] positionOnGrid) {
        this.positionOnGrid = positionOnGrid;
    }

    /**
     * @return Index 0 = cellRow; 1 = cellCol
     */
    public int[] getPosition() {
        return positionOnGrid;
    }


    public void setRotation(boolean isVertical) {
        this.isVertical = isVertical;
    }


    public boolean isVertical() {
        return isVertical;
    }


    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setIsHostShip(boolean isHostShip) {
        this.isHostShip = isHostShip;
    }

    public boolean getIsHostShip() {
        return isHostShip;
    }
    

    public boolean isHit(int row, int col) {
        int shipRow = positionOnGrid[0];
        int shipCol = positionOnGrid[1];

        for (int i = 0; i < SHIP_SIZE; i++) {
            int occupiedRow = isVertical ? shipRow + i : shipRow;
            int occupiedCol = isVertical ? shipCol : shipCol - i;

            if (row == occupiedRow && col == occupiedCol) {
                // Überprüfen, ob die Zelle bereits getroffen wurde
                for (int[] hitCell : hitCells) {
                    if (hitCell[0] == row && hitCell[1] == col) {
                        return false; // Zelle wurde bereits getroffen
                    }
                }

                // Zelle als getroffen markieren
                hitCells.add(new int[]{row, col});
                System.out.println("Schiff getroffen bei (" + row + ", " + col + ")");

                // Überprüfen, ob das Schiff zerstört ist
                if (hitCells.size() == SHIP_SIZE) {
                    isDestroyed = true;
                    System.out.println("Schiff zerstört!");
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Erstellt die visuelle Darstellung des Schiffs als Rectangle.
     *
     * @param cellSize Die Größe einer Zelle im Grid.
     * @param isVertical Gibt an, ob das Schiff vertikal oder horizontal ist.
     * @return Das erstellte Rectangle-Objekt.
     */
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
    public void updatedRotation(boolean isVertical) {
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
