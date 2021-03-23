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

    @Query("SELECT count(1) FROM form")
    fun size(): Long

    @Query("SELECT * FROM form order by f_id")
    fun getAll(): List<FormEntity>

    @Query("""SELECT form.f_id as formId,
                    form.title,
                    form.icon,
                    count(DISTINCT q.q_id) as questionCount,
                    count(DISTINCT r1.r_id) as countPasses,
                    count(DISTINCT a.a_id) as passedQuestionCount,
                    r.r_id as mainResultId,  
                    r.fk_ur_id as userId  
                    FROM form 
                    JOIN question q on form.f_id = q.fk_f_id
                    LEFT JOIN result r on r.fk_f_id = form.f_id and r.dateEnd is null                    
                    LEFT JOIN answer a on q.q_id = a.fk_q_id and a.fk_r_id  = r.r_id
                    LEFT JOIN result r1 on r1.fk_f_id = form.f_id and r1.dateEnd is not null
                    GROUP BY f_id
                    ORDER by f_id""")
    fun getAllInfo(): List<FormQuestionStatus>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(entity: FormEntity): Long

    @Update
    fun update(entity: FormEntity)

    @Delete
    fun delete(entity: FormEntity)

    @Query("DELETE FROM form WHERE f_id = :formId")
    fun deleteByFormId(formId: Long)

    @Query("DELETE FROM form WHERE title = :title")
    fun deleteByFormTitle(title: String)
}