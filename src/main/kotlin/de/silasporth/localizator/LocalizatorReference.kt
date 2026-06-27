package de.silasporth.localizator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class LocalizatorReference(element: PsiElement, range: TextRange, val key: String, val keyPrefix: String?) :
    PsiReferenceBase<PsiElement>(element, range) {

    override fun resolve() = LocalizatorFakePsiElement(element, key, keyPrefix)

    override fun bindToElement(element: PsiElement) = element
}
