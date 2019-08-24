package app.junhyounglee.archdroid.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class BindMvpPresenter(val presenter: KClass<*>)