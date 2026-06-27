package de.silasporth.localizator

import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.createSmartPointer

class LocalizatorDocumentationTarget(
    val element: LocalizatorFakePsiElement,
    val originalElement: PsiElement?
) : DocumentationTarget {
    override fun computePresentation(): TargetPresentation {
        return TargetPresentation.builder("Localizator App Link")
            .presentation()
    }

    override fun createPointer(): Pointer<out DocumentationTarget> {
        val elementPtr = element.createSmartPointer()
        val originalElementPtr = originalElement?.createSmartPointer()
        return Pointer {
            val element = elementPtr.dereference() ?: return@Pointer null
            LocalizatorDocumentationTarget(element, originalElementPtr?.dereference())
        }
    }

    override fun computeDocumentationHint(): String {
        val fullKey = if (element.keyPrefix != null) element.keyPrefix + "." + element.key else element.key
        return "Open '$fullKey' in Localizator"
    }
}