package battleships;

class Ship {
    private int size;
    private int startRow;
    private int startCol;
    private boolean horizontal;

    public Ship(int size, int startRow, int startCol, boolean horizontal) {
        this.size = size;
        this.startRow = startRow;
        this.startCol = startCol;
        this.horizontal = horizontal;
    }

    public int getSize() {
        return size;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public boolean isHorizontal() {
        return horizontal;
    }
}
