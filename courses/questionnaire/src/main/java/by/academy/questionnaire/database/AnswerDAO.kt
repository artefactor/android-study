package by.academy.questionnaire.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.academy.questionnaire.database.entity.AnswerEntity
import by.academy.questionnaire.database.entity.AnswerQuestion
import by.academy.questionnaire.database.entity.AnswerQuestionRelation

@Dao
interface AnswerDAO {

    @Query("SELECT count(1) FROM answer")
    fun size(): Int

    @Query("SELECT * FROM answer order by a_id")
    fun getAllInfo(): List<AnswerEntity>

    /**
     * пользователь уже прошел тест. Есть результаты.
     * нажимает "пройти еще раз", отвечает на 1-2 вопроса и решает что не будет проходить второй раз.
     * МОжно при этом не сбрасывать первые результаты.
     * А перезаписать их только тогда когда он полностью пройдет второй раз.
     */
    @Query("""
        SELECT q.* , a.* from question q
            LEFT JOIN result r 
                 ON  r.fk_f_id = q.fk_f_id
            LEFT JOIN answer a
                 ON  r.r_id=a.fk_r_id 
                    AND q.q_id = a.fk_q_id
            where r.r_id= :resultId 
            ORDER by q.`index`
                """)
    fun getAttemptAnswers(resultId: Long): List<AnswerQuestion>

    @Query("""SELECT * FROM answer WHERE a_id in 
        (SELECT a.a_id FROM answer a JOIN question q on q.q_id=a.fk_q_id AND q.fk_f_id = :formId)""")
    fun getByFormId(formId: Long) :List<AnswerEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: AnswerEntity): Long

    @Update
    fun update(entity: AnswerEntity)

    @Delete
    fun delete(entity: AnswerEntity)

    @Query("DELETE FROM answer")
    fun deleteAll()

    @Query("""DELETE FROM answer WHERE a_id in 
        (SELECT a.a_id FROM answer a JOIN question q on q.q_id=a.fk_q_id AND q.fk_f_id = :formId)""")
    fun deleteByFormId(formId: Long)

    @Query("""DELETE FROM answer WHERE fk_r_id = :resultId""")
    fun deleteByResultId(resultId: Long)

    @Query("""DELETE FROM answer WHERE fk_r_id in 
        (SELECT r.r_id FROM result r WHERE r.fk_f_id = :formId AND r.fk_ur_id = :userId AND r.dateEnd is null)""")
    fun deleteLastAttempt(formId: Long, userId: Long)


}