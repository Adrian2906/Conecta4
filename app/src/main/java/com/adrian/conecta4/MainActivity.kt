package com.adrian.conecta4

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children

class MainActivity : AppCompatActivity() {
    private val MAX_ROW: Int = 5
    private val MIN_ROW: Int = 0

    private lateinit var boardLinearLayout: LinearLayout

    private lateinit var turnTextView: TextView
    private lateinit var redScoreboardTextView: TextView
    private lateinit var yellowScoreboardTextView: TextView

    private lateinit var restartScoreboardButton: Button
    private lateinit var restartMatchButton: Button

    //TODO: make it immutable
    private lateinit var EMPTY_CELL: Drawable // in upper because it must be immutable
    //TODO: first match starts the RED piece
    // but second match starts the YELLOW piece
    // third RED and so on
    private var currentPiece: Piece = Piece.RED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        @SuppressLint("UseCompatLoadingForDrawables")
        EMPTY_CELL = resources.getDrawable(R.drawable.empty_cell, theme)
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

        for (column in boardLinearLayout.children) {
            column.setOnClickListener { it as LinearLayout
                play(it)
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun play(column: LinearLayout) {
        val drawable = resources.getDrawable(currentPiece.resourceDrawableId, theme)
        val played = putPieceOnBoard(column, drawable)

        if (played) {
            val nextPiece = currentPiece.otherPiece
            setTurnTextViewBackgroundColor(nextPiece.resourceColorId)
            setCurrentPiece(nextPiece)
        }
    }

    //TODO: use when game is ready
//    private fun sumScore(scoreboard: TextView) {
//        var score = scoreboard.text.toString().toInt()
//        score++
//        scoreboard.text = score.toString()
//    }

    private fun setTurnTextViewBackgroundColor(resourceId: Int) {
        val color =  resources.getColor(resourceId)
        turnTextView.setBackgroundColor(color)
    }

    private fun setCurrentPiece(piece: Piece) {
        currentPiece = piece
    }

    private fun putPieceOnBoard(column: LinearLayout, drawable: Drawable, index: Int = MAX_ROW): Boolean {
        if (index < MIN_ROW) return false

        val cell = column.getChildAt(index) as ImageView
        if (cell.drawable.constantState != EMPTY_CELL.constantState) return putPieceOnBoard(column, drawable, index - 1)
        cell.setImageDrawable(drawable)
        return true
    }
}

//TODO: extract to file
enum class Piece {
    RED {
        override val resourceColorId: Int
            get() = R.color.red

        override val resourceDrawableId: Int
            get() = R.drawable.red_cell

        override val otherPiece: Piece
            get() = YELLOW
    },
    YELLOW {
        override val resourceColorId: Int
            get() = R.color.yellow

        override val resourceDrawableId: Int
            get() = R.drawable.yellow_cell

        override val otherPiece: Piece
            get() = RED
    };

    abstract val resourceColorId: Int
    abstract val resourceDrawableId: Int
    abstract val otherPiece: Piece
}