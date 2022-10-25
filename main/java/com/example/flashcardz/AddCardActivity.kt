package com.example.flashcardz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        val cancelIcon = findViewById<ImageView>(R.id.cancel_question_button)
        cancelIcon.setOnClickListener {
            finish()
        }
        val questionEditText = findViewById<EditText>(R.id.inputQuestion)
        val answerEditText = findViewById<EditText>(R.id.inputAnswer)
        val saveIcon = findViewById<ImageView>(R.id.save_button)
        saveIcon.setOnClickListener{
            val questionString = questionEditText.text.toString()
            val answerString = answerEditText.text.toString()
            val data = Intent()
            data.putExtra("Question_Key", questionString)
            data.putExtra("Answer_Key", answerString)
            setResult(RESULT_OK,data)
            finish()
        }



    }
}