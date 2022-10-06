package com.example.midtermmovieapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.midtermmovieapp.utils.Converters


@Database(entities = [Movie::class], version = 6)
@TypeConverters(Converters::class)
abstract class MovieLocalDatabase:RoomDatabase() {
abstract fun movieDao():MovieDao
}