
package othello

import io.javalin.Javalin
import java.awt.Desktop;
import java.net.URI;


fun main() {

    val app = Javalin.create(){config -> config.addStaticFiles("/")}.start(7070)

    app.get("/makeMove"){ ctx ->
        val value = (ctx.queryParam("move")?.let { it } ?: "").toInt()
        ctx.result("Hier kommt das Ergebnis herein")
    }

    var o = Othello()

    while(!o.isGameOver()) {
        o = o.nextTurn()
        println(o)
        o = if(o.isPlayerXTurn()) {
            println("Please choose a move player X")
//            val input = readLine() ?: "0"
//            o.listMoves()[input.toInt()]
            o.listMoves().random()
        }
        else {
            println("I'm an AI and i choose this move here")
//            o.listMoves().random()
            o.bestMove()
        }
    }

    println(o)
    println("Score Player X: ${o.scorePlayerX()} ___ Score Player O: ${o.scorePlayerO()}")

}