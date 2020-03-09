
package app.junhyounglee.archroid.compiler.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment

/**
 * Create an abstract mvp base presenter class file.
 *
 *  ex)
 *  abstract class MvpSamplePresenter(view: SampleView)
 *      : AbsMvpPresenter<SampleView>(view)
 *      , SamplePresenter {
 *  }
 */
class MvpPresenterGenerator(processingEnv: ProcessingEnvironment)
    : SourceFileGenerator<MvpPresenterClassArgument>(processingEnv) {

    override fun onGenerate(argument: MvpPresenterClassArgument): TypeSpec {
        return argument.run {
            TypeSpec.classBuilder(argument.className.simpleName)
                .addKdoc(DOCUMENTATION)
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.ABSTRACT)
                .primaryConstructor(getConstructor(this))
                .superclass(getSuperClass(this))
                .addSuperclassConstructorParameter("%N", "view")
                .addSuperinterface(presenterType)
                .build()
        }
    }

    private fun getSuperClass(argument: MvpPresenterClassArgument): ParameterizedTypeName {
        return argument.run {
            ClassName(PRESENTER_PACKAGE, ABS_MVP_PRESENTER_CLASS).parameterizedBy(viewType)
        }
    }

    private fun getConstructor(argument: MvpPresenterClassArgument): FunSpec {
        return FunSpec.constructorBuilder()
            .addParameter("view", argument.viewType)
            .build()
    }


    companion object {
        private const val PRESENTER_PACKAGE = "app.junhyounglee.archroid.runtime.core.presenter"
        private const val ABS_MVP_PRESENTER_CLASS = "AbsMvpPresenter"
    }
}
