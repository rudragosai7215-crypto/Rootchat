package com.example.data.local

import com.example.data.model.Remedy

object MateriaMedicaDataset {

  val remedies: List<Remedy> = listOf(
    Remedy(
      abbreviation = "Nux-v",
      fullName = "Nux Vomica",
      commonName = "Poison Nut",
      kingdom = "Plant (Loganiaceae)",
      thermal = "Chilly",
      primaryMiasm = "Psora",
      keynotes = listOf(
        "Hyper-irritable, quarrelsome, oversensitive to noise, light, odors",
        "Ineffectual urging to stool with incomplete sensation",
        "Drowsy after meals, wakes 3 a.m. thinking of business",
        "Ailments from modern sedentary life, stimulants, alcohol, over-study"
      ),
      modalities = "< Morning, cold air, mental exertion, spices. > Warmth, evening, uninterrupted rest.",
      clinicalUses = "Gastritis, dyspepsia, migraine, acute hangover, chronic constipation, irritable bowel."
    ),
    Remedy(
      abbreviation = "Ars",
      fullName = "Arsenicum Album",
      commonName = "Arsenious Acid",
      kingdom = "Mineral",
      thermal = "Chilly",
      primaryMiasm = "Psora",
      keynotes = listOf(
        "Agonizing anxiety, restlessness, fear of death",
        "Burning pains relieved by heat, thirst for small sips of water",
        "Prostration out of proportion to illness",
        "Worse midnight to 2 a.m., fastidious disposition"
      ),
      modalities = "< Midnight (1-2 a.m.), cold air, cold food/drinks. > Heat, warm drinks, head elevated.",
      clinicalUses = "Food poisoning, acute gastroenteritis, bronchial asthma, urticaria, gangrene."
    ),
    Remedy(
      abbreviation = "Bry",
      fullName = "Bryonia Alba",
      commonName = "Wild Hops / White Bryony",
      kingdom = "Plant (Cucurbitaceae)",
      thermal = "Hot",
      primaryMiasm = "Psora",
      keynotes = listOf(
        "Extreme aggravation from slightest motion, better absolute rest",
        "Intense thirst for large quantities of cold water at long intervals",
        "Dryness of all mucous membranes, stitching pains in serous membranes",
        "Irritable, talks of business during delirium, desires to go home"
      ),
      modalities = "< Motion, exertion, warmth. > Absolute rest, pressure, lying on painful side.",
      clinicalUses = "Pleurisy, acute bronchitis, serous arthritis, appendicitis, bursting headache."
    ),
    Remedy(
      abbreviation = "Bell",
      fullName = "Belladonna",
      commonName = "Deadly Nightshade",
      kingdom = "Plant (Solanaceae)",
      thermal = "Hot",
      primaryMiasm = "Psora",
      keynotes = listOf(
        "Suddenness and violent intensity of onset",
        "Red face, dilated pupils, throbbing carotids, hot skin",
        "Delirium with hallucinations, biting, striking",
        "Hypersensitive to touch, light, noise, and jarring"
      ),
      modalities = "< Touch, jarring, light, lying down. > Semi-erect posture, bending backward.",
      clinicalUses = "Acute tonsillitis, high inflammatory fever, sunstroke, congestive migraine, otitis media."
    ),
    Remedy(
      abbreviation = "Puls",
      fullName = "Pulsatilla Nigricans",
      commonName = "Wind Flower",
      kingdom = "Plant (Ranunculaceae)",
      thermal = "Hot",
      primaryMiasm = "Sycosis",
      keynotes = listOf(
        "Mild, gentle, weeping disposition, craves and ameliorated by consolation",
        "Changeable, shifting symptoms; thirstless in all complaints",
        "Thick, bland, yellowish-green discharges",
        "Intolerant of warm, closed rooms; craves cool open air"
      ),
      modalities = "< Warm room, rich fat food, evening. > Cool open air, slow walking, cold applications.",
      clinicalUses = "Amenorrhoea, otitis media, conjunctivitis, indigestion from fatty pastries, varicose veins."
    ),
    Remedy(
      abbreviation = "Sulph",
      fullName = "Sulphur",
      commonName = "Sublimed Sulphur",
      kingdom = "Mineral",
      thermal = "Hot",
      primaryMiasm = "Psora",
      keynotes = listOf(
        "The great King of Anti-Psoric remedies, standing between health and disease",
        "Burning sensations in palms, soles (sticks feet out of bed), and vertex",
        "Morning diarrhea driving out of bed at 5 a.m.",
        "Dirty, untidy skin; aversion to bathing which aggravates all complaints"
      ),
      modalities = "< Warmth of bed, washing, 11 a.m. (hunger), standing. > Dry warm weather, open air.",
      clinicalUses = "Chronic eczema, psoriasis, recurrent boils, chronic morning diarrhea, suppressed eruptions."
    ),
    Remedy(
      abbreviation = "Lyc",
      fullName = "Lycopodium Clavatum",
      commonName = "Club Moss",
      kingdom = "Plant (Lycopodiaceae)",
      thermal = "Chilly",
      primaryMiasm = "Psora",
      keynotes = listOf(
        "Right-sided remedy; symptoms travel from right to left",
        "Characteristic aggravation from 4 p.m. to 8 p.m.",
        "Excessive flatulent distension of lower abdomen immediately after eating",
        "Intellectual keenness with weak muscular and digestive power"
      ),
      modalities = "< 4 to 8 p.m., right side, warm room, oysters. > Warm food and drinks, unconstricted clothing.",
      clinicalUses = "Renal calculi (right kidney), hepatic dysfunction, chronic flatulent dyspepsia, right pneumonia."
    ),
    Remedy(
      abbreviation = "Phos",
      fullName = "Phosphorus",
      commonName = "Phosphorus",
      kingdom = "Mineral",
      thermal = "Chilly (locally hot)",
      primaryMiasm = "Tubercular",
      keynotes = listOf(
        "Tall, slender, narrow-chested, affectionate, open-hearted individuals",
        "Craves ice-cold drinks and spicy salty food; vomits water as soon as it gets warm",
        "Hemorrhagic diathesis: slight wounds bleed profusely",
        "Great fear of thunderstorms, darkness, being alone"
      ),
      modalities = "< Lying on left side, evening, twilight, thunderstorm. > Cold food/drink, sleep, rubbing.",
      clinicalUses = "Hemoptysis, acute bronchitis, pneumonia (lower right lobe), hepatitis, hoarseness."
    ),
    Remedy(
      abbreviation = "Rhus-t",
      fullName = "Rhus Toxicodendron",
      commonName = "Poison Ivy",
      kingdom = "Plant (Anacardiaceae)",
      thermal = "Chilly",
      primaryMiasm = "Sycosis",
      keynotes = listOf(
        "Stiffness and lameness on first beginning to move, relieved by continued motion",
        "Great physical restlessness; must constantly change position",
        "Red triangular tip of tongue",
        "Ailments from getting wet while perspiring, straining muscles or sprains"
      ),
      modalities = "< First motion, rest, cold wet weather, night. > Continued motion, warm dry heat.",
      clinicalUses = "Acute sprains, sciatica, rheumatoid arthritis, herpes zoster, cellulitis."
    ),
    Remedy(
      abbreviation = "Acon",
      fullName = "Aconitum Napellus",
      commonName = "Monk's Hood",
      kingdom = "Plant (Ranunculaceae)",
      thermal = "Chilly",
      primaryMiasm = "Psora",
      keynotes = listOf(
        "Great fear and agonizing anxiety; predicts the hour of death",
        "Sudden, violent onset after exposure to dry, cold winds",
        "High fever with dry hot skin, bounding pulse, unquenchable thirst",
        "Restless tossing in bed"
      ),
      modalities = "< Evening, night, lying on affected side, cold wind. > Open air, quiet, rest.",
      clinicalUses = "First stage of inflammation, acute panic attack, croup, coryza from cold winds."
    ),
    Remedy(
      abbreviation = "Sil",
      fullName = "Silicea",
      commonName = "Pure Flint",
      kingdom = "Mineral",
      thermal = "Chilly",
      primaryMiasm = "Syphilis",
      keynotes = listOf(
        "The homeopathic scalpel: promotes suppuration or resolves it",
        "Extremely chilly, sensitive to cold drafts, must wrap head warmly",
        "Offensive, acrid foot sweat; ailments from suppressed sweat",
        "Lack of moral and physical stamina ('grit')"
      ),
      modalities = "< Cold air, uncovering, drafts, new moon. > Warmth, wrapping up head warmly.",
      clinicalUses = "Chronic abscesses, ingrown toenails, keloids, fistula, rachitic children, headache."
    ),
    Remedy(
      abbreviation = "Calc",
      fullName = "Calcarea Carbonica",
      commonName = "Carbonate of Lime (Oyster Shell)",
      kingdom = "Mineral",
      thermal = "Chilly",
      primaryMiasm = "Psora",
      keynotes = listOf(
        "Fair, fat, flabby, perspires easily especially on head during sleep",
        "Craves boiled eggs, chalk, indwelling indigestible things",
        "Chilly with cold, damp feet like cold damp stockings",
        "Anxious about health, dreads insanity or infection"
      ),
      modalities = "< Cold water, damp air, ascending stairs, full moon. > Dry weather, lying on painful side.",
      clinicalUses = "Pediatric delayed dentition, rickets, chronic adenoids, obesity, gallstones."
    ),
    Remedy(
      abbreviation = "Ign",
      fullName = "Ignatia Amara",
      commonName = "St. Ignatius Bean",
      kingdom = "Plant (Loganiaceae)",
      thermal = "Chilly",
      primaryMiasm = "Syphilis",
      keynotes = listOf(
        "Remedy of paradoxes and contradictions (e.g. sore throat better swallowing solids)",
        "Ailments from acute grief, disappointed love, shock",
        "Involuntary frequent deep sighing, hysterical globus hystericus",
        "Moody, silent brooding, aversion to tobacco smoke"
      ),
      modalities = "< Consolation, grief, coffee, tobacco. > Changing position, hard pressure, swallowing solids.",
      clinicalUses = "Acute grief, hysterical aphonia, nervous headache, chorea, acute emotional shock."
    ),
    Remedy(
      abbreviation = "Gels",
      fullName = "Gelsemium Sempervirens",
      commonName = "Yellow Jasmine",
      kingdom = "Plant (Loganiaceae)",
      thermal = "Chilly",
      primaryMiasm = "Psora",
      keynotes = listOf(
        "The 4 D's: Drowsy, Dull, Dizzy, Dumb (speechless)",
        "Muscular weakness, complete paralysis of will, trembling from weakness",
        "Ailments from anticipation, fright, bad news, stage fright",
        "Occipital headache extending to forehead, relieved by profuse urination"
      ),
      modalities = "< Bad news, damp weather, 10 a.m. > Profuse urination, bending forward.",
      clinicalUses = "Influenza, exam nervousness, anticipatory anxiety diarrhea, tension headache."
    ),
    Remedy(
      abbreviation = "Thuj",
      fullName = "Thuja Occidentalis",
      commonName = "Arbor Vitae (Tree of Life)",
      kingdom = "Plant (Cupressaceae)",
      thermal = "Chilly",
      primaryMiasm = "Sycosis",
      keynotes = listOf(
        "The great anchor sheet of Sycotic miasm and vaccine injuries",
        "Warts, polyps, condylomata, fleshy growths, oily greasy skin",
        "Fixed delusions: body made of glass, living animal in abdomen",
        "Sweat only on uncovered parts, sweet honey-like odor"
      ),
      modalities = "< Damp cold weather, vaccination, 3 a.m. & 3 p.m. > Warm dry air, drawing up limbs.",
      clinicalUses = "Warts, post-vaccinial syndrome, chronic gleet, ovarian cysts, alopecia."
    )
  )

  fun getRemedy(abbr: String): Remedy? {
    return remedies.find { it.abbreviation.equals(abbr, ignoreCase = true) }
  }
}
