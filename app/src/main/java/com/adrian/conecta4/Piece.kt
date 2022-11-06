package com.adrian.conecta4

enum class Piece {
    RED {
        override val resourceColorId: Int
            get() = R.color.red

        override val resourceDrawableId: Int
            get() = R.drawable.red_cell

        override val otherPiece: Piece
            get() = YELLOW

        override val spanishName: String
            get() = "ROJO"
    },
    YELLOW {
        override val resourceColorId: Int
            get() = R.color.yellow

        override val resourceDrawableId: Int
            get() = R.drawable.yellow_cell

        override val otherPiece: Piece
            get() = RED

        override val spanishName: String
            get() = "AMARILLO"
    };

    abstract val resourceColorId: Int
    abstract val resourceDrawableId: Int
    abstract val otherPiece: Piece
    abstract val spanishName: String
}
