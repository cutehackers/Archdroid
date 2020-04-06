package app.junhyounglee.archroid.compiler

import app.junhyounglee.archroid.compiler.codegen.ClassArgument
import com.google.auto.common.SuperficialValidation
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.sun.source.util.Trees
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeScanner
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * ArchCoordinator is an object that parse annotation and create architecture objects such as
 * MvpSampleActivityView, MvpSampleFragmentView.
 *
 * @MvpActivityView
 * @MvpMapActivityView
 * @MvpFragmentView
 * @MvpMapFragmentView
 * @MvpDialogFragmentView
 * @MvpPresenter
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class ArchCoordinator(
    protected val processingEnv: ProcessingEnvironment,
    internal val klassType: Class<out Annotation>
) {

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

    fun error(message: String, exception: Exception? = null) {
        val stackTrace = exception?.run {
            StringWriter().apply {
                this@run.printStackTrace(PrintWriter(this))
            }
        }

        processingEnv.error(stackTrace?.run { "$message\n$this" } ?: message)
    }

    protected fun getTargetTypeName(annotatedType: TypeElement): TypeName {
        val typeMirror = annotatedType.asType()

        var targetType = typeMirror.asTypeName()
        if (targetType is ParameterizedTypeName) {
            targetType = targetType.rawType
        }

        return targetType
    }

    fun getPackage(annotatedType: TypeElement): PackageElement {
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

    fun getSimpleTypeName(typeMirror: TypeMirror): String {
        val declaredType = typeMirror as DeclaredType
        val classType = declaredType.asElement() as TypeElement
        return classType.simpleName.toString()
    }

    fun getQualifiedTypeName(klass: Class<*>): String? = try {
        klass.canonicalName
    } catch (e: MirroredTypeException) {
        val classTypeMirror = e.typeMirror as DeclaredType
        val classTypeElement = classTypeMirror.asElement() as TypeElement
        classTypeElement.qualifiedName.toString()
    }

    fun getSimpleTypeName(klass: Class<*>): String = try {
        klass.simpleName
    } catch (e: MirroredTypeException) {
        val classTypeMirror = e.typeMirror as DeclaredType
        val classTypeElement = classTypeMirror.asElement() as TypeElement
        classTypeElement.simpleName.toString()
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

    fun getSuperClassAsTypeElement(element: TypeElement): TypeElement? {
        val parent: TypeMirror = element.superclass
        if (parent.kind == TypeKind.NONE) return null

        if (parent is DeclaredType) {
            if (parent.asElement() is TypeElement) {
                return toTypeElement(parent)
            }
        }
        return null
    }

    /**
     * Create a new class name with prefix 'Mvp'
     * @param annotatedType annotated class type element
     * @return Mvp view class name that will be generated by archroid annotation compiler
     */
    open fun createClassName(annotatedType: TypeElement): String {
        return "Mvp${annotatedType.simpleName}"
    }

    /**
     * Retrieve android resource id object from the given element and value.
     */
    protected fun getResourceIdentifier(element: Element, annotationMirror: AnnotationMirror, annotationValue: AnnotationValue, value: Int): Id {
        val tree: JCTree? = trees?.getTree(element, annotationMirror, annotationValue) as JCTree
        tree?.also {
            // tree can be null if the references are compiled types and not source
            rScanner.reset()
            it.accept(rScanner)
            if (rScanner.resourceIds.isNotEmpty()) {
                return rScanner.resourceIds.values.iterator().next()
            }
        }
        return Id(value)
    }


    private class RScanner : TreeScanner() {
        internal var resourceIds: MutableMap<Int, Id> = LinkedHashMap()

        override fun visitSelect(jcFieldAccess: JCTree.JCFieldAccess) {
            val symbol = jcFieldAccess.sym
            if (symbol.enclosingElement != null
                && symbol.enclosingElement.enclosingElement != null
                && symbol.enclosingElement.enclosingElement.enclClass() != null
            ) {
                try {
                    val value =
                        Objects.requireNonNull<Any>((symbol as Symbol.VarSymbol).constantValue) as Int
                    resourceIds[value] = Id(value, symbol)
                } catch (ignored: Exception) {
                }

            }
        }

        override fun visitLiteral(jcLiteral: JCTree.JCLiteral?) {
            try {
                val value = jcLiteral!!.value as Int
                resourceIds[value] = Id(value)
            } catch (ignored: Exception) {
            }

        }

        internal fun reset() {
            resourceIds.clear()
        }
    }

    companion object {

        internal var trees: Trees? = null
        private val rScanner = RScanner()

        internal const val MVP_VIEW_TYPE = "app.junhyounglee.archroid.runtime.core.view.MvpView"
        internal const val MVP_PRESENTER_TYPE = "app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter<VIEW>"
        internal const val ABS_MVP_PRESENTER_TYPE = "app.junhyounglee.archroid.runtime.core.presenter.AbsMvpPresenter<VIEW>"

        fun toTypeElement(type: DeclaredType) = type.asElement() as TypeElement

        fun capitalize(text: String): String {
            if (text.isEmpty() || text.isBlank()) {
                return text
            }

            val firstChar = text[0]
            if (firstChar.isLowerCase()) {
                return firstChar.toUpperCase() + text.substring(1)
            }

            return text
        }

        fun init(processingEnv: ProcessingEnvironment) {
            try {
                trees = Trees.instance(processingEnv)
            } catch (ignored: IllegalArgumentException) {
                try {
                    // Get original ProcessingEnvironment from Gradle-wrapped one or KAPT-wrapped one.
                    for (field in processingEnv.javaClass.declaredFields) {
                        if (field.name == "delegate" || field.name == "processingEnv") {
                            field.isAccessible = true
                            val javacEnv = field.get(processingEnv) as ProcessingEnvironment
                            trees = Trees.instance(javacEnv)
                            break
                        }
                    }
                } catch (e: Throwable) { }
            }
        }
    }

}