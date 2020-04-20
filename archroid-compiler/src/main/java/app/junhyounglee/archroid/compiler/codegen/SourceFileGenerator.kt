package app.junhyounglee.archroid.compiler.codegen

import app.junhyounglee.archroid.compiler.ArchroidProcessor
import app.junhyounglee.archroid.compiler.error
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment

abstract class SourceFileGenerator<ARGS : ClassArgument>(private val processingEnv: ProcessingEnvironment) {

    @Throws(IOException::class)
    fun generate(argument: ARGS) {
        /*
         * for custom class path can be set like this.
         *  val file = File(folder, argument.getFileName())
         *  FileSpec.get(argument.className.packageName, klass).writeTo(file)
         */

        val klass = onGenerate(argument)
        //val filer = processingEnv.filer

        //FileSpec.get(argument.className.packageName, klass).writeTo(filer)
        try {
            val kaptKotlinGeneratedDir = processingEnv.options[ArchroidProcessor.OPTION_KAPT_KOTLIN_GENERATED]
            if (kaptKotlinGeneratedDir == null) {
                processingEnv.error("Can't find the target directory for generated Kotlin files.")
                return
            }

            val dirs = File(kaptKotlinGeneratedDir).apply {
                mkdirs()
            }
            FileSpec.get(argument.className.packageName, klass).writeTo(dirs)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    abstract fun onGenerate(argument: ARGS): TypeSpec


    companion object {
        const val DOCUMENTATION = "Auto generated class from Archroid"

        internal const val CORE_PACKAGE = "app.junhyounglee.archroid.runtime.core"

        internal const val VIEW_PACKAGE = "$CORE_PACKAGE.view"
        internal const val VIEW_CLASS = "RootViewImpl"

        internal const val ROOT_VIEW_PACKAGE = "android.view"
        internal const val ROOT_VIEW_CLASS = "ViewGroup"

        internal const val PRESENTER_PACKAGE = "$CORE_PACKAGE.presenter"
        internal const val PRESENTER_PROVIDER_CLASS = "PresenterProviders"
    }
}

