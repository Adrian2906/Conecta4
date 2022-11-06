package com.adrian.conecta4

enum class Token {
    RED {
        override val resourceColorId: Int
            get() = R.color.red

        override val resourceDrawableId: Int
            get() = R.drawable.red_cell

        override val otherToken: Token
            get() = YELLOW

        override val spanishName: String
            get() = "ROJO"
    },
    YELLOW {
        override val resourceColorId: Int
            get() = R.color.yellow

        override val resourceDrawableId: Int
            get() = R.drawable.yellow_cell

        override val otherToken: Token
            get() = RED

        override val spanishName: String
            get() = "AMARILLO"
    };

    abstract val resourceColorId: Int
    abstract val resourceDrawableId: Int
    abstract val otherToken: Token
    abstract val spanishName: String
}
