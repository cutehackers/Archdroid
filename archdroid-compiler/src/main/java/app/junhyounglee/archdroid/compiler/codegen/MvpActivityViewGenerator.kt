package app.junhyounglee.archdroid.compiler.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.Filer

class MvpActivityViewGenerator(filer: Filer)
    : SourceFileGenerator<MvpActivityViewClassArgument>(filer) {

    private fun getSuperClass(argument: MvpActivityViewClassArgument): ParameterizedTypeName {
        return argument.run {
            ClassName(PACKAGE_NAME, CLASS_NAME).parameterizedBy(viewType, presenterType)
        }
    }

    override fun onGenerate(argument: MvpActivityViewClassArgument): TypeSpec {
        return argument.run {
            TypeSpec.classBuilder(argument.className.simpleName)
                .addKdoc(DOCUMENTATION)
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.ABSTRACT)
                .superclass(getSuperClass(this))
                .addSuperinterface(viewType)
                .build()
        }
    }


    companion object {
        private const val PACKAGE_NAME = "app.junhyounglee.archdroid.runtime.core"
        private const val CLASS_NAME = "MvpActivityLifecycleController"
    }
}
