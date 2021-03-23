package by.academy.questionnaire.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.academy.questionnaire.database.entity.QuestionEntity

@Dao
interface QuestionDAO {

    @Query("SELECT count(1) FROM question")
    fun size(): Long

    @Query("SELECT * FROM question WHERE fk_f_id = :questionId")
    fun get(questionId: Long): QuestionEntity

    @Query("SELECT * FROM question WHERE fk_f_id = :formId")
    fun getListByFormId(formId: Long): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: QuestionEntity): Long

    @Update
    fun update(entity: QuestionEntity)

    @Delete
    fun delete(entity: QuestionEntity)
}