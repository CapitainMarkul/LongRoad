package ru.aar_generator.plugin.config.option.variant

class VariantOption(
    private var configPart: VariantOptionApi.Variable
) : VariantOptionApi {

    enum class Platform(val platformName: String) {
        DEBUG("Debug"), // TODO() <==== Эту потом удалить
        X86_64("X86_64"),
        ARM_64("Arm64"),
        ARM_7A("Armv7a"),
        MULTI("Multi")
    }

    override fun configureForDebugVariable() {
        configPart.targetPlatform = Platform.DEBUG
    }

    override fun configureForX86Variable() {
        configPart.targetPlatform = Platform.X86_64
    }

    override fun configureForArm64Variable() {
        configPart.targetPlatform = Platform.ARM_64
    }

    override fun configureForArm7AVariable() {
        configPart.targetPlatform = Platform.ARM_7A
    }

    override fun configureForMultiVariable() {
        configPart.targetPlatform = Platform.MULTI
    }
}