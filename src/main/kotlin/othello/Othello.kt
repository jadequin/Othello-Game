package othello

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

//http://mnemstudio.org/game-reversi-example-2.htm

class Othello (private val board: List<Int> = List(27) { 0 } + listOf(-1, 1, 0, 0, 0, 0, 0, 0, 1, -1) + List(27) { 0 }, private val turn: Int = +1, private val history: List<Othello> = emptyList()) {


    companion object {
        private const val DEPTH = 3
        private const val REGION_5_RATING = 100
        private const val REGION_4_RATING = -50
        private const val REGION_3_RATING = 50

//        private val hashNumbers = mutableListOf(-153302156, -197532887, -773697510, -1117490641, 1029773505, 1163373828, -310661128, 1441566705, 141122477, -1107452014, -37383893, -481488927, -221624646, -1520623053, 1659357589, -911827022, 1950548759, -10099425, -546335165, -273271330, -376862941, 1232162713, -1572847734, -1225569026, -337161932, -1132847772, 782719852, -335151012, -2120185197, 1378606966, 797463103, 1219091910, 2071713483, -1499638805, -1414985328, -603225948, 1281446691, -1505507012, 1298389316, -1459860916, -1570010520, -1512243024, 228667136, 322489237, -584228625, -1678752751, -1019378742, 867414954, 1201421529, 1079943843, -215578640, -660580320, 1564339864, -1853519535, -54220504, 504703561, -1104273998, -688510423, -764168, 282045898, 881278404, -1829279990, -534468265, 1663616106, -67678262, -986762340, -1184412268, -1377737939, -1979124835, 1965793919, 1117248887, 542966945, 1462603196, 844304838, -1585511259, -1755288458, -1776450226, 1128671910, 1966610504, 1405128788, -1662001561, -936007574, -2040134417, -1797867031, -1046825846, 1968773802, -30493006, 441202240, 379738478, -1814882629, -87924839, -2027870631, 1423252485, 723684457, 883310653, 569853173, 541981625, 185086435, 1901123248, -438127184, 165202936, -1866319114, 538941337, 9449813, 1182174194, -1846554754, -2071161437, 129049453, 761853601, -1500449512, -1374897384, -2053927655, 2115538697, -508633060, -1989838392, 886358305, 446213313, -528228406, 181605679, -361018036, -306099023, 1639537090, -1438192938, -441080591, -988894692, 1959102649, -1382589191, -96444211)
//        private val hashTable: List<List<Int>> = List(64) { List(2) { hashNumbers.removeAt(0) } }

        private val zobristHashNumbers = mutableListOf(9213785217457174661, -6854019191140820120, 5851203439463682651, 5119773464219795646, 4229333390981732264, 3777225547669199560, -1149358560376487342, -4978495733234328431, -6283931165537228580, 6883302461893537525, -6777925102861180641, 8165913457401214068, -7770616801164535238, -8185253175131326903, 4385813439313420742, -3340155526862025030, 9188013448076478542, -3518406767734430543, 1127295822337415421, -4722890967406441332, -4656670788948027239, -1322133165273128400, -5059519363658235984, -5422814441067843484, 2409074725646574608, -2998978305115768485, -3988493112391023237, 8228580600946472082, 5777966077796446619, 2828259658126513628, -5103184597327908045, -5817622107849526854, -756098395964473193, 7103248896208573168, -3041575774390706851, -6061331376967805612, -4993265679745621163, 5736787008414095849, 592327861790926517, -8651059954108946389, -6264557214969583208, -5639373032087449428, 2265435376921006761, -6704505120464640801, 1877457305223240475, -6496160173916579789, 4172776641498195932, -8610976905687916957, 6534525167835824649, -6650316872547034356, -231740294868229469, 4707762984682518143, 7034409077213644163, 6287973577650826200, 62311069668603787, 4276516371613049935, 8943145315849245726, 4911806083653284813, 7376881122211177937, 6678787669932542658, -6785928166432973214, 7784853296627757365, -3395128453850457624, 4662137234704424732, 3756211372996354380, -128451958240590355, 2661926223044026359, 1125229771451244404, 5289817802424975564, 7926717102491653607, 4702329013175658072, -2591013951865841353, 5295836919405982280, 3073248427080906152, -7676027234037870565, -886067651073589341, -8341052153770297576, -7306206413897602870, -1666725842361231487, 1947340100705129037, -2314632754004547833, -1599301754384846725, -2625386563909533567, 4429147653934306203, 6387200530080287448, -5695954477635835395, -5977994081558144186, -5279924632118882485, -8084507009190115649, 8798804739731012900, -7275797128831625554, -346101380940009238, -8921530075096115996, -5976744313954690341, -4889414884561968435, 1175743334018804318, -6728157299631042588, 8951626940648885151, -7943145435546164376, -598863353540400547, 1809378763124067663, 85199296328191490, -5979637302388444549, -1577561746144763149, 8899781160428046379, -3034374427112372951, -6130405629038366028, 134370686153547776, -1360646971721163921, 2187290990903179087, 8940149513727439349, -4756449230323520072, 8532897492727041161, 2616334535408688192, 7226353288520670165, -7329478179646402783, -1527759732579949095, 6700870175655574494, 6862671758807366688, -3294596584341470056, -4168974007974551244, -1204212157999784800, -6992457244479483565, 5155258826067419725, -5295349250686852510, -7587906686897012210, -6222243928015343839, 7920307183373055682)
        private val zobristHashTable: List<List<Long>> = List(64) { List(2) { zobristHashNumbers.removeAt(0) } }

        val results = hashMapOf<Othello, Int>()

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

        private val rows = listOf(
                //horizontal
                listOf(0, 1, 2, 3, 4, 5, 6, 7),
                listOf(8, 9, 10, 11, 12, 13, 14, 15),
                listOf(16, 17, 18, 19, 20, 21, 22, 23),
                listOf(24, 25, 26, 27, 28, 29, 30, 31),
                listOf(32, 33, 34, 35, 36, 37, 38, 39),
                listOf(40, 41, 42, 43, 44, 45, 46, 47),
                listOf(48, 49, 50, 51, 52, 53, 54, 55),
                listOf(56, 57, 58, 59, 60, 61, 62, 63),

                //vertical
                listOf(0, 8, 16, 24, 32, 40, 48, 56),
                listOf(1, 9, 17, 25, 33, 41, 49, 57),
                listOf(2, 10, 18, 26, 34, 42, 50, 58),
                listOf(3, 11, 19, 27, 35, 43, 51, 59),
                listOf(4, 12, 20, 28, 36, 44, 52, 60),
                listOf(5, 13, 21, 29, 37, 45, 53, 61),
                listOf(6, 14, 22, 30, 38, 46, 54, 62),
                listOf(7, 15, 23, 31, 39, 47, 55, 63),

                //top-left-to-bottom-right-diagonal
                listOf(56),
                listOf(48, 57),
                listOf(40, 49, 58),
                listOf(32, 41, 50, 59),
                listOf(24, 33, 42, 51, 60),
                listOf(16, 25, 34, 43, 52, 61),
                listOf(8, 17, 26, 35, 44, 53, 62),
                listOf(0, 9, 18, 27, 36, 45, 54, 63),
                listOf(1, 10, 19, 28, 37, 46, 55),
                listOf(2, 11, 20, 29, 38, 47),
                listOf(3, 12, 21, 30, 39),
                listOf(4, 13, 22, 31),
                listOf(5, 14, 23),
                listOf(6, 15),
                listOf(7),

                //top-right-to-bottom-left-diagonal
                listOf(63),
                listOf(55, 62),
                listOf(47, 54, 61),
                listOf(39, 46, 53, 60),
                listOf(31, 38, 45, 52, 59),
                listOf(23, 30, 37, 44, 51, 58),
                listOf(15, 22, 29, 36, 43, 50, 57),
                listOf(7, 14, 21, 28, 35, 42, 49, 56),
                listOf(6, 13, 20, 27, 34, 41, 48),
                listOf(5, 12, 19, 26, 33, 40),
                listOf(4, 11, 18, 25, 32),
                listOf(3, 10, 17, 24),
                listOf(2, 9, 16),
                listOf(1, 8),
                listOf(0)
        )

        private val region5 = listOf(0, 7, 56, 63)
        private val region4 = listOf(1, 6, 8,9, 14,15, 48,49, 54,55, 57, 62)
        private val region3 = listOf(2,3,4,5, 16, 23, 24, 31, 32, 39, 40, 47, 58,59,60,61)
    }


