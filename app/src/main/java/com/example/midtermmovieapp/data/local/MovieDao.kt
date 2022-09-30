package com.example.midtermmovieapp.data.local

import androidx.room.*

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie")
    suspend fun getAll(): List<Movie>

    @Query("DELETE FROM movie")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(vararg movies: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovie(movie:Movie)

    @Delete
    suspend fun delete(movie: Movie)
}