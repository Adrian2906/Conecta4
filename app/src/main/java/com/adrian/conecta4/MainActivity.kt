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
        //added temporally logic to board. Columns (board's children) should have the logic
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
            redScoreboardTextView.text = "0"
            yellowScoreboardTextView.text = "0"
        }

        restartMatchButton = findViewById(R.id.restartMatchButton)
        restartMatchButton.setOnClickListener {
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