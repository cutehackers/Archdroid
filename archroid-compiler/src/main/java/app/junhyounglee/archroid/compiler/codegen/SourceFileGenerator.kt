package app.junhyounglee.archroid.compiler.codegen

import app.junhyounglee.archroid.compiler.ArchroidProcessor
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

        try {
            val kaptKotlinGeneratedDir = processingEnv.options[ArchroidProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME]
            val file = File(kaptKotlinGeneratedDir, "${argument.className.simpleName}.kt")
            FileSpec.get(argument.className.packageName, klass).writeTo(file)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    abstract fun onGenerate(argument: ARGS): TypeSpec


    companion object {
        const val DOCUMENTATION = "Auto generated class from Archroid"
    }
}

