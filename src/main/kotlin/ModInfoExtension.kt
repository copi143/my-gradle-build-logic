import org.gradle.api.Project
import javax.inject.Inject

open class ModInfoExtension @Inject constructor(project: Project) {
    val id: String = project.property("modId") as String
    val name: String = project.property("modName") as String
    val author: String = project.property("modAuthor") as String
    val license: String = project.property("license") as String
    val credits: String = project.property("credits") as String
    val description: String = project.property("description") as String
}
