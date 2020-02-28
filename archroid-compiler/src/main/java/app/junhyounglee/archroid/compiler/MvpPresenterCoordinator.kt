package app.junhyounglee.archroid.compiler

import app.junhyounglee.archroid.annotations.MvpPresenter
import app.junhyounglee.archroid.compiler.codegen.ClassArgument
import app.junhyounglee.archroid.compiler.codegen.MvpPresenterClassArgument
import app.junhyounglee.archroid.compiler.codegen.MvpPresenterGenerator
import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

/**
 * @MvpPresenter(SampleView::class, SamplePresenter::class)
 * Parse @MvpPresenter annotations and create a base class for concrete presenter class.
 * Base presenter class will be created as a form of an abstract class which extends from
 * AbsMvpPresenter class. And it's name is decided by the class annotated with @MvpPresenter.
 *
 *  ex)
 *  interface ISamplePresenter {
 *      fun foo()
 *  }
 *
 *  @MvpPresenter(SampleView::class, ISamplePresenter::class)
 *  class SamplePresenter(view: SampleView) : MvpSamplePresenter(view) {
 *      ...
 *  }
 */
class MvpPresenterCoordinator(processingEnv: ProcessingEnvironment)
    : MvpBaseCoordinator(processingEnv, MvpPresenter::class.java) {

    override fun onRoundProcess(roundEnv: RoundEnvironment, element: Element): ClassArgument? {
        val annotatedType = element as TypeElement

        // parse @MvpPresenter elements
        return parseMvpPresenter(annotatedType)
    }

    private fun parseMvpPresenter(annotatedType: TypeElement): ClassArgument? {
        /*
         * Step 1. only class can be annotated with @MvpPresenter
         *  val elementType: TypeMirror = annotatedType.asType()
         */
        if (!annotatedType.kind.isClass) {
            error(annotatedType, "Only classes can be annotated with @MvpPresenter")
            return null
        }

        val builder = MvpPresenterClassArgument.builder()
            .setClassName(ClassName(getPackage(annotatedType).qualifiedName.toString(), createClassName(annotatedType)))
            .setTargetTypeName(getTargetTypeName(annotatedType))

        /*
         * Step 2. validate annotations for this class
         *  1. MvpPresenter
         *   @param view: interface that extends MvpView
         *   @param presenter: interface that extends MvpPresenter
         */
        annotatedType.annotationMirrors.forEach { annotationMirror ->
            warning("Archroid> annotation : ${annotationMirror.annotationType.asElement().simpleName}")

            val annotationName = annotationMirror.annotationType.asElement().simpleName

            when {
                // parse MvpPresenter annotation
                annotationName.contentEquals(MvpPresenter::class.simpleName) -> {
                    annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
                        when {
                            // @param view
                            entry.key.simpleName.contentEquals("view") -> {
                                getViewType(annotationName, entry.toPair())?.also { viewType: ClassName ->
                                    builder.setViewType(viewType)
                                }
                            }
                            // @param presenter
                            entry.key.simpleName.contentEquals("presenter") -> {
                                getSuperInterfaceTypeOfPresenter(annotatedType, annotationName, entry.toPair())?.also { presenterType: ClassName ->
                                    builder.setPresenterType(presenterType)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!builder.isValid()) {
            error(annotatedType, "${annotatedType.simpleName} annotation requires valid mvp view and presenter interface.")
            return null
        }

        return builder.build()
    }

    override fun onGenerateSourceFile(classArgument: ClassArgument) {
        val argument = classArgument as MvpPresenterClassArgument
        MvpPresenterGenerator(filer).apply { generate(argument) }
    }
}
