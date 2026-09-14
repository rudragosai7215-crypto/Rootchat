package com.example.ui.screens.caseflow

/**
 * Classical Repertory chapter organization model adhering to Dr. J.T. Kent's
 * classical 33-chapter hierarchy with nested anatomical sub-sections and
 * dynamic grouping of extended/supplementary clinical rubrics.
 */
data class BrowseSubSection(
    val title: String,
    val rawChapters: List<String>
)

data class BrowseChapterItem(
    val id: String,
    val orderNumber: Int?, // 1..33 for Section 1 (Classical Kent), null for Section 2
    val title: String,
    val rawChapters: List<String>,
    val subSections: List<BrowseSubSection> = emptyList(),
    val isClassical: Boolean = true
) {
    val isComposite: Boolean get() = subSections.isNotEmpty()
}

object ClassicalRepertoryCatalog {
    /**
     * Exact 33 Classical Chapters in Dr. Kent's original order
     */
    val CLASSICAL_SPECS: List<BrowseChapterItem> = listOf(
        BrowseChapterItem(
            id = "mind",
            orderNumber = 1,
            title = "Mind",
            rawChapters = listOf("MIND")
        ),
        BrowseChapterItem(
            id = "vertigo",
            orderNumber = 2,
            title = "Vertigo",
            rawChapters = listOf("VERTIGO")
        ),
        BrowseChapterItem(
            id = "head",
            orderNumber = 3,
            title = "Head",
            rawChapters = listOf("HEAD")
        ),
        BrowseChapterItem(
            id = "eye",
            orderNumber = 4,
            title = "Eye",
            rawChapters = listOf("EYE")
        ),
        BrowseChapterItem(
            id = "vision",
            orderNumber = 5,
            title = "Vision",
            rawChapters = listOf("VISION")
        ),
        BrowseChapterItem(
            id = "ear",
            orderNumber = 6,
            title = "Ear",
            rawChapters = listOf("EAR")
        ),
        BrowseChapterItem(
            id = "hearing",
            orderNumber = 7,
            title = "Hearing",
            rawChapters = listOf("HEARING")
        ),
        BrowseChapterItem(
            id = "nose",
            orderNumber = 8,
            title = "Nose",
            rawChapters = listOf("NOSE")
        ),
        BrowseChapterItem(
            id = "face",
            orderNumber = 9,
            title = "Face",
            rawChapters = listOf("FACE")
        ),
        BrowseChapterItem(
            id = "mouth",
            orderNumber = 10,
            title = "Mouth",
            rawChapters = listOf("MOUTH")
        ),
        BrowseChapterItem(
            id = "teeth",
            orderNumber = 11,
            title = "Teeth",
            rawChapters = listOf("TEETH")
        ),
        BrowseChapterItem(
            id = "throat",
            orderNumber = 12,
            title = "Throat",
            rawChapters = listOf("THROAT")
        ),
        BrowseChapterItem(
            id = "external_throat",
            orderNumber = 13,
            title = "External Throat",
            rawChapters = listOf("EXTERNAL THROAT")
        ),
        BrowseChapterItem(
            id = "stomach",
            orderNumber = 14,
            title = "Stomach",
            rawChapters = listOf("STOMACH", "APPETITE"),
            subSections = listOf(
                BrowseSubSection("All Stomach & Appetite", listOf("STOMACH", "APPETITE")),
                BrowseSubSection("Stomach (Main)", listOf("STOMACH")),
                BrowseSubSection("Appetite", listOf("APPETITE"))
            )
        ),
        BrowseChapterItem(
            id = "abdomen",
            orderNumber = 15,
            title = "Abdomen",
            rawChapters = listOf("ABDOMEN")
        ),
        BrowseChapterItem(
            id = "rectum",
            orderNumber = 16,
            title = "Rectum",
            rawChapters = listOf("RECTUM")
        ),
        BrowseChapterItem(
            id = "stool",
            orderNumber = 17,
            title = "Stool",
            rawChapters = listOf("STOOL")
        ),
        BrowseChapterItem(
            id = "urinary_organs",
            orderNumber = 18,
            title = "Urinary Organs",
            rawChapters = listOf("BLADDER", "KIDNEYS", "PROSTATE GLAND", "URETHRA", "URINE"),
            subSections = listOf(
                BrowseSubSection("All Urinary Organs", listOf("BLADDER", "KIDNEYS", "PROSTATE GLAND", "URETHRA", "URINE")),
                BrowseSubSection("Bladder", listOf("BLADDER")),
                BrowseSubSection("Kidneys", listOf("KIDNEYS")),
                BrowseSubSection("Prostate Gland", listOf("PROSTATE GLAND")),
                BrowseSubSection("Urethra", listOf("URETHRA")),
                BrowseSubSection("Urine", listOf("URINE"))
            )
        ),
        BrowseChapterItem(
            id = "male_genitalia",
            orderNumber = 19,
            title = "Male Genitalia",
            rawChapters = listOf("GENITALIA MALE")
        ),
        BrowseChapterItem(
            id = "female_genitalia",
            orderNumber = 20,
            title = "Female Genitalia",
            rawChapters = listOf("GENITALIA FEMALE")
        ),
        BrowseChapterItem(
            id = "larynx_and_trachea",
            orderNumber = 21,
            title = "Larynx and Trachea",
            rawChapters = listOf("LARYNX AND TRACHEA", "LARYNX & TRACHEA")
        ),
        BrowseChapterItem(
            id = "respiration",
            orderNumber = 22,
            title = "Respiration",
            rawChapters = listOf("RESPIRATION")
        ),
        BrowseChapterItem(
            id = "cough",
            orderNumber = 23,
            title = "Cough",
            rawChapters = listOf("COUGH")
        ),
        BrowseChapterItem(
            id = "expectoration",
            orderNumber = 24,
            title = "Expectoration",
            rawChapters = listOf("EXPECTORATION")
        ),
        BrowseChapterItem(
            id = "chest",
            orderNumber = 25,
            title = "Chest",
            rawChapters = listOf("CHEST", "HEART & CIRCULATION"),
            subSections = listOf(
                BrowseSubSection("All Chest", listOf("CHEST", "HEART & CIRCULATION")),
                BrowseSubSection("Chest (Main)", listOf("CHEST")),
                BrowseSubSection("Heart & Circulation", listOf("HEART & CIRCULATION"))
            )
        ),
        BrowseChapterItem(
            id = "back",
            orderNumber = 26,
            title = "Back",
            rawChapters = listOf("BACK")
        ),
        BrowseChapterItem(
            id = "extremities",
            orderNumber = 27,
            title = "Extremities",
            rawChapters = listOf("EXTREMITIES")
        ),
        BrowseChapterItem(
            id = "sleep",
            orderNumber = 28,
            title = "Sleep",
            rawChapters = listOf("SLEEP")
        ),
        BrowseChapterItem(
            id = "chill",
            orderNumber = 29,
            title = "Chill",
            rawChapters = listOf("CHILL")
        ),
        BrowseChapterItem(
            id = "fever",
            orderNumber = 30,
            title = "Fever",
            rawChapters = listOf("FEVER")
        ),
        BrowseChapterItem(
            id = "perspiration",
            orderNumber = 31,
            title = "Perspiration",
            rawChapters = listOf("PERSPIRATION")
        ),
        BrowseChapterItem(
            id = "skin",
            orderNumber = 32,
            title = "Skin",
            rawChapters = listOf("SKIN")
        ),
        BrowseChapterItem(
            id = "generalities",
            orderNumber = 33,
            title = "Generalities",
            rawChapters = listOf("GENERALITIES")
        )
    )

