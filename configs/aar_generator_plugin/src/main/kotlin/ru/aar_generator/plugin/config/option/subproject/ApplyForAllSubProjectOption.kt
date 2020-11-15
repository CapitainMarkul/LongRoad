package ru.aar_generator.plugin.config.option.subproject

class ApplyForAllSubProjectOption(
    private var configPart: ApplyForAllSubProjectOptionApi.Variable
) : ApplyForAllSubProjectOptionApi {

    override fun enable() {
        configPart.applyForAllSubProjects = true
    }

    override fun disable() {
        configPart.applyForAllSubProjects = false
    }
}