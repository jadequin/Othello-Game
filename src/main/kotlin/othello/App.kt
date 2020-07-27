
package othello

import io.javalin.Javalin
import java.lang.Exception

class Game(var othello: Othello = Othello(), val p1IsCom: Boolean = false, val p2IsCom: Boolean = true) {

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


class App(var game: Game = Game()) {
    init {
        val app = Javalin.create {config -> config.addStaticFiles("/public")}.start(7070)

        app.patch("hello") { ctx ->
            ctx.result("hh")
        }

        app.get("/makeMove"){ ctx ->
            val pos: Int = ctx.queryParam("move")!!.toInt()
            game.othello = game.othello.makeMove(pos)
            ctx.result(game.othello.toString())
        }

        app.get("/state"){ ctx ->
            ctx.result(game.othello.toString())
        }

        app.get("/newGame") { ctx ->
            val p1IsCom = ctx.queryParam("first")?:"" == "com"
            val p2IsCom = ctx.queryParam("second")?:"" == "com"

            game = Game(Othello(), p1IsCom, p2IsCom)
            ctx.result(game.othello.toString())
        }

        app.get("/undo") {ctx ->
            game.othello = game.othello.undo()
            ctx.result(game.othello.toString())
        }

        app.get("/bestMove") { ctx ->
            game.othello = game.othello.bestMove()
            ctx.result(game.othello.toString())
        }

        app.get("/randomMove") {ctx ->
            game.othello = game.othello.randomMove()
            ctx.result(game.othello.toString())
        }
    }
}

fun main() {
    App()
}