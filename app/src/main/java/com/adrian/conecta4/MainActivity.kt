package com.adrian.conecta4

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children

@SuppressLint("UseCompatLoadingForDrawables")
class MainActivity : AppCompatActivity() {
    private val maxIndexRow: Int = 5
    private val minIndexRow: Int = 0
    private val maxIndexColumn: Int = 6

    private val boardLinearLayout: LinearLayout by lazy { findViewById(R.id.boardLinearLayout) }

    private val turnImageView: ImageView by lazy { findViewById(R.id.turnTextView) }
    private val redScoreboardTextView: TextView by lazy { findViewById(R.id.redScoreboardTextView) }
    private val yellowScoreboardTextView: TextView by lazy { findViewById(R.id.yellowScoreboardTextView) }

    private val restartScoreboardButton: Button by lazy { findViewById(R.id.restartScoreboardButton) }
    private val restartGameButton: Button by lazy { findViewById(R.id.restartGameButton) }

    private val emptyCell: Drawable by lazy { resources.getDrawable(R.drawable.empty_cell, theme) }

    private var initialToken: Token = Token.RED
    private var currentToken: Token = Token.RED
    private val game: Game by lazy { Game(boardLinearLayout) }

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
        val turn = Turn(columnIndex, rowIndexPlayed, drawable)

        val builderDialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setNeutralButton(R.string.dialog_neutral_button){ _, _ ->
                startNewGame()
            }

        return when (game.play(turn)) {
            Result.WIN -> win(builderDialog)
            Result.REPLAY -> return
            Result.NOTHING -> setCurrentToken(currentToken.otherToken)
            Result.DRAW -> draw(builderDialog)
        }
    }

    private fun win(builderDialog: AlertDialog.Builder) {
        sumScore()
        builderDialog
            .setTitle(R.string.dialog_win_title)
            .setMessage(getString(R.string.dialog_message, currentToken.spanishName))
            .create()
            .show()
    }

    private fun draw(builderDialog: AlertDialog.Builder) {
        builderDialog
            .setTitle(R.string.dialog_draw_title)
            .create()
            .show()
    }

    private fun startNewGame() {
        initialToken = initialToken.otherToken
        startGame()
    }

    private fun startGame() {
        game.setTokensPlayed(0)
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
        setDrawableToTurnImageView(currentToken.resourceDrawableId)
    }

    private fun setDrawableToTurnImageView(resourceId: Int) {
        val drawable =  resources.getDrawable(resourceId, theme)
        turnImageView.setImageDrawable(drawable)
    }

    private fun putTokenOnBoard(column: LinearLayout, drawable: Drawable, index: Int = maxIndexRow): Int {
        if (index < minIndexRow)
            return GameConstants.CAN_NOT_MAKE_MOVE

        val cell = column.getChildAt(index) as ImageView

        if (cell.drawable.constantState != emptyCell.constantState)
            return putTokenOnBoard(column, drawable, index - 1)

        cell.setImageDrawable(drawable)
        return index
    }
}
