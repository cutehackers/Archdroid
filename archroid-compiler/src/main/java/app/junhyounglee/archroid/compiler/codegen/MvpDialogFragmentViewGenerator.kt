package app.junhyounglee.archroid.compiler.codegen

import app.junhyounglee.archroid.compiler.Id
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Filer

class MvpDialogFragmentViewGenerator (filer: Filer)
    : SourceFileGenerator<MvpViewClassArgument>(filer) {

    override fun onGenerate(argument: MvpViewClassArgument): TypeSpec {
        return argument.run {
            val builder = TypeSpec.classBuilder(argument.className.simpleName)
                .addKdoc(DOCUMENTATION)
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.ABSTRACT)
                .superclass(getSuperClass(this))
                .addSuperinterface(viewType)
                .addProperty(getPropertyRootImpl())
                .addProperty(getPropertyRootView())
                .addProperty(getPropertyIsRootViewAlive())
                .addFunction(getFunCreateMvpView(argument))
                .addFunction(getFunOnCreateMvpPresenter(argument))

            layoutResId.let { id ->
                if (id.value != 0) {
                    builder.addProperty(getPropertyLayoutResId(id)).build()
                } else {
                    builder.build()
                }
            }
        }
    }

    private fun getSuperClass(argument: MvpViewClassArgument): ParameterizedTypeName {
        return argument.run {
            ClassName(CORE_PACKAGE, LIFECYCLE_CONTROLLER_CLASS).parameterizedBy(viewType, presenterType)
        }
    }
    private fun getPropertyRootImpl(): PropertySpec {
        return PropertySpec.builder("impl", ClassName(VIEW_PACKAGE, VIEW_CLASS))
            .addModifiers(KModifier.PRIVATE)
            .initializer("RootViewImpl()")
            .build()
    }

    private fun getPropertyLayoutResId(resourceId: Id): PropertySpec {
        return PropertySpec.builder("layoutResId", Int::class)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .getter(FunSpec.getterBuilder().addStatement("return %L", resourceId.code).build())
            .build()
    }

    private fun getPropertyRootView(): PropertySpec {
        val rootViewType = ClassName(ROOT_VIEW_PACKAGE, ROOT_VIEW_CLASS)
        return PropertySpec.builder("rootView", rootViewType)
            .mutable()
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .getter(
                FunSpec.getterBuilder()
                    .beginControlFlow("return if (impl.isViewAlive)")
                    .addStatement("impl.container!!")
                    .endControlFlow()
                    .beginControlFlow("else")
                    .addStatement("throw %T(%S)", ClassName.bestGuess("java.lang.NullPointerException"), "Root content view is null!")
                    .endControlFlow()
                    .build())
            .setter(FunSpec.setterBuilder().addParameter("value", rootViewType).addStatement("impl.container = value").build())
            .build()
    }

    private fun getPropertyIsRootViewAlive(): PropertySpec {
        return PropertySpec.builder("isRootViewAlive", Boolean::class)
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .getter(FunSpec.getterBuilder().addStatement("return impl.isViewAlive").build())
            .build()
    }

    private fun getFunCreateMvpView(argument: MvpViewClassArgument): FunSpec {
        return FunSpec.builder("onCreateMvpView")
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .returns(argument.viewType)
            .addStatement("return this")
            .build()
    }

    private fun getFunOnCreateMvpPresenter(argument: MvpViewClassArgument): FunSpec {
        return FunSpec.builder("onCreateMvpPresenter")
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .returns(argument.presenterType)
            .addStatement("return %T(this)", argument.presenterType)
            .build()
    }


    companion object {
        private const val CORE_PACKAGE = "app.junhyounglee.archroid.runtime.core"
        private const val LIFECYCLE_CONTROLLER_CLASS = "MvpDialogFragmentLifecycleController"

        private const val VIEW_PACKAGE = "$CORE_PACKAGE.view"
        private const val VIEW_CLASS = "RootViewImpl"

        private const val ROOT_VIEW_PACKAGE = "android.view"
        private const val ROOT_VIEW_CLASS = "ViewGroup"
    }
}