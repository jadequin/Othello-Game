package othello

import kotlin.io.writeText
import java.io.File

class OthelloDBInit {
    fun writeToFile(history: HashMap<Othello, Long>) {
        File("somefile.txt").writeText(history.entries.joinToString("\n") { "${it.key}, ${it.value}" })
    }
}