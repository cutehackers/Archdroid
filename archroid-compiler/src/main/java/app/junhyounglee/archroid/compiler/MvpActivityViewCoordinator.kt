package app.junhyounglee.archroid.compiler

import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.BindMvpPresenterFactory
import app.junhyounglee.archroid.annotations.MvpActivityView
import app.junhyounglee.archroid.compiler.codegen.ClassArgument
import app.junhyounglee.archroid.compiler.codegen.MvpActivityViewGenerator
import app.junhyounglee.archroid.compiler.codegen.MvpViewClassArgument
import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement


/**
 * @MvpActivityView(SampleView::class, R.layout.activity_simple)
 *  Build step
 *  1. is SampleView an interface and a subclass of MvpView? if not error. only an interface can be used.
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
class MvpActivityViewCoordinator(processingEnv: ProcessingEnvironment)
    : MvpBaseCoordinator(processingEnv, MvpActivityView::class.java) {

    override fun onRoundProcess(roundEnv: RoundEnvironment, element: Element): ClassArgument? {
        val annotatedType = element as TypeElement

        // parse @MvpActivityView elements
        return parseMvpActivityView(annotatedType)
    }

    /**
     * Parse following annotations:
     *  @MvpActivityView(SampleView::class, R.layout.activity_simple)
     *  @BindMvpPresenter(SamplePresenter::class)
     *  class SampleActivityView
     *
     * @param annotatedType annotated class type element
     * @return mvp view argument instance, null otherwise
     */
    private fun parseMvpActivityView(annotatedType: TypeElement): MvpViewClassArgument? {

        /*
         * Step 1. only class can be annotated with @MvpActivityView
         *  val elementType: TypeMirror = annotatedType.asType()
         */
        if (!annotatedType.kind.isClass) {
            error(annotatedType, "Only classes can be annotated with @MvpActivityView")
            return null
        }

        val builder = MvpViewClassArgument.builder()
            .setClassName(ClassName(getPackage(annotatedType).qualifiedName.toString(), createClassName(annotatedType)))
            .setTargetTypeName(getTargetTypeName(annotatedType))

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
                // parse MvpActivityView annotation
                annotationName.contentEquals(MvpActivityView::class.simpleName) -> {
                    if (!parseMvpView(annotatedType, builder, annotationMirror)) {
                        // failed to parse annotation argument
                        return@parseMvpActivityView null
                    }
                }

                // parse BindMvpPresenterFactory annotation
                annotationName.contentEquals(BindMvpPresenterFactory::class.simpleName) -> {

                }

                // parse BindMvpPresenter annotation
                annotationName.contentEquals(BindMvpPresenter::class.simpleName) -> {
                    if (!parseMvpPresenter(annotatedType, builder, annotationMirror)) {
                        // failed to parse annotation argument
                        return@parseMvpActivityView null
                    }
                }
            }
        }

        if (!builder.isValid()) {
            error(annotatedType, "${annotatedType.simpleName} class requires annotation @MvpActivityView and @BindMvpPresenter.")
            return null
        }

        val enclosing = annotatedType.enclosingElement
        warning("Archroid> parent qualifiedName: ${enclosing.kind}, element: ${getName(enclosing)}")

        annotatedType.enclosedElements.forEach {
            warning("Archroid> children, ${getName(it)}")
        }

        return builder.build()
    }

    override fun onGenerateSourceFile(classArgument: ClassArgument) {
        val argument = classArgument as MvpViewClassArgument
        MvpActivityViewGenerator(filer).apply { generate(argument) }
    }

}