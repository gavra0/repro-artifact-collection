import org.gradle.api.artifacts.transform.ArtifactTransform
import java.io.File
import java.io.IOException


class JarTransform : ArtifactTransform() {

    override fun transform(file: File): List<File> {
        val outputDirectory = outputDirectory
        outputDirectory.mkdirs()

        try {
            if (!file.exists()) {
                throw RuntimeException("Error: $file does not exist")
            }
            println("Processing file: $file")
            return listOf(file)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }
}
