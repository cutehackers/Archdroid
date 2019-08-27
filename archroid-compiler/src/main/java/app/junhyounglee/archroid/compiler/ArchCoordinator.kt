package app.junhyounglee.archroid.compiler

import app.junhyounglee.archroid.compiler.codegen.ClassArgument
import com.google.auto.common.SuperficialValidation
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * ArchCoordinator is an object that parse annotation and create architecture objects such as
 * MvpSampleActivityView, MvpSampleFragmentView.
 */
abstract class ArchCoordinator(
    private val processingEnv: ProcessingEnvironment,
    private val klassType: Class<out Annotation>) {

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

    fun process(roundEnv: RoundEnvironment) {
        for (element: Element in roundEnv.getElementsAnnotatedWith(klassType)) {
            if (!SuperficialValidation.validateElement(element)) continue

            try {
                onRoundProcess(roundEnv, element)?.let {
                    onGenerateSourceFile(it)
                }
            } catch (e: Exception) {
                error(element, exception = e)
            }
        }
    }

    abstract fun onRoundProcess(roundEnv: RoundEnvironment, element: Element): ClassArgument?

    abstract fun onGenerateSourceFile(classArgument: ClassArgument)

    protected fun getTargetTypeName(annotatedType: TypeElement): TypeName {
        val typeMirror = annotatedType.asType()

        var targetType = typeMirror.asTypeName()
        if (targetType is ParameterizedTypeName) {
            targetType = targetType.rawType
        }

        return targetType
    }

    protected fun getPackage(annotatedType: TypeElement): PackageElement {
        return elements.getPackageOf(annotatedType).apply {
            check(!isUnnamed) {"Annotated class should have appropriate package name"}
        }
    }

    fun isInterfaceType(typeMirror: TypeMirror): Boolean {
        return typeMirror is DeclaredType && typeMirror.asElement().kind == ElementKind.INTERFACE
    }

    fun isClassType(typeMirror: TypeMirror): Boolean {
        return typeMirror is DeclaredType && typeMirror.asElement().kind == ElementKind.CLASS
    }

    fun isSubTypeOfType(typeMirror: TypeMirror, otherType: String): Boolean {
        if (isTypeEqual(typeMirror, otherType)) {
            return true
        }
        if (typeMirror.kind != TypeKind.DECLARED) {
            return false
        }
        val declaredType = typeMirror as DeclaredType
        val typeArguments = declaredType.typeArguments
        if (typeArguments.size > 0) {
            val typeString = StringBuilder(declaredType.asElement().toString())
            typeString.append('<')
            for (i in typeArguments.indices) {
                if (i > 0) {
                    typeString.append(',')
                }
                typeString.append('?')
            }
            typeString.append('>')
            if (typeString.toString() == otherType) {
                return true
            }
        }
        val element = declaredType.asElement() as? TypeElement ?: return false
        val superType = element.superclass
        if (isSubTypeOfType(superType, otherType)) {
            return true
        }
        for (interfaceType in element.interfaces) {
            if (isSubTypeOfType(interfaceType, otherType)) {
                return true
            }
        }
        return false
    }

    fun isTypeEqual(typeMirror: TypeMirror, otherType: String): Boolean {
        return otherType == typeMirror.toString()
    }

    fun getQualifiedTypeName(typeMirror: TypeMirror): String {
        val declaredType = typeMirror as DeclaredType
        val classType = declaredType.asElement() as TypeElement
        return classType.qualifiedName.toString()
    }

    fun getQualifiedTypeName(klass: Class<*>): String? = try {
        klass.canonicalName
    } catch (e: MirroredTypeException) {
        val classTypeMirror = e.typeMirror as DeclaredType
        val classTypeElement = classTypeMirror.asElement() as TypeElement
        classTypeElement.qualifiedName.toString()
    }

    fun getSimpeTypeName(klass: Class<*>): String = try {
        klass.simpleName
    } catch (e: MirroredTypeException) {
        val classTypeMirror = e.typeMirror as DeclaredType
        val classTypeElement = classTypeMirror.asElement() as TypeElement
        classTypeElement.simpleName.toString()
    }

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


    companion object {
        fun toTypeElement(type: DeclaredType) = type.asElement() as TypeElement
    }
}
