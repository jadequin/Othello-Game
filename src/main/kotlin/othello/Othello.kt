package othello

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

//http://mnemstudio.org/game-reversi-example-2.htm

class Othello (private val colors: List<Long> = listOf(34628173824L, 68853694464L), private val turn: Int = +1, private val prevOthello: Othello? = null, private val freeSpace: Int = 60) {


    companion object {
        private const val DEPTH = 5
        private const val MC_SEARCHES = 300

        val results = hashMapOf<Pair<Long, Long>, Int>()

        private val boardRotations = listOf(
                listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63),
                listOf(7, 15, 23, 31, 39, 47, 55, 63, 6, 14, 22, 30, 38, 46, 54, 62, 5, 13, 21, 29, 37, 45, 53, 61, 4, 12, 20, 28, 36, 44, 52, 60, 3, 11, 19, 27, 35, 43, 51, 59, 2, 10, 18, 26, 34, 42, 50, 58, 1, 9, 17, 25, 33, 41, 49, 57, 0, 8, 16, 24, 32, 40, 48, 56),
                listOf(63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0),
                listOf(56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 28, 20, 12, 4, 61, 53, 45, 37, 29, 21, 13, 5, 62, 54, 46, 38, 30, 22, 14, 6, 63, 55, 47, 39, 31, 23, 15, 7)
        )

