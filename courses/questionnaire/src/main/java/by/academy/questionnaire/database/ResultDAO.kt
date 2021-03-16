package by.academy.questionnaire.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.academy.questionnaire.database.entity.ResultEntity
import by.academy.questionnaire.database.entity.ResultUser

@Dao
interface ResultDAO {

    @Query("SELECT * FROM result order by fk_f_id")
    fun getAll(): List<ResultEntity>

    @Query("SELECT * FROM result WHERE fk_f_id  = :formId order by fk_f_id")
    fun getAllByFormId(formId: Long): List<ResultUser>


    @Query("""SELECT * FROM result WHERE 
            fk_u_id = :userId AND fk_f_id  = :formId AND attempt = :attempt""")
    fun getInfo(formId: Long, userId: Long = 1, attempt: Int = 1): ResultUser

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: ResultEntity): Long

    @Update
    fun update(entity: ResultEntity)

    @Delete
    fun delete(entity: ResultEntity)

    @Query("""DELETE  FROM result WHERE 
            fk_u_id = :userId AND fk_f_id  = :formId AND attempt = :attempt""")
    fun delete(formId: Long, userId: Long = 1, attempt: Int = 1)

    @Query("DELETE FROM result")
    fun deleteAll()
}