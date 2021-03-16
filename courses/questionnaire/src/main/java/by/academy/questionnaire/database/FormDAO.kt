package by.academy.questionnaire.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.academy.questionnaire.database.entity.FormEntity
import by.academy.questionnaire.database.entity.FormQuestionStatus

@Dao
interface FormDAO {

    @Query("SELECT * FROM form order by f_id")
    fun getAll(): List<FormEntity>

    @Query("""SELECT form.f_id as id,
                    form.title,
                    count(q.q_id) as questionCount, 
                    count(a.a_id) as passedQuestionCount  
                    FROM form 
                    JOIN question q on form.f_id = q.fk_f_id
                    LEFT JOIN answer a on q.q_id = a.fk_q_id
                    GROUP BY f_id
                    ORDER by f_id""")
    fun getAllInfo(): List<FormQuestionStatus>

    @Query("""SELECT form.f_id as id, form.title, count(q.q_id) as questionCount, 0 as passedQuestionCount 
            FROM form JOIN question q on form.f_id = q.fk_f_id WHERE form.f_id = :formId GROUP BY f_id""")
    fun getInfo(formId: Long): FormQuestionStatus

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: FormEntity): Long

    @Update
    fun update(entity: FormEntity)

    @Delete
    fun delete(entity: FormEntity)
}