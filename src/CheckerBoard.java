import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class CheckerBoard extends JPanel {
    public static final int BOARD_SIZE = 8;
    public static final int TILE_SIZE = 50;
    private Checker selectedChecker;
    private int selectedCheckerRow;
    private int selectedCheckerCol;
    private Checker[][] board;
    public Checker getSelectedChecker() {
        return selectedChecker;
    }
    public int getSelectedCheckerRow() {
        return selectedCheckerRow;
    }
    public int getSelectedCheckerCol() {
        return selectedCheckerCol;
    }
    public void deselectChecker() {
        selectedChecker = null;
    }
    public CheckerBoard() {
        board = new Checker[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }
    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    if (row < 3) {
                        board[row][col] = new Checker(Checker.Type.BLACK);
                    } else if (row > 4) {
                        board[row][col] = new Checker(Checker.Type.RED);
                    }
                }
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawCheckers(g);
    }
    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Color tileColor = (row + col) % 2 == 0 ? Color.WHITE : Color.BLACK;
                g.setColor(tileColor);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }
    private void drawCheckers(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Checker checker = board[row][col];
                if (checker != null) {
                    g.setColor(checker.getType() == Checker.Type.RED ? Color.RED : Color.GRAY);
                    g.fillOval(col * TILE_SIZE + 5, row * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10);
                    if (checker.isKing()) {
                        g.setColor(Color.YELLOW);
                        g.drawOval(col * TILE_SIZE + 10, row * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                    }
                }
                if (selectedChecker != null && row == selectedCheckerRow && col == selectedCheckerCol) {
                    g.setColor(Color.GREEN);
                    g.drawRect(col * TILE_SIZE + 2, row * TILE_SIZE + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                    List<Point> validMoves = getValidMoves(row, col);
                    for (Point move : validMoves) {
                        g.setColor(Color.CYAN);
                        g.fillOval(move.y * TILE_SIZE + 15, move.x * TILE_SIZE + 15, TILE_SIZE - 30, TILE_SIZE - 30);
                    }
                }
            }
        }
    }
    private Checker.Type currentPlayer = Checker.Type.RED;
    public void selectChecker(int row, int col) {
        Checker checker = board[row][col];
        if (checker != null && checker.getType() == currentPlayer) {
            selectedChecker = checker;
            selectedCheckerRow = row;
            selectedCheckerCol = col;
        }
    }
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < 0 || toRow >= BOARD_SIZE || toCol < 0 || toCol >= BOARD_SIZE) {
            return false;
        }

        if (board[toRow][toCol] != null) {
            return false;
        }

        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (selectedChecker.isKing()) {
            return rowDiff == colDiff && (rowDiff == 1 || canJump(fromRow, fromCol, toRow, toCol));
        } else {
            int forward = currentPlayer == Checker.Type.RED ? -1 : 1;
            if (toRow - fromRow == forward && colDiff == 1) {
                return true;
            } else if (rowDiff == 2 && colDiff == 2) {
                return canJump(fromRow, fromCol, toRow, toCol);
            }
        }

        return false;
    }
    private boolean canJump(int fromRow, int fromCol, int toRow, int toCol) {
        int middleRow = (fromRow + toRow) / 2;
        int middleCol = (fromCol + toCol) / 2;
        Checker middleChecker = board[middleRow][middleCol];
        return middleChecker != null && middleChecker.getType() != currentPlayer;
    }
    public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = selectedChecker;
        board[fromRow][fromCol] = null;

        int rowDiff = Math.abs(toRow - fromRow);
        if (rowDiff == 2) {
            int middleRow = (fromRow + toRow) / 2;
            int middleCol = (fromCol + toCol) / 2;
            board[middleRow][middleCol] = null;
            List<Point> validMoves = getValidMoves(toRow, toCol);
            boolean canJumpAgain = false;
            for (Point move : validMoves) {
                if (Math.abs(move.x - toRow) == 2) {
                    canJumpAgain = true;
                    break;
                }
            }
            if (canJumpAgain) {
                selectedCheckerRow = toRow;
                selectedCheckerCol = toCol;
                return;
            }
        }

        if ((currentPlayer == Checker.Type.RED && toRow == 0) || (currentPlayer == Checker.Type.BLACK && toRow == BOARD_SIZE - 1)) {
            selectedChecker.makeKing();
        }
        selectedChecker = null;
    }
    public void switchPlayer() {
        currentPlayer = currentPlayer == Checker.Type.RED ? Checker.Type.BLACK : Checker.Type.RED;
    }
    public List<Point> getValidMoves(int row, int col) {
        List<Point> validMoves = new ArrayList<>();
        if (selectedChecker.isKing()) {
            for (int rowDirection = -1; rowDirection <= 1; rowDirection += 2) {
                for (int colDirection = -1; colDirection <= 1; colDirection += 2) {
                    int newRow = row + rowDirection;
                    int newCol = col + colDirection;
                    while (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE) {
                        if (isValidMove(row, col, newRow, newCol)) {
                            validMoves.add(new Point(newRow, newCol));
                        }
                        newRow += rowDirection;
                        newCol += colDirection;
                    }
                }
            }
        } else {
            int[] colDirections = {-1, 1};
            int forward = currentPlayer == Checker.Type.RED ? -1 : 1;
            for (int colDirection : colDirections) {
                int newRow = row + forward;
                int newCol = col + colDirection;
                if (isValidMove(row, col, newRow, newCol)) {
                    validMoves.add(new Point(newRow, newCol));
                }
            }
        }
        return validMoves;
    }

}

