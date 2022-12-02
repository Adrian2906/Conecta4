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
    private val alignedTokensToWin: Int = 4
    private val maxTokens: Int = 42

    private val boardLinearLayout: LinearLayout by lazy { findViewById(R.id.boardLinearLayout) }

    private val turnTextView: TextView by lazy { findViewById(R.id.turnTextView) }
    private val redScoreboardTextView: TextView by lazy { findViewById(R.id.redScoreboardTextView) }
    private val yellowScoreboardTextView: TextView by lazy { findViewById(R.id.yellowScoreboardTextView) }

    private val restartScoreboardButton: Button by lazy { findViewById(R.id.restartScoreboardButton) }
    private val restartGameButton: Button by lazy { findViewById(R.id.restartGameButton) }

    private val emptyCell: Drawable by lazy { resources.getDrawable(R.drawable.empty_cell, theme) }

    private var initialToken: Token = Token.RED
    private var currentToken: Token = Token.RED
    private var tokensPlayed: Int = 0

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
        val drawable = resources.getDrawable(currentToken.resourceDrawableId, theme)
        val rowIndexPlayed = putTokenOnBoard(column, drawable)

        if (canNotMakeMove == rowIndexPlayed) return

        val turn = Turn(columnIndex, rowIndexPlayed, drawable)
        val isWinner = isWinner(turn)

        if (isWinner) {
            sumScore()
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_win_title)
                .setMessage(getString(R.string.dialog_message, currentToken.spanishName))
                .setCancelable(false)
                .setNeutralButton(R.string.dialog_neutral_button){ _, _ ->
                    startNewGame()
                }
                .create()
                .show()

            return
        }

        tokensPlayed++

        if (maxTokens == tokensPlayed) {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_draw_title)
                .setCancelable(false)
                .setNeutralButton(R.string.dialog_neutral_button){ _, _ ->
                    startNewGame()
                }
                .create()
                .show()

            return
        }

        setCurrentToken(currentToken.otherToken)
    }

    private fun startNewGame() {
        initialToken = initialToken.otherToken
        startGame()
    }

    private fun startGame() {
        tokensPlayed = 0
        clearBoard()
        setCurrentToken(initialToken)
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
        val scoreboard = getScoreboardTextViewByCurrentToken()
        var score = scoreboard.text.toString().toInt()
        score++
        scoreboard.text = score.toString()
    }

    private fun getScoreboardTextViewByCurrentToken(): TextView {
        return when(currentToken) {
            Token.RED -> redScoreboardTextView
            Token.YELLOW -> yellowScoreboardTextView
        }
    }

    private fun setCurrentToken(token: Token) {
        currentToken = token
        setTurnTextViewBackgroundColor(currentToken.resourceColorId)
    }

    private fun setTurnTextViewBackgroundColor(resourceId: Int) {
        val color =  resources.getColor(resourceId)
        turnTextView.setBackgroundColor(color)
    }

    private fun putTokenOnBoard(column: LinearLayout, drawable: Drawable, index: Int = maxIndexRow): Int {
        if (index < minIndexRow)
            return canNotMakeMove

        val cell = column.getChildAt(index) as ImageView

        if (cell.drawable.constantState != emptyCell.constantState)
            return putTokenOnBoard(column, drawable, index - 1)

        cell.setImageDrawable(drawable)
        return index
    }

    private fun isWinner(turn: Turn): Boolean {
        val horizontalTokensCount = countHorizontal(turn)
        if (hasLineUpEnough(plusOne(horizontalTokensCount))) return true

        val verticalTokensCount = countVertical(turn)
        if (hasLineUpEnough(plusOne(verticalTokensCount))) return true

        val softDiagonalTokensCount = countSoftDiagonal(turn)
        if (hasLineUpEnough(plusOne(softDiagonalTokensCount))) return true

        val hardDiagonalTokensCount = countHardDiagonal(turn)
        if (hasLineUpEnough(plusOne(hardDiagonalTokensCount))) return true

        return false
    }

    private fun countHorizontal(turn: Turn): Int {
        val cellsToLeft = countSameTokens(subtractOne, notModifier, turn)
        val cellsToRight = countSameTokens(plusOne, notModifier, turn)

        return cellsToLeft + cellsToRight
    }

    private fun countVertical(turn: Turn): Int {
        val cellsToUp = countSameTokens(notModifier, plusOne, turn)
        val cellsToDown = countSameTokens(notModifier, subtractOne, turn)

        return cellsToUp + cellsToDown
    }

    private fun countSoftDiagonal(turn: Turn): Int {
        val cellsToUpAndLeft = countSameTokens(subtractOne, plusOne, turn)
        val cellsToDownAndRight = countSameTokens(plusOne, subtractOne, turn)

        return cellsToUpAndLeft + cellsToDownAndRight
    }

    private fun countHardDiagonal(turn: Turn): Int {
        val cellsToUpAndRight = countSameTokens(plusOne, plusOne, turn)
        val cellsToDownAndLeft = countSameTokens(subtractOne, subtractOne, turn)

        return cellsToUpAndRight + cellsToDownAndLeft
    }

    private fun getCell(columnIndex: Int, rowIndex: Int): ImageView? {
        val column = boardLinearLayout.getChildAt(columnIndex) as LinearLayout? ?: return null
        return column.getChildAt(rowIndex) as ImageView?
    }

    private fun countSameTokens(
        modifierColumnIndex: (Int) -> Int,
        modifierRowIndex: (Int) -> Int,
        turn: Turn,
        count: Int = 0
    ): Int {
        val columnIndexModified = modifierColumnIndex(turn.columnIndex)
        val rowIndexModified = modifierRowIndex(turn.rowIndex)
        val cell = getCell(columnIndexModified, rowIndexModified)

        if (isTokenInCell(turn.drawable, cell)) {
            val modifiedTurn = turn.copy(columnIndex = columnIndexModified, rowIndex = rowIndexModified)
            return countSameTokens(modifierColumnIndex, modifierRowIndex, modifiedTurn, plusOne(count))
        }

        return count
    }

    private val plusOne = {num: Int -> num + 1}
    private val subtractOne = {num: Int -> num - 1}
    private val notModifier = {num: Int -> num}
    private val isTokenInCell = {drawable: Drawable, cell: ImageView? -> cell != null && drawable.constantState == cell.drawable.constantState}
    private val hasLineUpEnough = {alignedTokensCount: Int -> alignedTokensCount >= alignedTokensToWin}
}
