package app.junhyounglee.archroid.compiler

import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.BindMvpPresenterFactory
import app.junhyounglee.archroid.annotations.MvpActivityView
import app.junhyounglee.archroid.compiler.codegen.*
import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror


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
//                    annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
//                        when {
//                            // @param view
//                            entry.key.simpleName.contentEquals("view") -> {
//                                // entry.value.value == List<ClassType>
//                                // val typeMirrors = entry.value.value as List<TypeMirror>
//                                // entry.value.value == ClassType
//                                val typeMirror = entry.value.value as TypeMirror
//                                warning("Archroid> MvpActivityView argument key: ${entry.key.simpleName}, value: ${entry.value.value}, interface(${isInterfaceType(typeMirror)}), subTypeOf(${isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)})")
//
//                                // interface that extends MvpView
//                                if (!isInterfaceType(typeMirror) || !isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)) {
//                                    error(entry.key, "@MvpActivityView's view parameter should be an interface that extends from MvpView.")
//                                    return@parseMvpActivityView null
//                                }
//
//                                // set view class argument
//                                val viewType = ClassName.bestGuess(getQualifiedTypeName(typeMirror))
//                                builder.setViewType(viewType)
//                            }
//
//                            // @param layoutResId
//                            entry.key.simpleName.contentEquals("layoutResId") -> {
//                                warning("Archroid> MvpActivityView argument key: ${entry.key.simpleName}, value: ${entry.value.value}")
//
//                                val layoutResId = entry.value.value as Int
//                                if (layoutResId == 0) {
//                                    error(entry.key, "@MvpActivityView's layoutResId parameter should be a valid resource id.")
//                                    return@parseMvpActivityView null
//                                }
//
//                                // set content layout resource id
//                                val contentViewId = getResourceIdentifier(annotatedType, annotationMirror, entry.value, entry.value.value as Int)
//                                builder.setContentView(contentViewId)
//                            }
//                        }
//                    }
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

//                    annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
//                        if (entry.key.simpleName.contentEquals("presenter")) {
//                            val typeMirror = entry.value.value as TypeMirror
//                            warning("Archroid> BindMvpPresenter argument key: ${entry.key.simpleName}, value: ${entry.value.value}")
//
//                            if (!isClassType(typeMirror)) {
//                                error(entry.key, "@BindMvpPresenter should have appropriate presenter class.")
//                                return@parseMvpActivityView null
//                            }
//
//                            val presenterType = typeMirror as DeclaredType
//                            val presenter = presenterType.asElement()
//
//
//                            // check if it's an abstract class
//                            if (presenter.modifiers.contains(Modifier.ABSTRACT)) {
//                                error(entry.key, "Abstract class ${presenter.simpleName} cannot be annotated with @BindMvpPresenter.")
//                                return@parseMvpActivityView null
//                            }
//
//                            val parent: TypeMirror = (presenterType.asElement() as TypeElement).superclass
//                            val parentType = (parent as DeclaredType)
//
//                            // check if the class extends from MvpPresenter<VIEW>
//                            if (!isSubTypeOfType(toTypeElement(parentType).asType(), MVP_PRESENTER_TYPE)) {
//                                error(entry.key, "Class ${presenter.simpleName} should extend from MvpPresenter for @BindMvpPresenter. current parent type is ${toTypeElement(parentType).asType()}")
//                                return@parseMvpActivityView null
//                            }
//
//                            // check if a public constructor containing MvpView inherited view as a parameter is given
//                            var found = false
//                            loop@ for (enclosed in presenter.enclosedElements) {
//                                if (enclosed.kind == ElementKind.CONSTRUCTOR) {
//                                    val constructor = enclosed as ExecutableElement
//                                    for (param: VariableElement in constructor.parameters) {
//                                        // has MvpView parameter
//                                        if (isSubTypeOfType(param.asType(), MVP_VIEW_TYPE)) {
//                                            found = true
//                                            break
//                                        }
//                                    }
//                                }
//                            }
//                            if (!found) {
//                                error(entry.key, "@BindMvpPresenter requires a constructor that contains a view extends from MvpView.")
//                                return@parseMvpActivityView null
//                            }
//
//                            // set presenter class argument
//                            builder.setPresenterType(ClassName.bestGuess(getQualifiedTypeName(typeMirror)))
//                        }
//                    }
                }
            }
        }

        if (!builder.isValid()) {
            error(annotatedType, "${annotatedType.simpleName} class requires annotation @MvpActivity and @BindMvpPresenter.")
            return null
        }

        val enclosing = annotatedType.enclosingElement
        warning("Archroid> parent qualifiedName: ${enclosing.kind}, element: ${getName(enclosing)}")

        annotatedType.enclosedElements.forEach {
            warning("Archroid> children, ${getName(it)}")
        }

        return builder.build()
    }

    private fun parseBindMvpPresenter(annotationMirror: AnnotationMirror): TypeMirror? {
        // annotation as element
        val element = annotationMirror.annotationType.asElement()
        val annotationName = annotationMirror.annotationType.asElement().simpleName

        if (!annotationName.contentEquals(BindMvpPresenter::class.simpleName)) {
            error(element, "Parsing invalid annotation. @BindMvpPresenter is only allowed. ")
            return null
        }

        annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
            if (entry.key.simpleName.contentEquals("presenter")) {
                val typeMirror = entry.value.value as TypeMirror
                warning("Archroid> BindMvpPresenter argument key: ${entry.key.simpleName}, value: ${entry.value.value}")

                if (!isClassType(typeMirror)) {
                    error(entry.key, "@BindMvpPresenter should have appropriate presenter class.")
                    return@parseBindMvpPresenter null
                }

                val presenterType = typeMirror as DeclaredType
                val presenter = presenterType.asElement()


                // check if it's an abstract class
                if (presenter.modifiers.contains(Modifier.ABSTRACT)) {
                    error(entry.key, "Abstract class ${presenter.simpleName} cannot be annotated with @BindMvpPresenter.")
                    return@parseBindMvpPresenter null
                }

                val parent: TypeMirror = (presenterType.asElement() as TypeElement).superclass
                val parentType = (parent as DeclaredType)

                // check if the class extends from MvpPresenter<VIEW>
                if (!isSubTypeOfType(toTypeElement(parentType).asType(), MVP_PRESENTER_TYPE)) {
                    error(entry.key, "Class ${presenter.simpleName} should extend from MvpPresenter for @BindMvpPresenter. current parent type is ${toTypeElement(parentType).asType()}")
                    return@parseBindMvpPresenter null
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
                    return@parseBindMvpPresenter null
                }

                return typeMirror
            }
        }
        return null
    }

    override fun onGenerateSourceFile(classArgument: ClassArgument) {
        val argument = classArgument as MvpViewClassArgument
        MvpActivityViewGenerator(filer).apply { generate(argument) }
    }

}