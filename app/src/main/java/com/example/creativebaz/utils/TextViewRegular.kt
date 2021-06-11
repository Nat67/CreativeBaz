package com.example.creativebaz.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView

class TextViewRegular (context: Context, attrs : AttributeSet) : AppCompatEditText(context, attrs) {

    init {
        //applyFont()
    }

    private fun applyFont() {
        val typeface: Typeface =
                Typeface.createFromAsset(context.assets, "Cinzel-Regular.ttf")
        setTypeface(typeface)
    }
}