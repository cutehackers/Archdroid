package app.junhyounglee.archdroid.compiler

import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * ArchCoordinator is an object that parse annotation and create architecture objects such as
 * MvpSampleActivityView, MvpSampleFragmentView.
 */
abstract class ArchCoordinator(private val processingEnv: ProcessingEnvironment) {

    protected val filer: Filer by lazy {
        processingEnv.filer
    }

    protected val messager: Messager by lazy {
        processingEnv.messager
    }

    protected val elements: Elements by lazy {
        processingEnv.elementUtils
    }

    protected val types: Types by lazy {
        processingEnv.typeUtils
    }

    abstract fun process(roundEnv: RoundEnvironment): Boolean

    fun toTypeElement(type: DeclaredType) = type.asElement() as TypeElement

    fun warning(message: String, annotation: Element? = null) {
        processingEnv.warning(message, annotation)
    }

    fun error(annotation: Element, message: String? = null, exception: Exception? = null) {
        val stackTrace = exception?.run {
            StringWriter().apply {
                this@run.printStackTrace(PrintWriter(this))
            }
        }

        val msg = message ?: "Unable to parse ${annotation.simpleName} annotation."

        processingEnv.error(stackTrace?.run { "$msg\n$this" } ?: msg, annotation)
    }

    fun getName(e: Element): String {
        return if (e.kind.isClass || e.kind.isInterface) {
            elements.getBinaryName(e as TypeElement).toString()
        } else if (e.kind == ElementKind.PACKAGE) {
            e.simpleName.toString()
        } else {
            getName(e.enclosingElement) + '.'.toString() + e.simpleName
        }
    }
}
