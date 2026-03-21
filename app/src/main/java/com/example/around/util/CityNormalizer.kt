package com.example.around.util

object CityNormalizer {

    fun canonical(city: String?): String {
        val normalized = city?.trim().orEmpty()

        return when (normalized.lowercase()) {
            "hod hasharon",
            "hod-ha-sharon",
            "hod ha sharon",
            "hod hasharon",
            "hod hasharon israel",
            "hod hasharon, israel",
            "hod hasharon israel",
            "hod hasharon, israel" -> "Hod Hasharon"

            "tel aviv",
            "tel-aviv",
            "tel aviv yafo",
            "tel aviv-yafo",
            "tel-aviv-yafo",
            "tel aviv israel",
            "tel aviv, israel" -> "Tel Aviv"

            "petah tikva",
            "petah-tikva",
            "petah tikwa",
            "petah-tikwa" -> "Petah Tikva"

            "rishon lezion",
            "rishon le zion",
            "rishon-lezion" -> "Rishon LeZion"

            else -> normalized
        }
    }
}