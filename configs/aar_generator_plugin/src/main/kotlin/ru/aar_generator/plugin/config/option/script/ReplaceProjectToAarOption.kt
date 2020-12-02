package ru.aar_generator.plugin.config.option.script

class ReplaceProjectToAarOption(
    private var configPart: ReplaceProjectToAarOptionApi.Variable
) : ReplaceProjectToAarOptionApi {

    override fun enable() {
        configPart.needRunReplaceProjectToAarScript = true
    }

    override fun disable() {
        configPart.needRunReplaceProjectToAarScript = false
    }
}