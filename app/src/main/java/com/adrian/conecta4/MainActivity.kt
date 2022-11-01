package com.adrian.conecta4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var boardLinearLayout: LinearLayout

    private lateinit var turnTextView: TextView
    private lateinit var redScoreboardTextView: TextView
    private lateinit var yellowScoreboardTextView: TextView

    private lateinit var restartScoreboardButton: Button
    private lateinit var restartMatchButton: Button

    //TODO: first match starts the RED piece
    // but second match starts the YELLOW piece
    // third RED and so on
    private var currentPiece: Piece = Piece.RED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTextViews()
        setBoard()
        setButtons()
    }

    private fun setTextViews() {
        redScoreboardTextView = findViewById(R.id.redScoreboardTextView)
        yellowScoreboardTextView = findViewById(R.id.yellowScoreboardTextView)
        turnTextView = findViewById(R.id.turnTextView)
    }

    private fun setBoard() {
        boardLinearLayout = findViewById(R.id.boardLinearLayout)
        //TODO: Columns (board's children) should have onClickListener not the board
        boardLinearLayout.setOnClickListener {
            when(currentPiece) {
                Piece.RED -> changeToYellowPiece()
                Piece.YELLOW -> changeToRedPiece()
            }
        }
    }

    private fun setButtons() {
        restartScoreboardButton = findViewById(R.id.restartScoreboardButton)
        restartScoreboardButton.setOnClickListener {
            //TODO: it restart the scoreboard, but it should restart turn too??
            // That is, RED should start always after restart scoreboard??
            redScoreboardTextView.text = "0"
            yellowScoreboardTextView.text = "0"
        }

        restartMatchButton = findViewById(R.id.restartMatchButton)
        restartMatchButton.setOnClickListener {
            //TODO: restart match so current piece will be the start piece
            setCurrentPiece(Piece.RED)
            setTurnTextViewBackgroundColor(R.color.red)
        }
    }

    private fun changeToRedPiece() {
        sumScore(yellowScoreboardTextView)
        setTurnTextViewBackgroundColor(R.color.red)
        setCurrentPiece(Piece.RED)
    }

    private fun changeToYellowPiece() {
        sumScore(redScoreboardTextView)
        setTurnTextViewBackgroundColor(R.color.yellow)
        setCurrentPiece(Piece.YELLOW)
    }

    private fun sumScore(scoreboard: TextView) {
        var score = scoreboard.text.toString().toInt()
        score++
        scoreboard.text = score.toString()
    }

    private fun setTurnTextViewBackgroundColor(resourceId: Int) {
        val color =  resources.getColor(resourceId)
        turnTextView.setBackgroundColor(color)
    }

    private fun setCurrentPiece(piece: Piece) {
        currentPiece = piece
    }
}

enum class Piece { RED, YELLOW }