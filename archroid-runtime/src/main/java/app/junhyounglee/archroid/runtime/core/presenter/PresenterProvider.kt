package app.junhyounglee.archroid.runtime.core.presenter

import app.junhyounglee.archroid.runtime.core.view.MvpView

/**
 * TODO create a method that binds view instance to Presenter
 */
class PresenterProvider(
    private val presenterStore: PresenterStore,
    private val factory: Factory
) {

    fun <V: MvpView, P : MvpPresenter<V>> get(key: String, presenterType: Class<P>, viewType: Class<V>, view: V): P {
        var instance: MvpPresenter<out MvpView>? = presenterStore.get(key)
        if (presenterType.isInstance(instance)) {
            @Suppress("UNCHECKED_CAST")
            return instance as P
        }

        instance = factory.create(presenterType, viewType, view)
        presenterStore.put(key, instance)
        return instance
    }

    fun <V : MvpView, P : MvpPresenter<V>> get(presenterType: Class<P>, viewType: Class<V>, view: V): P {
        val name = presenterType.canonicalName
            ?: throw IllegalArgumentException("Local and anonymous classes can not be Presenters")
        return get("$DEFAULT_KEY:$name", presenterType, viewType, view)
    }


    interface Factory {
        fun <V: MvpView, P : MvpPresenter<V>> create(presenterType: Class<P>, viewType: Class<V>, view: V): P
    }

    open class NewInstanceFactory : Factory {
        override fun <V: MvpView, P : MvpPresenter<V>> create(presenterType: Class<P>, viewType: Class<V>, view: V): P {
            return try {
                presenterType.getConstructor(viewType).newInstance(view)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $presenterType", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $presenterType", e)
            }
        }
    }

    companion object {
        private const val DEFAULT_KEY = "app.junhyounglee.archroid.runtime.core.presenter.PresenterProvider.DEFAULT_KEY"
    }
}