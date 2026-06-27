package de.silasporth.localizator

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class LocalizatorFakePsiElement(val parentElement: PsiElement, val key: String, val keyPrefix: String?) :
    FakePsiElement(), Navigatable {

    override fun getParent(): PsiElement = parentElement

    override fun getName(): String = key

    override fun navigate(requestFocus: Boolean) = openInLocalizator()

    override fun canNavigate(): Boolean = true

    private fun openInLocalizator() {
        val virtualFile = parentElement.containingFile?.virtualFile ?: return
        val filePath = virtualFile.path

        val encodedFilePath = URLEncoder.encode(filePath, StandardCharsets.UTF_8)
        val encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
        val query = StringBuilder("file=$encodedFilePath&key=$encodedKey")

        keyPrefix?.let {
            query.append("&prefix=").append(URLEncoder.encode(it, StandardCharsets.UTF_8))
        }

        val url = "localizator://open?$query"

        try {
            val osName = System.getProperty("os.name")?.lowercase()
            val command = when {
                osName?.contains("mac") == true -> arrayOf("open", url)
                osName?.contains("win") == true -> arrayOf("cmd", "/c", "start", "", url)
                osName?.contains("linux") == true -> arrayOf("xdg-open", url)
                else -> throw RuntimeException("Unsupported OS: $osName")
            }

            LOG.info("Executing command: ${command.joinToString(" ")}")
            val process = ProcessBuilder(*command).start()

            // Check if the process exited with an error quickly
            if (process.waitFor(500, java.util.concurrent.TimeUnit.MILLISECONDS) && process.exitValue() != 0) {
                throw RuntimeException("Command failed with exit code ${process.exitValue()}")
            }
            return

        } catch (e: Exception) {
            LOG.warn("Direct OS execution failed for $url, falling back to BrowserUtil", e)
            BrowserUtil.browse(url)
        }
    }

    companion object {
        private val LOG = Logger.getInstance(LocalizatorFakePsiElement::class.java)
    }
}