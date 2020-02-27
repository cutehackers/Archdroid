package app.junhyounglee.archroid.compiler

import app.junhyounglee.archroid.annotations.MvpActivityView
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.google.common.collect.SetMultimap
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic


/**
 * @AutoService(Processor::class) register processor to the compiler
 *  https://github.com/google/auto/tree/master/service
 */
@AutoService(Processor::class)
class ArchroidProcessor : AbstractProcessor() {

    private var sdk: Int = 1
    private var debuggable: Boolean = false

    private lateinit var elements: Elements
    private lateinit var types: Types
    private lateinit var messager: Messager

    private lateinit var coordinators: ImmutableList<ArchCoordinator>

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return ImmutableSet.of(
            OPTION_MIN_SDK,
            OPTION_DEBUGGABLE
        )
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> = LinkedHashSet<String>().apply {
        getSupportedAnnotations().forEach { annotation ->
            this.add(annotation.canonicalName)
        }
    }

    private fun getSupportedAnnotations(): Set<Class<out Annotation>> {
        return LinkedHashSet<Class<out Annotation>>().apply {
            add(MvpActivityView::class.java)
        }
    }

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        val filer = processingEnv.filer
        messager = processingEnv.messager
        elements = processingEnv.elementUtils
        types = processingEnv.typeUtils

        // initialize compiler options
        setUpOptions(processingEnv)
        setUpCoordinator(processingEnv)
    }

    private fun setUpOptions(env: ProcessingEnvironment) {
        env.options.also { options ->
            val minSdk = options[OPTION_MIN_SDK]
            options[OPTION_MIN_SDK]?.apply {
                try {
                    this@ArchroidProcessor.sdk = Integer.parseInt(this)
                } catch (e: NumberFormatException) {
                    env.warning("Unable to parse supplied minSdk option '$minSdk'. Falling back to API 1 support.")
                }
            }
            debuggable = "true" == options[OPTION_DEBUGGABLE]
        }
    }

    private fun setUpCoordinator(processingEnv: ProcessingEnvironment) {
        coordinators = ImmutableList.builder<ArchCoordinator>().run {
            add(MvpActivityViewCoordinator(processingEnv))
            add(MvpFragmentViewCoordinator(processingEnv))
            add(MvpDialogFragmentViewCoordinator(processingEnv))
            build()
        }
    }

    /**
     * @return If true is returned, the annotation types are claimed and subsequent processors will
     * not be asked to process them; if false is returned, the annotation types are unclaimed and
     * subsequent processors may be asked to process them.
     */
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (annotations.isEmpty()) {
            return true
        }

        coordinators.forEach { coordinator ->
            coordinator.process(roundEnv)
        }

        return false
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

    fun error(message: String, exception: Exception? = null) {
        val stackTrace = exception?.run {
            StringWriter().apply {
                this@run.printStackTrace(PrintWriter(this))
            }
        }

        processingEnv.error(stackTrace?.run { "$message\n$this" } ?: message)
    }


    companion object {
        const val OPTION_MIN_SDK = "archroid_min_sdk"
        const val OPTION_DEBUGGABLE = "archroid_debuggable"
    }
}

fun ProcessingEnvironment.warning(message: String, element: Element? = null) {
    messager.printMessage(Diagnostic.Kind.WARNING, message, element)
}

fun ProcessingEnvironment.error(message: String, element: Element? = null) {
    messager.printMessage(Diagnostic.Kind.ERROR, message, element)
}

fun ProcessingEnvironment.mandatory(message: String, element: Element? = null) {
    messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, message, element)
}

//@AutoService(Processor::class)
//class ArchroidProcessor : BasicAnnotationProcessor() {
//
//    private var sdk: Int = 1
//    private var debuggable: Boolean = false
//
//    override fun getSupportedSourceVersion(): SourceVersion  = SourceVersion.latestSupported()
//
//    override fun initSteps(): Iterable<ProcessingStep> {
//        // initialize compiler option
//        setUpOptions(processingEnv)
//
//        processingEnv.mandatory("Archroid> initiating Archroid annotation processor.")
//        processingEnv.warning("Archroid> initiating annotation processor.")
//
//        // list architecture object generator
//        return listOf(ViewGenerator(processingEnv))
//    }
//
//    private fun setUpOptions(env: ProcessingEnvironment) {
//        env.options.also { options ->
//            val minSdk = options[OPTION_MIN_SDK]
//            minSdk?.apply {
//                try {
//                    this@ArchroidProcessor.sdk = Integer.parseInt(this)
//                } catch (e: NumberFormatException) {
//                    env.warning("Unable to parse supplied minSdk option '$minSdk'. Falling back to API 1 support.")
//                }
//            }
//            debuggable = "true" == options[OPTION_DEBUGGABLE]
//        }
//    }
//
//    companion object {
//        const val OPTION_MIN_SDK = "archroid_min_sdk"
//        const val OPTION_DEBUGGABLE = "archroid_debuggable"
//    }
//}

