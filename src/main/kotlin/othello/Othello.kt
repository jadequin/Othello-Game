package othello

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

//http://mnemstudio.org/game-reversi-example-2.htm

class Othello (val players: List<Long> = listOf(34628173824L, 68853694464L), private val turn: Int = +1, private val prevOthello: Othello? = null, private val freeSpace: Int = 60) {


    companion object {
        private const val DEPTH = 4
        private const val MC_SEARCHES = 250

        val results = hashMapOf<Othello, Int>()
    }

    val boardPos = {pos: Int -> board() and (1L shl pos) != 0L}
    val currentPos = {pos: Int -> players[(-turn + 1).sign] and (1L shl pos) != 0L}
    val otherPos = {pos: Int -> players[(turn + 1).sign] and (1L shl pos) != 0L}

    fun board() = players[0] or players[1]

    //TODO: Muss hier minBy oder maxBy verwendet werden?
    fun bestMove(): Othello {
        var countMoves = 0.0;
        return listMoves().minBy {
            print("${((countMoves++ / countValidMoves()) * 100).toInt()}%\r");
            return@minBy it.monteCarloResult()
        }!!
    }

    fun undo(times: Int = 1): Othello = if(prevOthello == null || times == 0) this else prevOthello.undo(times - 1)

    fun nextTurn() = if (!isMoveAvailable()) switchTurns() else this

    fun switchTurns() = Othello(players, -turn, prevOthello, freeSpace)

    fun isMoveAvailable() = (0..63).any { isValidMove(it) }

    fun isValidMove(pos: Int) = !boardPos(pos) && flips(pos) != 0L

    fun countValidMoves() = (0..63).count { isValidMove(it) }

    //Game ends when there are no more moves left for both players
    fun isGameOver() = !nextTurn().isMoveAvailable()

    fun isPlayerXTurn() = turn == 1

    fun scorePlayerX() = (0..63).count { players[0] and (1L shl it) != 0L }
    fun scorePlayerO() = (0..63).count { players[1] and (1L shl it) != 0L }


    fun result() = (scorePlayerX() - scorePlayerO()).sign * turn

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
                    players = if(turn == 1)
                        listOf(players[0] or mask or (1L shl it), players[1] and mask.inv())
                    else
                        listOf(players[0] and mask.inv(), players[1] or mask or (1L shl it)),
                    turn = -turn,
                    prevOthello = this,
                    freeSpace = freeSpace - 1
            ) }




    //TODO: Hier werden große Werte aufsummiert, die teilweise gar nicht zueinander passen ?
    fun monteCarloResult(): Int {
        return(1..MC_SEARCHES).sumBy {
            val randomPlay: Othello = randomLateGame()
            return@sumBy randomPlay.alphaBeta().sign * if(randomPlay.turn == this.turn) 1 else -1
        }.sign
    }

    fun randomLateGame(): Othello = if(!isGameOver() && freeSpace > DEPTH + 1)
        nextTurn().listMoves().random().randomLateGame()
    else
        this

    //alpha beta with side effects on hash table
    fun alphaBeta(depth: Int = DEPTH, alpha: Int = Int.MAX_VALUE * -turn, beta: Int = Int.MAX_VALUE * turn): Int {

        if(results[this] != null)
            return results[this]!! * (depth + 1)

        if(isGameOver())
            return result() * (depth + 1) * 100_000

        if(!isMoveAvailable())
            return -switchTurns().alphaBeta(depth, -beta, -alpha)

        //alpha-beta-implementation combined with negamax
        val bestScore = run {
            listMoves().fold(alpha) {
                bestScore, move ->
                val score = -move.alphaBeta(depth - 1, -beta, -bestScore)
                if(bestScore in beta until score)
                    return@run bestScore
                return@fold max(bestScore, score)
            }
        }

        results[this] = bestScore
        return bestScore
    }

    //https://www.chessprogramming.org/Flipping_Mirroring_and_Rotating
    private fun hashVal(player: Long): Long {

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

            //used this functions for the magic numbers:
            //                fun shapeToString(n: Long) = (0..63).forEach { print((if(it%8==0) "\n" else "") + (if((1L shl it) and n != 0L ) "#" else " ")) }
            //                fun x(vararg i: Int): Long = i.fold(0L) { acc, i -> acc or (1L shl i) }

            //start with the middle line because it doesn't need to be reflected
            return (n and -9205322385119247871) or
                    (n and 72057594037927936 shr 49) or (n and 128 shl 49) or
                    (n and 144396663052566528 shr 42) or (n and 32832 shl 42) or
                    (n and 288794425616760832 shr 35) or (n and 8405024 shl 35) or
                    (n and 577588855528488960 shr 28) or (n and 2151686160 shl 28) or
                    (n and 1155177711073755136 shr 21) or (n and 550831656968 shl 21) or
                    (n and 2310355422147575808 shr 14) or (n and 141012904183812 shl 14) or
                    (n and 4620710844295151872 shr 7) or (n and 36099303471055874 shl 7)
        }






        //var, da damit die Performance stark verbessert wird, indem mit der bestehenden Transformierung weiter gearbeitet wird
        var minimum = player
        var rot = player

        rot = diagonalMirror(rot)
        minimum = min(minimum, rot)
        rot = horizontalMirror(rot)
        minimum = min(minimum, rot)
        rot = diagonalMirror(rot)
        minimum = min(minimum, rot)
        rot = horizontalMirror(rot)
        minimum = min(minimum, rot)
        rot = verticalMirror(rot)
        minimum = min(minimum, rot)
        rot = diagonalMirror(rot)
        minimum = min(minimum, rot)
        rot = horizontalMirror(rot)
        minimum = min(minimum, rot)

        return minimum
    }

    private fun hashValPlayer1() = hashVal(players[0])
    private fun hashValPlayer2() = hashVal(players[1])

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Othello) return false
        return other.turn == this.turn && other.hashValPlayer1() == this.hashValPlayer1() && other.hashValPlayer2() == this.hashValPlayer2()
    }

//    override fun toString(): String {
//        return (0..63).joinToString(prefix = "-".repeat(17) + "\n|", postfix = "|\n" + "-".repeat(17), separator = "|") {
//            (if(it != 0 && it%8==0) "\n|" else "") + (if(players[0] shr it and 1L == 1L) "X" else if(players[1] shr it and 1L == 1L) "O" else " ")
//        }
//    }

    override fun toString(): String {
            return (0..63).joinToString(separator = "", prefix = "<table>", postfix = "</table>") {
                (if(it%8==0 && it != 0) "</tr>" else "") +
                        (if(it%8==0) "<tr class='row'>" else "") +
                        "<td class='square' id='${it/8}_${it%8}' " +
                        (if(boardPos(it)) " style='background-image: url(\"${if(currentPos(it) && turn == 1) "black" else "white"}-circle.png\"); background-repeat: no-repeat;background-position: center; background-size: cover;'" else "") +
                        "></td>"

            }

    }
}