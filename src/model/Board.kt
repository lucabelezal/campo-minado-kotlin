package model

import java.util.*

class Board(
    val numRows: Int,
    val numColumns: Int,
    private val numMines: Int
) {
    private val fields = ArrayList<ArrayList<Field>>()
    private val callbacks = ArrayList<(BoardEvent) -> Unit>()

    init {
        generateFields()
        associateNeighbors()
        drawMines()
    }

    private fun generateFields() {
        for (row in 0..<numRows) {
            fields.add(ArrayList())
            for (column in 0..<numColumns) {
                val newField = Field(row, column)
                newField.onEvent(this::checkDefeatOrVictory)
                fields[row].add(newField)
            }
        }
    }

    private fun associateNeighbors() {
        forEachField { associateNeighbors(it) }
    }

    private fun associateNeighbors(field: Field) {
        val (row, column) = field
        val rows = arrayOf(row - 1, row, row + 1)
        val columns = arrayOf(column - 1, column, column + 1)

        rows.forEach { r ->
            columns.forEach { c ->
                val current = fields.getOrNull(r)?.getOrNull(c)
                current?.takeIf { field != it }?.let { field.addNeighbor(it) }
            }
        }
    }

    private fun drawMines() {
        val generator = Random()

        var drawnRow = -1
        var drawnColumn = -1
        var currentMineCount = 0

        while (currentMineCount < this.numMines) {
            drawnRow = generator.nextInt(numRows)
            drawnColumn = generator.nextInt(numColumns)

            val drawnField = fields[drawnRow][drawnColumn]
            if (drawnField.safe) {
                drawnField.mine()
                currentMineCount++
            }
        }
    }

    private fun goalAchieved(): Boolean {
        var playerWon = true
        forEachField { if (!it.goalAchieved) playerWon = false }
        return playerWon
    }

    private fun checkDefeatOrVictory(field: Field, event: FieldEvent) {
        if (event == FieldEvent.EXPLOSION) {
            callbacks.forEach { it(BoardEvent.DEFEAT) }
        } else if (goalAchieved()) {
            callbacks.forEach { it(BoardEvent.VICTORY) }
        }
    }

    fun forEachField(callback: (Field) -> Unit) {
        fields.forEach { row -> row.forEach(callback) }
    }

    fun onEvent(callback: (BoardEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun restart() {
        forEachField { it.reset() }
        drawMines()
    }
}