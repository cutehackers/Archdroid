package app.junhyounglee.archdroid.compiler

import app.junhyounglee.archdroid.annotations.BindMvpPresenter
import app.junhyounglee.archdroid.annotations.MvpActivityView
import com.google.auto.common.SuperficialValidation
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeKind
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
 */
class MvpViewCoordinator(processingEnv: ProcessingEnvironment) : ArchCoordinator(processingEnv) {

    override fun process(roundEnv: RoundEnvironment): Boolean {
        // parse @MvpActivityView elements
        for (element: Element in roundEnv.getElementsAnnotatedWith(MvpActivityView::class.java)) {
            if (!SuperficialValidation.validateElement(element)) continue

            val annotatedType = element as TypeElement
            try {
                if (!parseMvpActivityView(annotatedType)) {
                    return true
                }
            } catch (e: Exception) {
                error(annotatedType, exception = e)
                return true
            }
        }

        return false
    }

    /**
     * @MvpActivityView(SampleView::class)
     * @BindMvpPresenter(SamplePresenter::class)
     * class SampleActivityView
     * @return true if parse MvpActivityView, BindMvpPresenter annotation successfully, otherwise
     * false
     */
    private fun parseMvpActivityView(annotatedType: TypeElement): Boolean {

        /*
         * Step 1. only class can be annotated with @MvpActivityView
         *  val elementType: TypeMirror = annotatedType.asType()
         */
        if (!annotatedType.kind.isClass) {
            error(annotatedType, "Only classes can be annotated with @MvpActivityView")
            return false
        }

        /*
         * Step 2. validate annotations for this class
         *  1. MvpActivityView
         *   @param view: interface that extends MvpView
         *  2. BindMvpPresenter
         *   @param presenter: should ba a class extending MvpPresenter that has a view extends
         *      MvpView
         */
        //val mvpActivityView: MvpActivityView = annotatedType.getAnnotation(MvpActivityView::class.java)
        //warning("Archdroid> annotation : ${getQualifiedTypeName(mvpActivityView)}")

        annotatedType.annotationMirrors.forEach { annotationMirror ->
            warning("Archdroid> annotation : ${annotationMirror.annotationType.asElement().simpleName}")

            val annotationName = annotationMirror.annotationType.asElement().simpleName
            when {
                // MvpActivityView annotation
                annotationName.contentEquals(MvpActivityView::class.simpleName) -> {
                    annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
                        if (entry.key.simpleName.contentEquals("view")) {
                            // entry.value.value == ClassType
                            val typeMirror = entry.value.value as TypeMirror
                            // entry.value.value == List<ClassType>
                            //val typeMirrors = entry.value.value as List<TypeMirror>
                            warning("Archdroid> MvpActivityView argument key: ${entry.key.simpleName}, value: ${entry.value.value}, interface(${isInterfaceType(typeMirror)}), subTypeOf(${isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)})")

                            // interface that extends MvpView
                            if (!isInterfaceType(typeMirror) || !isSubTypeOfType(typeMirror, MVP_VIEW_TYPE)) {
                                error(entry.key, "@MvpActivityView's view parameter should be an interface that extends from MvpView.")
                                return@parseMvpActivityView false
                            }
                        }
                    }
                }

                // BindMvpPresenter annotation
                annotationName.contentEquals(BindMvpPresenter::class.simpleName) -> {
                    annotationMirror.elementValues.forEach { entry: Map.Entry<ExecutableElement, AnnotationValue> ->
                        val typeMirror = entry.value.value as TypeMirror
                    }
                }
            }
        }

        // Step 3. validate BindMvpPresenter annotation.



        val packageName = elements.getPackageOf(annotatedType).run {
            if (isUnnamed) {
                null
            } else {
                qualifiedName
            }
        }
        warning("Archdroid> findTargetObjects(), packageName: $packageName, simpleName: ${annotatedType.simpleName}")

        val baseView = annotatedType.getAnnotation(MvpActivityView::class.java)
        warning("Archdroid> base view: $baseView")

        val enclosing = annotatedType.enclosingElement
        warning("Archdroid> parent qualifiedName: ${enclosing.kind}, element: ${getName(enclosing)}")

        annotatedType.enclosedElements.forEach {
            warning("Archdroid> children, ${getName(it)}")
        }

        return true
    }

    private fun isValidClass(annotationMirror: AnnotationMirror): Boolean {


        return true
    }

    private fun isInterfaceType(typeMirror: TypeMirror): Boolean {
        return typeMirror is DeclaredType && typeMirror.asElement().kind == ElementKind.INTERFACE
    }

    private fun isSubTypeOfType(typeMirror: TypeMirror, otherType: String): Boolean {
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

    private fun isTypeEqual(typeMirror: TypeMirror, otherType: String): Boolean {
        return otherType == typeMirror.toString()
    }

    private fun getQualifiedTypeName(annotation: Annotation): String? = try {
        (annotation as MvpActivityView).annotationClass.qualifiedName
    } catch (e: MirroredTypeException) {
        val classTypeMirror = e.typeMirror as DeclaredType
        val classTypeElement = classTypeMirror.asElement() as TypeElement
        classTypeElement.qualifiedName.toString()
    }

    private fun getSimpeTypeName(klass: Class<*>): String = try {
        klass.simpleName
    } catch (e: MirroredTypeException) {
        val classTypeMirror = e.typeMirror as DeclaredType
        val classTypeElement = classTypeMirror.asElement() as TypeElement
        classTypeElement.simpleName.toString()
    }


    companion object {
        private const val MVP_VIEW_TYPE = "app.junhyounglee.archdroid.runtime.core.view.MvpView"
    }
}