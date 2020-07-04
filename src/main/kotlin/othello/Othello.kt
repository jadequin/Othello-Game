package othello

import kotlin.math.min
import kotlin.random.Random

class Othello (private val board: List<Int> = IntArray(27){0}.toList() + listOf(-1, 1, 0, 0, 0, 0, 0, 0, 1, -1) + IntArray(27){0}.toList(), private val turn: Int = +1, private val history: List<Othello> = listOf()) {

    companion object {
        //fill with random bit strings of 64 bits
        val zobristTable: Array<LongArray> = Array(64) {LongArray(2) {Random.nextLong()} } //TODO: Unique random numbers
    }

    private val rows = listOf(
            //horizontal
            listOf(0,1,2,3,4,5,6,7),
            listOf(8,9,10,11,12,13,14,15),
            listOf(16,17,18,19,20,21,22,23),
            listOf(24,25,26,27,28,29,30,31),
            listOf(32,33,34,35,36,37,38,39),
            listOf(40,41,42,43,44,45,46,47),
            listOf(48,49,50,51,52,53,54,55),
            listOf(56,57,58,59,60,61,62,63),

            //vertical
            listOf(0,8,16,24,32,40,48,56),
            listOf(1,9,17,25,33,41,49,57),
            listOf(2,10,18,26,34,42,50,58),
            listOf(3,11,19,27,35,43,51,59),
            listOf(4,12,20,28,36,44,52,60),
            listOf(5,13,21,29,37,45,53,61),
            listOf(6,14,22,30,38,46,54,62),
            listOf(7,15,23,31,39,47,55,63),

            //top-left-to-bottom-right-diagonal
            listOf(56),
            listOf(48,57),
            listOf(40,49,58),
            listOf(32,41,50,59),
            listOf(24,33,42,51,60),
            listOf(16,25,34,43,52,61),
            listOf(8,17,26,35,44,53,62),
            listOf(0,9,18,27,36,45,54,63),
            listOf(1,10,19,28,37,46,55),
            listOf(2,11,20,29,38,47),
            listOf(3,12,21,30,39),
            listOf(4,13,22,31),
            listOf(5,14,23),
            listOf(6,15),
            listOf(7),

            //top-right-to-bottom-left-diagonal
            listOf(63),
            listOf(55,62),
            listOf(47,54,61),
            listOf(39,46,53,60),
            listOf(31,38,45,52,59),
            listOf(23,30,37,44,51,58),
            listOf(15,22,29,36,43,50,57),
            listOf(7,14,21,28,35,42,49,56),
            listOf(6,13,20,27,34,41,48),
            listOf(5,12,19,26,33,40),
            listOf(4,11,18,25,32),
            listOf(3,10,17,24),
            listOf(2,9,16),
            listOf(1,8),
            listOf(0)
   )
    private fun rowsWithPos(pos: Int) = rows.filter { it.contains(pos) }

    fun move(pos: Int): Othello {
        
        return Othello(board = board.take(pos) + listOf(turn) + board.drop(pos + 1), turn = -turn, history = history.plus(this))
    }

    fun undo() = history.lastOrNull() ?: this

    /*
    returns a list of all valid moves from the current position
     */
    fun availableMoves() = board.mapIndexedNotNull { index, _ -> if(isValidMove(index)) move(index) else null }

    fun isGameOver() = availableMoves().isEmpty()

    fun isValidMove(pos: Int): Boolean {

        //is position already in use?
        if(board[pos] != 0)
            return false

        //are chips with the same color on a row?
        if(rowsWithPos(pos).none { row -> row.any { board[it] == turn } })
            return false

        //is a chip with the same color and with at least one from the different color between?
        return rowsWithPos(pos).any { row ->
            val colorsLeft = row.take(row.indexOf(pos)).map { board[it] }
            val colorsRight = row.drop(row.indexOf(pos) + 1).map { board[it] }

            return@any (colorsLeft.contains(turn) && colorsLeft.lastOrNull()?:0 == -turn) || (colorsRight.contains(turn) && colorsRight.firstOrNull()?:0 == -turn)
        }
    }

    override fun toString(): String {
        var cnt = 1
        val ROW_COL_SIZE = 8
        return board.joinToString(separator = "|", prefix = "\n" + "-".repeat(ROW_COL_SIZE * 2 + 1) + "\n|", postfix = "-".repeat(ROW_COL_SIZE * 2 + 1),
                transform = { (if(it == -1) "O" else if(it == 1) "X" else " ") + (if(cnt++ % ROW_COL_SIZE == 0) "|\n" else "") })
    }


    /*
     Checks the equality depending on the Zobrist Hash Code
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return if(other is Othello) other.zobristHashCode() == this.zobristHashCode() else false
    }


    //https://en.wikipedia.org/wiki/Zobrist_hashing
    fun zobristHashCode(): Long {

        val boardRotations = listOf(
            listOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63),
            listOf(7,15,23,31,39,47,55,63,6,14,22,30,38,46,54,62,5,13,21,29,37,45,53,61,4,12,20,28,36,44,52,60,3,11,19,27,35,43,51,59,2,10,18,26,34,42,50,58,1,9,17,25,33,41,49,57,0,8,16,24,32,40,48,56),
            listOf(63,62,61,60,59,58,57,56,55,54,53,52,51,50,49,48,47,46,45,44,43,42,41,40,39,38,37,36,35,34,33,32,31,30,29,28,27,26,25,24,23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0),
            listOf(56,48,40,32,24,16,8,0,57,49,41,33,25,17,9,1,58,50,42,34,26,18,10,2,59,51,43,35,27,19,11,3,60,52,44,36,28,20,12,4,61,53,45,37,29,21,13,5,62,54,46,38,30,22,14,6,63,55,47,39,31,23,15,7)
        ).map { rot -> rot.map {board[it]} }

        val boardReflections = listOf(
                listOf(56,57,58,59,60,61,62,63,48,49,50,51,52,53,54,55,40,41,42,43,44,45,46,47,32,33,34,35,36,37,38,39,24,25,26,27,28,29,30,31,16,17,18,19,20,21,22,23,8,9,10,11,12,13,14,15,0,1,2,3,4,5,6,7),
                listOf(63,55,47,39,31,23,15,7,62,54,46,38,30,22,14,6,61,53,45,37,29,21,13,5,60,52,44,36,28,20,12,4,59,51,43,35,27,19,11,3,58,50,42,34,26,18,10,2,57,49,41,33,25,17,9,1,56,48,40,32,24,16,8,0),
                listOf(7,6,5,4,3,2,1,0,15,14,13,12,11,10,9,8,23,22,21,20,19,18,17,16,31,30,29,28,27,26,25,24,39,38,37,36,35,34,33,32,47,46,45,44,43,42,41,40,55,54,53,52,51,50,49,48,63,62,61,60,59,58,57,56),
                listOf(0,8,16,24,32,40,48,56,1,9,17,25,33,41,49,57,2,10,18,26,34,42,50,58,3,11,19,27,35,43,51,59,4,12,20,28,36,44,52,60,5,13,21,29,37,45,53,61,6,14,22,30,38,46,54,62,7,15,23,31,39,47,55,63)
        ).map { rot -> rot.map {board[it]} }


        return (boardRotations + boardReflections).fold(Long.MAX_VALUE) {
            minimum, b ->
            min(minimum, (b.indices).fold(0L) { acc, j ->  if(b[j] != 0) acc xor zobristTable[j][if(b[j] == 1) 0 else 1] else acc})
        }
    }
}