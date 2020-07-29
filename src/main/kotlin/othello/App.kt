
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
            val p1IsCom = ctx.queryParam("first")?:"" == "com"
            val p2IsCom = ctx.queryParam("second")?:"" == "com"

            othello = Othello()
            isP1COM = p1IsCom
            isP2COM = p2IsCom

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
//    try { Desktop.getDesktop().browse(URL("http://localhost:7070/").toURI()) } catch (e: Exception) { }
//    App()
    test()
}

fun test() {
    var o = Othello()
    while(!o.isGameOver()) {
        o = o.nextTurn()
        println(o)
        o = if(o.isPlayer1Turn()) o.bestMove() else o.randomMove()
    }
    println(o)
    println("AI:${o.scorePlayer1()}, Random:${o.scorePlayer2()}")
}