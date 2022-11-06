package com.adrian.conecta4

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children

@SuppressLint("UseCompatLoadingForDrawables")
class MainActivity : AppCompatActivity() {
    private val maxIndexRow: Int = 5
    private val minIndexRow: Int = 0
    private val minIndexColumn: Int = 0
    private val maxIndexColumn: Int = 6
    private val canNotMakeMove: Int = -1
    private val alignedPiecesToWin: Int = 4

    private val boardLinearLayout: LinearLayout by lazy { findViewById(R.id.boardLinearLayout) }

    private val turnTextView: TextView by lazy { findViewById(R.id.turnTextView) }
    private val redScoreboardTextView: TextView by lazy { findViewById(R.id.redScoreboardTextView) }
    private val yellowScoreboardTextView: TextView by lazy { findViewById(R.id.yellowScoreboardTextView) }

    private val restartScoreboardButton: Button by lazy { findViewById(R.id.restartScoreboardButton) }
    private val restartGameButton: Button by lazy { findViewById(R.id.restartGameButton) }

    private val emptyCell: Drawable by lazy { resources.getDrawable(R.drawable.empty_cell, theme) }

    private var initialPiece: Piece = Piece.RED
    private var currentPiece: Piece = Piece.RED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        setOnCLickBoard()
        setOnClickButtons()
    }

    private fun setOnCLickBoard() {
        for (column in boardLinearLayout.children) {
            column.setOnClickListener { it as LinearLayout
                play(it)
            }
        }
    }

    private fun setOnClickButtons() {
        restartScoreboardButton.setOnClickListener {
            redScoreboardTextView.text = "0"
            yellowScoreboardTextView.text = "0"
        }

        restartGameButton.setOnClickListener {
            startGame()
        }
    }

    private fun play(column: LinearLayout) {
        val columnIndex = boardLinearLayout.indexOfChild(column)
        val drawable = resources.getDrawable(currentPiece.resourceDrawableId, theme)
        val rowIndexPlayed = putPieceOnBoard(column, drawable)

        if (canNotMakeMove == rowIndexPlayed) return

        val isWinner = isWinner(columnIndex, rowIndexPlayed, drawable)

        if (isWinner) {
            sumScore()
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(getString(R.string.dialog_message, currentPiece.spanishName))
                .setCancelable(false)
                .setNeutralButton(R.string.dialog_neutral_button){ _, _ ->
                    startNewGame()
                }
                .create()
                .show()

            return
        }

        val nextPiece = currentPiece.otherPiece
        setCurrentPiece(nextPiece)
    }

    private fun startNewGame() {
        initialPiece = initialPiece.otherPiece
        startGame()
    }

    private fun startGame() {
        clearBoard()
        setCurrentPiece(initialPiece)
    }

    private fun clearBoard() {
        val columns = boardLinearLayout.children
        columns.forEach { column ->
            column as LinearLayout
            column.children.forEach { it as ImageView
                it.setImageDrawable(emptyCell)
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

    private fun setCurrentPiece(piece: Piece) {
        currentPiece = piece
        setTurnTextViewBackgroundColor(currentPiece.resourceColorId)
    }

    private fun setTurnTextViewBackgroundColor(resourceId: Int) {
        val color =  resources.getColor(resourceId)
        turnTextView.setBackgroundColor(color)
    }

    private fun putPieceOnBoard(column: LinearLayout, drawable: Drawable, index: Int = maxIndexRow): Int {
        if (index < minIndexRow)
            return canNotMakeMove

        val cell = column.getChildAt(index) as ImageView

        if (cell.drawable.constantState != emptyCell.constantState)
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

        while (index > minIndexColumn && !brakeChain) {
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

        while (index < maxIndexColumn && !brakeChain) {
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

        if (alignedPiecesCount >= alignedPiecesToWin) return true
        alignedPiecesCount = 1

        //CHECK VERTICAL PIECES
        val playedColumn = boardLinearLayout.getChildAt(columnIndex) as LinearLayout

        //DOWN
        brakeChain = false
        index = rowIndex

        while (index < maxIndexRow && !brakeChain) {
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

        while (index > minIndexRow && !brakeChain) {
            index--
            val cell = playedColumn.getChildAt(index) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedPiecesCount += 1
            else
                brakeChain = true
        }
        //UP
        //CHECK VERTICAL PIECES

        if (alignedPiecesCount >= alignedPiecesToWin) return true
        alignedPiecesCount = 1

        //CHECK DIAGONAL PIECES
        brakeChain = false
        var horizontalIndex = columnIndex
        var verticalIndex = rowIndex

        //DOWN AND LEFT
        while (horizontalIndex > minIndexColumn && verticalIndex < maxIndexRow && !brakeChain) {
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

        while (horizontalIndex < maxIndexColumn && verticalIndex < maxIndexRow && !brakeChain) {
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

        while (horizontalIndex < maxIndexColumn && verticalIndex > minIndexRow && !brakeChain) {
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

        while (horizontalIndex > minIndexColumn && verticalIndex > minIndexRow && !brakeChain) {
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

        return alignedPiecesCount >= alignedPiecesToWin
    }
}
