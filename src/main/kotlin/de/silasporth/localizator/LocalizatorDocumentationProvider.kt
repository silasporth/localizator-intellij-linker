package de.silasporth.localizator

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement

class LocalizatorDocumentationProvider : PsiDocumentationTargetProvider {
    override fun documentationTarget(element: PsiElement, originalElement: PsiElement?): DocumentationTarget? {
        if (element !is LocalizatorFakePsiElement) return null

        return LocalizatorDocumentationTarget(element, originalElement)
    }
}
