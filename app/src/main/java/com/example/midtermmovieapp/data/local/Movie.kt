package com.example.midtermmovieapp.data.local

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo(name = "poster_path")
    val posterPath: String,
    @ColumnInfo(name = "original_title")
    val originalTitle: String,
    @ColumnInfo(name = "overview")
    val overview: String,
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double,
    @ColumnInfo(name = "favorites_logo")
    var favoritesLogo:Bitmap
)