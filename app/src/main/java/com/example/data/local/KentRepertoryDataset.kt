package com.example.data.local

import com.example.data.model.KentRubric
import com.example.data.model.RemedyGrade

object KentRepertoryDataset {

  val chapters = listOf(
    "All",
    "Mind",
    "Vertigo",
    "Head",
    "Eyes",
    "Ears",
    "Nose",
    "Face",
    "Mouth & Teeth",
    "Throat",
    "Stomach",
    "Abdomen",
    "Rectum & Stool",
    "Urinary Organs",
    "Genitalia",
    "Larynx & Respiration",
    "Cough & Chest",
    "Back",
    "Extremities",
    "Sleep & Dreams",
    "Fever & Chill",
    "Perspiration",
    "Skin",
    "Generalities"
  )

  val rubrics: List<KentRubric> = listOf(
    // === MIND ===
    KentRubric(
      id = "mind_01",
      chapter = "Mind",
      rubricName = "Anxiety - health, about",
      subRubric = "Fear of serious illness, incurable disease",
      remedies = listOf(
        RemedyGrade("Ars", 3),
        RemedyGrade("Calc", 3),
        RemedyGrade("Nit-ac", 3),
        RemedyGrade("Phos", 3),
        RemedyGrade("Acon", 2),
        RemedyGrade("Ign", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Puls", 2),
        RemedyGrade("Sulph", 2),
        RemedyGrade("Lyc", 1)
      ),
      miasm = "Psora",
      modality = "< midnight, < alone"
    ),
    KentRubric(
      id = "mind_02",
      chapter = "Mind",
      rubricName = "Irritability - contradiction, from",
      subRubric = "Intolerant of contradiction; flies into rage",
      remedies = listOf(
        RemedyGrade("Aur", 3),
        RemedyGrade("Ign", 3),
        RemedyGrade("Lyc", 3),
        RemedyGrade("Nux-v", 3),
        RemedyGrade("Sep", 3),
        RemedyGrade("Bry", 2),
        RemedyGrade("Cham", 2),
        RemedyGrade("Coloc", 2),
        RemedyGrade("Sulph", 2)
      ),
      miasm = "Psora",
      modality = "< morning"
    ),
    KentRubric(
      id = "mind_03",
      chapter = "Mind",
      rubricName = "Fear - death, of - predicts time of death",
      subRubric = "Sudden agonizing panic, restlessness",
      remedies = listOf(
        RemedyGrade("Acon", 3),
        RemedyGrade("Ars", 3),
        RemedyGrade("Gels", 2),
        RemedyGrade("Phos", 2),
        RemedyGrade("Bell", 1),
        RemedyGrade("Plat", 1)
      ),
      miasm = "Psora",
      modality = "< twilight, < night"
    ),
    KentRubric(
      id = "mind_04",
      chapter = "Mind",
      rubricName = "Weeping - consolation, ameliorates",
      subRubric = "Gentle, yielding disposition; seeks sympathy",
      remedies = listOf(
        RemedyGrade("Puls", 3),
        RemedyGrade("Ign", 2),
        RemedyGrade("Phos", 2),
        RemedyGrade("Sep", 1),
        RemedyGrade("Nat-m", 1)
      ),
      miasm = "Sycosis",
      modality = "> consolation, > open air"
    ),
    KentRubric(
      id = "mind_05",
      chapter = "Mind",
      rubricName = "Restlessness - physical & mental with exhaustion",
      subRubric = "Moves constantly from bed to chair, dreads being left alone",
      remedies = listOf(
        RemedyGrade("Ars", 3),
        RemedyGrade("Rhus-t", 3),
        RemedyGrade("Acon", 2),
        RemedyGrade("Bell", 2),
        RemedyGrade("Merc", 2),
        RemedyGrade("Phos", 2)
      ),
      miasm = "Psora",
      modality = "< 1 a.m. to 3 a.m."
    ),
    KentRubric(
      id = "mind_06",
      chapter = "Mind",
      rubricName = "Grief - silent, brooding with involuntary sighing",
      subRubric = "Ailments from disappointed love, bereavement",
      remedies = listOf(
        RemedyGrade("Ign", 3),
        RemedyGrade("Nat-m", 3),
        RemedyGrade("Ph-ac", 3),
        RemedyGrade("Aur", 2),
        RemedyGrade("Caust", 2),
        RemedyGrade("Lach", 2),
        RemedyGrade("Staph", 2)
      ),
      miasm = "Syphilis",
      modality = "< consolation"
    ),
    KentRubric(
      id = "mind_07",
      chapter = "Mind",
      rubricName = "Anticipation - ailments from stage fright or exams",
      subRubric = "Diarrhoea, trembling before public appearance",
      remedies = listOf(
        RemedyGrade("Gels", 3),
        RemedyGrade("Arg-n", 3),
        RemedyGrade("Lyc", 3),
        RemedyGrade("Sil", 2),
        RemedyGrade("Acon", 1),
        RemedyGrade("Puls", 1)
      ),
      miasm = "Psora",
      modality = "< excitement"
    ),

    // === HEAD ===
    KentRubric(
      id = "head_01",
      chapter = "Head",
      rubricName = "Pain - headache - motion, slightest, agg.",
      subRubric = "Bursting headache, relieved by absolute rest and pressure",
      remedies = listOf(
        RemedyGrade("Bry", 3),
        RemedyGrade("Bell", 3),
        RemedyGrade("Gels", 2),
        RemedyGrade("Nat-m", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Sil", 2),
        RemedyGrade("Spig", 2)
      ),
      miasm = "Psora",
      modality = "< motion, > pressure, > dark room"
    ),
    KentRubric(
      id = "head_02",
      chapter = "Head",
      rubricName = "Pain - throbbing - congestive with flushed face",
      subRubric = "Violent pulsations of carotids, heat in head, cold feet",
      remedies = listOf(
        RemedyGrade("Bell", 3),
        RemedyGrade("Glon", 3),
        RemedyGrade("Meli", 3),
        RemedyGrade("Bry", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Sulph", 2),
        RemedyGrade("Acon", 1)
      ),
      miasm = "Psora",
      modality = "< light, < noise, < jarring"
    ),
    KentRubric(
      id = "head_03",
      chapter = "Head",
      rubricName = "Pain - right-sided, ascending from nape to forehead",
      subRubric = "Temple pain, settles over right eye; better tightly wrapping",
      remedies = listOf(
        RemedyGrade("Sil", 3),
        RemedyGrade("Sang", 3),
        RemedyGrade("Bell", 2),
        RemedyGrade("Lyc", 2),
        RemedyGrade("Spig", 2),
        RemedyGrade("Gels", 1)
      ),
      miasm = "Psora",
      modality = "> wrapping head warm, > sleep"
    ),
    KentRubric(
      id = "head_04",
      chapter = "Head",
      rubricName = "Pain - gastric, from overeating or alcohol/spices",
      subRubric = "Morning headache with sour bitter vomiting, irritability",
      remedies = listOf(
        RemedyGrade("Nux-v", 3),
        RemedyGrade("Puls", 2),
        RemedyGrade("Ant-c", 2),
        RemedyGrade("Bry", 2),
        RemedyGrade("Iris", 2),
        RemedyGrade("Sulph", 1)
      ),
      miasm = "Psora",
      modality = "< morning waking, < rich food"
    ),
    KentRubric(
      id = "head_05",
      chapter = "Head",
      rubricName = "Vertigo - looking upward or turning in bed",
      subRubric = "Objects seem to whirl around, dizziness with nausea",
      remedies = listOf(
        RemedyGrade("Con", 3),
        RemedyGrade("Bry", 2),
        RemedyGrade("Calc", 2),
        RemedyGrade("Coccl", 2),
        RemedyGrade("Gels", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Sil", 2)
      ),
      miasm = "Psora",
      modality = "< turning head, < motion"
    ),

    // === EYES ===
    KentRubric(
      id = "eye_01",
      chapter = "Eyes",
      rubricName = "Photophobia - daylight and artificial light agg.",
      subRubric = "Intense intolerance of light with burning and acrid tears",
      remedies = listOf(
        RemedyGrade("Bell", 3),
        RemedyGrade("Con", 3),
        RemedyGrade("Euphr", 3),
        RemedyGrade("Merc", 3),
        RemedyGrade("Ars", 2),
        RemedyGrade("Puls", 2),
        RemedyGrade("Sulph", 2)
      ),
      miasm = "Syphilis",
      modality = "< light, < warmth"
    ),
    KentRubric(
      id = "eye_02",
      chapter = "Eyes",
      rubricName = "Discharge - purulent, thick, bland yellow-green",
      subRubric = "Eyelids glued together in the morning, painless",
      remedies = listOf(
        RemedyGrade("Puls", 3),
        RemedyGrade("Arg-n", 3),
        RemedyGrade("Hep", 2),
        RemedyGrade("Merc", 2),
        RemedyGrade("Sulph", 2),
        RemedyGrade("Calc", 1)
      ),
      miasm = "Sycosis",
      modality = "< warm room, > open air"
    ),

    // === THROAT ===
    KentRubric(
      id = "throat_01",
      chapter = "Throat",
      rubricName = "Pain - swallowing, on - empty swallowing agg.",
      subRubric = "Stitching pain extending to ears, lump sensation",
      remedies = listOf(
        RemedyGrade("Ign", 3),
        RemedyGrade("Lach", 3),
        RemedyGrade("Bell", 3),
        RemedyGrade("Hep", 3),
        RemedyGrade("Merc", 3),
        RemedyGrade("Phos", 2),
        RemedyGrade("Baryt-c", 2),
        RemedyGrade("Apis", 2)
      ),
      miasm = "Psora",
      modality = "< empty swallowing, < warm drinks"
    ),
    KentRubric(
      id = "throat_02",
      chapter = "Throat",
      rubricName = "Tonsillitis - left side beginning, moving to right",
      subRubric = "Extreme sensitivity to neck touch, purplish discoloration",
      remedies = listOf(
        RemedyGrade("Lach", 3),
        RemedyGrade("Phyt", 2),
        RemedyGrade("Merc", 2),
        RemedyGrade("Bell", 1),
        RemedyGrade("Sabad", 1)
      ),
      miasm = "Syphilis",
      modality = "< tight collar, < hot drinks, < after sleep"
    ),
    KentRubric(
      id = "throat_03",
      chapter = "Throat",
      rubricName = "Pain - splinters, sensation of fishbone or splinter",
      subRubric = "Sharp stitching pain on swallowing, hypersensitive",
      remedies = listOf(
        RemedyGrade("Hep", 3),
        RemedyGrade("Nit-ac", 3),
        RemedyGrade("Arg-n", 3),
        RemedyGrade("Sil", 2),
        RemedyGrade("Calc", 1)
      ),
      miasm = "Syphilis",
      modality = "< cold air, > warm drinks"
    ),
    KentRubric(
      id = "throat_04",
      chapter = "Throat",
      rubricName = "Inflammation - right-sided tonsillitis, better warm drinks",
      subRubric = "Pain begins on right and spreads to left, desires hot drinks",
      remedies = listOf(
        RemedyGrade("Lyc", 3),
        RemedyGrade("Bell", 2),
        RemedyGrade("Merc", 2),
        RemedyGrade("Phyt", 2)
      ),
      miasm = "Psora",
      modality = "> warm drinks, < 4 p.m. to 8 p.m."
    ),

    // === STOMACH ===
    KentRubric(
      id = "stomach_01",
      chapter = "Stomach",
      rubricName = "Thirst - unquenchable, for small quantities frequently",
      subRubric = "Drinking causes nausea or vomiting immediately",
      remedies = listOf(
        RemedyGrade("Ars", 3),
        RemedyGrade("Acon", 2),
        RemedyGrade("Bell", 2),
        RemedyGrade("Chin", 2),
        RemedyGrade("Phos", 2),
        RemedyGrade("Rhus-t", 1)
      ),
      miasm = "Psora",
      modality = "< cold water, > sips of warm water"
    ),
    KentRubric(
      id = "stomach_02",
      chapter = "Stomach",
      rubricName = "Thirst - large quantities at long intervals",
      subRubric = "Excessive dryness of lips, tongue, mucous membranes",
      remedies = listOf(
        RemedyGrade("Bry", 3),
        RemedyGrade("Sulph", 2),
        RemedyGrade("Nat-m", 2),
        RemedyGrade("Verat", 2),
        RemedyGrade("Phos", 1)
      ),
      miasm = "Psora",
      modality = "> cold drinks"
    ),
    KentRubric(
      id = "stomach_03",
      chapter = "Stomach",
      rubricName = "Nausea - constant, unrelieved by vomiting",
      subRubric = "Clean tongue with persistent nausea and salivation",
      remedies = listOf(
        RemedyGrade("Ip", 3),
        RemedyGrade("Ars", 2),
        RemedyGrade("Ant-t", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Tab", 2),
        RemedyGrade("Puls", 1)
      ),
      miasm = "Psora",
      modality = "< smell of food"
    ),
    KentRubric(
      id = "stomach_04",
      chapter = "Stomach",
      rubricName = "Eructations - sour, acrid heartburn with heaviness",
      subRubric = "Stone-like weight in epigastrium after eating, 2 hours later",
      remedies = listOf(
        RemedyGrade("Nux-v", 3),
        RemedyGrade("Puls", 2),
        RemedyGrade("Rob", 2),
        RemedyGrade("Lyc", 2),
        RemedyGrade("Sulph", 2),
        RemedyGrade("Carb-v", 2)
      ),
      miasm = "Psora",
      modality = "< heavy meals, < coffee, < sedentary life"
    ),
    KentRubric(
      id = "stomach_05",
      chapter = "Stomach",
      rubricName = "Thirstlessness - acute fever with dry mouth",
      subRubric = "Mouth is dry yet patient has absolutely no desire to drink",
      remedies = listOf(
        RemedyGrade("Puls", 3),
        RemedyGrade("Apis", 3),
        RemedyGrade("Gels", 3),
        RemedyGrade("Ant-t", 1),
        RemedyGrade("Sep", 1)
      ),
      miasm = "Sycosis",
      modality = "< warm room"
    ),
    KentRubric(
      id = "stomach_06",
      chapter = "Stomach",
      rubricName = "Desires - cold food and ice-cold water, vomited when warm",
      subRubric = "Drinks are retained until they become warm in stomach",
      remedies = listOf(
        RemedyGrade("Phos", 3),
        RemedyGrade("Ars", 2),
        RemedyGrade("Bism", 2),
        RemedyGrade("Verat", 2)
      ),
      miasm = "Tubercular",
      modality = "> cold drinks temporarily"
    ),

    // === ABDOMEN ===
    KentRubric(
      id = "abdomen_01",
      chapter = "Abdomen",
      rubricName = "Pain - colic - bending double ameliorates",
      subRubric = "Violent cramping, relieved by hard pressure and bending double",
      remedies = listOf(
        RemedyGrade("Coloc", 3),
        RemedyGrade("Mag-p", 3),
        RemedyGrade("Cham", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Plumb", 2),
        RemedyGrade("Bell", 1)
      ),
      miasm = "Psora",
      modality = "> bending double, > hard pressure, > warm applications"
    ),
    KentRubric(
      id = "abdomen_02",
      chapter = "Abdomen",
      rubricName = "Distension - flatulence, lower abdomen especially",
      subRubric = "Cannot bear tightness of clothing around waist, 4 to 8 p.m.",
      remedies = listOf(
        RemedyGrade("Lyc", 3),
        RemedyGrade("Carb-v", 3),
        RemedyGrade("Chin", 3),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Sulph", 2),
        RemedyGrade("Arg-n", 2)
      ),
      miasm = "Psora",
      modality = "< 4 p.m. to 8 p.m., > passing flatus"
    ),
    KentRubric(
      id = "abdomen_03",
      chapter = "Abdomen",
      rubricName = "Pain - appendicular region, sensitive to touch and jarring",
      subRubric = "Right iliac fossa tenderness, right thigh flexed",
      remedies = listOf(
        RemedyGrade("Bell", 3),
        RemedyGrade("Bry", 3),
        RemedyGrade("Merc", 2),
        RemedyGrade("Rhus-t", 2),
        RemedyGrade("Echin", 1)
      ),
      miasm = "Psora",
      modality = "< jarring, < movement"
    ),

    // === RECTUM & STOOL ===
    KentRubric(
      id = "rectum_01",
      chapter = "Rectum & Stool",
      rubricName = "Constipation - ineffectual urging, frequent",
      subRubric = "Constant desire with passing only small quantities, feels unfinished",
      remedies = listOf(
        RemedyGrade("Nux-v", 3),
        RemedyGrade("Anac", 2),
        RemedyGrade("Lyc", 2),
        RemedyGrade("Plat", 2),
        RemedyGrade("Sep", 2),
        RemedyGrade("Sulph", 2)
      ),
      miasm = "Psora",
      modality = "< morning"
    ),
    KentRubric(
      id = "rectum_02",
      chapter = "Rectum & Stool",
      rubricName = "Diarrhoea - early morning, drives out of bed",
      subRubric = "Urgent, sudden painless stool at 5 a.m., cannot wait",
      remedies = listOf(
        RemedyGrade("Sulph", 3),
        RemedyGrade("Aloe", 3),
        RemedyGrade("Podo", 3),
        RemedyGrade("Nat-s", 2),
        RemedyGrade("Phos", 2),
        RemedyGrade("Thuj", 2)
      ),
      miasm = "Psora",
      modality = "< 5 a.m., < warm room"
    ),
    KentRubric(
      id = "rectum_03",
      chapter = "Rectum & Stool",
      rubricName = "Haemorrhoids - painful, burning like fire, relieved by cold",
      subRubric = "Bleeding protruding piles with stinging, burning soreness",
      remedies = listOf(
        RemedyGrade("Aloe", 3),
        RemedyGrade("Ham", 3),
        RemedyGrade("Nit-ac", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Paeon", 2),
        RemedyGrade("Sulph", 2),
        RemedyGrade("Ars", 1)
      ),
      miasm = "Sycosis",
      modality = "> cold bathing, < standing"
    ),

    // === COUGH & CHEST ===
    KentRubric(
      id = "cough_01",
      chapter = "Cough & Chest",
      rubricName = "Cough - dry, hard, racking, painful in chest",
      subRubric = "Patient holds chest with hands during cough; motion agg.",
      remedies = listOf(
        RemedyGrade("Bry", 3),
        RemedyGrade("Dros", 2),
        RemedyGrade("Phos", 2),
        RemedyGrade("Bell", 2),
        RemedyGrade("Acon", 2),
        RemedyGrade("Rumx", 2),
        RemedyGrade("Caust", 1)
      ),
      miasm = "Psora",
      modality = "< breathing deep, < moving, > absolute rest"
    ),
    KentRubric(
      id = "cough_02",
      chapter = "Cough & Chest",
      rubricName = "Cough - loose morning, dry night with thick expectoration",
      subRubric = "Must sit up in bed to cough, bland yellow discharge",
      remedies = listOf(
        RemedyGrade("Puls", 3),
        RemedyGrade("Hep", 2),
        RemedyGrade("Merc", 2),
        RemedyGrade("Phos", 2),
        RemedyGrade("Sep", 2),
        RemedyGrade("Calc", 1)
      ),
      miasm = "Sycosis",
      modality = "< warm room, > open air"
    ),
    KentRubric(
      id = "cough_03",
      chapter = "Cough & Chest",
      rubricName = "Cough - spasmodic, paroxysmal, suffocative",
      subRubric = "Severe violent cough ending in retching or epistaxis",
      remedies = listOf(
        RemedyGrade("Dros", 3),
        RemedyGrade("Ip", 3),
        RemedyGrade("Cupr", 3),
        RemedyGrade("Bell", 2),
        RemedyGrade("Hep", 2),
        RemedyGrade("Hyos", 2)
      ),
      miasm = "Tubercular",
      modality = "< midnight, < lying down"
    ),
    KentRubric(
      id = "cough_04",
      chapter = "Cough & Chest",
      rubricName = "Respiration - asthmatic, suffocating, worse midnight",
      subRubric = "Cannot lie flat, must sit bent forward, great anxiety",
      remedies = listOf(
        RemedyGrade("Ars", 3),
        RemedyGrade("Ip", 2),
        RemedyGrade("Kali-c", 2),
        RemedyGrade("Nat-s", 2),
        RemedyGrade("Sumb", 2),
        RemedyGrade("Spong", 2)
      ),
      miasm = "Psora",
      modality = "< 1 to 2 a.m., > warm drinks, > sitting bent"
    ),

    // === EXTREMITIES ===
    KentRubric(
      id = "ext_01",
      chapter = "Extremities",
      rubricName = "Pain - joints - first motion agg., continuous motion amel.",
      subRubric = "Stiffness, tearing rheumatism from damp cold weather",
      remedies = listOf(
        RemedyGrade("Rhus-t", 3),
        RemedyGrade("Bry", 2),
        RemedyGrade("Calc", 2),
        RemedyGrade("Lyc", 2),
        RemedyGrade("Ruta", 2),
        RemedyGrade("Sulph", 1)
      ),
      miasm = "Sycosis",
      modality = "< beginning motion, > continued gentle motion, > heat"
    ),
    KentRubric(
      id = "ext_02",
      chapter = "Extremities",
      rubricName = "Pain - joints - slightest motion agg., better rest",
      subRubric = "Hot, swollen, red joints with stitching pain",
      remedies = listOf(
        RemedyGrade("Bry", 3),
        RemedyGrade("Bell", 2),
        RemedyGrade("Colch", 2),
        RemedyGrade("Led", 2),
        RemedyGrade("Merc", 2),
        RemedyGrade("Phyt", 1)
      ),
      miasm = "Psora",
      modality = "< motion, > lying on painful side"
    ),
    KentRubric(
      id = "ext_03",
      chapter = "Extremities",
      rubricName = "Pain - gouty, small joints, begins in feet and ascends",
      subRubric = "Lack of vital heat, affected parts cold to touch but relieved by cold",
      remedies = listOf(
        RemedyGrade("Led", 3),
        RemedyGrade("Colch", 3),
        RemedyGrade("Benz-ac", 2),
        RemedyGrade("Lyc", 2),
        RemedyGrade("Sulph", 1)
      ),
      miasm = "Sycosis",
      modality = "> cold water application"
    ),

    // === SLEEP ===
    KentRubric(
      id = "sleep_01",
      chapter = "Sleep",
      rubricName = "Sleeplessness - business thoughts or mental activity",
      subRubric = "Wakes at 3 a.m. thinking of work, falls asleep when time to rise",
      remedies = listOf(
        RemedyGrade("Nux-v", 3),
        RemedyGrade("Coff", 3),
        RemedyGrade("Lyc", 2),
        RemedyGrade("Ars", 2),
        RemedyGrade("Calc", 2),
        RemedyGrade("Sulph", 2)
      ),
      miasm = "Psora",
      modality = "< 3 a.m. to 4 a.m."
    ),
    KentRubric(
      id = "sleep_02",
      chapter = "Sleep",
      rubricName = "Sleep - unrefreshing, wakes more tired than when retiring",
      subRubric = "Heavy head, morning languor, nightmares of drowning or falling",
      remedies = listOf(
        RemedyGrade("Nat-m", 3),
        RemedyGrade("Sep", 3),
        RemedyGrade("Mag-c", 2),
        RemedyGrade("Phos", 2),
        RemedyGrade("Lyc", 2),
        RemedyGrade("Sulph", 2)
      ),
      miasm = "Sycosis",
      modality = "< morning waking"
    ),

    // === FEVER & CHILL ===
    KentRubric(
      id = "fever_01",
      chapter = "Fever & Chill",
      rubricName = "Fever - sudden onset after exposure to dry cold wind",
      subRubric = "High fever, rapid full bounding pulse, extreme restlessness and fear",
      remedies = listOf(
        RemedyGrade("Acon", 3),
        RemedyGrade("Bell", 3),
        RemedyGrade("Bry", 2),
        RemedyGrade("Ferr-p", 2),
        RemedyGrade("Gels", 1)
      ),
      miasm = "Psora",
      modality = "< night, < dry cold wind"
    ),
    KentRubric(
      id = "fever_02",
      chapter = "Fever & Chill",
      rubricName = "Fever - gradual onset, drowsy, dull, dizzy with trembling",
      subRubric = "Heavy eyelids, occipital ache, absence of thirst during heat",
      remedies = listOf(
        RemedyGrade("Gels", 3),
        RemedyGrade("Bapt", 3),
        RemedyGrade("Bry", 2),
        RemedyGrade("Rhus-t", 2),
        RemedyGrade("Eup-per", 2)
      ),
      miasm = "Psora",
      modality = "> profuse urination"
    ),

    // === SKIN ===
    KentRubric(
      id = "skin_01",
      chapter = "Skin",
      rubricName = "Itching - voluptuous, worse warmth of bed, scratches raw",
      subRubric = "Burning after scratching; aversion to bathing",
      remedies = listOf(
        RemedyGrade("Sulph", 3),
        RemedyGrade("Psor", 3),
        RemedyGrade("Ars", 2),
        RemedyGrade("Graph", 2),
        RemedyGrade("Merc", 2),
        RemedyGrade("Mez", 2)
      ),
      miasm = "Psora",
      modality = "< warmth of bed, < washing"
    ),
    KentRubric(
      id = "skin_02",
      chapter = "Skin",
      rubricName = "Eruptions - eczema, sticky, honey-like oozing exudation",
      subRubric = "Fissures in bends of elbows, behind ears, obese sluggish patients",
      remedies = listOf(
        RemedyGrade("Graph", 3),
        RemedyGrade("Mez", 2),
        RemedyGrade("Hep", 2),
        RemedyGrade("Petr", 2),
        RemedyGrade("Sulph", 2),
        RemedyGrade("Calc", 1)
      ),
      miasm = "Psora",
      modality = "< heat, > wrapping"
    ),
    KentRubric(
      id = "skin_03",
      chapter = "Skin",
      rubricName = "Warts - fleshy, cauliflower-like, peduncular or fig-warts",
      subRubric = "Condylomata, skin tags, history of suppressed gonorrhea",
      remedies = listOf(
        RemedyGrade("Thuj", 3),
        RemedyGrade("Nit-ac", 3),
        RemedyGrade("Caust", 2),
        RemedyGrade("Dulcam", 2),
        RemedyGrade("Nat-s", 1)
      ),
      miasm = "Sycosis",
      modality = "< damp weather"
    ),

    // === GENERALITIES ===
    KentRubric(
      id = "gen_01",
      chapter = "Generalities",
      rubricName = "Cold - air, drafts of, agg. - extreme chilly patient",
      subRubric = "Sensitive to cold air, wrapping head and body in warm blankets",
      remedies = listOf(
        RemedyGrade("Hep", 3),
        RemedyGrade("Psor", 3),
        RemedyGrade("Sil", 3),
        RemedyGrade("Ars", 3),
        RemedyGrade("Calc", 3),
        RemedyGrade("Nux-v", 3),
        RemedyGrade("Rhus-t", 2),
        RemedyGrade("Sep", 2)
      ),
      miasm = "Psora",
      modality = "< cold, > warm room"
    ),
    KentRubric(
      id = "gen_02",
      chapter = "Generalities",
      rubricName = "Heat - warm room agg., craves cool fresh open air",
      subRubric = "Suffocation in closed rooms, throws off bed covers",
      remedies = listOf(
        RemedyGrade("Puls", 3),
        RemedyGrade("Sulph", 3),
        RemedyGrade("Apis", 3),
        RemedyGrade("Arg-n", 3),
        RemedyGrade("Iod", 3),
        RemedyGrade("Sec", 2),
        RemedyGrade("Nat-m", 2),
        RemedyGrade("Lach", 2)
      ),
      miasm = "Sycosis",
      modality = "> open air, < heat of stove"
    ),
    KentRubric(
      id = "gen_03",
      chapter = "Generalities",
      rubricName = "Periodicity - complaints return at fixed hours or days",
      subRubric = "Regular intermittent return of symptoms (e.g. 10 a.m., 3 p.m.)",
      remedies = listOf(
        RemedyGrade("Chin", 3),
        RemedyGrade("Ars", 3),
        RemedyGrade("Cedr", 3),
        RemedyGrade("Nat-m", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Sulph", 2)
      ),
      miasm = "Psora",
      modality = "< clock-like regularity"
    ),

    // === VERTIGO ===
    KentRubric(
      id = "vertigo_01",
      chapter = "Vertigo",
      rubricName = "Vertigo - looking upward or turning head",
      subRubric = "Whirling sensation, reeling as if intoxicated, worse lying down or turning",
      remedies = listOf(
        RemedyGrade("Con", 3),
        RemedyGrade("Bry", 3),
        RemedyGrade("Calc", 2),
        RemedyGrade("Coccl", 2),
        RemedyGrade("Gels", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Sil", 2)
      ),
      miasm = "Psora",
      modality = "< turning head, < motion, > closing eyes"
    ),
    KentRubric(
      id = "vertigo_02",
      chapter = "Vertigo",
      rubricName = "Vertigo - occipital, with heaviness of eyelids and dim vision",
      subRubric = "Muscular weakness, dizziness originating from base of brain",
      remedies = listOf(
        RemedyGrade("Gels", 3),
        RemedyGrade("Cocc", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Petr", 2),
        RemedyGrade("Phos", 1)
      ),
      miasm = "Psora",
      modality = "< motion, > profuse urination"
    ),

    // === EARS ===
    KentRubric(
      id = "ear_01",
      chapter = "Ears",
      rubricName = "Discharge - purulent, offensive, thick yellow-green",
      subRubric = "Chronic otorrhea, painless, worse warm room, better open air",
      remedies = listOf(
        RemedyGrade("Puls", 3),
        RemedyGrade("Hep", 3),
        RemedyGrade("Merc", 3),
        RemedyGrade("Sil", 2),
        RemedyGrade("Tell", 2),
        RemedyGrade("Calc", 1)
      ),
      miasm = "Syphilis",
      modality = "< night, > warm wrap"
    ),
    KentRubric(
      id = "ear_02",
      chapter = "Ears",
      rubricName = "Pain - otalgia, stitching, bursting, intolerant of touch",
      subRubric = "Hypersensitive to noise and draft of cold wind, patient frantic",
      remedies = listOf(
        RemedyGrade("Cham", 3),
        RemedyGrade("Hep", 3),
        RemedyGrade("Bell", 3),
        RemedyGrade("Puls", 2),
        RemedyGrade("Acon", 2),
        RemedyGrade("Merc", 2)
      ),
      miasm = "Psora",
      modality = "< cold wind, < touch"
    ),

    // === NOSE ===
    KentRubric(
      id = "nose_01",
      chapter = "Nose",
      rubricName = "Coryza - acrid, burning excoriating discharge from nose",
      subRubric = "Profuse watery fluent coryza, nose red and raw, better cold open air",
      remedies = listOf(
        RemedyGrade("All-c", 3),
        RemedyGrade("Ars", 3),
        RemedyGrade("Euphr", 2),
        RemedyGrade("Merc", 2),
        RemedyGrade("Nux-v", 2)
      ),
      miasm = "Psora",
      modality = "< warm room, > open air"
    ),
    KentRubric(
      id = "nose_02",
      chapter = "Nose",
      rubricName = "Obstruction - nose dry, blocked at night in bed",
      subRubric = "Must breathe through mouth, snuffles of infants, fluent in daytime",
      remedies = listOf(
        RemedyGrade("Nux-v", 3),
        RemedyGrade("Samb", 3),
        RemedyGrade("Am-c", 2),
        RemedyGrade("Lyc", 2),
        RemedyGrade("Puls", 2)
      ),
      miasm = "Psora",
      modality = "< night in bed, < dry room"
    ),

    // === FACE ===
    KentRubric(
      id = "face_01",
      chapter = "Face",
      rubricName = "Pain - facial neuralgia, right-sided, sharp electric shocks",
      subRubric = "Tic douloureux, aggravated by cold draft, touch, chewing; relieved by warm applications",
      remedies = listOf(
        RemedyGrade("Mag-p", 3),
        RemedyGrade("Bell", 3),
        RemedyGrade("Spig", 2),
        RemedyGrade("Coloc", 2),
        RemedyGrade("Acon", 1)
      ),
      miasm = "Psora",
      modality = "< cold draft, > heat and firm pressure"
    ),
    KentRubric(
      id = "face_02",
      chapter = "Face",
      rubricName = "Discoloration - flushed, fiery red during fever and headache",
      subRubric = "Face red, hot, throbbing carotids with pupils dilated",
      remedies = listOf(
        RemedyGrade("Bell", 3),
        RemedyGrade("Acon", 3),
        RemedyGrade("Glon", 3),
        RemedyGrade("Ferr-p", 2),
        RemedyGrade("Gels", 2)
      ),
      miasm = "Psora",
      modality = "< light, < noise"
    ),

    // === MOUTH & TEETH ===
    KentRubric(
      id = "mouth_01",
      chapter = "Mouth & Teeth",
      rubricName = "Tongue - coated thick milky white",
      subRubric = "Thick white fur as if whitewashed, associated with gastric disturbances",
      remedies = listOf(
        RemedyGrade("Ant-c", 3),
        RemedyGrade("Bry", 2),
        RemedyGrade("Nux-v", 2),
        RemedyGrade("Puls", 2),
        RemedyGrade("Merc", 1)
      ),
      miasm = "Psora",
      modality = "< sour food, < summer heat"
    ),
    KentRubric(
      id = "mouth_02",
      chapter = "Mouth & Teeth",
      rubricName = "Tongue - red triangular tip with fever and restlessness",
      subRubric = "Coated white with clean red apex, dry brown streak down center",
      remedies = listOf(
        RemedyGrade("Rhus-t", 3),
        RemedyGrade("Bapt", 2),
        RemedyGrade("Ars", 2),
        RemedyGrade("Verat-v", 2)
      ),
      miasm = "Psora",
      modality = "< cold damp"
    ),
    KentRubric(
      id = "mouth_03",
      chapter = "Mouth & Teeth",
      rubricName = "Breath - offensive, fetid, with profuse nocturnal salivation",
      subRubric = "Metallic taste, flabby indented tongue showing teeth marks",
      remedies = listOf(
        RemedyGrade("Merc", 3),
        RemedyGrade("Nit-ac", 3),
        RemedyGrade("Kreos", 2),
        RemedyGrade("Hep", 2),
        RemedyGrade("Bapt", 2)
      ),
      miasm = "Syphilis",
      modality = "< night, < warmth of bed"
    ),

    // === URINARY ORGANS ===
    KentRubric(
      id = "urine_01",
      chapter = "Urinary Organs",
      rubricName = "Urination - involuntary on coughing, sneezing or laughing",
      subRubric = "Weakness of sphincter vesicae, urine spurts out involuntarily",
      remedies = listOf(
        RemedyGrade("Caust", 3),
        RemedyGrade("Puls", 3),
        RemedyGrade("Nat-m", 2),
        RemedyGrade("Squil", 2),
        RemedyGrade("Ferr-p", 1)
      ),
      miasm = "Psora",
      modality = "< coughing, < winter cold"
    ),
    KentRubric(
      id = "urine_02",
      chapter = "Urinary Organs",
      rubricName = "Burning - during and after urination with intense tenesmus",
      subRubric = "Constant desire to urinate, passes drop by drop with cutting burning agony",
      remedies = listOf(
        RemedyGrade("Canth", 3),
        RemedyGrade("Apis", 3),
        RemedyGrade("Merc-c", 3),
        RemedyGrade("Sars", 2),
        RemedyGrade("Equis", 2),
        RemedyGrade("Berb", 2)
      ),
      miasm = "Psora",
      modality = "< drinking water, > sitting still"
    ),

    // === GENITALIA ===
    KentRubric(
      id = "genit_01",
      chapter = "Genitalia",
      rubricName = "Menses - painful dysmenorrhea, relieved by heat and bending double",
      subRubric = "Spasmodic ovarian cramping, flow dark, bearing down sensation",
      remedies = listOf(
        RemedyGrade("Mag-p", 3),
        RemedyGrade("Coloc", 3),
        RemedyGrade("Cham", 2),
        RemedyGrade("Sep", 2),
        RemedyGrade("Puls", 2),
        RemedyGrade("Vib", 2)
      ),
      miasm = "Psora",
      modality = "> hot water bag, > bending double"
    ),
    KentRubric(
      id = "genit_02",
      chapter = "Genitalia",
      rubricName = "Bearing down - sensation as if pelvic organs would escape through vulva",
      subRubric = "Must cross legs tightly to prevent prolapse, indifferent to loved ones",
      remedies = listOf(
        RemedyGrade("Sep", 3),
        RemedyGrade("Lil-t", 3),
        RemedyGrade("Bell", 2),
        RemedyGrade("Nat-m", 2),
        RemedyGrade("Murex", 2)
      ),
      miasm = "Psora",
      modality = "< standing, > crossing legs, > vigorous exercise"
    ),

    // === LARYNX & RESPIRATION ===
    KentRubric(
      id = "resp_01",
      chapter = "Larynx & Respiration",
      rubricName = "Asthma - suffocative breathing, worse 2 a.m. to 4 a.m.",
      subRubric = "Must sit up leaning forward with elbows on knees, cold sweat",
      remedies = listOf(
        RemedyGrade("Kali-c", 3),
        RemedyGrade("Ars", 3),
        RemedyGrade("Ant-t", 2),
        RemedyGrade("Ipec", 2),
        RemedyGrade("Med", 2)
      ),
      miasm = "Sycosis",
      modality = "< 2-4 a.m., > leaning forward"
    ),
    KentRubric(
      id = "resp_02",
      chapter = "Larynx & Respiration",
      rubricName = "Hoarseness - chronic, worse morning and talking",
      subRubric = "Painful raw sensation in larynx, cannot speak above a whisper",
      remedies = listOf(
        RemedyGrade("Caust", 3),
        RemedyGrade("Phos", 3),
        RemedyGrade("Carb-v", 2),
        RemedyGrade("Dros", 2),
        RemedyGrade("Hep", 2)
      ),
      miasm = "Psora",
      modality = "< overuse of voice, < morning"
    ),

    // === BACK ===
    KentRubric(
      id = "back_01",
      chapter = "Back",
      rubricName = "Pain - lumbago, worse beginning motion, better continued motion",
      subRubric = "Stiffness in lumbar region, feels bruised as if broken, worse resting",
      remedies = listOf(
        RemedyGrade("Rhus-t", 3),
        RemedyGrade("Bry", 2),
        RemedyGrade("Berb", 2),
        RemedyGrade("Calc-f", 2),
        RemedyGrade("Ruta", 2),
        RemedyGrade("Nux-v", 2)
      ),
      miasm = "Psora",
      modality = "< first motion, < cold damp weather, > warm dry rubbing"
    ),
    KentRubric(
      id = "back_02",
      chapter = "Back",
      rubricName = "Pain - sacrum and lumbar, radiating to hips and thighs",
      subRubric = "Severe aching weakness in small of back, requires hard support behind back",
      remedies = listOf(
        RemedyGrade("Kali-c", 3),
        RemedyGrade("Sep", 3),
        RemedyGrade("Nat-m", 2),
        RemedyGrade("Aescul", 2),
        RemedyGrade("Berb", 2)
      ),
      miasm = "Psora",
      modality = "< morning in bed, > hard pressure against back"
    ),

    // === PERSPIRATION ===
    KentRubric(
      id = "sweat_01",
      chapter = "Perspiration",
      rubricName = "Sweat - profuse, oily, offensive, staining linen yellow",
      subRubric = "Copious night sweat without relief of fever, leaves patient weak",
      remedies = listOf(
        RemedyGrade("Merc", 3),
        RemedyGrade("Hep", 3),
        RemedyGrade("Psor", 2),
        RemedyGrade("Sil", 2),
        RemedyGrade("Sulph", 2)
      ),
      miasm = "Syphilis",
      modality = "< night, < warmth of bed"
    ),
    KentRubric(
      id = "sweat_02",
      chapter = "Perspiration",
      rubricName = "Sweat - head and neck, during sleep especially in children",
      subRubric = "Wets the pillow all around, sour smelling, large open fontanelles",
      remedies = listOf(
        RemedyGrade("Calc", 3),
        RemedyGrade("Sil", 3),
        RemedyGrade("Cham", 2),
        RemedyGrade("Sanic", 2)
      ),
      miasm = "Psora",
      modality = "< during sleep"
    )
  )

  // Clinical quick-presets for common totality analysis
  data class TotalityPreset(
    val title: String,
    val description: String,
    val caseType: String,
    val defaultComplaint: String,
    val rubricIds: List<String>
  )

  val presets = listOf(
    TotalityPreset(
      title = "Acute Dyspepsia & Gastric Catarrh",
      description = "After rich food/alcohol, morning irritability, sour eructations, ineffectual urging",
      caseType = "Acute",
      defaultComplaint = "Acid reflux, morning headache, nausea, constipation",
      rubricIds = listOf("head_04", "stomach_04", "rectum_01", "mind_02", "sleep_01")
    ),
    TotalityPreset(
      title = "Sudden High Fever & Inflammation",
      description = "Violent bounding pulse, throbbing headache, restlessness, photophobia",
      caseType = "Acute",
      defaultComplaint = "Acute sudden fever, throbbing congestive headache",
      rubricIds = listOf("fever_01", "head_02", "eye_01", "throat_01", "mind_03")
    ),
    TotalityPreset(
      title = "Acute Tonsillitis & Sore Throat",
      description = "Swallowing agg., throat pain to ears, sensitive to cold/warmth",
      caseType = "Acute",
      defaultComplaint = "Severe painful swallowing, inflamed tonsils, fever",
      rubricIds = listOf("throat_01", "throat_02", "throat_03", "stomach_01", "gen_01")
    ),
    TotalityPreset(
      title = "Chronic Rheumatic Joint Pain",
      description = "Stiffness, aggravated first motion, weather sensitive, damp cold agg.",
      caseType = "Chronic",
      defaultComplaint = "Chronic joint arthritis, morning stiffness, worse damp weather",
      rubricIds = listOf("ext_01", "ext_02", "mind_05", "gen_01", "sleep_02")
    ),
    TotalityPreset(
      title = "Catarrhal Bronchitis & Cough",
      description = "Dry painful chest cough, thirstless or thirsty, worse night",
      caseType = "Acute",
      defaultComplaint = "Persistent dry cough, painful stitching in chest",
      rubricIds = listOf("cough_01", "cough_02", "stomach_02", "head_01", "mind_04")
    ),
    TotalityPreset(
      title = "Chronic Allergic / Eczematous Diathesis",
      description = "Burning itching skin, worse warmth of bed, morning diarrhea, thirsty",
      caseType = "Chronic",
      defaultComplaint = "Chronic eczema, pruritus worse bed heat, fatigue",
      rubricIds = listOf("skin_01", "rectum_02", "stomach_02", "gen_02", "mind_01")
    )
  )
}
