package by.academy.questionnaire.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import by.academy.questionnaire.database.entity.AnswerEntity
import by.academy.questionnaire.database.entity.FormEntity
import by.academy.questionnaire.database.entity.QuestionEntity
import by.academy.questionnaire.database.entity.ResultEntity
import by.academy.questionnaire.database.entity.UserEntity

const val TAG_DATABASE = "TAG_DATABASE"
const val USER_MAIN = 1L
const val FORM_BURNOUT_MBI = 1L
const val FORM_WORK_ENGAGEMENT_UWES = 2L
const val FORM_DEMO = 3L

@Database(exportSchema = false, version = 5,
        entities = [
            FormEntity::class, QuestionEntity::class, AnswerEntity::class, UserEntity::class,
            ResultEntity::class
        ])
@TypeConverters(Converters::class)
abstract class DatabaseInfo : RoomDatabase() {

    abstract fun getFormDAO(): FormDAO
    abstract fun getQuestionDAO(): QuestionDAO
    abstract fun getAnswerDAO(): AnswerDAO
    abstract fun getUserDAO(): UserDAO
    abstract fun getResultDAO(): ResultDAO

    companion object {
        fun init(context: Context) =
                lazy {
                    Log.i(TAG_DATABASE, "init database")
//                    val migration = object : Migration(2, 3){
//                        override fun migrate(database: SupportSQLiteDatabase) {
//                            database.execSQL("DROP TABLE answer");
//                        }
//                    }
                    Room.databaseBuilder(context, DatabaseInfo::class.java, "database")
                            .allowMainThreadQueries()
//                            .addMigrations(migration)
                            .fallbackToDestructiveMigration()
                            .build().apply {
                                if (getFormDAO().getAll().isEmpty()) {
                                    initTestData(this)
                                }
                            }
                }

        private fun initTestData(db: DatabaseInfo) {
            val demoQuestion = 22 + 17L
            db.getUserDAO().apply {
                add(UserEntity(USER_MAIN, "User"))
            }
            db.getFormDAO().apply {
                add(FormEntity(FORM_BURNOUT_MBI, "Опросник эмоционального выгорания"))
                add(FormEntity(FORM_WORK_ENGAGEMENT_UWES, "Опросник увлеченности работой"))
                add(FormEntity(FORM_DEMO, "Демо опросник"))
            }
            db.getQuestionDAO().apply {
                add(QuestionEntity(1, FORM_BURNOUT_MBI, 1, "Я чувствую себя эмоционально опустошенным."))
                add(QuestionEntity(2, FORM_BURNOUT_MBI, 2, "После работы я чувствую себя как «выжатый лимон»."))
                add(QuestionEntity(3, FORM_BURNOUT_MBI, 3, "Утром я чувствую усталость и нежелание идти на работу."))
                add(QuestionEntity(4, FORM_BURNOUT_MBI, 4, "Я хорошо понимаю, что чувствуют мои коллеги и стараюсь учитывать это в интересах дела"))
                add(QuestionEntity(5, FORM_BURNOUT_MBI, 5, "Я чувствую, что общаюсь с некоторыми коллегами без теплоты и расположения к ним"))
                add(QuestionEntity(6, FORM_BURNOUT_MBI, 6, "После работы мне на некоторое время хочется уединиться"))
                add(QuestionEntity(7, FORM_BURNOUT_MBI, 7, "Я умею находить правильное решение в конфликтных ситуациях, возникающих при общении с коллегами"))
                add(QuestionEntity(8, FORM_BURNOUT_MBI, 8, "Я чувствую угнетенность и апатию"))
                add(QuestionEntity(9, FORM_BURNOUT_MBI, 9, "Я уверен, что моя работа нужна людям"))
                add(QuestionEntity(10, FORM_BURNOUT_MBI, 10, "В последнее время я стал более черствым по отношению к тем, с кем я работаю"))
                add(QuestionEntity(11, FORM_BURNOUT_MBI, 11, "Я замечаю, что моя работа ожесточает меня"))
                add(QuestionEntity(12, FORM_BURNOUT_MBI, 12, "У меня много планов на будущее, и я верю в их осуществление"))
                add(QuestionEntity(13, FORM_BURNOUT_MBI, 13, "Моя работа все больше меня разочаровывает"))
                add(QuestionEntity(14, FORM_BURNOUT_MBI, 14, "Мне кажется, что я слишком много работаю"))
                add(QuestionEntity(15, FORM_BURNOUT_MBI, 15, "Бывает, что мне действительно безразлично то, что происходит с некоторыми моими подчиненными/ воспитанниками и коллегам"))
                add(QuestionEntity(16, FORM_BURNOUT_MBI, 16, "Мне хочется уединиться и отдохнуть от всего и всех"))
                add(QuestionEntity(17, FORM_BURNOUT_MBI, 17, "Я легко могу создать атмосферу доброжелательности и сотрудничества в коллективе"))
                add(QuestionEntity(18, FORM_BURNOUT_MBI, 18, "Во время работы я чувствую приятное оживление"))
                add(QuestionEntity(19, FORM_BURNOUT_MBI, 19, "Благодаря своей работе я уже сделал в жизни много действительно ценного"))
                add(QuestionEntity(20, FORM_BURNOUT_MBI, 20, "Я чувствую равнодушие и потерю интереса ко многому, что радовало меня в моей работе"))
                add(QuestionEntity(21, FORM_BURNOUT_MBI, 21, "На работе я спокойно справляюсь с эмоциональными проблемам"))
                add(QuestionEntity(22, FORM_BURNOUT_MBI, 22, "В последнее время мне кажется, что коллеги и подчиненные все чаще перекладывают на меня груз своих проблем и обязанностей."))


                add(QuestionEntity(22 + 1, FORM_WORK_ENGAGEMENT_UWES, 1, "Во время работы меня переполняет энергия"))
                add(QuestionEntity(22 + 2, FORM_WORK_ENGAGEMENT_UWES, 2, "Моя работа целенаправленна и осмысленна"))
                add(QuestionEntity(22 + 3, FORM_WORK_ENGAGEMENT_UWES, 3, "Когда я работаю, время пролетает незаметно"))
                add(QuestionEntity(22 + 4, FORM_WORK_ENGAGEMENT_UWES, 4, "Во время работы я испытываю прилив сил и энергии"))
                add(QuestionEntity(22 + 5, FORM_WORK_ENGAGEMENT_UWES, 5, "Я полон энтузиазма в отношении своей работы"))
                add(QuestionEntity(22 + 6, FORM_WORK_ENGAGEMENT_UWES, 6, "Во время работы я забываю обо всем окружающем"))
                add(QuestionEntity(22 + 7, FORM_WORK_ENGAGEMENT_UWES, 7, "Моя работа вдохновляет меня"))
                add(QuestionEntity(22 + 8, FORM_WORK_ENGAGEMENT_UWES, 8, "Проснувшись утром, я радуюсь тому, что пойду на работу"))
                add(QuestionEntity(22 + 9, FORM_WORK_ENGAGEMENT_UWES, 9, "Я счастлив, когда интенсивно работаю"))
                add(QuestionEntity(22 + 10, FORM_WORK_ENGAGEMENT_UWES, 10, "Я горжусь своей работой"))
                add(QuestionEntity(22 + 11, FORM_WORK_ENGAGEMENT_UWES, 11, "Я ухожу в работу с головой"))
                add(QuestionEntity(22 + 12, FORM_WORK_ENGAGEMENT_UWES, 12, "Могу работать в течение длительного времени без перерывов"))
                add(QuestionEntity(22 + 13, FORM_WORK_ENGAGEMENT_UWES, 13, "Работа ставит передо мной сложные и интересные задачи"))
                add(QuestionEntity(22 + 14, FORM_WORK_ENGAGEMENT_UWES, 14, "Я позволяю работе «уносить» меня"))
                add(QuestionEntity(22 + 15, FORM_WORK_ENGAGEMENT_UWES, 15, "В работе я очень настойчив и не отвлекаюсь на постороннее"))
                add(QuestionEntity(22 + 16, FORM_WORK_ENGAGEMENT_UWES, 16, "Мне трудно отложить работу в сторону"))
                add(QuestionEntity(22 + 17, FORM_WORK_ENGAGEMENT_UWES, 17, "Я продолжаю работать даже тогда, когда дела идут плохо"))


                add(QuestionEntity(demoQuestion + 1, FORM_DEMO, 1, "Моя работа вдохновляет меня"))
                add(QuestionEntity(demoQuestion + 2, FORM_DEMO, 2, "Я горжусь своей работой"))
            }

            db.getAnswerDAO().apply {
                val firstChoice = 1
                add(AnswerEntity(1, demoQuestion + 1, firstChoice, 1, 1L))

            }

        }
    }
}