package app.junhyounglee.archroid.runtime.core.presenter

import app.junhyounglee.archroid.runtime.core.view.MvpView
import java.lang.RuntimeException

class PresenterProvider(
    private val presenterStore: PresenterStore,
    private val factory: Factory
) {

    fun <T : MvpPresenter<out MvpView>> get(key: String, model: Class<T>): T {
        var presenter: MvpPresenter<out MvpView>? = presenterStore.get(key)
        if (model.isInstance(presenter)) {
            @Suppress("UNCHECKED_CAST")
            return presenter as T
        }

        presenter = factory.create(model)
        presenterStore.put(key, presenter)
        return presenter
    }

    fun <T : MvpPresenter<out MvpView>> get(model: Class<T>): T {
        val name = model.canonicalName
            ?: throw IllegalArgumentException("Local and anonymous classes can not be Presenters")
        return get("$DEFAULT_KEY:$name", model)
    }


    interface Factory {
        fun <T : MvpPresenter<out MvpView>> create(model: Class<T>): T
    }

    open class NewInstanceFactory : Factory {
        override fun <T : MvpPresenter<out MvpView>> create(model: Class<T>): T {
            return try {
                model.newInstance()
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $model", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $model", e)
            }
        }
    }

    companion object {
        private const val DEFAULT_KEY = "app.junhyounglee.archroid.runtime.core.presenter.PresenterProvider.DEFAULT_KEY"
    }
}