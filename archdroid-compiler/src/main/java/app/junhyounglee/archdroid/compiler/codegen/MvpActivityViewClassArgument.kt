package app.junhyounglee.archdroid.compiler.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

class MvpActivityViewClassArgument(
    targetTypeName: TypeName,
    className: ClassName,
    val viewType: ClassName,
    val presenterType: ClassName
) : ClassArgument(targetTypeName, className) {

    override fun getFileName(): String = "MvpSampleActivityView"


    class Builder {
        private lateinit var targetTypeName: TypeName
        private lateinit var className: ClassName
        private lateinit var viewType: ClassName
        private lateinit var presenterType: ClassName

        fun targetTypeName(targetTypeName: TypeName) = apply { this.targetTypeName = targetTypeName }

        fun className(className: ClassName) = apply { this.className = className }

        fun viewType(viewType: ClassName) = apply { this.viewType = viewType }

        fun presenterType(presenterType: ClassName) = apply { this.presenterType = presenterType }

        fun build() = MvpActivityViewClassArgument(
            targetTypeName,
            className,
            viewType,
            presenterType
        )
    }

    companion object {

        fun builder() = Builder()
    }
}
