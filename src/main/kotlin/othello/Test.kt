package othello

class Test {

    /* You can paste in here your Triples received from the Print to console command.
    They will be used to create instances of an Othello board. */

    private val scenario1 = Triple(53429417757163718L, -53429417757163976L, 1) //P1 blocks P2
    private val scenario2 = Triple(53429417757163718L, -53429417757163976L, 1)
    private val scenario3 = Triple(53429417757163718L, -53429417757163976L, 1)
    private val scenario4 = Triple(53429417757163718L, -53429417757163976L, 1)
    private val scenario5 = Triple(53429417757163718L, -53429417757163976L, 1)





    // ==================================== TEST LOGIC / DO NOT CHANGE ====================================

    private val scenarios = listOf(scenario1, scenario2, scenario3, scenario4, scenario5)

    init {
        println("----------- TEST MODE START -----------------------------------------------------------------------------------------\n")
        println("Player1 = X                  Player2 = O\n\n")
        println("Positive evaluation values are good for the current player, while negative values could lead to a loss")
        println("---------------------------------------------------------------------------------------------------------------------\n\n\n\n")

        //execute all test cases
        scenarios.indices.forEach {
            println("\n\n\n\n")
            println("###########################################################################")
            println("#                           Test ${it+1}                                        #")
            println("###########################################################################")

            var o = Othello.of(scenarios[it].first, scenarios[it].second, scenarios[it].third)

            println()
            println()

            println(o.toString())

            println()
            println("~~~~~~~~~~~~~~~~~~~~ EVALUATIONS ~~~~~~~~~~~~~~~~~~~~")
            o.validPositions().forEach {
                pos ->
                println("Position $pos = " + o.makeMove(pos).alphaBeta())
            }

            println()

            println("~~~~~~~~~~~~~~~~~~~~ BEST MOVE ~~~~~~~~~~~~~~~~~~~~")
            println("at position " + o.validPositions().maxBy { pos -> o.makeMove(pos).alphaBeta() })
            println()
            println(o.bestMove().toString())

            println()

            println("~~~~~~~~~~~~~~~~~~~~ LEADS TO ~~~~~~~~~~~~~~~~~~~~")
            println(if(o.perfectGame().result() == 1) "Win for player1" else if(o.perfectGame().result() == -1) "Win for player2" else "Tie")
            println()
            println(o.perfectGame())
            println("Score: (P1=${o.perfectGame().scorePlayer1()} | P2=${o.perfectGame().scorePlayer2()})")
        }
    }
}