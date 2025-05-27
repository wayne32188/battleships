package battleships;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Die Klasse Ship repräsentiert ein Schiff im Spiel Battleships.
 * Sie speichert die Größe, Position, Ausrichtung und den Zustand des Schiffs
 * sowie die getroffenen Zellen.
 * Außerdem enthält sie Methoden zur visuellen Darstellung und Interaktion.
 */
public class Ship {

    /** Die Größe (Länge) des Schiffs in Feldern. */
    private final int SHIP_SIZE;

    /** Die Startposition des Schiffs im Grid (Index 0 = Zeile, 1 = Spalte). */
    private int[] positionOnGrid;

    /**
     * Gibt an, ob das Schiff vertikal (true) oder horizontal (false) ausgerichtet
     * ist.
     */
    private boolean isVertical;

    /** Das Rectangle-Objekt für die visuelle Darstellung des Schiffs. */
    private Rectangle shipVisuals;

    /** Gibt an, ob das Schiff zerstört wurde. */
    private boolean isDestroyed = false;

    /** Gibt an, ob das Schiff dem Host-Spieler gehört. */
    private boolean isHostShip = false;

    /** Liste der bereits getroffenen Zellen des Schiffs. */
    private final List<int[]> hitCells = new ArrayList<>();

    /**
     * Konstruktor für ein Schiff.
     * 
     * @param SIZE       Die Größe (Länge) des Schiffs.
     * @param isVertical Gibt an, ob das Schiff vertikal ausgerichtet ist.
     */
    public Ship(int SIZE, boolean isVertical) {
        this.SHIP_SIZE = SIZE;
        this.isVertical = isVertical;
    }

    /**
     * Gibt die Größe (Länge) des Schiffs zurück.
     * 
     * @return Die Schiffsgröße.
     */
    public int getSize() {
        return SHIP_SIZE;
    }

    /**
     * Gibt das Rectangle-Objekt der visuellen Darstellung zurück.
     * 
     * @return Das Rectangle-Objekt.
     */
    public Rectangle getShipVisuals() {
        return shipVisuals;
    }

    /**
     * Setzt die Startposition des Schiffs im Grid.
     * 
     * @param positionOnGrid Array mit [Zeile, Spalte].
     */
    public void setPosition(int[] positionOnGrid) {
        this.positionOnGrid = positionOnGrid;
    }

    /**
     * Gibt die Startposition des Schiffs im Grid zurück.
     * 
     * @return Array mit [Zeile, Spalte].
     */
    public int[] getPosition() {
        return positionOnGrid;
    }

    /**
     * Setzt die Ausrichtung des Schiffs.
     * 
     * @param isVertical True für vertikal, false für horizontal.
     */
    public void setRotation(boolean isVertical) {
        this.isVertical = isVertical;
    }

    /**
     * Gibt zurück, ob das Schiff vertikal ausgerichtet ist.
     * 
     * @return True, wenn vertikal.
     */
    public boolean isVertical() {
        return isVertical;
    }

    /**
     * Gibt zurück, ob das Schiff zerstört wurde.
     * 
     * @return True, wenn zerstört.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * Setzt, ob das Schiff dem Host-Spieler gehört.
     * 
     * @param isHostShip True, wenn Host-Schiff.
     */
    public void setIsHostShip(boolean isHostShip) {
        this.isHostShip = isHostShip;
    }

    /**
     * Gibt zurück, ob das Schiff dem Host-Spieler gehört.
     * 
     * @return True, wenn Host-Schiff.
     */
    public boolean getIsHostShip() {
        return isHostShip;
    }

    /**
     * Prüft, ob das Schiff an der angegebenen Position getroffen wurde.
     * Markiert die Zelle als getroffen und prüft, ob das Schiff zerstört ist.
     * 
     * @param row Die Zeile des Schusses.
     * @param col Die Spalte des Schusses.
     * @return True, wenn das Schiff getroffen wurde, sonst false.
     */
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
                hitCells.add(new int[] { row, col });
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
     * Erstellt die visuelle Darstellung des Schiffs als StackPane mit Rectangle und
     * Zahl.
     * 
     * @param cellSize   Die Größe einer Zelle im Grid.
     * @param isVertical Gibt an, ob das Schiff vertikal oder horizontal ist.
     * @return Das erstellte StackPane-Objekt.
     */
    public StackPane createShipVisuals(int cellSize, boolean isVertical) {
        shipVisuals = new Rectangle(
                isVertical ? cellSize : cellSize * SHIP_SIZE,
                isVertical ? cellSize * SHIP_SIZE : cellSize);
        shipVisuals.setFill(Color.GRAY);
        shipVisuals.setStroke(Color.BLACK);
        shipVisuals.setStrokeWidth(3);

        Label sizeLabel = new Label(String.valueOf(SHIP_SIZE));
        sizeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        StackPane shipPane = new StackPane(shipVisuals, sizeLabel);

        // Drag-and-drop wie gehabt auf shipPane statt Rectangle
        shipPane.setOnDragDetected(event -> {
            shipPane.setMouseTransparent(true);
            Dragboard db = shipPane.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(getSize()));
            db.setContent(content);
            event.consume();
        });


        shipPane.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                shipPane.setVisible(false);
            }
            shipPane.setMouseTransparent(false);
            event.consume();
        });

        return shipPane;
    }

    /**
     * Aktualisiert die Ausrichtung des Schiffs und passt die visuelle Darstellung
     * an.
     * 
     * @param isVertical Gibt an, ob das Schiff vertikal oder horizontal sein soll.
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