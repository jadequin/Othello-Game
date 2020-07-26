
package othello

import io.javalin.Javalin


class Game(var othello: Othello = Othello()) {
    init {
        val app = Javalin.create {config -> config.addStaticFiles("/public")}.start(7070)

        app.get("/makeMove"){ ctx ->
            val pos = (ctx.queryParam("move")?.let { it } ?: "").toInt()
            othello = othello.makeMove(pos)
            ctx.result(othello.toString())
        }

        app.get("/state"){ ctx ->
            ctx.result(othello.toString())
        }
    }

    fun play() {
        while(!othello.isGameOver()) {
            othello = othello.nextTurn()
            println(othello)
            othello = if(othello.isPlayerXTurn()) {
                println("Please choose a move player X")
//            val input = readLine() ?: "0"
//            o.listMoves()[input.toInt()]
                othello.listMoves().random()
            }
            else {
                println("I'm an AI and i choose this move here")
//            o.listMoves().random()
                othello.bestMove()
            }
        }

        println(othello)
        println("Score Player X: ${othello.scorePlayerX()} ___ Score Player O: ${othello.scorePlayerO()}")
    }
}

fun main() {
    Game()
}