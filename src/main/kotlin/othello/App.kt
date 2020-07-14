
package othello

import kotlin.io.readLine

fun main() {

    //Test3
    var o = Othello()
    println(o)
    o = o.availableMoves().random()
    println(o)
//    (0..2).forEach { o = o.availableMoves().random() }
//    o.minimax()
//    println("Player ${if(o.isPlayerXTurn()) "X" else "O"} will ${if(o.result() == 1) "win" else if(o.result() == -1) "loose" else "tie up with the opponent"}")
//    while(!o.isGameOver()) o = o.bestMove()
//    println("Player ${if(o.isPlayerXTurn()) "X" else "O"} got the ${if(o.result() == 1) "win" else if(o.result() == -1) "loose" else "tie up with the opponent"}")


    //Test2
//    var o = Othello()
//    (0..100).forEach {
//        println(o.monteCarloResult())
//    }


    //Test1
//    var o = Othello()
//    (0..50).forEach { _ -> if(!o.isMoveAvailable()) { o = o.switchTurns(); return@forEach;}; o = o.availableMoves().random() }
//    o.minimax()
//
//    var myTurn = true
//
//    while(!o.isGameOver()) {
//
//        //my turn
//        if(!o.isMoveAvailable()) {
//            o = o.switchTurns()
//            myTurn = !myTurn
//        }
//
//        if(myTurn) {
//            println(o)
//            println("Player ${if(o.isPlayerXTurn()) "x" else "o"} Turn!")
//            val intInput = readLine()!!.toInt()
//            o = o.availableMoves()[intInput]
//            myTurn = !myTurn
//        }
//
//        //coms turn
//        if(!o.isMoveAvailable()) {
//            o = o.switchTurns()
//            myTurn = !myTurn
//        }
//
//        if(!myTurn) {
//            println(o)
//            println("Player ${if(o.isPlayerXTurn()) "x" else "o"} Turn!")
//            o = o.bestMove()
//            myTurn = !myTurn
//        }
//
//    }
//    println("Score Player X: ${o.scorePlayerX()} ___ Score Player O: ${o.scorePlayerO()}")
}