/**
 * PresenterGenerator
 * ViewGenerator
 */
abstract class ArchroidObjectGenerator(
    private val processingEnv: ProcessingEnvironment
) : BasicAnnotationProcessor.ProcessingStep {

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

    override fun annotations(): Set<Class<out Annotation>> {
        return getSupportedAnnotations()
    }

    protected abstract fun getSupportedAnnotations(): Set<Class<out Annotation>>

    fun warning(message: String, annotation: Element? = null) {
        processingEnv.warning(message, annotation)
    }

    fun error(annotation: Element, e: Exception) {
        val stackTrace = StringWriter().apply {
            e.printStackTrace(PrintWriter(this))
        }
        processingEnv.error("Unable to parse ${annotation.simpleName} annotation.\n$stackTrace", annotation)
    }

    companion object {

        fun getMirror(element: Element, annotation: Class<out Annotation>): AnnotationMirror? {
            for (mirror in element.annotationMirrors) {
                if (mirror.annotationType.toString() == annotation.canonicalName) {
                    return mirror
                }
            }
            return null
        }

        fun hasAnnotationWithName(element: Element, simpleName: String): Boolean {
            for (mirror in element.annotationMirrors) {
                val annotationName = mirror.annotationType.asElement().simpleName.toString()
                if (simpleName == annotationName) {
                    return true
                }
            }
            return false
        }
    }
}

/**
 * @MvpActivityView(SampleView::class)
 *  Build step
 *  1. is SampleView interface and subclass of MvpView? if not error. only interface can be used.
 *
 * @BindMvpPresenter(SamplePresenter::class)
 *  2. is SamplePresenter subclass of MvpPresenter which has SampleView as generic type?
 *     if not error. SamplePresenter should be a class which has a constructor containing SampleView
 *     parameter. If the presenter is an abstract class or an interface, it cannot be created from
 *     MVP view base class, MvpSampleActivityView.
 *
 *  3. create abstract mvp base class
 *     ex)
 *     abstract class MvpSampleActivityView
 *          : MvpActivityLifecycleController<SampleView, SamplePresenter>()
 *          , SampleView {
 */
class ViewGenerator(processingEnv: ProcessingEnvironment) : ArchroidObjectGenerator(processingEnv) {

    override fun getSupportedAnnotations(): Set<Class<out Annotation>> = LinkedHashSet<Class<out Annotation>>().apply {
        add(MvpActivityView::class.java)
    }

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): Set<Element> {
        val deferredElements = ImmutableSet.builder<Element>()

        getSupportedAnnotations().forEach {
            val elements: Set<Element> = elementsByAnnotation.get(it)
            for (elem in elements) {
                warning("Archroid> ViewGenerator: parsing annotation: ${it.simpleName}", elem)

                try {
                    parseAndCreateTarget(it, elem)
                } catch (e: TypeNotPresentException) {
                    deferredElements.add(elem)

                    error(elem, e)
                }
            }
        }

        return deferredElements.build()
    }

    private fun parseAndCreateTarget(annotation: Class<out Annotation>, element: Element) {
        //val annotatedType = element as TypeElement

        val packageName = elements.getPackageOf(element).run {
            if (isUnnamed) {
                null
            } else {
                qualifiedName
            }
        }

        warning("Archroid> ViewGenerator.parseAndCreateTarget(), packageName: $packageName, simpleName: ${element.simpleName}")

        val annotatedType = element as TypeElement
        val enclosing = annotatedType.enclosingElement
        warning("Archroid> ViewGenerator.parseAndCreateTarget(), qualifiedName: ${enclosing.kind}, enclosing element: ${enclosing.simpleName}")

        val baseView = element.getAnnotation(MvpActivityView::class.java)
        warning("Archroid> ViewGenerator: base view: $baseView")

        element.enclosedElements.forEach {
            warning("Archroid> enclosed(), ${getName(it)}")
        }

//        if (!annotatedType.kind.isClass) {
//            val msg = "Only class can be annotated with @MvpActivityView"
//            error(annotatedType, msg)
//            throw IllegalAccessException(msg)
//        }
    }

    private fun getName(e: Element): String {
        return if (e.kind.isClass || e.kind.isInterface) {
            elements.getBinaryName(e as TypeElement).toString()
        } else if (e.kind == ElementKind.PACKAGE) {
            e.simpleName.toString()
        } else {
            getName(e.enclosingElement) + '.'.toString() + e.simpleName
        }
    }

}

