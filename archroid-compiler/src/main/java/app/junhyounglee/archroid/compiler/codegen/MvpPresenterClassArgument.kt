package app.junhyounglee.archroid.compiler.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

class MvpPresenterClassArgument(
    targetTypeName: TypeName,
    className: ClassName,
    val viewType: ClassName,
    val presenterType: ClassName
) : ClassArgument(targetTypeName, className) {

    class Builder {
        private lateinit var targetTypeName: TypeName
        private lateinit var className: ClassName
        private lateinit var viewType: ClassName
        private lateinit var presenterType: ClassName

        fun setTargetTypeName(targetTypeName: TypeName) = apply { this.targetTypeName = targetTypeName }

        fun setClassName(className: ClassName) = apply { this.className = className }

        fun setViewType(viewType: ClassName) = apply { this.viewType = viewType }

        fun setPresenterType(presenterType: ClassName) = apply { this.presenterType = presenterType }

        fun isValid(): Boolean {
            return this::targetTypeName.isInitialized
                    && this::className.isInitialized
                    && this::viewType.isInitialized
                    && this::presenterType.isInitialized
        }

        fun build() = MvpPresenterClassArgument(
            targetTypeName,
            className,
            viewType,
            presenterType
        )
    }
}