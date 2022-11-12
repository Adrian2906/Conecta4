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

        val isWinner = isWinner(columnIndex, rowIndexPlayed, drawable)

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

    private fun isWinner(columnIndex: Int, rowIndex: Int, drawable: Drawable): Boolean {
        //TODO: REFACTOR
        var alignedTokensCount = 1

        //CHECK HORIZONTAL TOKENS
        //LEFT
        var brakeChain = false
        var index = columnIndex

        while (index > minIndexColumn && !brakeChain) {
            index--
            val column = boardLinearLayout.getChildAt(index) as LinearLayout
            val cell = column.getChildAt(rowIndex) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedTokensCount += 1
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
                alignedTokensCount += 1
            else
                brakeChain = true
        }
        //RIGHT
        //CHECK HORIZONTAL TOKENS

        if (alignedTokensCount >= alignedTokensToWin) return true
        alignedTokensCount = 1

        //CHECK VERTICAL TOKENS
        val playedColumn = boardLinearLayout.getChildAt(columnIndex) as LinearLayout

        //DOWN
        brakeChain = false
        index = rowIndex

        while (index < maxIndexRow && !brakeChain) {
            index++
            val cell = playedColumn.getChildAt(index) as ImageView

            if (cell.drawable.constantState == drawable.constantState)
                alignedTokensCount += 1
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
                alignedTokensCount += 1
            else
                brakeChain = true
        }
        //UP
        //CHECK VERTICAL TOKENS

        if (alignedTokensCount >= alignedTokensToWin) return true
        alignedTokensCount = 1

        //CHECK DIAGONAL TOKENS
        //SAME DIAGONALS
        //DOWN AND LEFT - UP AND RIGHT
        //DOWN AND RIGHT - UP AND LEFT
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
                alignedTokensCount += 1
            else
                brakeChain = true
        }
        //DOWN AND LEFT

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
                alignedTokensCount += 1
            else
                brakeChain = true
        }
        //UP AND RIGHT

        if (alignedTokensCount >= alignedTokensToWin) return true
        alignedTokensCount = 1

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
                alignedTokensCount += 1
            else
                brakeChain = true
        }
        //DOWN AND RIGHT

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
                alignedTokensCount += 1
            else
                brakeChain = true
        }
        //UP AND LEFT
        //CHECK DIAGONAL TOKENS

        return alignedTokensCount >= alignedTokensToWin
    }
}
