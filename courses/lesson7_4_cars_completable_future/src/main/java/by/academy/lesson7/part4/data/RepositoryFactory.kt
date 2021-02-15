package by.academy.lesson7.part4.data

import android.content.Context
import by.academy.lesson7.part4.data.DatabaseInfo.Companion.init

class RepositoryFactory {

    fun getRepository(context: Context): AbstractDataRepository = DatabaseRepository(init(context).value)
}