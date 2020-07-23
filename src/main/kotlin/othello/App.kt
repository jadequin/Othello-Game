
package othello

import io.javalin.Javalin

fun main() {

    val app = Javalin.create().start(7070)

    readLine()
    app.stop()

    var o = Othello()
    o.simulatePlays(100)



//    while(!o.isGameOver()) {
//        o = o.nextTurn()
//        println(o)
//        o = if(o.isPlayerXTurn()) {
//        println("Please choose a move player X")
////            val input = readLine() ?: "0"
////            o.listMoves()[input.toInt()]
//            o.listMoves().random()
//        }
//        else {
//        println("I'm an AI and i choose this move here")
////            o.listMoves().random()
//            o.bestMove()
//        }
//    }
//
//    println(o)
//    println("Score Player X: ${o.scorePlayerX()} ___ Score Player O: ${o.scorePlayerO()}")
//    println(Othello.results.values)

}