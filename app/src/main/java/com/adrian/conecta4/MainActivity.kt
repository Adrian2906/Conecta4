package com.adrian.conecta4

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children

class MainActivity : AppCompatActivity() {
    private val MAX_INDEX_ROW: Int = 5
    private val MIN_INDEX_ROW: Int = 0
    private val MIN_INDEX_COLUMN: Int = 0
    private val MAX_INDEX_COLUMN: Int = 6
    private val CAN_NOT_MAKE_MOVE: Int = -1
    private val ALIGNED_PIECES_TO_WIN: Int = 4

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
            setTurnTextViewBackgroundColor(Piece.RED.resourceColorId)
            clearBoard()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun play(column: LinearLayout) {
        val columnIndex = boardLinearLayout.indexOfChild(column)
        val drawable = resources.getDrawable(currentPiece.resourceDrawableId, theme)
        val rowIndexPlayed = putPieceOnBoard(column, drawable)

        if (CAN_NOT_MAKE_MOVE == rowIndexPlayed) return

        val isWinner = isWinner(columnIndex, rowIndexPlayed, drawable)

        if (isWinner) {
            Toast.makeText(this@MainActivity, "FOO", Toast.LENGTH_LONG).show()
            sumScore()
            clearBoard()
            return
        }

        val nextPiece = currentPiece.otherPiece
        setTurnTextViewBackgroundColor(nextPiece.resourceColorId)
        setCurrentPiece(nextPiece)
    }

    private fun clearBoard() {
        val columns = boardLinearLayout.children
        columns.forEach { column ->
            column as LinearLayout
            column.children.forEach { it as ImageView
                it.setImageDrawable(EMPTY_CELL)
            }
        }
    }

    private fun sumScore() {
        val scoreboard = getScoreboardTextViewByCurrentPiece()
        var score = scoreboard.text.toString().toInt()
        score++
        scoreboard.text = score.toString()
    }

    private fun getScoreboardTextViewByCurrentPiece(): TextView {
        return when(currentPiece) {
            Piece.RED -> redScoreboardTextView
            Piece.YELLOW -> yellowScoreboardTextView
        }
    }

    private fun setTurnTextViewBackgroundColor(resourceId: Int) {
        val color =  resources.getColor(resourceId)
        turnTextView.setBackgroundColor(color)
    }

    private fun setCurrentPiece(piece: Piece) {
        currentPiece = piece
    }

    private fun putPieceOnBoard(column: LinearLayout, drawable: Drawable, index: Int = MAX_INDEX_ROW): Int {
        if (index < MIN_INDEX_ROW)
            return CAN_NOT_MAKE_MOVE

        val cell = column.getChildAt(index) as ImageView

        if (cell.drawable.constantState != EMPTY_CELL.constantState)
            return putPieceOnBoard(column, drawable, index - 1)

        cell.setImageDrawable(drawable)
        return index
    }

    private fun isWinner(columnIndex: Int, rowIndex: Int, drawable: Drawable): Boolean {
        //TODO: REFACTOR
        var alignedPiecesCount = 1

        //CHECK HORIZONTAL PIECES
        //LEFT
        var brakeChain = false
        var index = columnIndex

        while (index > MIN_INDEX_COLUMN && !brakeChain) {
            index--
            val column = boardLinearLayout.getChildAt(index) as LinearLayout
            val cell = column.getChildAt(rowIndex) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedPiecesCount += 1
            else
                brakeChain = true
        }
        //LEFT

        //RIGHT
        brakeChain = false
        index = columnIndex

        while (index < MAX_INDEX_COLUMN && !brakeChain) {
            index++
            val column = boardLinearLayout.getChildAt(index) as LinearLayout
            val cell = column.getChildAt(rowIndex) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedPiecesCount += 1
            else
                brakeChain = true
        }
        //RIGHT
        //CHECK HORIZONTAL PIECES

        if (alignedPiecesCount >= ALIGNED_PIECES_TO_WIN) return true
        alignedPiecesCount = 1

        //CHECK VERTICAL PIECES
        val playedColumn = boardLinearLayout.getChildAt(columnIndex) as LinearLayout

        //DOWN
        brakeChain = false
        index = rowIndex

        while (index < MAX_INDEX_ROW && !brakeChain) {
            index++
            val cell = playedColumn.getChildAt(index) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedPiecesCount += 1
            else
                brakeChain = true
        }
        //DOWN

        //UP
        brakeChain = false
        index = rowIndex

        while (index > MIN_INDEX_ROW && !brakeChain) {
            index--
            val cell = playedColumn.getChildAt(index) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedPiecesCount += 1
            else
                brakeChain = true
        }
        //UP
        //CHECK VERTICAL PIECES

        if (alignedPiecesCount >= ALIGNED_PIECES_TO_WIN) return true
        alignedPiecesCount = 1

        //CHECK DIAGONAL PIECES
        brakeChain = false
        var horizontalIndex = columnIndex
        var verticalIndex = rowIndex

        //DOWN AND LEFT
        while (horizontalIndex > MIN_INDEX_COLUMN && verticalIndex < MAX_INDEX_ROW && !brakeChain) {
            horizontalIndex--
            verticalIndex++

            val column = boardLinearLayout.getChildAt(horizontalIndex) as LinearLayout
            val cell = column.getChildAt(verticalIndex) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedPiecesCount += 1
            else
                brakeChain = true
        }
        //DOWN AND LEFT

        //DOWN AND RIGHT
        brakeChain = false
        horizontalIndex = columnIndex
        verticalIndex = rowIndex

        while (horizontalIndex < MAX_INDEX_COLUMN && verticalIndex < MAX_INDEX_ROW && !brakeChain) {
            horizontalIndex++
            verticalIndex++

            val column = boardLinearLayout.getChildAt(horizontalIndex) as LinearLayout
            val cell = column.getChildAt(verticalIndex) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedPiecesCount += 1
            else
                brakeChain = true
        }
        //DOWN AND RIGHT

        //UP AND RIGHT
        brakeChain = false
        horizontalIndex = columnIndex
        verticalIndex = rowIndex

        while (horizontalIndex < MAX_INDEX_COLUMN && verticalIndex > MIN_INDEX_ROW && !brakeChain) {
            horizontalIndex++
            verticalIndex--

            val column = boardLinearLayout.getChildAt(horizontalIndex) as LinearLayout
            val cell = column.getChildAt(verticalIndex) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedPiecesCount += 1
            else
                brakeChain = true
        }
        //UP AND RIGHT

        //UP AND LEFT
        brakeChain = false
        horizontalIndex = columnIndex
        verticalIndex = rowIndex

        while (horizontalIndex > MIN_INDEX_COLUMN && verticalIndex > MIN_INDEX_ROW && !brakeChain) {
            horizontalIndex--
            verticalIndex--

            val column = boardLinearLayout.getChildAt(horizontalIndex) as LinearLayout
            val cell = column.getChildAt(verticalIndex) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedPiecesCount += 1
            else
                brakeChain = true
        }
        //UP AND LEFT
        //CHECK DIAGONAL PIECES

        return alignedPiecesCount >= ALIGNED_PIECES_TO_WIN
    }
}