    private fun move(pos: Int): Othello {
        assert(isValidMove(pos)) { "Position for the move is invalid" }

        val switchingPositions = mutableSetOf<Int>()
        switchingPositions.add(pos) //add moving position

        val rowsWithPos = rows.filter { it.contains(pos) }

        rowsWithPos.forEach {
            val leftPositions = it.subList(0, it.indexOf(pos)).dropWhile { p -> board[p] != turn }
            val rightPositions = it.subList(it.indexOf(pos) + 1, it.size).dropLastWhile { p -> board[p] != turn }

            val changingPositions = leftPositions.takeLastWhile { p -> board[p] == -turn } + rightPositions.takeWhile { p -> board[p] == -turn }
            changingPositions.forEach { p -> switchingPositions.add(p) }
        }

        return Othello(
                board = board.mapIndexed { index, i -> if (switchingPositions.contains(index)) turn else i },
                turn = -turn,
                history = history.plus(this)
        )

    }

    fun bestMove() = listMoves().maxBy { it.nextTurn().alphaBeta() }!!

    fun undo(times: Int = 1) = history.takeLast(times).firstOrNull() ?: this

    fun switchTurns() = Othello(board = board, turn = -turn, history = history)

    /* returns a list of all valid moves from the current position */
    fun listMoves() = (board.indices).mapNotNull { if (isValidMove(it)) move(it) else null }

    fun isMoveAvailable() = (board.indices).any { isValidMove(it) }

