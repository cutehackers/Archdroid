package app.junhyounglee.archroid.compiler.codegen

import app.junhyounglee.archroid.compiler.Id
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

class MvpViewClassArgument(
    targetTypeName: TypeName,
    className: ClassName,
    val viewType: ClassName,
    val presenterType: ClassName,
    val layoutResId: Id
) : ClassArgument(targetTypeName, className) {

    class Builder {
        private lateinit var targetTypeName: TypeName
        private lateinit var className: ClassName
        private lateinit var viewType: ClassName
        private lateinit var presenterType: ClassName
        private lateinit var layoutResId: Id

        fun setTargetTypeName(targetTypeName: TypeName) = apply { this.targetTypeName = targetTypeName }

        fun setClassName(className: ClassName) = apply { this.className = className }

        fun setViewType(viewType: ClassName) = apply { this.viewType = viewType }

        fun setPresenterType(presenterType: ClassName) = apply { this.presenterType = presenterType }

        fun setContentView(layoutResId: Id) = apply { this.layoutResId = layoutResId }

        fun isValid(): Boolean {
            return this::targetTypeName.isInitialized
                    && this::className.isInitialized
                    && this::viewType.isInitialized
                    && this::presenterType.isInitialized
                    && this::layoutResId.isInitialized
        }

        fun build() = MvpViewClassArgument(
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