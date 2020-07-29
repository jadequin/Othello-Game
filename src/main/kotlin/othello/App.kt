
package othello

import io.javalin.Javalin
import java.awt.Desktop
import java.net.URL


class App(var othello: Othello = Othello(), var isP1COM: Boolean = false, var isP2COM: Boolean = true) {
    init {

        val app = Javalin.create {config -> config.addStaticFiles("/public")}.start(7070)

        app.get("/makeMove"){ ctx ->
            val pos: Int = ctx.queryParam("move")!!.toInt()
            if(!othello.isGameOver())
                othello = othello.makeMove(pos).nextTurn()
            ctx.result(othello.htmlResponse())
        }

        app.get("/state"){ ctx ->
            ctx.result(othello.nextTurn().htmlResponse())
        }

        app.get("/newGame") { ctx ->
            othello = Othello()
            ctx.result( othello.htmlResponse())
        }

        app.get("/undo") {ctx ->
            othello = othello.undo().nextTurn()
            ctx.result(othello.htmlResponse())
        }

        app.get("/bestMove") { ctx ->
            if(!othello.isGameOver())
                othello = othello.bestMove().nextTurn()
            ctx.result( othello.htmlResponse())
        }

        app.get("/randomMove") {ctx ->
            if(!othello.isGameOver())
                othello = othello.randomMove().nextTurn()
            ctx.result(othello.htmlResponse())
        }
    }
}

fun main() {
    try { Desktop.getDesktop().browse(URL("http://localhost:7070/").toURI()) } catch (e: Exception) { }
    App()
//    test2()
}

fun test2() {
    var o = Othello()
    while(!o.isGameOver()) {
        o = o.nextTurn()
        o = if(o.isPlayer1Turn()) o.randomMove() else o.bestMove()
    }
    println(o)
    println("(${o.scorePlayer1()}|${o.scorePlayer2()})")
}

fun test1() {
    var o: Othello
    do {
        o = Othello()
        while (!o.isGameOver()) {
            o = o.nextTurn()
            o = o.randomMove()
        }
    } while(o.result() != 0)

    //o is a tie now
    println("Tied up game state:")
    o = o.undo(2)
    println(o)
    println((if(o.isPlayer1Turn()) "P1" else "P2") + " turn")
    println("(${o.scorePlayer1()}|${o.scorePlayer2()})")
    o.listMoves().forEach { print(it.alphaBeta().toString() + " ") }
    println()
    println()


    println("First moves:")
    val firstBefore = o.listMoves()[0]; println(firstBefore); println(if(firstBefore.isPlayer1Turn()) "P1" else "P2")
    val first = firstBefore.nextTurn().listMoves()[0]; println(first); println(if(first.isPlayer1Turn()) "P1" else "P2")
    println()
    println((if(first.result() == 1) "P1 wins" else if(first.result() == -1) "P2 wins" else "Tie") + "(${first.scorePlayer1()}|${first.scorePlayer2()})")

    println("Second moves:")
    val secondBefore = o.listMoves()[1]; println(secondBefore); println(if(secondBefore.isPlayer1Turn()) "P1" else "P2")
    val second = secondBefore.nextTurn().listMoves()[0]; println(second); println(if(second.isPlayer1Turn()) "P1" else "P2")
    println((if(second.result() == 1) "P1 wins" else if(second.result() == -1) "P2 wins" else "Tie") + "(${second.scorePlayer1()}|${second.scorePlayer2()})")

}