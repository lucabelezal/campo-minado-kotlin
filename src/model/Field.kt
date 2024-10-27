package model

data class Field(val row: Int, val column: Int) {

    private val neighbors = ArrayList<Field>()
    private val callbacks = ArrayList<(Field, FieldEvent) -> Unit>()

    private var marked: Boolean = false
    private var opened: Boolean = false
    private var mined: Boolean = false

    val unmarked: Boolean get() = !marked
    val closed: Boolean get() = !opened
    val safe: Boolean get() = !mined
    val goalAchieved: Boolean get() = safe && opened || mined && marked
    val numberOfMinedNeighbors: Int get() = neighbors.filter { it.mined }.size
    val safeNeighborhood: Boolean
        get() = neighbors.map { it.safe }.reduce { result, safe -> result && safe }

    fun addNeighbor(neighbor: Field) {
        neighbors.add(neighbor)
    }

    fun onEvent(callback: (Field, FieldEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun open() {
        if (closed) {
            opened = true
            if (mined) {
                callbacks.forEach { it(this, FieldEvent.EXPLOSION) }
            } else {
                callbacks.forEach { it(this, FieldEvent.OPENING) }
                neighbors.filter { it.closed && it.safe && safeNeighborhood }.forEach { it.open() }
            }
        }
    }

    fun toggleMark() {
        if (closed) {
            marked = !marked
            val event = if (marked) FieldEvent.MARKING else FieldEvent.UNMARKING
            callbacks.forEach { it(this, event) }
        }
    }

    fun mine() {
        mined = true
    }

    fun reset() {
        opened = false
        mined = false
        marked = false
        callbacks.forEach { it(this, FieldEvent.RESET) }
    }
}