    val ADDITIONAL_SPECS: List<BrowseChapterItem> = listOf(
        BrowseChapterItem(
            id = "blood",
            orderNumber = null,
            title = "Blood",
            rawChapters = listOf("BLOOD", "CIRCULATION"),
            isClassical = false
        ),
        BrowseChapterItem(
            id = "clinical",
            orderNumber = null,
            title = "Clinical",
            rawChapters = listOf("CLINICAL", "GENERAL"),
            isClassical = false
        )
    )

    fun buildSections(allDbChapters: List<String>): Pair<List<BrowseChapterItem>, List<BrowseChapterItem>> {
        val dbUpperSet = allDbChapters.map { it.uppercase().trim() }.toSet()

        // Section 1: Classical 33 chapters
        val classicalItems = CLASSICAL_SPECS.map { spec ->
            val availableRaw = spec.rawChapters.filter { dbUpperSet.contains(it.uppercase()) }
            val availableSubSections = if (spec.subSections.isNotEmpty()) {
                spec.subSections.mapNotNull { sub ->
                    val subRaw = sub.rawChapters.filter { dbUpperSet.contains(it.uppercase()) }
                    if (subRaw.isNotEmpty()) sub.copy(rawChapters = subRaw) else null
                }
            } else emptyList()

            spec.copy(
                rawChapters = if (availableRaw.isNotEmpty()) availableRaw else spec.rawChapters,
                subSections = if (availableSubSections.isNotEmpty()) availableSubSections else spec.subSections
            )
        }

        // Raw chapters in Section 1
        val classicalRawSet = CLASSICAL_SPECS.flatMap { it.rawChapters }.map { it.uppercase() }.toSet()

        // Section 2: Blood, Clinical and any remaining chapters in DB
        val otherDbItems = allDbChapters
            .filter { !classicalRawSet.contains(it.uppercase().trim()) && it.uppercase().trim() != "BLOOD" && it.uppercase().trim() != "CLINICAL" }
            .sorted()
            .map { raw ->
                val formattedName = raw.lowercase().split(" ")
                    .joinToString(" ") { word -> word.replaceFirstChar { char -> char.uppercase() } }
                BrowseChapterItem(
                    id = "add_${raw.lowercase().replace(" ", "_")}",
                    orderNumber = null,
                    title = formattedName,
                    rawChapters = listOf(raw),
                    isClassical = false
                )
            }

        val additionalItems = ADDITIONAL_SPECS.map { spec ->
            val availableRaw = spec.rawChapters.filter { dbUpperSet.contains(it.uppercase()) }
            spec.copy(rawChapters = if (availableRaw.isNotEmpty()) availableRaw else spec.rawChapters)
        } + otherDbItems

        return Pair(classicalItems, additionalItems)
    }
}
