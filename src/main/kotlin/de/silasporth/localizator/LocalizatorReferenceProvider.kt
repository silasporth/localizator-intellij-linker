package de.silasporth.localizator

import com.intellij.lang.javascript.psi.*
import com.intellij.lang.javascript.psi.util.JSDestructuringUtil
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext

class LocalizatorReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element !is JSLiteralExpression || !element.isQuotedLiteral) return emptyArray()

        val key = element.stringValue ?: return emptyArray()
        if (key.isBlank()) return emptyArray()

        // First parent should be the argumentlist and then the function call
        val callExpression = element.parent.parent as? JSCallExpression ?: return emptyArray()
        if (callExpression.methodExpression == null) return emptyArray()

        val arguments = callExpression.arguments
        if (arguments.isEmpty() || arguments[0] != element) return emptyArray()

        val methodExpression = callExpression.methodExpression as? JSReferenceExpression ?: return emptyArray()

        // Find the useTranslation call where methodName comes from
        val resolved = methodExpression.resolve() ?: return emptyArray()
        val useTranslationMethod = findUseTranslationCall(resolved) ?: return emptyArray()
        val keyPrefix = findKeyPrefix(useTranslationMethod)

        val range = TextRange(1, element.textLength - 1) // exclude quotes
        return arrayOf(LocalizatorReference(element, range, key, keyPrefix))
    }

    private fun findUseTranslationCall(resolved: PsiElement): JSCallExpression? {
        // Expecting something like: const { t } = useTranslation('ns', { keyPrefix: 'prefix' })
        // resolved should be the variable 't'
        // Todo: support other ways the translation function could have been obtained
        val variable = resolved as? JSVariable ?: return null

        val callExpression =
            JSDestructuringUtil.getNearestDestructuringInitializer(variable) as? JSCallExpression ?: return null

        return if (callExpression.methodExpression?.text == "useTranslation") callExpression else null
    }

    private fun findKeyPrefix(useTranslationMethodElement: JSCallExpression): String? {
        val args = useTranslationMethodElement.arguments
        if (args.size < 2) return null

        val options = args[1] as? JSObjectLiteralExpression ?: return null
        val keyPrefixProperty = options.findProperty("keyPrefix") ?: return null
        val value = keyPrefixProperty.value as? JSLiteralExpression ?: return null
        return value.stringValue
    }
}
