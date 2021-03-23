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

    @Query("SELECT count(1) FROM result")
    fun size(): Long

    @Query("SELECT * FROM result order by fk_f_id")
    fun getAll(): List<ResultEntity>

    @Query("SELECT * FROM result WHERE fk_f_id  = :formId ORDER by dateEnd, dateStart DESC")
    fun getAllByFormId(formId: Long): List<ResultUser>

    @Query("""SELECT * FROM result WHERE fk_ur_id = :userId AND fk_f_id  = :formId ORDER by dateEnd, dateStart DESC""")
    fun getInfo(formId: Long, userId: Long): List<ResultUser>

    @Query("""SELECT * FROM result WHERE  r_id = :resultId""")
    fun getInfo(resultId: Long): ResultUser?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: ResultEntity): Long

    @Update
    fun update(entity: ResultEntity)

    @Delete
    fun delete(entity: ResultEntity)

    @Query("""DELETE  FROM result WHERE fk_f_id  = :formId """)
    fun deleteByFormId(formId: Long)

    @Query("""DELETE  FROM result WHERE r_id  = :resultId """)
    fun deleteById(resultId: Long)

    @Query("DELETE FROM result")
    fun deleteAll()
}