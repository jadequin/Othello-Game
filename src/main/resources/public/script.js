
let http = new XMLHttpRequest();
var start = Date.now()
var end

//initial game state call
sendGET("state")

//SENDER
function sendGET(request) {
    http.open('GET', request);
    http.send();
    start = Date.now()
}

//RECEIVER
http.onload = function() {
    if (status == 0 || (status >= 200 && status < 400)) {

        end = Date.now()

        /*
        Html Response with the following form:
            boardAsHTML###turn###scorePlayer1###scorePlayer2###result

        example:
            <table>...</table###-1###23###41###0

         */

        document.getElementById('responseTime').innerText = (end - start).toString()

        let elements = this.responseText.split("###")

        document.getElementById('game').innerHTML = elements[0]
        document.getElementById('turn').innerText = elements[1] === '1' ? "P1" : "P2"
        document.getElementById('scoreP1').innerText = elements[2]
        document.getElementById('scoreP2').innerText = elements[3]
        document.getElementById('win').innerText = elements[4] === '1'? 'Player 1 Wins!!!' : elements[4] === '-1'? 'Player 2 Wins!!!' : elements[4] === '0'? 'Tie !' : ""

        let isP1Turn = elements[1] === '1'
        let isGameOver = elements[4] !== 'nowin'

        //activate click events for the game board
        document.getElementById('game').style.pointerEvents="all"

        //check if there are automatic moves to be made
        if(!isGameOver && ((isP1Turn && document.getElementById('p1Computer').checked) || (!isP1Turn && document.getElementById('p2Computer').checked))) {
            document.getElementById('game').style.pointerEvents="none"
            setTimeout(() => { sendGET('bestMove') }, 650); //delay the request
        }
        else if(!isGameOver && ((isP1Turn && document.getElementById('p1Random').checked) || (!isP1Turn && document.getElementById('p2Random').checked))) {
            document.getElementById('game').style.pointerEvents="none"
            setTimeout(() => { sendGET('randomMove') }, 650); //delay the request
        }
    }
}

function sendMove(request) {
    sendGET(request)
}