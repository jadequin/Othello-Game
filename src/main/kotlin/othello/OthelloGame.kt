package othello

import kotlin.math.sign

interface OthelloGame {
    fun listMoves(): List<Othello>
    fun makeMove(pos: Int): Othello
    fun undo(): Othello
    fun switchTurns(): Othello //essential because sometimes a player takes two turns
    fun isGameOver(): Boolean
    fun isPlayer1Turn(): Boolean
    fun scorePlayer1(): Int
    fun scorePlayer2(): Int
    fun result() = (scorePlayer1() - scorePlayer2()).sign
}