        private val boardReflections = listOf(
                listOf(56, 57, 58, 59, 60, 61, 62, 63, 48, 49, 50, 51, 52, 53, 54, 55, 40, 41, 42, 43, 44, 45, 46, 47, 32, 33, 34, 35, 36, 37, 38, 39, 24, 25, 26, 27, 28, 29, 30, 31, 16, 17, 18, 19, 20, 21, 22, 23, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7),
                listOf(63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 12, 4, 59, 51, 43, 35, 27, 19, 11, 3, 58, 50, 42, 34, 26, 18, 10, 2, 57, 49, 41, 33, 25, 17, 9, 1, 56, 48, 40, 32, 24, 16, 8, 0),
                listOf(7, 6, 5, 4, 3, 2, 1, 0, 15, 14, 13, 12, 11, 10, 9, 8, 23, 22, 21, 20, 19, 18, 17, 16, 31, 30, 29, 28, 27, 26, 25, 24, 39, 38, 37, 36, 35, 34, 33, 32, 47, 46, 45, 44, 43, 42, 41, 40, 55, 54, 53, 52, 51, 50, 49, 48, 63, 62, 61, 60, 59, 58, 57, 56),
                listOf(0, 8, 16, 24, 32, 40, 48, 56, 1, 9, 17, 25, 33, 41, 49, 57, 2, 10, 18, 26, 34, 42, 50, 58, 3, 11, 19, 27, 35, 43, 51, 59, 4, 12, 20, 28, 36, 44, 52, 60, 5, 13, 21, 29, 37, 45, 53, 61, 6, 14, 22, 30, 38, 46, 54, 62, 7, 15, 23, 31, 39, 47, 55, 63)
        )
    }

    val boardPos = {pos: Int -> board() and (1L shl pos) != 0L}
    val currentPos = {pos: Int -> colors[(-turn + 1).sign] and (1L shl pos) != 0L}
    val otherPos = {pos: Int -> colors[(turn + 1).sign] and (1L shl pos) != 0L}

    fun board() = colors[0] or colors[1]

    fun bestMove() = listMoves().maxBy { it.monteCarloResult() }!!

    fun undo(times: Int = 1): Othello = if(prevOthello == null || times == 0) this else prevOthello.undo(times - 1)

    fun nextTurn() = if (!isMoveAvailable()) switchTurns() else this

    fun switchTurns() = Othello(colors, -turn, prevOthello, freeSpace)

    fun isMoveAvailable() = (0..63).any { isValidMove(it) }

    //Game ends when there are no more moves left for both players
    fun isGameOver() = !nextTurn().isMoveAvailable()

    fun isPlayerXTurn() = turn == 1

    fun scorePlayerX() = (0..63).count { colors[0] and (1L shl it) != 0L }
    fun scorePlayerO() = (0..63).count { colors[1] and (1L shl it) != 0L }

    /*
    @return +infinite if current player wins, -infinite
    if it is midgame and there is no winner yet make a heuristic evaluation
     */
    fun result() = (scorePlayerX() - scorePlayerO()) * turn

    fun flips(pos: Int): Long {
        var flips = 0L

        //left
        if(pos%8 > 1 && otherPos(pos-1))
            for(i in pos-2 downTo (pos/8)*8) {
                if(!boardPos(i))
                    break
                if(currentPos(i))
                    (i+1..pos-1).forEach { flips = flips or (1L shl it) }
            }

        //right
        if(pos%8 < 6 && otherPos(pos+1))
            for(i in pos+2 .. (pos/8)*8+7) {
                if(!boardPos(i))
                    break
                if(currentPos(i))
                    (pos+1..i-1).forEach { flips = flips or (1L shl it) }
            }

        //up
        if(pos > 15 && otherPos(pos-8))
            for(i in pos-16 downTo pos%8 step 8) {
                if(!boardPos(i))
                    break
                if(currentPos(i))
                    (i+8..pos-8 step 8).forEach { flips = flips or (1L shl it) }
            }

        //down
        if(pos < 48 && otherPos(pos+8))
            for(i in pos+16 .. pos%8+56 step 8) {
                if(!boardPos(i))
                    break
                if(currentPos(i))
                    (pos+8..i-8 step 8).forEach { flips = flips or (1L shl it) }
            }

        //diagonal /up
        if(pos/8 > 1 && pos%8 < 6 && otherPos(pos-7))
        {
            var i = pos-14
            while(i > 7 && i%8 < 7) {
                if(!boardPos(i))
                    break
                if(currentPos(i))
                    (i+7..pos-7 step 7).forEach {flips = flips or (1L shl it) }
                i -=7
            }
        }

        //diagonal \up
        if(pos/8 > 1 && pos%8 > 1 && otherPos(pos-9))
        {
            var i = pos-18
            while(i > 7 && i%8 > 0) {
                if(!boardPos(i))
                    break
                if(currentPos(i))
                    (i+9..pos-9 step 9).forEach { flips = flips or (1L shl it)}
                i -= 9
            }
        }

        //diagonal /down
        if(pos/8 < 6 && pos%8 > 1 && otherPos(pos+7))
        {
            var i = pos+14
            while(i < 56 && i%8 > 0) {
                if(!boardPos(i))
                    break
                if(currentPos(i))
                    (pos+7..i-7 step 7).forEach { flips = flips or (1L shl it)}
                i += 7
            }
        }

        //diagonal \down
        if(pos/8 <6 && pos%8 <6 && otherPos(pos+9))
        {
            var i = pos+18
            while(i < 56 && i%8 < 7) {
                if(!boardPos(i))
                    break
                if(currentPos(i))
                    (pos+9..i-9 step 9).forEach { flips = flips or (1L shl it) }
                i += 9
            }
        }

        return flips
    }

    fun listMoves() = (0..63).mapNotNull {
        val mask = flips(it)
        if(mask == 0L || boardPos(it))
            null
        else
            Othello(
                    colors = if(turn == 1)
                        listOf(colors[0] or mask or (1L shl it), colors[1] and mask.inv())
                    else
                        listOf(colors[0] and mask.inv(), colors[1] or mask or (1L shl it)),
                    turn = -turn,
                    prevOthello = this,
                    freeSpace = freeSpace - 1
            ) }


    fun isValidMove(pos: Int) = !boardPos(pos) && flips(pos) != 0L


    fun randomEndGame(): Othello = if(!isGameOver() && freeSpace > DEPTH + 1)
        nextTurn().listMoves().random().randomEndGame()
    else
        this

    fun monteCarloResult(): Int {
        println(".")
        return(1..MC_SEARCHES).sumBy {
            val randomPlay = randomEndGame()
            return@sumBy randomPlay.alphaBeta() * if(randomPlay.isPlayerXTurn() == this.isPlayerXTurn()) 1 else -1
        }.sign
    }

    fun alphaBeta(alpha: Int = -Int.MAX_VALUE, beta: Int = Int.MAX_VALUE): Int {

        if(results[Pair(mirAndRot(colors[0]), mirAndRot(colors[1]))] != null)
            return results[Pair(mirAndRot(colors[0]), mirAndRot(colors[1]))]!! * -turn

        if(isGameOver())
            return result()

        if(!isMoveAvailable())
            return switchTurns().alphaBeta(alpha, beta)

        //alpha-beta-implementation combined with negamax
        val bestScore = run {
            listMoves().fold(alpha) {
                bestScore, move ->
                val score = -move.alphaBeta(-beta, -bestScore)
                if(bestScore in beta until score)
                    return@run bestScore
                return@fold max(bestScore, score)
            }
        }

        results[Pair(mirAndRot(colors[0]), mirAndRot(colors[1]))] = bestScore
        return bestScore
    }

    fun mirAndRot(player: Long): Long {
        return (boardRotations + boardReflections).fold(Long.MAX_VALUE) {
            minimum, list ->
            min(minimum, list.fold(0L) { acc, i -> acc or (player and (1L shl i)) })
        }
    }

    fun verticalMirror(n: Long): Long {
        return (n and 72340172838076673 shl 7) or (n and -9187201950435737472 shr 7) or
                (n and 144680345676153346 shl 5) or (n and 4629771061636907072 shr 5) or
                (n and 289360691352306692 shl 3) or (n and 2314885530818453536 shr 3) or
                (n and 578721382704613384 shl 1) or (n and 1157442765409226768 shr 1)
    }

    fun horizontalMirror(n: Long): Long {
        return (n and 255 shl 56) or (n and -72057594037927936 shr 56) or
                (n and 65280 shl 40) or (n and 71776119061217280 shr 40) or
                (n and 16711680 shl 24) or (n and 280375465082880 shr 24) or
                (n and 4278190080 shl 8) or (n and 1095216660480 shr 8)
    }

    fun diagonalMirror(n: Long): Long  {
        //diagonal CUT FOLD from top left to right bottom

        //used this function for the magic numbers:
        //                                          fun x(vararg i: Int): Long = i.fold(0L) { acc, i -> acc or (1L shl i) }

        //start with the middle line because it doesn't need to be reflected
        return -9205322385119247871 or
                (n and 72057594037927936 shr 49) or (n and 128 shl 49) or
                (n and 144396663052566528 shr 42) or (n and 32832 shl 42) or
                (n and 288794425616760832 shr 35) or (n and 8405024 shl 35) or
                (n and 577588855528488960 shr 28) or (n and 2151686160 shl 28) or
                (n and 1155177711073755136 shr 21) or (n and 550831656968 shl 21) or
                (n and 2310355422147575808 shr 14) or (n and 141012904183812 shl 14) or
                (n and 4620710844295151872 shr 7) or (n and 36099303471055874 shl 7)
    }

    //magic number 17 = row length of 8 times 2 for the spaces plus one in the end for correction
    override fun toString(): String {
        return (0..63).joinToString(prefix = "-".repeat(17) + "\n|", postfix = "|\n" + "-".repeat(17), separator = "|") {
            (if(it != 0 && it%8==0) "\n|" else "") + (if(colors[0] shr it and 1L == 1L) "X" else if(colors[1] shr it and 1L == 1L) "O" else " ")
        }
    }
}