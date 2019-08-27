package app.junhyounglee.archroid.compiler

import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpActivityView
import app.junhyounglee.archroid.compiler.codegen.ClassArgument
import app.junhyounglee.archroid.compiler.codegen.MvpActivityViewClassArgument
import app.junhyounglee.archroid.compiler.codegen.MvpActivityViewGenerator
import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror


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
 *
 */
class MvpViewCoordinator(processingEnv: ProcessingEnvironment)
    : ArchCoordinator(processingEnv, MvpActivityView::class.java) {

    override fun onRoundProcess(roundEnv: RoundEnvironment, element: Element): ClassArgument? {
        val annotatedType = element as TypeElement

        // parse @MvpActivityView elements
        return parseMvpActivityView(annotatedType)
    }

    /**
     * @MvpActivityView(SampleView::class)
     * @BindMvpPresenter(SamplePresenter::class)
     * class SampleActivityView
     * @return true if MvpActivityView, BindMvpPresenter annotation parsed successfully, otherwise
     * false
     */
    private fun parseMvpActivityView(annotatedType: TypeElement): MvpActivityViewClassArgument? {

        /*
         * Step 1. only class can be annotated with @MvpActivityView
         *  val elementType: TypeMirror = annotatedType.asType()
         */
        if (!annotatedType.kind.isClass) {
            error(annotatedType, "Only classes can be annotated with @MvpActivityView")
            return null
        }

        // TODO build class name with given primary string ex) Mvp{Sample}ActivityView
        val builder = MvpActivityViewClassArgument.builder()
            .targetTypeName(getTargetTypeName(annotatedType))
            .className(ClassName(getPackage(annotatedType).qualifiedName.toString(), "MvpSampleActivityView"))

        /*
         * Step 2. validate annotations for this class
         *  1. MvpActivityView
         *   @param view: interface that extends MvpView
         *  2. BindMvpPresenter
         *   @param presenter: should ba a class extending MvpPresenter that has a view extends
         *      MvpView
         */
        //val mvpActivityView: MvpActivityView = annotatedType.getAnnotation(MvpActivityView::class.java)
        //warning("Archroid> annotation : ${getQualifiedTypeName(mvpActivityView)}")

        annotatedType.annotationMirrors.forEach { annotationMirror ->
            warning("Archroid> annotation : ${annotationMirror.annotationType.asElement().simpleName}")

            val annotationName = annotationMirror.annotationType.asElement().simpleName
            when {
                // MvpActivityView annotation
                annotationName.contentEquals(MvpActivityView::class.simpleName) -> {
                    annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
                        if (entry.key.simpleName.contentEquals("view")) {
                            // entry.value.value == List<ClassType>
                            // val typeMirrors = entry.value.value as List<TypeMirror>
                            // entry.value.value == ClassType
                            val typeMirror = entry.value.value as TypeMirror
                            warning("Archroid> MvpActivityView argument key: ${entry.key.simpleName}, value: ${entry.value.value}, interface(${isInterfaceType(typeMirror)}), subTypeOf(${isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)})")

                            // interface that extends MvpView
                            if (!isInterfaceType(typeMirror) || !isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)) {
                                error(entry.key, "@MvpActivityView's view parameter should be an interface that extends from MvpView.")
                                return@parseMvpActivityView null
                            }

                            // set view class argument
                            builder.viewType(ClassName.bestGuess(getQualifiedTypeName(typeMirror)))
                        }
                    }
                }

                // BindMvpPresenter annotation
                annotationName.contentEquals(BindMvpPresenter::class.simpleName) -> {
                    annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
                        if (entry.key.simpleName.contentEquals("presenter")) {
                            val typeMirror = entry.value.value as TypeMirror
                            warning("Archroid> BindMvpPresenter argument key: ${entry.key.simpleName}, value: ${entry.value.value}")

                            if (!isClassType(typeMirror)) {
                                error(entry.key, "@BindMvpPresenter should have appropriate presenter class.")
                                return@parseMvpActivityView null
                            }

                            val presenterType = typeMirror as DeclaredType
                            val presenter = presenterType.asElement()


                            // check if it's an abstract class
                            if (presenter.modifiers.contains(Modifier.ABSTRACT)) {
                                error(entry.key, "Abstract class ${presenter.simpleName} cannot be annotated with @BindMvpPresenter.")
                                return@parseMvpActivityView null
                            }

                            val parent: TypeMirror = (presenterType.asElement() as TypeElement).superclass
                            val parentType = (parent as DeclaredType)

                            // check if the class extends from MvpPresenter<VIEW>
                            if (!isSubTypeOfType(toTypeElement(parentType).asType(), MVP_PRESENTER_TYPE)) {
                                error(entry.key, "Class ${presenter.simpleName} should extend from MvpPresenter for @BindMvpPresenter. current parent type is ${toTypeElement(parentType).asType()}")
                                return@parseMvpActivityView null
                            }

                            // check if a public constructor containing MvpView inherited view as a parameter is given
                            var found = false
                            loop@ for (enclosed in presenter.enclosedElements) {
                                if (enclosed.kind == ElementKind.CONSTRUCTOR) {
                                    val constructor = enclosed as ExecutableElement
                                    for (param: VariableElement in constructor.parameters) {
                                        // has MvpView parameter
                                        if (isSubTypeOfType(param.asType(), MVP_VIEW_TYPE)) {
                                            found = true
                                            break
                                        }
                                    }
                                }
                            }
                            if (!found) {
                                error(entry.key, "@BindMvpPresenter requires a constructor that contains a view extends from MvpView.")
                                return@parseMvpActivityView null
                            }

                            // set presenter class argument
                            builder.presenterType(ClassName.bestGuess(getQualifiedTypeName(typeMirror)))
                        }
                    }
                }
            }
        }

        val enclosing = annotatedType.enclosingElement
        warning("Archroid> parent qualifiedName: ${enclosing.kind}, element: ${getName(enclosing)}")

        annotatedType.enclosedElements.forEach {
            warning("Archroid> children, ${getName(it)}")
        }

        return builder.build()
    }

    override fun onGenerateSourceFile(classArgument: ClassArgument) {
        val argument = classArgument as MvpActivityViewClassArgument
        MvpActivityViewGenerator(filer).run {
            generate(argument)
        }
    }

    private fun isValidClass(annotationMirror: AnnotationMirror): Boolean {

        return true
    }


    companion object {
        internal const val MVP_VIEW_TYPE = "app.junhyounglee.archroid.runtime.core.view.MvpView"
        internal const val MVP_PRESENTER_TYPE = "app.junhyounglee.archroid.runtime.core.presenter.MvpPresenter<VIEW>"
    }
}