    //Game ends when there are no more moves left for both players
    fun isGameOver() = !this.isMoveAvailable() && !switchTurns().isMoveAvailable()

    fun isPlayerXTurn() = turn == 1

    fun scorePlayerX() = board.count { it == 1 }
    fun scorePlayerO() = board.count { it == -1 }

    /*
    @return 1 if current player wins, -1 if the other player wins and 0 if it's a tie
     */
    fun result(): Int {
        if(isGameOver())
            return (scorePlayerX() - scorePlayerO()).sign * turn * Int.MAX_VALUE
        else
            return (listMoves().size +
                    region3.sumBy { if(board[it] == turn) REGION_3_RATING else 0 } +
                    region4.sumBy { if(board[it] == turn) REGION_4_RATING else 0 } +
                    region5.sumBy { if(board[it] == turn) REGION_5_RATING else 0 }) * turn
    }

    private fun isValidMove(pos: Int): Boolean {

        //is the position in the board range?
        if (pos !in board.indices)
            return false

        //is position already in use?
        if (board[pos] != 0)
            return false


        val rowsWithPos = rows.filter { it.contains(pos) }

        //are chips with the same color on a row?
        if (rowsWithPos.none { row -> row.any { board[it] == turn } })
            return false

        //is a chip with the same color and with at least one from the different color between?
        return rowsWithPos.any { row ->
            val leftPositions = row.take(row.indexOf(pos)).map { board[it] }
            val rightPositions = row.drop(row.indexOf(pos) + 1).map { board[it] }

            return@any (leftPositions.contains(turn) && leftPositions.lastOrNull() ?: 0 == -turn) || (rightPositions.contains(turn) && rightPositions.firstOrNull() ?: 0 == -turn)
        }
    }

    fun nextTurn() = if (!isMoveAvailable()) switchTurns() else this

//    fun randomEndGame(): Othello {
//        return if(nextTurn().isGameOver())
//            nextTurn().undo().undo().undo().undo().undo() //5 times undo
//        else
//            nextTurn().listMoves().random().randomEndGame()
//    }
//
//    fun simulatePlays(times: Int = 100) = (1..times).sumBy {
//        val randomPlay = randomEndGame()
//        return@sumBy randomPlay.alphaBeta() * if(randomPlay.isPlayerXTurn() == this.isPlayerXTurn()) 1 else -1
//    }
//
//    fun monteCarloResult(): Int {
//
//        val monteCarloResults = listMoves().map { println("."); it.nextTurn().simulatePlays() }
//        println(monteCarloResults.size)
//        return monteCarloResults.max()!!.sign //return the sign of the highest result
//    }

    fun alphaBeta(depth: Int = DEPTH, alpha: Int = -Int.MAX_VALUE, beta: Int = Int.MAX_VALUE): Int {
        if(results[this] != null)
            return results[this]!! * -turn


        if(isGameOver())
            return result()

        if(!isMoveAvailable())
            return switchTurns().alphaBeta(depth, alpha, beta)

        if(depth == 0)
            return result()

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

        //minimax-implementation
//        val bestScore = listMoves().map { -it.alphaBeta(depth - 1, alpha, beta) }.min()!!

        results[this] = bestScore * -turn
        return bestScore
    }


    override fun toString(): String {
        var cnt = 1
        val ROW_COL_SIZE = 8
        return board.joinToString(separator = "|", prefix = "\n" + "-".repeat(ROW_COL_SIZE * 2 + 1) + "\n|", postfix = "-".repeat(ROW_COL_SIZE * 2 + 1),
                transform = { (if(it == -1) "O" else if(it == 1) "X" else " ") + (if(cnt++ % ROW_COL_SIZE == 0) "|\n" else "") })
    }


    /*
     Checks the equality depending on the hash code
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Othello) return false
        return other.zobristHashCode() == this.zobristHashCode()
    }

    //https://en.wikipedia.org/wiki/Zobrist_hashing
    fun zobristHashCode(): Long {
        val bRotation = boardRotations.map { rotation -> rotation.map {board[it]} }
        val bReflection = boardReflections.map { reflection -> reflection.map {board[it]} }

        /*
        Return the smallest hashcode
         */

        return (bRotation + bReflection).fold(Long.MAX_VALUE) {
            minimum, b ->
            min(minimum, (b.indices).fold(0L) {
                acc, j ->
                if(b[j] != 0)
                    acc xor zobristHashTable[j][if(b[j] == 1) 0 else 1]
                else
                    acc
            })
        } * turn
    }


//    override fun hashCode(): Int {
//        val bRotation = boardRotations.map { rotation -> rotation.map {board[it]} }
//        val bReflection = boardReflections.map { reflection -> reflection.map {board[it]} }
//
//        /*
//        Return the smallest hashcode
//         */
//        return (bRotation + bReflection).fold(Int.MAX_VALUE) {
//            minimum, b ->
//            min(minimum, (b.indices).fold(0) {
//                acc, j ->
//                if(b[j] != 0) acc xor hashTable[j][if(b[j] == 1) 0 else 1] else acc
//            })
//        } * turn
//    }
}