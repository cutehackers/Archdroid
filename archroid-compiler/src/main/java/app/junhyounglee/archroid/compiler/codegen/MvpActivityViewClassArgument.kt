package app.junhyounglee.archroid.compiler.codegen

import app.junhyounglee.archroid.compiler.Id
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

class MvpActivityViewClassArgument(
    targetTypeName: TypeName,
    className: ClassName,
    val viewType: ClassName,
    val presenterType: ClassName,
    val layoutResId: Id? = null
) : ClassArgument(targetTypeName, className) {

    override fun getFileName(): String = "MvpSampleActivityView"


    class Builder {
        private lateinit var targetTypeName: TypeName
        private lateinit var className: ClassName
        private lateinit var viewType: ClassName
        private lateinit var presenterType: ClassName
        private var layoutResId: Id? = null

        fun targetTypeName(targetTypeName: TypeName) = apply { this.targetTypeName = targetTypeName }

        fun className(className: ClassName) = apply { this.className = className }

        fun viewType(viewType: ClassName) = apply { this.viewType = viewType }

        fun presenterType(presenterType: ClassName) = apply { this.presenterType = presenterType }

        fun contentView(layoutResId: Id) = apply { this.layoutResId = layoutResId }

        fun build() = MvpActivityViewClassArgument(
            targetTypeName,
            className,
            viewType,
            presenterType,
            layoutResId
        )
    }

    companion object {

        fun builder() = Builder()
    }
}
