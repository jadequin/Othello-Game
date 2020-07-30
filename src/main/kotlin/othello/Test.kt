package othello

class Test {

    /* You can paste in here your Triples received from the Print to console command.
    They will be used to create instances of an Othello board. */

    private val scenario1 = Triple(25992864229951327L, -4637678882657339360L, 1) //P1 has the chance to win with the next move
    private val scenario2 = Triple(-510983174747348704L, 150695204540931807L, -1) //P2 has the chance to win within the next two moves
    private val scenario3 = Triple(74614631758284231L, -74614631758290416L, -1) //P2 can block a risky potential win in 4 moves
    private val scenario4 = Triple(40541744654237950L, -40682482142593535L, 1) //P1 blocks a potential win for P2
    private val scenario5 = Triple(0, 0L, 1) //alpha-beta horizon ends with depth 4, so there is no scenario5





    // ==================================== TEST LOGIC / DO NOT CHANGE ====================================

    private val scenarios = listOf(scenario1, scenario2, scenario3, scenario4)

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