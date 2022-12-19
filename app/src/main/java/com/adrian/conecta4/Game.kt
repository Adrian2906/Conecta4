package com.adrian.conecta4

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.LinearLayout

class Game(private val boardLinearLayout: LinearLayout) {
    private val plusOne = { num: Int -> num + 1 }
    private val hasLineUpEnough = { alignedTokensCount: Int ->
        alignedTokensCount >= GameConstants.ALIGNED_TOKENS_TO_WIN
    }
    private val subtractOne = { num: Int -> num - 1 }
    private val notModifier = { num: Int -> num }
    private val isTokenInCell = { drawable: Drawable, cell: ImageView? ->
        cell != null && drawable.constantState == cell.drawable.constantState
    }

    private var tokensPlayed: Int = 0

    fun setTokensPlayed(tokensPlayed: Int) {
        this.tokensPlayed = tokensPlayed
    }

    fun play(turn: Turn): Result {
        if (GameConstants.CAN_NOT_MAKE_MOVE == turn.rowIndex)
            return Result.REPLAY

        val isWinner = isWinner(turn)
        if (isWinner)
            return Result.WIN

        tokensPlayed++
        if (tokensPlayed == GameConstants.MAX_TOKENS)
            return Result.DRAW

        return Result.NOTHING
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

    private fun getCell(columnIndex: Int, rowIndex: Int): ImageView? {
        val column = boardLinearLayout.getChildAt(columnIndex) as LinearLayout? ?: return null
        return column.getChildAt(rowIndex) as ImageView?
    }
}