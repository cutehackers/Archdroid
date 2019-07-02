package app.junhyounglee.compiler

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableList
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment

//@AutoService(Processor::class)
//sealed class ArchdroidProcessor : AbstractProcessor() {
//
//    @Synchronized
//    override fun init(env: ProcessingEnvironment) {
//        super.init(env)
//
//        val filer = env.filer
//        val messager = env.messager
//        val elements = env.elementUtils
//        val types = env.typeUtils
//    }
//
//    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
//        return false
//    }
//
//    override fun getSupportedAnnotationTypes(): Set<String> = mutableSetOf<String>().apply {
//        add(MvpActivityView::class.java.canonicalName)
//    }
//
//    override fun getSupportedSourceVersion(): SourceVersion {
//        return SourceVersion.latestSupported()
//    }
//
//}

@AutoService(Processor::class)
sealed class ArchdroidProcessor : BasicAnnotationProcessor() {

    override fun initSteps(): ImmutableList<ProcessingStep> {
        val filer = processingEnv.filer
        val messager = processingEnv.messager
        val elements = processingEnv.elementUtils
        val types = processingEnv.typeUtils

        return ImmutableList.of(
        )
    }

    override fun postRound(roundEnv: RoundEnvironment) {


    }
}
