package by.academy.lesson8.part2

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import androidx.fragment.app.commit
import by.academy.lesson8.part2.fragment.CarFragmentManager
import by.academy.lesson8.part2.fragment.CarListFragment
import by.academy.utils.FilesAndImagesUtils.appendLogFile

const val APPLOG_LOG = "applog.log"

const val WORK_ITEM = "workitem"
const val CAR_ITEM = "caritem"
const val CAR_ITEM_ID = "carId"

class MainActivity : AppCompatActivity(), CarFragmentManager {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        /* TODO Денис, вопрос про тулбар
         В предыдущей версии без фрагментов включал тулбар руками в активити.
         В реализации на фрагментах оставил включение тулбара на потом,
         но он сам работает без этой строчки.
         ПОэтому вопрос: вообще нужно его включать отдельно или нет?
         И если использовать фрагменты, то в каждом фрагменте свой тулбар?
         При переключении с фрагмента 1 на фрагмент 2 нужно убирать тулбар фрагмента 1 и включать тулбар фрагмента 2?
        */
//        setSupportActionBar(findViewById(R.id.toolbar))

        appendLogFile(applicationContext, APPLOG_LOG)
        val string = savedInstanceState?.getString("FILTER_VALUE")
        Log.i("fragmentA", "onCreate $string")

        if (supportFragmentManager.fragments.isEmpty()) {
            selectCarListFragment()
        }
    }

    private fun selectCarListFragment() {
        supportFragmentManager.commit { add<CarListFragment>(R.id.fragmentContainer) }
    }

    override fun openFragment(fragment1: Fragment, fragment2Class: Class<out Fragment>, bundle: Bundle) {
        supportFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            hide(fragment1)
            add(R.id.fragmentContainer, fragment2Class, bundle)
            this.addToBackStack(fragment2Class.simpleName)
        }
    }

    override fun openFragment(fragment1: Fragment, fragment2: Fragment) {
        supportFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            hide(fragment1)
            add(R.id.fragmentContainer, fragment2);
            addToBackStack(fragment2.javaClass.simpleName)
        }
    }

    /**
     * TODO Денис, вопрос про навигацию между фрагментами
     * Код получился сложный, потому что
     * я решил не убирать предыдущие фрагменты, чтобы их не создавать заново.
     *
     * я такую логику хотел реализовать:
     *
     * А. Use-case "сохранение фильтра"
     * - Есть список с полем фильтра.
     * - Если я отфильтровал список - нажал в редактирование машины, вернулся обратно - то я хочу сохранить фильтрацию.
     *   Для этого мне нужно:
     *   1) либо скрывать фрагмент-список (fragment.hide), затем его показывать - show)
     *   2) либо пересоздавать заново фрагменты, а значение фильра сохранять в live-data
     *   3) либо его сохранять как поле в активити.
     *
     *   Я выбрал первый способ, потому что второй мне показался громоздким, и пересоздавать фрагменты,
     *   на которые мы уже создали и на которые вернемся следующим действием, - расточительно.
     *   Сейчас думаю что лучше было бы третий способ выбрать.
     *   Денис, Как лучше?
     *
     * В. Use-case "отображение добавленного элемента"
     * - Если сохраняется фильтр, то когда мы фильтруем, а потом заходим в добавление элемента и сохраняем его
     * - то если новый элемент не подходит под фильтр - то странно если мы его не видим.
     * - Я хочу чтобы в таком случае фильтр оставался. но при этом показывался в списке еще только что добавленный элемент.
     *   Для этого:
     *   1) я передаю его в livedata
     *   2) можно было в bundle передавать, но если bundle используешь, то  для этого фрагменты пересоздаются, я так понял, что другого способа с bundle нет
     */
    override fun closeFragment(fragment2: Fragment) {
        val i = supportFragmentManager.fragments.size - 2
        val fragment = supportFragmentManager.fragments[i]
        supportFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            remove(fragment2)
            show(fragment)
            // здесь если бы я это в поле класса сохранил - то было бы проще.
//            replace(R.id.fragmentContainer, fragment1)
        }
        supportFragmentManager.popBackStack()
        // если мы делаем show(), то тогда onResume не сработает, нужно руками обновлять
        // Денис, вопрос - нормально ли так?
        fragment.onResume()
    }

    /* TODO Денис, такой вопрос про инкапсуляцию навигации
    // я постарался вынести методы в интерфейс, чтобы как-то централизовано управлять этим процессом.
     потому что когда делал - то в одном месте написал одним способом, в другом - другом.
     но я же всегда могу вызвать это явно, защиты от такого прямого вызова нет:
     requireActivity().supportFragmentManager.popBackStack()

    И вообще - это нормальный способ закрывать фрагменты? Или нужны эти транзишены выставлять?
     КАк обычно делают?
     */
    override fun simpleBack() {
        supportFragmentManager.popBackStack()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }


            /* TODO Денис. Вопрос про сохранение в бандл.

             Хотел при закрытии активити сохранять значение введенное в поиск,
             а потом - при запуске доставать и показывать в фильтре.

             Получается, что в Bundle значение сохраняется только при повороте активити.
             Если закрываю приложение руками в эмуляторе - то ничего не запоминается.
             Правильно ли я понимаю,что в таком случае значение фильтра нужно ложить не в бандл,
             а в preferenses?
         */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val string = savedInstanceState?.getString("FILTER_VALUE")
        Log.i("fragmentA", "onRestoreInstanceState $string")
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Log.i("fragmentA", "onSaveInstanceState")
        supportFragmentManager.fragments.forEach { f: Fragment ->
            f.onSaveInstanceState(outState)
        }
        super.onSaveInstanceState(outState, outPersistentState)
    }

}