package com.example.simuladorDado

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var diceImage: ImageView
    private lateinit var rollButton: Button
    private lateinit var resultText: TextView
    private lateinit var progressBar: View

    // Lista explícita de recursos drawable (corrige o erro de inferência de tipo)
    private val diceImages: List<Int> = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6
    )

    private var isRolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        diceImage = findViewById(R.id.diceImage)
        rollButton = findViewById(R.id.rollButton)
        resultText = findViewById(R.id.resultText)
        progressBar = findViewById(R.id.progressBar)

        rollButton.setOnClickListener {
            if (!isRolling) {
                rollDice()
            }
        }
    }

    private fun rollDice() {
        isRolling = true
        rollButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
        resultText.text = "Jogando..."

        // Animação de lançamento (subida)
        val throwUp = ObjectAnimator.ofFloat(diceImage, "translationY", -500f)
        throwUp.duration = 300
        throwUp.interpolator = AccelerateInterpolator()

        // Animação de rotação
        val rotate = ObjectAnimator.ofFloat(diceImage, "rotation", 0f, 1080f)
        rotate.duration = 1000
        rotate.interpolator = AccelerateDecelerateInterpolator()

        // Animação de queda
        val fallDown = ObjectAnimator.ofFloat(diceImage, "translationY", 0f)
        fallDown.duration = 700
        fallDown.interpolator = DecelerateInterpolator()

        throwUp.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                rotate.start()
                fallDown.start()

                // Muda as faces do dado durante a rotação
                val handler = Handler(Looper.getMainLooper())
                var changes = 0
                val maxChanges = 10

                val runnable = object : Runnable {
                    override fun run() {
                        if (changes < maxChanges) {
                            val randomImage = diceImages.random()
                            diceImage.setImageResource(randomImage)
                            changes++
                            handler.postDelayed(this, 100L)
                        } else {
                            // Resultado final
                            val finalValue = (1..6).random()
                            diceImage.setImageResource(getDiceImage(finalValue))
                            resultText.text = "Resultado: $finalValue"
                            isRolling = false
                            rollButton.isEnabled = true
                            progressBar.visibility = View.INVISIBLE
                        }
                    }
                }
                handler.postDelayed(runnable, 150)
            }
        })
        throwUp.start()
    }

    private fun getDiceImage(value: Int): Int {
        return when (value) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            else -> R.drawable.dice_1
        }
    }
}