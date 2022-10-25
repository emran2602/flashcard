package com.example.flashcardz

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.flashcardz.Flashcard
import com.example.flashcardz.FlashcardDao

@Database(entities = [Flashcard::class], version = 1)

abstract class AppDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
}
