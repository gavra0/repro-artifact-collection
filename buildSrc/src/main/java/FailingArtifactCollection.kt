import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.file.FileCollection
import java.io.File
import java.util.concurrent.Callable
import java.util.function.Consumer


class FailingArtifactCollection(
        project: Project,
        mainArtifact: ArtifactCollection) : ArtifactCollection {

    private val fileCollection: FileCollection
    private val filterResolver: FilterResolver

    init {
        filterResolver = FilterResolver(mainArtifact)

        fileCollection = project.files(filterResolver)
                .builtBy(mainArtifact.artifactFiles.buildDependencies)
    }

    override fun getArtifactFiles() = fileCollection

    override fun getArtifacts() = filterResolver.getArtifactResults()

    override fun getFailures(): Collection<Throwable> {
        return filterResolver.mainArtifacts.failures
    }

    override fun iterator() = artifacts.iterator() as MutableIterator<ResolvedArtifactResult>
    override fun spliterator() = artifacts.spliterator()

    override fun forEach(action: Consumer<in ResolvedArtifactResult>) {
        artifacts.forEach(action)
    }

    private class FilterResolver(
            val mainArtifacts: ArtifactCollection) : Callable<Collection<File>> {

        fun getArtifactResults(): Set<ResolvedArtifactResult> {
            return mainArtifacts.artifacts.asSequence().toSet()
        }

        override fun call(): Collection<File>? {
            return mainArtifacts.artifacts.asSequence().map { it.file }.toSet()
        }
    }
}
