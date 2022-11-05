package com.adrian.conecta4

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
