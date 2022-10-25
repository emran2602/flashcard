package com.example.flashcardz

import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import kotlin.math.hypot


class MainActivity : AppCompatActivity() {
    lateinit var flashcardDatabase: FlashcardDatabase
    var allFlashcards = mutableListOf<Flashcard>()
    var currentCardDisplayedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        val flashcardAnswer = findViewById<TextView>(R.id.flashcard_answer)
        flashcardQuestion.setOnClickListener{
//            flashcardQuestion.visibility = View.INVISIBLE
//            flashcardAnswer.visibility = View.VISIBLE

// get the center for the clipping circle
            val cx = flashcardAnswer.width / 2
            val cy = flashcardAnswer.height / 2

// get the final radius for the clipping circle
            val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()


// create the animator for this view (the start radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(flashcardAnswer, cx, cy, 0f, finalRadius)

// hide the question and show the answer to prepare for playing the animation!
            flashcardQuestion.visibility = View.INVISIBLE
            flashcardAnswer.visibility = View.VISIBLE

            anim.duration = 300
            anim.start()




        }
        flashcardAnswer.setOnClickListener {
            flashcardQuestion.visibility = View.VISIBLE
            flashcardAnswer.visibility = View.INVISIBLE
        }
        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        val leftOutAnim = AnimationUtils.loadAnimation(this, R.anim.left_out)
        val rightInAnim = AnimationUtils.loadAnimation(this, R.anim.right_in)
        leftOutAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // this method is called when the animation first starts
                findViewById<View>(R.id.flashcard_question).startAnimation(leftOutAnim)
            }

            override fun onAnimationEnd(animation: Animation?) {
                // this method is called when the animation is finished playing
                findViewById<View>(R.id.flashcard_question).startAnimation(rightInAnim)

            }

            override fun onAnimationRepeat(p0: Animation?) {
                TODO("Not yet implemented")
            }
        })

        findViewById<View>(R.id.next_button).setOnClickListener {
            if (allFlashcards.size == 0) {

                // return here, so that the rest of the code in this onClickListener doesn't execute
                return@setOnClickListener
            }

            // advance our pointer index so we can show the next card
            currentCardDisplayedIndex++

            // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
            if(currentCardDisplayedIndex >= allFlashcards.size) {
                Snackbar.make(
                    findViewById<TextView>(R.id.flashcard_question), // This should be the TextView for displaying your flashcard question
                    "You've reached the end of the cards, going back to start.",
                    Snackbar.LENGTH_SHORT)
                    .show()
                currentCardDisplayedIndex = 0
            }

            // set the question and answer TextViews with data from the database
            allFlashcards = flashcardDatabase.getAllCards().toMutableList()
            val (question, answer) = allFlashcards[currentCardDisplayedIndex]


            findViewById<View>(R.id.flashcard_question).startAnimation(leftOutAnim)

            findViewById<TextView>(R.id.flashcard_answer).text = answer
            findViewById<TextView>(R.id.flashcard_question).text = question



        }


        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            val data: Intent? = result.data
            if (data != null){
                val questionString = data.getStringExtra("Question_Key")
                val answerString = data.getStringExtra("Answer_Key")
                flashcardQuestion.text = questionString
                flashcardAnswer.text = answerString
                if (!questionString.isNullOrEmpty() && !answerString.isNullOrEmpty()) {
                    flashcardDatabase.insertCard(Flashcard(questionString, answerString))
                    allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                }
            } else {
                Log.i("Emran: MainActivity", "Returned null data from AddCardActivity")
            }
        }
        val addIcon = findViewById<ImageView>(R.id.add_question_button)
        addIcon.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }
}
