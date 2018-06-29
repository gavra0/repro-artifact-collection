import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.artifacts.ArtifactAttributes
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

class MyPlugin : Plugin<Project> {
    override fun apply(project: Project?) {
        project as Project

        project.dependencies.registerTransform { reg ->
            reg.from.attribute(ArtifactAttributes.ARTIFACT_FORMAT, "jar")
            reg.to.attribute(ArtifactAttributes.ARTIFACT_FORMAT, "my-format")
            reg.artifactTransform(JarTransform::class.java)
        }

        val artifactView =
                project
                        .configurations
                        .getByName("compile")
                        .incoming.artifactView {
                    it.attributes.attribute(ArtifactAttributes.ARTIFACT_FORMAT, "my-format")
                }.artifacts

        val forSuccess = WorkingArtifactCollection(project, artifactView)
        val forFailure = FailingArtifactCollection(project, artifactView)

        project.tasks.create("success", MyTask::class.java, { myTask -> myTask.inputs = forSuccess.artifactFiles })
        project.tasks.create("fail", MyTask::class.java, { myTask -> myTask.inputs = forFailure.artifactFiles })
    }
}

open class MyTask : DefaultTask() {
    @InputFiles
    var inputs: FileCollection? = null

    @TaskAction
    fun run() {
        println(inputs!!.files.map { "\n" + it })
    }
}