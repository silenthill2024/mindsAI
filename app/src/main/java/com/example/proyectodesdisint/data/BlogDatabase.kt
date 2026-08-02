package com.example.proyectodesdisint.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "blogs")
data class BlogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val fecha: Long = System.currentTimeMillis(),
    val autor: String = "Usuario MindsAI"
)

@Entity(
    tableName = "replies",
    foreignKeys = [
        ForeignKey(
            entity = BlogEntity::class,
            parentColumns = ["id"],
            childColumns = ["blogId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReplyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val blogId: Int,
    val texto: String,
    val autor: String = "MindsAI Assistant",
    val fecha: Long = System.currentTimeMillis()
)

@Dao
interface BlogDao {
    @Insert
    suspend fun insertBlog(blog: BlogEntity)

    @Insert
    suspend fun insertReply(reply: ReplyEntity)

    @Query("SELECT * FROM blogs ORDER BY fecha DESC")
    fun getAllBlogs(): Flow<List<BlogEntity>>

    @Query("SELECT * FROM replies WHERE blogId = :blogId ORDER BY fecha ASC")
    fun getRepliesForBlog(blogId: Int): Flow<List<ReplyEntity>>
}

@Database(entities = [BlogEntity::class, ReplyEntity::class], version = 1)
abstract class BlogDatabase : RoomDatabase() {
    abstract fun blogDao(): BlogDao

    companion object {
        @Volatile
        private var INSTANCE: BlogDatabase? = null

        fun getDatabase(context: Context): BlogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BlogDatabase::class.java,
                    "blog_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
