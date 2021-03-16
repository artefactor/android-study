package by.academy.questionnaire.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.academy.questionnaire.database.entity.UserEntity

@Dao
interface UserDAO {

    @Query("SELECT * FROM user order by u_id")
    fun getAll(): List<UserEntity>

    @Query("SELECT * FROM user WHERE u_id = :userId")
    fun getInfo(userId: Long): UserEntity

    @Query("SELECT * FROM user WHERE name = :userName")
    fun findByUserName(userName: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: UserEntity): Long

    @Update
    fun update(entity: UserEntity)

    @Delete
    fun delete(entity: UserEntity)
}