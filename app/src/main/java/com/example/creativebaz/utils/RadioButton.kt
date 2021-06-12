package com.example.creativebaz.utils


import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class RadioButton(context: Context, attrs: AttributeSet) :
    AppCompatRadioButton(context, attrs) {

    init {
        // Call the function to apply the font to the components.
        applyFont()
    }

    private fun applyFont() {

        // This is used to get the file from the assets folder and set it to the title textView.
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Cinzel-Regular.ttf")
        setTypeface(typeface)
    }
}