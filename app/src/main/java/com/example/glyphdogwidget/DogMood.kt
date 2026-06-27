package com.example.glyphdogwidget

enum class DogMood(
    val label: String,
    val dogArt: String,
    val glyphBrightness: Int,   // 0–4095
    val glyphPulse: Boolean
) {
    ENERGETIC(
        label = "Energetic! ⚡",
        dogArt = "=^o^=\n /|\\ \n/ | \\",
        glyphBrightness = 4095,
        glyphPulse = true
    ),
    HAPPY(
        label = "Happy :)",
        dogArt = "(^‿^)\n /|\\ \n/ | \\",
        glyphBrightness = 3000,
        glyphPulse = false
    ),
    CALM(
        label = "Calm ..",
        dogArt = "(•_•)\n /|\\ \n/ | \\",
        glyphBrightness = 1800,
        glyphPulse = false
    ),
    TIRED(
        label = "Tired ~",
        dogArt = "(-_-)\n /|\\ \n/ | \\",
        glyphBrightness = 800,
        glyphPulse = false
    ),
    EXHAUSTED(
        label = "Exhausted zzz",
        dogArt = "(x_x)\n /|\\ \n/ | \\",
        glyphBrightness = 200,
        glyphPulse = false
    );

    companion object {
        fun fromBattery(percent: Int): DogMood = when {
            percent >= 80 -> ENERGETIC
            percent >= 60 -> HAPPY
            percent >= 40 -> CALM
            percent >= 20 -> TIRED
            else          -> EXHAUSTED
        }
    }
}
