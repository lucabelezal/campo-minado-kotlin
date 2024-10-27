package view

import model.Field
import model.FieldEvent
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.SwingUtilities

private val BG_COLOR_NORMAL = Color(184, 184, 184)
private val BG_COLOR_MARKED = Color(8, 179, 247)
private val BG_COLOR_EXPLOSION = Color(189, 66, 68)
private val TEXT_COLOR_GREEN = Color(0, 100, 0)

class FieldButton(private val field: Field) : JButton() {

    init {
        font = font.deriveFont(Font.BOLD)
        background = BG_COLOR_NORMAL
        isOpaque = true
        border = BorderFactory.createBevelBorder(0)
        addMouseListener(MouseClickListener(field, { it.open() }, { it.toggleMark() }))

        field.onEvent(this::applyStyle)
    }

    private fun applyStyle(field: Field, event: FieldEvent) {
        when(event) {
            FieldEvent.EXPLOSION -> applyExplodedStyle()
            FieldEvent.OPENING -> applyOpenedStyle()
            FieldEvent.MARKING -> applyMarkedStyle()
            else -> applyDefaultStyle()
        }

        SwingUtilities.invokeLater {
            repaint()
            validate()
        }
    }

    private fun applyExplodedStyle() {
        background = BG_COLOR_EXPLOSION
        text = "X"
    }

    private fun applyOpenedStyle() {
        background = BG_COLOR_NORMAL
        border = BorderFactory.createLineBorder(Color.GRAY)

        foreground = when (field.numberOfMinedNeighbors) {
            1 -> TEXT_COLOR_GREEN
            2 -> Color.BLUE
            3 -> Color.YELLOW
            4, 5, 6 -> Color.RED
            else -> Color.PINK
        }

        text = if (field.numberOfMinedNeighbors > 0) field.numberOfMinedNeighbors.toString() else ""
    }

    private fun applyMarkedStyle() {
        background = BG_COLOR_MARKED
        foreground = Color.BLACK
        text = "M"
    }

    private fun applyDefaultStyle() {
        background = BG_COLOR_NORMAL
        border = BorderFactory.createBevelBorder(0)
        text = ""
    }
}