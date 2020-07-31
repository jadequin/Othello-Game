package othello

import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign


class Othello (private val players: List<Long> = listOf(34628173824L, 68853694464L), private val turn: Int = +1, private val prevOthello: Othello? = null): OthelloGame {


    companion object {
        private const val DEPTH = 4
        private val BOARD_INDICES = 0..63
        private val ratings = listOf(20, -3, 11, 8, 8, 11, -3, 20, -3, -7, -4, 1, 1, -4, -7, -3, 11, -4, 2, 2, 2, 2, -4, 11, 8, 1, 2, -3, -3, 2, 1, 8 , 8, 1, 2, -3, -3, 2, 1, 8, 11, -4, 2, 2, 2, 2, -4, 11, -3, -7, -4, 1, 1, -4, -7, -3,20, -3, 11, 8, 8, 11, -3, 20)

        //Descending sorted indices by value for earlier cut offs
        private val ratingIndicesSortedDesc = ratings.indices.sortedByDescending { ratings[it] }

        private val results = hashMapOf<Othello, Int>()

        fun of(p1: Long, p2: Long, turn: Int): Othello {
            return Othello(listOf(p1, p2), turn, null)
        }



        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  DATABASE INIT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        private val database = File("src/main/resources/db.txt")

        //read all saved results from DB
        init {
            val equalBoard = { players: List<Long> -> Othello(players)}

            database.forEachLine {
                val entry = it.split(";")
                val players = entry[0].substring(1, entry[0].length - 1).split(", ")
                val key = equalBoard(listOf(players[0].toLong(), players[1].toLong()))
                val value = entry[1].toInt()
                results[key] = value
            }
        }
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  BOARD SHIFTERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    private val boardPos = { pos: Int -> board() and (1L shl pos) != 0L}
    private val currentPos = { pos: Int -> players[(-turn + 1).sign] and (1L shl pos) != 0L}
    private val otherPos = {pos: Int -> players[(turn + 1).sign] and (1L shl pos) != 0L}

    private fun board() = players[0] or players[1]





    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  MOVE GENERATING METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    override fun listMoves() = ratingIndicesSortedDesc.mapNotNull {
        val mask = flips(it)
        if(mask == 0L || boardPos(it))
            return@mapNotNull null
        else
            return@mapNotNull Othello(
                    players = if(turn == 1)
                        listOf(players[0] or mask or (1L shl it), players[1] and mask.inv())
                    else
                        listOf(players[0] and mask.inv(), players[1] or mask or (1L shl it)),
                    turn = -turn,
                    prevOthello = this
            ) }

    override fun makeMove(pos: Int) = Othello(
            players = if(turn == 1)
                listOf(players[0] or flips(pos) or (1L shl pos), players[1] and flips(pos).inv())
            else
                listOf(players[0] and flips(pos).inv(), players[1] or flips(pos) or (1L shl pos)),
            turn = -turn,
            prevOthello = this
    )

    fun bestMove(): Othello = listMoves().shuffled().maxBy { it.alphaBeta() }!!

    fun randomMove() = if(isGameOver()) this else nextTurn().listMoves().random()

    override fun undo(): Othello = prevOthello ?: this

    fun nextTurn() = if (!isMoveAvailable()) switchTurns() else this

    override fun switchTurns() = Othello(players, -turn, prevOthello)

    private fun flips(pos: Int): Long {
        var flips = 0L
        //left
        if(pos%8 > 1 && otherPos(pos-1))
            for(i in pos-2 downTo (pos/8)*8) {
                if(!boardPos(i))
                    break
                if(currentPos(i)) {
                    (i + 1 until pos).forEach { flips = flips or (1L shl it) }
                    break
                }
            }
        //right
        if(pos%8 < 6 && otherPos(pos+1))
            for(i in pos+2 .. (pos/8)*8+7) {
                if(!boardPos(i))
                    break
                if(currentPos(i)) {
                    (pos+1 until i).forEach { flips = flips or (1L shl it) }
                    break
                }
            }

        //up
        if(pos > 15 && otherPos(pos-8))
            for(i in pos-16 downTo pos%8 step 8) {
                if(!boardPos(i))
                    break
                if(currentPos(i)) {
                    (i+8..pos-8 step 8).forEach { flips = flips or (1L shl it) }
                    break
                }
            }

        //down
        if(pos < 48 && otherPos(pos+8))
            for(i in pos+16 .. pos%8+56 step 8) {
                if(!boardPos(i))
                    break
                if(currentPos(i)) {
                    (pos+8..i-8 step 8).forEach { flips = flips or (1L shl it) }
                    break
                }
            }

        //diagonal /up
        if(pos/8 > 1 && pos%8 < 6 && otherPos(pos-7))
        {
            for(i in pos-14 downTo Int.MIN_VALUE step 7) {
                if(!boardPos(i) || !(i > 7 && i%8 < 7))
                    break
                if(currentPos(i)) {
                    (i+7..pos-7 step 7).forEach {flips = flips or (1L shl it) }
                    break
                }
            }
        }

        //diagonal \up
        if(pos/8 > 1 && pos%8 > 1 && otherPos(pos-9))
        {
            for(i in pos-18 downTo Int.MIN_VALUE step 9) {
                if(!boardPos(i) || !(i > 7 && i%8 > 0))
                    break
                if(currentPos(i)) {
                    (i+9..pos-9 step 9).forEach { flips = flips or (1L shl it)}
                    break
                }
            }
        }

        //diagonal /down
        if(pos/8 < 6 && pos%8 > 1 && otherPos(pos+7))
        {
            for(i in pos+14 .. Int.MAX_VALUE step 7) {
                if(!boardPos(i) || !(i < 56 && i%8 > 0))
                    break
                if(currentPos(i)) {
                    (pos+7..i-7 step 7).forEach { flips = flips or (1L shl it)}
                    break
                }
            }
        }

        //diagonal \down
        if(pos/8 <6 && pos%8 <6 && otherPos(pos+9))
        {
            for(i in pos+18..Int.MAX_VALUE step 9) {
                if(!boardPos(i) || !(i < 56 && i%8 < 7))
                    break
                if(currentPos(i)) {
                    (pos+9..i-9 step 9).forEach { flips = flips or (1L shl it) }
                    break
                }
            }
        }

        return flips
    }


    tailrec fun randomGame(it: Othello = this): Othello = if(it.isGameOver()) it else randomGame(it.nextTurn().listMoves().random())
    tailrec fun perfectGame(it: Othello = this): Othello = if(it.isGameOver()) it else perfectGame(it.nextTurn().bestMove())

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  VALIDATION METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    private fun isMoveAvailable() = BOARD_INDICES.any { isValidMove(it) }

    private fun isValidMove(pos: Int) = !boardPos(pos) && flips(pos) != 0L

    private fun countValidMoves() = BOARD_INDICES.count { isValidMove(it) }

    fun validPositions() = BOARD_INDICES.mapNotNull { if(isValidMove(it)) it else null }



    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  GAME STATUS METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    //Game ends when there are no more moves left for both players
    override fun isGameOver() = !nextTurn().isMoveAvailable()

    override fun isPlayer1Turn() = turn == 1

    override fun scorePlayer1() = BOARD_INDICES.count { players[0] and (1L shl it) != 0L }
    override fun scorePlayer2() = BOARD_INDICES.count { players[1] and (1L shl it) != 0L }

    //returns 1 if player1 has more disc, -1 if it is player2 or 0 if it is a tie
    override fun result() = (scorePlayer1() - scorePlayer2()).sign




    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  GAME HEURISTICS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    //http://home.datacomm.ch/t_wolf/tw/misc/reversi/html/index.html
    //https://kartikkukreja.wordpress.com/2013/03/30/heuristic-function-for-reversiothello/

    private fun dynamicHeuristic(): Int {
        val corners = listOf(0, 7, 56, 63)
        val cornerCloseness = listOf(1,8,9, 6,14,15, 48,49,57, 54,55,62)


        //CORNERS CAPTURED
        val cornerVal = 25 * (corners.count { currentPos(it) } - corners.count { otherPos(it) })
        val cornerClosenessVal = -12.5 * (cornerCloseness.indices.count { !boardPos(corners[it/3]) && currentPos(cornerCloseness[it]) } - cornerCloseness.indices.count { !boardPos(corners[it/3]) && otherPos(cornerCloseness[it]) })

        //MOBILITY
        val myMobility = this.countValidMoves()
        val oppMobility = switchTurns().countValidMoves()
        val mobilityVal = if(myMobility > oppMobility) (100.0 * myMobility)/(myMobility + oppMobility) else if(myMobility < oppMobility) -(100.0 * oppMobility)/(myMobility + oppMobility) else 0.0

        //STABILITY
        val pieceRating = BOARD_INDICES.sumBy { if(currentPos(it)) ratings[it] else if(otherPos(it)) -ratings[it] else 0 }

        //COIN PARITY
        val myPieces = BOARD_INDICES.count { currentPos(it) }
        val oppPieces = BOARD_INDICES.count { otherPos(it) }
        val pieceDifference = if(myPieces > oppPieces) (100.0 * myPieces)/(myPieces + oppPieces) else if(myPieces < oppPieces) -(100.0 * oppPieces)/(myPieces + oppPieces) else 0.0

        //WEIGHTED SCORE
        return ((10.0 * pieceDifference) + (801.724 * cornerVal) + (382.026 * cornerClosenessVal) + (78.922 * mobilityVal) + (10.0 * pieceRating)).toInt() // + (74.396 * )
    }




    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  ALPHA BETA EVALUATION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    //alpha beta with side effects on hash table
    fun alphaBeta(depth: Int = DEPTH, alpha: Int = -Int.MAX_VALUE, beta: Int = Int.MAX_VALUE): Int {

        //look up the table if there is a key with the current setup
        if(results[this] != null)
            return results[this]!! * depth

        //maybe there is a result with a swapped key... invert it then
        if(results[swappedBoard()] != null)
            return results[this]!! * depth * -1

        if(isGameOver())
            return result() * depth * 1000 * -turn

        if(!isMoveAvailable())
            return -switchTurns().alphaBeta(depth, -beta, -alpha)

        if(depth == 0)
            return dynamicHeuristic() * depth * -turn

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
        //database.appendText("$players;$bestScore\n") //uncomment to save much more results to the database
        return bestScore
    }

    //helper function for further use of the symmetries
    private fun swappedBoard() = Othello(listOf(players[1], players[0]))

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  SYMMETRIE BY MIRRORS AND ROTATIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

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




    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  EQUALS AND toSTRING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Othello) return false
        return other.hashValPlayer1() == this.hashValPlayer1() && other.hashValPlayer2() == this.hashValPlayer2()
    }

    override fun toString(): String {
        return BOARD_INDICES.joinToString(prefix = "-".repeat(33) + "\n|", postfix = "|\n" + "-".repeat(33), separator = "|") {
            (if(it != 0 && it%8==0) "\n|" else "") + (if(players[0] shr it and 1L == 1L) " X " else if(players[1] shr it and 1L == 1L) " O " else "%3d".format(it))
        }
    }






    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  HTML RESPONSE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    /*
        boardAsHTML###turn###scorePlayer1###scorePlayer2###result
        <table>...</table###-1###23###41###0
    */
    fun htmlResponse(): String {
            return BOARD_INDICES.joinToString(separator = "", prefix = "<table>", postfix = "</table>") {
                (if(it%8==0 && it != 0) "</tr>" else "") +
                        (if(it%8==0) "<tr>" else "") +
                        "<td class='" +
                        (if(isValidMove(it))
                            (if(turn == 1)
                                "blackSuggSquare' onclick='sendGET(\"makeMove?move=$it\")"
                            else
                                "whiteSuggSquare' onclick='sendGET(\"makeMove?move=$it\")" )
                        else if(currentPos(it) && turn == 1)
                            "blackSquare"
                        else if(currentPos(it) && turn == -1)
                            "whiteSquare"
                        else if(otherPos(it) && turn == 1)
                            "whiteSquare"
                        else if(otherPos(it) && turn == -1)
                            "blackSquare"
                        else
                            "square") +

                        "'></td>"
            } +
                    "###" +
                    turn +
                    "###" +
                    scorePlayer1() +
                    "###" +
                    scorePlayer2() +
                    "###" +
                    if(isGameOver()) result() else "nowin"
    }

    //Used for the Print to console action
    fun toTriple(): String {
        return "Triple(${players[0]}L, ${players[1]}L, $turn)"
    }
}