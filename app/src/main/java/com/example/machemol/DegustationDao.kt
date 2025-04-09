package com.example.machemol

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import android.content.Context
import com.google.gson.Gson

@Dao
interface DegustationDao {

    @Insert
    suspend fun insert(degustation: Degustation)

    @Delete
    suspend fun delete(degustation: Degustation)

    @Update
    suspend fun update(degustation: Degustation)

    @Query("SELECT * FROM degustationen ORDER BY degudatum DESC")
    fun getAll(): Flow<List<Degustation>>

    @Query("SELECT * FROM degustationen WHERE winzer LIKE '%' || :winzer || '%'")
    fun searchByWinzer(winzer: String): Flow<List<Degustation>>

    @Query("SELECT * FROM degustationen WHERE id = :id")
    fun getById(id: Int): Flow<Degustation?>

    @Query("SELECT * FROM degustationen")
    suspend fun getAllOnce(): List<Degustation>
}
