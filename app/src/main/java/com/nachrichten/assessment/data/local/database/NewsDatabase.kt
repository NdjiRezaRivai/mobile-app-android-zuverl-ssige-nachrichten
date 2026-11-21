package com.nachrichten.assessment.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nachrichten.assessment.data.local.dao.ArticleDao
import com.nachrichten.assessment.data.local.dao.SourceDao
import com.nachrichten.assessment.data.local.entity.ArticleEntity
import com.nachrichten.assessment.data.local.entity.SourceEntity

@Database(
    entities = [SourceEntity::class, ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract val sourceDao: SourceDao
    abstract val articleDao: ArticleDao
}
