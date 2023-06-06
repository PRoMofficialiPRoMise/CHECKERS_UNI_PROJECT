import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class CheckerGame extends JFrame {
    private CheckerBoard checkerBoard;
    private int TILE_SIZE = 50;

    public CheckerGame() {
        setTitle("Шашки");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        checkerBoard = new CheckerBoard();
        checkerBoard.setPreferredSize(new Dimension(400, 400));
        checkerBoard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;
                handleClick(row, col);
            }
        });
        add(checkerBoard);

        pack();
        setLocationRelativeTo(null);
    }

    private void handleClick(int row, int col) {
        Checker selectedChecker = checkerBoard.getSelectedChecker();
        int selectedCheckerRow = checkerBoard.getSelectedCheckerRow();
        int selectedCheckerCol = checkerBoard.getSelectedCheckerCol();

        if (selectedChecker == null) {
            checkerBoard.selectChecker(row, col);
        } else {
            if (checkerBoard.isValidMove(selectedCheckerRow, selectedCheckerCol, row, col)) {
                checkerBoard.makeMove(selectedCheckerRow, selectedCheckerCol, row, col);
                checkerBoard.switchPlayer();
            }
            checkerBoard.deselectChecker();
        }
        checkerBoard.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CheckerGame game = new CheckerGame();
            game.setVisible(true);
        });
    }
}
