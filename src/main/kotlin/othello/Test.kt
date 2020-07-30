package othello

class Test {

    /* You can paste in here your Triples received from the Print to console command.
    They will be used to create instances of an Othello board. */

    private val scenario1 = Triple(-107445466743278338L, 107304724943178497L, -1) //P2 fights for a win (other option is a tie)
    private val scenario2 = Triple(2L, 3L, 1)
    private val scenario3 = Triple(2L, 3L, 1)
    private val scenario4 = Triple(2L, 3L, 1)
    private val scenario5 = Triple(2L, 3L, 1)






    // ==================================== TEST LOGIC / DO NOT CHANGE ====================================

    init {
        println("----------- TEST MODE -----------\n")
        println("Player1 = X, Player2 = O\n\n\n")


        var test1 = Othello.of(scenario1.first, scenario1.second, scenario1.third)


    }
}