package app.junhyounglee.archroid.compiler

import app.junhyounglee.archroid.compiler.codegen.MvpViewClassArgument
import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

abstract class MvpBaseCoordinator(processingEnv: ProcessingEnvironment, klassType: Class<out Annotation>)
    : ArchCoordinator(processingEnv, klassType) {

    protected fun parseMvpView(
        annotatedType: TypeElement,
        builder: MvpViewClassArgument.Builder,
        annotationMirror: AnnotationMirror
    ): Boolean {
        val annotationName = annotationMirror.annotationType.asElement().simpleName

        annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
            when {
                // @param view
                entry.key.simpleName.contentEquals("view") -> {
                    getViewType(annotationName, entry.toPair())?.also { viewType: ClassName ->
                        builder.setViewType(viewType)
                    }
//                    val typeMirror = entry.value.value as TypeMirror
//                    warning("Archroid> $annotationName argument key: ${entry.key.simpleName}, value: ${entry.value.value}, interface(${isInterfaceType(typeMirror)}), subTypeOf(${isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)})")
//
//                    // interface that extends MvpView
//                    if (!isInterfaceType(typeMirror) || !isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)) {
//                        error(entry.key, "$annotationName's view parameter should be an interface that extends from MvpView.")
//                        return false
//                    }
//
//                    // set view class argument
//                    val viewType = ClassName.bestGuess(getQualifiedTypeName(typeMirror))
//                    builder.setViewType(viewType)
                }

                // @param layoutResId
                entry.key.simpleName.contentEquals("layoutResId") -> {
                    warning("Archroid> $annotationName argument key: ${entry.key.simpleName}, value: ${entry.value.value}")

                    val layoutResId = entry.value.value as Int
                    if (layoutResId == 0) {
                        error(entry.key, "$annotationName's layoutResId parameter should be a valid resource id.")
                        return false
                    }

                    // set content layout resource id
                    val contentViewId = getResourceIdentifier(annotatedType, annotationMirror, entry.value, entry.value.value as Int)
                    builder.setContentView(contentViewId)
                }
            }
        }

        return true
    }

    protected fun getViewType(annotationName: Name, entry: Pair<ExecutableElement, AnnotationValue>): ClassName? {
        val typeMirror = entry.second.value as TypeMirror
        warning("Archroid> $annotationName argument key: ${entry.first.simpleName}, value: ${entry.second.value}, interface(${isInterfaceType(typeMirror)}), subTypeOf(${isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)})")

        // interface that extends MvpView
        if (!isInterfaceType(typeMirror) || !isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)) {
            error(entry.first, "$annotationName's view parameter should be an interface that extends from MvpView.")
            return null
        }

        // view class
        return ClassName.bestGuess(getQualifiedTypeName(typeMirror))
    }

    protected fun parseMvpPresenter(
        annotatedType: TypeElement,
        builder: MvpViewClassArgument.Builder,
        annotationMirror: AnnotationMirror
    ): Boolean {
        val annotationName = annotationMirror.annotationType.asElement().simpleName

        annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
            if (entry.key.simpleName.contentEquals("presenter")) {
                getPresenterType(annotationName, entry.toPair())?.also { presenterType: ClassName ->
                    builder.setPresenterType(presenterType)
                }
//                val typeMirror = entry.value.value as TypeMirror
//                warning("Archroid> $annotationName argument key: ${entry.key.simpleName}, value: ${entry.value.value}")
//
//                if (!isClassType(typeMirror)) {
//                    error(entry.key, "$annotationName should have appropriate presenter class.")
//                    return@parseMvpPresenter false
//                }
//
//                val presenterType = typeMirror as DeclaredType
//                val presenter = presenterType.asElement()
//
//
//                // check if it's an abstract class
//                if (presenter.modifiers.contains(Modifier.ABSTRACT)) {
//                    error(entry.key, "Abstract class ${presenter.simpleName} cannot be annotated with $annotationName.")
//                    return@parseMvpPresenter false
//                }
//
//                val parent: TypeMirror = (presenterType.asElement() as TypeElement).superclass
//                val parentType = (parent as DeclaredType)
//
//                // check if the class extends from MvpPresenter<VIEW>
//                if (!isSubTypeOfType(toTypeElement(parentType).asType(), ABS_MVP_PRESENTER_TYPE)) {
//                    error(entry.key, "Class ${presenter.simpleName} should extend from AbsMvpPresenter for $annotationName. current parent type is ${toTypeElement(parentType).asType()}")
//                    return@parseMvpPresenter false
//                }
//
//                // check if a public constructor has a view parameter that extends from MvpView
//                var found = false
//                loop@ for (enclosed in presenter.enclosedElements) {
//                    if (enclosed.kind == ElementKind.CONSTRUCTOR) {
//                        val constructor = enclosed as ExecutableElement
//                        for (param: VariableElement in constructor.parameters) {
//                            // has MvpView parameter
//                            if (isSubTypeOfType(param.asType(), MVP_VIEW_TYPE)) {
//                                found = true
//                                break
//                            }
//                        }
//                    }
//                }
//                if (!found) {
//                    error(entry.key, "$annotationName requires a constructor that contains a view extends from MvpView.")
//                    return@parseMvpPresenter false
//                }
//
//                // set presenter class argument
//                builder.setPresenterType(ClassName.bestGuess(getQualifiedTypeName(typeMirror)))
            }
        }

        return true
    }

    /**
     * @param abstractCheck if true, this will check if a given presenter class abstract and returns
     * false if it's abstract? default value is true.
     */
    protected fun getPresenterType(
        annotationName: Name,
        entry: Pair<ExecutableElement, AnnotationValue>,
        abstractCheck: Boolean = true
    ): ClassName? {
        val typeMirror = entry.second.value as TypeMirror
        warning("Archroid> $annotationName argument key: ${entry.first.simpleName}, value: ${entry.second.value}")

        if (!isClassType(typeMirror)) {
            error(entry.first, "$annotationName should have appropriate presenter class.")
            return null
        }

        val presenterType = typeMirror as DeclaredType
        val presenter = presenterType.asElement()

        // check if it's an abstract class
        if (abstractCheck) {
            if (presenter.modifiers.contains(Modifier.ABSTRACT)) {
                error(entry.first, "Abstract class ${presenter.simpleName} cannot be annotated with $annotationName.")
                return null
            }
        }

        val parent: TypeMirror = (presenterType.asElement() as TypeElement).superclass
        val parentType = (parent as DeclaredType)

        // check if the class extends from MvpPresenter<VIEW>
        if (!isSubTypeOfType(toTypeElement(parentType).asType(), ABS_MVP_PRESENTER_TYPE)) {
            error(entry.first, "Class ${presenter.simpleName} should extend from AbsMvpPresenter for $annotationName. current parent type is ${toTypeElement(parentType).asType()}")
            return null
        }

        // check if a public constructor has a view parameter that extends from MvpView
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
            error(entry.first, "$annotationName requires a constructor that contains a view extends from MvpView.")
            return null
        }

        // presenter class
        return ClassName.bestGuess(getQualifiedTypeName(typeMirror))
    }

}