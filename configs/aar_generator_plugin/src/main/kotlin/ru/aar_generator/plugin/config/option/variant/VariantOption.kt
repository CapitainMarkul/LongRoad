package ru.aar_generator.plugin.config.option.variant

import ru.aar_generator.plugin.config.option.variant.VariantOptionApi.Platform

class VariantOption(
    private var configPart: VariantOptionApi.Variable
) : VariantOptionApi {

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