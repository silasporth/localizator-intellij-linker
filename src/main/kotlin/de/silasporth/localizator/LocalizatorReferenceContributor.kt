package de.silasporth.localizator

import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar

class LocalizatorReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val pattern = PlatformPatterns.psiElement(JSLiteralExpression::class.java)
        val provider = LocalizatorReferenceProvider()

        registrar.registerReferenceProvider(pattern, provider)
    }
}
