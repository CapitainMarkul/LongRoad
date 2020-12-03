package ru.aar_generator.plugin.config.option.milestones

class MilestonesVersionOption(
    private var configPart: MilestonesVersionOptionApi.Variable
) : MilestonesVersionOptionApi {

    override fun setMilestonesVersion(version: String) {
        configPart.milestonesVersion = version
    }
}