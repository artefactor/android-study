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

    @Query("SELECT * FROM answer order by a_id")
    fun getAllInfo(): List<AnswerEntity>

    /**
     * метод рабочий, но пока решил это не делать чтобы не усложнять.
     * будет одна попытка, которая перезаписываются.
     * и можно пройти под другом и это тоже перезаписать.
     *
     * Можно будет сделать например, такой вариант:
     * пользователь уже прошел тест. Есть результаты.
     * нажимает "пройти еще раз", отвечает на 1-2 вопроса и решает что не будет проходить второй раз.
     * МОжно при этом не сбрасывать первые результаты.
     * А перезаписать их только тогда когда он полностью пройдет второй раз.
     *
     * но это пока не сейчас
     */
    @Deprecated("method will be used in next iteration")
    // max attempt
    @Query(
            """SELECT  a.* from answer a 
                JOIN question q 
                   on q.q_id=a.fk_q_id  AND q.fk_f_id = :formId 
                WHERE attempt = 
                    (SELECT MAX(attempt)  from answer a JOIN question q on q.q_id=a.fk_q_id  AND q.fk_f_id = :formId )
                ORDER by q.`index`
                """)
    fun getLastAttemptListByForm(formId: Long): List<AnswerEntity>

    @Deprecated("method fully will be used in next iteration")
    @Query(
    """SELECT q.* , a.* from question q 
               LEFT JOIN answer a
                 ON q.q_id=a.fk_q_id
                    AND fk_u_id = :userId
                    AND attempt = 
                        (SELECT MAX(attempt)  from answer a JOIN question q on q.q_id=a.fk_q_id  
                            AND fk_u_id = :userId
                            AND q.fk_f_id = :formId )
                WHERE q.fk_f_id = :formId
                ORDER by q.`index`
                """)
    fun getLastAttemptListWithAnswersByFormId(formId: Long, userId: Long = 1): List<AnswerQuestion>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: AnswerEntity): Long

    @Update
    fun update(entity: AnswerEntity)

    @Delete
    fun delete(entity: AnswerEntity)

    @Query("DELETE FROM answer")
    fun deleteAll()

    /**
     * когда добавлю несколько попыток, то тогда надо будет дописать метод
     * "удаление последней попытки"
     */
    @Query("""DELETE FROM answer WHERE fk_u_id= :userId AND attempt = :attempt AND a_id in 
        (SELECT a.a_id FROM answer a JOIN question q on q.q_id=a.fk_q_id AND q.fk_f_id = :formId)""")
    fun deleteByFormId(formId: Long, attempt: Int = 1, userId: Long = 1)

}