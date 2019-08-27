package app.junhyounglee.archroid.compiler.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.IOException
import javax.annotation.processing.Filer

abstract class SourceFileGenerator<ARGS : ClassArgument>(private val filer: Filer) {

    @Throws(IOException::class)
    fun generate(argument: ARGS) {
        /*
         * for custom class path can be set like this.
         *  val file = File(folder, argument.getFileName())
         *  FileSpec.get(argument.className.packageName, klass).writeTo(file)
         */

        val klass = onGenerate(argument)

        FileSpec.get(argument.className.packageName, klass).writeTo(filer)
    }

    abstract fun onGenerate(argument: ARGS): TypeSpec


    companion object {
        const val DOCUMENTATION = "Auto generated class from Archroid"
    }
}
