import org.gradle.language.base.plugins.LifecycleBasePlugin.CLEAN_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME

plugins {
    base
}

plugins.withId("base") {
    val buildTask = tasks.named(BUILD_TASK_NAME).callSubprojectTasks(BUILD_TASK_NAME)
    tasks.named(CHECK_TASK_NAME).callSubprojectTasks(CHECK_TASK_NAME)
    tasks.named(CLEAN_TASK_NAME).callSubprojectTasks(CLEAN_TASK_NAME)
    tasks.named(ASSEMBLE_TASK_NAME).callSubprojectTasks(ASSEMBLE_TASK_NAME)
    tasks.register("release", DefaultTask::class.java).configure {
        group = BUILD_GROUP
        description = "Assembles, tests, and publishes this project."
        dependsOn(buildTask)
        mustRunAfter(buildTask.get())
    }
}

private fun <T: Task> TaskProvider<T>.callSubprojectTasks(vararg dependOnTasks: String, configure: ((task: Task) -> Unit)? = null): TaskProvider<T> {
    this.configure {
        if (configure != null) {
            configure(this)
        }
        this.project.subprojects.forEach { project ->
            for (dependOnTask in dependOnTasks) {
                project.tasks.findByName(dependOnTask)?.let {
                    dependsOn(it)
                    mustRunAfter(it)
                }
            }
        }
    }
    return this
}
