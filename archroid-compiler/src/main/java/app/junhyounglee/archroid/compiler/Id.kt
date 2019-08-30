package app.junhyounglee.archroid.compiler

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

import com.sun.tools.javac.code.Symbol

/**
 * Represents an ID of an Android resource.
 * copy of butterknife.compiler.Id class
 */
class Id @JvmOverloads constructor(val value: Int, rSymbol: Symbol? = null) {

    val code: CodeBlock
    val qualifed: Boolean

    init {
        if (rSymbol != null) {
            val className = ClassName(rSymbol.packge().qualifiedName.toString(), R,
                rSymbol.enclClass().name.toString())
            val resourceName = rSymbol.name.toString()

            this.code = if (className.topLevelClassName() == ANDROID_R)
                CodeBlock.of("%L.%N", className, resourceName)
            else
                CodeBlock.of("%T.%N", className, resourceName)
            this.qualifed = true
        } else {
            this.code = CodeBlock.of("%L", value)
            this.qualifed = false
        }
    }

    override fun equals(o: Any?): Boolean {
        return o is Id && value == o.value
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String {
        throw UnsupportedOperationException("Please use value or code explicitly")
    }

    companion object {
        private val ANDROID_R = ClassName("android", "R")
        private val R = "R"
    }
}
