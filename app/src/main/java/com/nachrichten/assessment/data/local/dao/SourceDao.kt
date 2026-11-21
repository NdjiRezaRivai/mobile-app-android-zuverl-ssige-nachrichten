package com.nachrichten.assessment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nachrichten.assessment.data.local.entity.SourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {

    @Query("SELECT * FROM sources WHERE category = :category")
    fun getSourcesByCategory(category: String): Flow<List<SourceEntity>>

    @Query("SELECT * FROM sources WHERE category = :category AND (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')")
    fun searchSources(category: String, query: String): Flow<List<SourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: List<SourceEntity>)

    @Query("DELETE FROM sources WHERE category = :category")
    suspend fun clearSourcesByCategory(category: String)
}
