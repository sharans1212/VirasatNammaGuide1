package com.example.virasatnammaguide

data class HeritageSite(
    val id: String,
    val nameEn: String,
    val nameKn: String,
    val distanceKm: Double,
    val summaryEn: String,
    val summaryKn: String,
    val descriptionEn: String,
    val descriptionKn: String,
    val architectureEn: String,
    val architectureKn: String,
    val legendEn: String,
    val legendKn: String,
    val hiddenFactEn: String,
    val hiddenFactKn: String,
    val imageUrl: String
) {
    fun name(kannada: Boolean) = if (kannada) nameKn else nameEn
    fun summary(kannada: Boolean) = if (kannada) summaryKn else summaryEn
    fun description(kannada: Boolean) = if (kannada) descriptionKn else descriptionEn
    fun architecture(kannada: Boolean) = if (kannada) architectureKn else architectureEn
    fun legend(kannada: Boolean) = if (kannada) legendKn else legendEn
    fun hiddenFact(kannada: Boolean) = if (kannada) hiddenFactKn else hiddenFactEn
}
