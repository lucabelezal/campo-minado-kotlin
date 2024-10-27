package view

import model.Board
import model.BoardEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class MainScreen : JFrame() {

    private val board = Board(numRows = 16, numColumns = 30, numMines = 89)
    private val boardPanel = BoardPanel(board)

    init {
        board.onEvent(this::showResult)
        add(boardPanel)

        setSize(690, 438)
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Minesweeper"
        isVisible = true
    }

    private fun showResult(event: BoardEvent) {
        SwingUtilities.invokeLater {
            val msg = when(event) {
                BoardEvent.VICTORY -> "You won!"
                BoardEvent.DEFEAT -> "You lost... :P"
            }

            JOptionPane.showMessageDialog(this, msg)
            board.restart()

            boardPanel.repaint()
            boardPanel.validate()
        }
    }
}