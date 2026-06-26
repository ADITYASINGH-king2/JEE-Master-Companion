package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

data class MechanismEntry(
    val id: String,
    val name: String,
    val reagents: String,
    val summary: String,
    val mentorAdvice: String,
    val mechanismSteps: List<String>,
    val generalEquation: String,
    val topic: String
)

@OptIn(FlowPreview::class)
class HinglishMechanismViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTopic = MutableStateFlow("All")
    val selectedTopic: StateFlow<String> = _selectedTopic.asStateFlow()

    val allEntries = listOf(
        MechanismEntry(
            id = "aldol",
            name = "Aldol Condensation",
            reagents = "Dilute NaOH / KOH, Heat",
            summary = "Boss, agar aldehyde ya ketone ke paas kam se kam ek alpha-hydrogen hai, toh dilute alkali ke presence mein wo self-condense hoke beta-hydroxy carbonyl banayega. Fir heat karne par H2O nikal jayega aur alpha,beta-unsaturated product milega!",
            mentorAdvice = "Ekdum solid baat: Agar alpha-hydrogen nahi hai, toh Aldol ke sapne mat dekhna! Tab direct Cannizzaro Reaction karwana.",
            generalEquation = "2 CH3-CHO + dil. NaOH -> CH3-CH(OH)-CH2-CHO --(Heat)--> CH3-CH=CH-CHO + H2O",
            mechanismSteps = listOf(
                "Step 1: OH- base aayega aur acidic alpha-H+ ko khinch lega. Enolate ion (resonance stabilized nucleophile) ban jayega.",
                "Step 2: Ye enolate ion doosre carbonyl molecule ke electrophilic carbon pe attack karega (Nucleophilic Addition).",
                "Step 3: Oxygen pe negative charge aayega jo solvent se proton le kar OH (alcohol) ban jayega.",
                "Step 4: Heat karne par alpha position se H aur beta position se OH nikalkar double bond bana denge (Dehydration)."
            ),
            topic = "Carbonyl Compounds"
        ),
        MechanismEntry(
            id = "cannizzaro",
            name = "Cannizzaro Reaction",
            reagents = "Concentrated NaOH / KOH (50% Soln)",
            summary = "Arre ye toh bilkul self-redox (disproportionation) reaction hai! Jab aldehyde ke paas ZERO alpha-hydrogen hote hain, toh concentrated base ki presence mein ek molecule oxidize hoke carboxylic acid salt banta hai, aur doosra reduce hoke alcohol banata hai.",
            mentorAdvice = "Super Trick: Formaldehyde (HCHO) aur Benzaldehyde (PhCHO) iske sabse favorite examples hain. Isko Aldol se bilkul mix mat karna!",
            generalEquation = "2 HCHO + Conc. NaOH -> HCOONa (Sodium Formate) + CH3OH (Methanol)",
            mechanismSteps = listOf(
                "Step 1: OH- nucleophile ki tarah formal carbonyl carbon pe attack karega (kyuki iske paas alpha-H to hai nahi, to proton nahi khinch sakta).",
                "Step 2: Oxygen negative charge back-kick karega aur hydride (H-) ion lift-off hoke doosre aldehyde ke carbon pe attack karega. (Ye Hydride transfer step Rate Determining Step hai!)",
                "Step 3: Ek acid ban gaya aur ek alkoxide ion. Fast proton transfer hoga taaki stabler carboxylate salt aur alcohol mil sake."
            ),
            topic = "Carbonyl Compounds"
        ),
        MechanismEntry(
            id = "grignard",
            name = "Grignard Reagent Attack",
            reagents = "R-MgX in Dry Ether, then H3O+",
            summary = "Grignard (R-MgX) organic chemistry ka superhero hai! Isme R par d- (partial negative) charge hota hai jo ek boht strong nucleophile aur strong base ki tarah kaam karta hai. Carbonyl carbon pe attack karke alcohols banata hai.",
            mentorAdvice = "ALERT: Grignard ko paani ya moisture se boht nafrat hai! Agar thoda sa bhi acidic H+ mil gaya, toh ye direct alkane bana dega (R-H) aur carbon-carbon coupling ka mauka chala jayega.",
            generalEquation = "R-MgX + R-CHO -> R-CH(OMgX)-R --(H3O+)--> R-CH(OH)-R + Mg(OH)X",
            mechanismSteps = listOf(
                "Step 1: R-MgX mein C-Mg bond high polarized hai. C par d- aur Mg par d+ charge hota hai.",
                "Step 2: Carbonyl carbon partial positive (d+) hota hai, toh R- nucleophile uspar attack karega.",
                "Step 3: C=O bond polarize hoke O- banta hai jo MgX+ ke sath complex banata hai.",
                "Step 4: Acidic workup (H3O+) dene par O-MgX convert ho jata hai stable OH (alcohol) group mein."
            ),
            topic = "Alcohols & Ethers"
        ),
        MechanismEntry(
            id = "sn1",
            name = "Sn1 Substitution",
            reagents = "Polar Protic Solvents (H2O, EtOH, MeOH)",
            summary = "Sn1 matlab Substitution Nucleophilic Unimolecular! Ye multi-step reaction hai jo carbocation intermediate ke through chalti hai. Iski speed sirf substrate (alkyl halide) ke concentration pe depend karti hai.",
            mentorAdvice = "Mentor Mantra: Carbocation ban raha hai matlab Rearrangement (Hydride/Alkyl shift) ho sakta hai! 3rd-degree alkyl halides isko khushi-khushi dete hain.",
            generalEquation = "R-X (3-degree) -> R+ + X- --(Nu-)--> R-Nu",
            mechanismSteps = listOf(
                "Step 1 (Slowest / RDS): Leaving group (X-) nikal jata hai aur ek planar Carbocation intermediate banata hai.",
                "Step 2 (Rearrangement): Agar carbocation 1-degree ya 2-degree hai aur rearrangement se zyada stable ban sakta hai, toh shift hota hai.",
                "Step 3 (Fast Attack): Nucleophile plane ke dono side (front aur back) se attack kar sakta hai, jisse racemic mixture milta hai (Racemization)."
            ),
            topic = "Alkyl Halides"
        ),
        MechanismEntry(
            id = "sn2",
            name = "Sn2 Substitution",
            reagents = "Polar Aprotic Solvents (DMSO, Acetone, DMF), Strong Nu-",
            summary = "Sn2 matlab Substitution Nucleophilic Bimolecular! Ye single-step process hai (no intermediate!). Nucleophile peeche se (backside) attack karta hai jab leaving group aage se nikal raha hota hai.",
            mentorAdvice = "Steric Hindrance check karo! Jitni kam bheed hogi, Sn2 utna fast chalega. Isiliye speed order hai: Methyl > 1-degree > 2-degree > 3-degree.",
            generalEquation = "Nu- + R-X -> [Nu...R...X] (Transition State) -> Nu-R + X-",
            mechanismSteps = listOf(
                "Step 1 (Single Step / Concerted): Strong nucleophile Carbon par backside se attack shuru karta hai.",
                "Step 2 (Transition State): Ek high-energy penta-coordinated transition state banti hai jahan Nu-C bond ban raha hota hai aur C-X bond toot raha hota hai.",
                "Step 3 (Inversion): Leaving group poori tarah nikal jata hai aur configuration flip ho jati hai (Walden Inversion!). Bilkul barish mein chata ulta hone jaisa."
            ),
            topic = "Alkyl Halides"
        ),
        MechanismEntry(
            id = "reimer_tiemann",
            name = "Reimer-Tiemann Reaction",
            reagents = "CHCl3 (Chloroform) + Aqueous NaOH",
            summary = "Phenol ko Salicylaldehyde (ortho-formylphenol) mein badalne ka ekdum solid tarika! Is reaction ki sabse khas baat ye hai ki isme dichlorocarbene (:CCl2) intermediate banta hai.",
            mentorAdvice = "Exam Tip: Dichlorocarbene ek electrophile hai (neutral but electron-deficient). Multiple JEE papers mein iska intermediate pucha gaya hai!",
            generalEquation = "Phenol + CHCl3 + NaOH -> Salicylaldehyde (Ortho-formylphenol)",
            mechanismSteps = listOf(
                "Step 1: NaOH base CHCl3 se proton abstracts karke :CCl3- banata hai, jo Cl- loose karke :CCl2 (Dichlorocarbene) deta hai.",
                "Step 2: Phenol base ki presence mein phenoxide ion banata hai, jo ring ko activate karta hai ortho attack ke liye.",
                "Step 3: Activated benzene ring electrophilic :CCl2 par attack karti hai.",
                "Step 4: Hydrolysis aur rearrangement ke baad ortho-position pe -CHO group fit ho jata."
            ),
            topic = "Phenols"
        ),
        MechanismEntry(
            id = "clemmensen",
            name = "Clemmensen Reduction",
            reagents = "Zinc Amalgam (Zn/Hg) + Concentrated HCl",
            summary = "Aldehyde ya Ketone ke carbonyl carbon (=O) ko CH2 group mein convert karne ki fast track machine! Carbonyl group direct alkane mein badal jata hai under acidic conditions.",
            mentorAdvice = "Caution: Agar aapke substrate mein acid-sensitive groups (jaise -OH ya double bond) hain, toh Clemmensen mat karna! Tab Wolff-Kishner use karna, jo basic condition mein chalta hai.",
            generalEquation = "R-CO-R' + Zn/Hg + HCl -> R-CH2-R'",
            mechanismSteps = listOf(
                "Step 1: Concentrated HCl se H+ carbonyl oxygen ko protonate karke electrophilic character badhata hai.",
                "Step 2: Zinc metal electrons transfer karta hai protonated carbonyl carbon ko, carbon-metal bond formation hoti hai.",
                "Step 3: C-O bond clean cleave hota hai aur extra protons add hote hain.",
                "Step 4: CH2 group complete hota hai aur organic layer se high yield alkane recover kar lete hain."
            ),
            topic = "Reduction Reactions"
        ),
        MechanismEntry(
            id = "wolff_kishner",
            name = "Wolff-Kishner Reduction",
            reagents = "Hydrazine (NH2NH2) + KOH in Ethylene Glycol, Heat",
            summary = "Clemmensen ka bhai, lekin basic conditions wala! Ye bhi aldehyde/ketone ke carbonyl ko direct methylene (-CH2-) alkane mein badal deta hai. Bas iski driving force N2 gas ka nikalna hai.",
            mentorAdvice = "Pro-Tip: Base-sensitive groups ho toh Clemmensen lagao, acid-sensitive groups ho toh Wolff-Kishner! Dono ka target ek hi hai - carbonyl ko CH2 banana.",
            generalEquation = "R-CO-R' + NH2NH2 --(KOH, glycol)--> R-CH2-R' + N2",
            mechanismSteps = listOf(
                "Step 1: Carbonyl molecule hydrazine ke sath react karke hydrazone intermediate (C=N-NH2) banata hai.",
                "Step 2: KOH base hydrazone ke nitrogen se N-H proton khinchta hai.",
                "Step 3: Resonance se negative charge carbon pe aata hai, jo glycol solvent se proton le leta hai.",
                "Step 4: Doosre N-H proton ko base hatata hai, jisse stable N2 gas udh jati hai (irreversible driving force!) aur CH2 group ban jata hai."
            ),
            topic = "Reduction Reactions"
        )
    )

    val topics = listOf("All") + allEntries.map { it.topic }.distinct()

    val filteredEntries: StateFlow<List<MechanismEntry>> = combine(
        _searchQuery,
        _selectedTopic
    ) { query, topic ->
        Pair(query, topic)
    }
    .debounce(300)
    .mapLatest { (query, topic) ->
        allEntries.filter { entry ->
            val matchesTopic = topic == "All" || entry.topic == topic
            val matchesQuery = query.isBlank() || 
                    entry.name.contains(query, ignoreCase = true) ||
                    entry.summary.contains(query, ignoreCase = true) ||
                    entry.reagents.contains(query, ignoreCase = true) ||
                    entry.topic.contains(query, ignoreCase = true)
            matchesTopic && matchesQuery
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = allEntries
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onTopicSelected(topic: String) {
        _selectedTopic.value = topic
    }
}
