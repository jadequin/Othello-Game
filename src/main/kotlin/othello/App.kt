
package othello

import io.javalin.Javalin
import java.awt.Desktop
import java.net.URL


class App() {

    var othello: Othello = Othello()

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

        app.get("/printToConsole") {ctx ->
            println("Current board. Just copy and paste it into a test scenario!\n" + othello.toTriple())
            ctx.result(othello.htmlResponse())
        }
    }
}

fun main() {
    try { Desktop.getDesktop().browse(URL("http://localhost:7070/").toURI()) } catch (e: Exception) { }
    App()
}