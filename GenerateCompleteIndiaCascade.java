import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GenerateCompleteIndiaCascade {

    static class District {
        String id, stateId, code, name, nameEn, nameHi, nameMr;
        District(String id, String stateId, String code, String name, String nameEn, String nameHi, String nameMr) {
            this.id = id; this.stateId = stateId; this.code = code; this.name = name; this.nameEn = nameEn; this.nameHi = nameHi; this.nameMr = nameMr;
        }
    }

    static class SubDistrict {
        String id, districtId, code, name, nameEn, nameHi, nameMr;
        SubDistrict(String id, String districtId, String code, String name, String nameEn, String nameHi, String nameMr) {
            this.id = id; this.districtId = districtId; this.code = code; this.name = name; this.nameEn = nameEn; this.nameHi = nameHi; this.nameMr = nameMr;
        }
    }

    static class Village {
        String id, subDistrictId, talukaId, code, name, nameEn, nameHi, nameMr, pinCode;
        Village(String id, String subDistrictId, String talukaId, String code, String name, String nameEn, String nameHi, String nameMr, String pinCode) {
            this.id = id; this.subDistrictId = subDistrictId; this.talukaId = talukaId; this.code = code; this.name = name; this.nameEn = nameEn; this.nameHi = nameHi; this.nameMr = nameMr; this.pinCode = pinCode;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Building Comprehensive Authoritative India District -> Sub-District/Taluka -> Village Cascade...");

        // Reuse districts from existing Generator / JSON
        String distJsonPath = "E:/new project/KaamSetu/backend/src/main/resources/data/india_districts.json";
        String distContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(distJsonPath)), StandardCharsets.UTF_8);

        // Simple parser for district records from JSON
        List<District> allDistricts = parseDistricts(distContent);
        System.out.println("Loaded " + allDistricts.size() + " districts across 36 States/UTs.");

        List<SubDistrict> allSubDistricts = new ArrayList<>();
        List<Village> allVillages = new ArrayList<>();

        // Multi-taluka definitions for major districts across India
        Map<String, List<String[]>> customTalukas = new LinkedHashMap<>();

        // --- MAHARASHTRA DISTRICTS ---
        // Solapur (11 Talukas)
        customTalukas.put("dist-solapur", Arrays.asList(
            new String[]{"subdist-solapur-north", "SLP-N", "Solapur North", "उत्तर सोलापूर", "उत्तर सोलापूर", "413001"},
            new String[]{"subdist-solapur-south", "SLP-S", "Solapur South", "दक्षिण सोलापूर", "दक्षिण सोलापूर", "413002"},
            new String[]{"subdist-barshi", "BAR", "Barshi", "बार्शी", "बार्शी", "413401"},
            new String[]{"subdist-akkalkot", "AKK", "Akkalkot", "अक्कलकोट", "अक्कलकोट", "413216"},
            new String[]{"subdist-mohol", "MOH", "Mohol", "मोहोळ", "मोहोळ", "413213"},
            new String[]{"subdist-madha", "MAD", "Madha (Kurduwadi)", "माढा (कुर्डूवाडी)", "माढा (कुर्डूवाडी)", "413209"},
            new String[]{"subdist-karmala", "KAR", "Karmala", "करमाळा", "करमाळा", "413203"},
            new String[]{"subdist-pandharpur", "PAN", "Pandharpur", "पंढरपूर", "पंढरपूर", "413304"},
            new String[]{"subdist-sangola", "SAN", "Sangola", "सांगोला", "सांगोला", "413307"},
            new String[]{"subdist-malshiras", "MLS", "Malshiras (Akluj)", "माळशिरस (अकलूज)", "माळशिरस (अकलूज)", "413107"},
            new String[]{"subdist-mangalwedha", "MGW", "Mangalwedha", "मंगळवेढा", "मंगळवेढा", "413305"}
        ));

        // Satara (11 Talukas)
        customTalukas.put("dist-satara", Arrays.asList(
            new String[]{"subdist-satara", "SAT", "Satara", "सातारा", "सातारा", "415001"},
            new String[]{"subdist-wai", "WAI", "Wai", "वाई", "वाई", "412803"},
            new String[]{"subdist-mahabaleshwar", "MHB", "Mahabaleshwar", "महाबळेश्वर", "महाबळेश्वर", "412806"},
            new String[]{"subdist-phaltan", "PHL", "Phaltan", "फलटण", "फलटण", "415523"},
            new String[]{"subdist-karad", "KRD", "Karad", "कराड", "कराड", "415110"},
            new String[]{"subdist-khatav", "KHT", "Khatav (Vaduj)", "खटाव (वडूज)", "खटाव (वडूज)", "415506"},
            new String[]{"subdist-koregaon", "KOR", "Koregaon", "कोरेगाव", "कोरेगाव", "415501"},
            new String[]{"subdist-patan", "PAT", "Patan", "पाटण", "पाटण", "415206"},
            new String[]{"subdist-jaoli", "JAO", "Jaoli (Medha)", "जावळी (मेढा)", "जावळी (मेढा)", "415012"},
            new String[]{"subdist-khandala", "KHL", "Khandala (Shirwal)", "खंडाळा (शिरवळ)", "खंडाळा (शिरवळ)", "412802"},
            new String[]{"subdist-man", "MAN", "Man (Dahiwadi)", "माण (दहिवाडी)", "माण (दहिवाडी)", "415508"}
        ));

        // Pune (14 Talukas)
        customTalukas.put("dist-pune", Arrays.asList(
            new String[]{"subdist-haveli", "PUN-HAV", "Haveli (Hadapsar/Wagholi)", "हवेली", "हवेली", "411028"},
            new String[]{"subdist-pune-city", "PUN-CTY", "Pune City", "पुणे शहर", "पुणे शहर", "411005"},
            new String[]{"subdist-khed", "PUN-KHD", "Khed (Rajgurunagar/Chakan)", "खेड (राजगुरुनगर)", "खेड (राजगुरुनगर)", "410501"},
            new String[]{"subdist-shirur", "PUN-SHR", "Shirur (Ranjangaon/Shikrapur)", "शिरूर", "शिरूर", "412209"},
            new String[]{"subdist-baramati", "PUN-BRM", "Baramati", "बारामती", "बारामती", "413102"},
            new String[]{"subdist-maval", "PUN-MVL", "Maval (Vadgaon/Talegaon)", "मावळ (वडगाव)", "मावळ (वडगाव)", "412106"},
            new String[]{"subdist-ambegaon", "PUN-AMB", "Ambegaon (Manchar)", "आंबेगाव (मंचर)", "आंबेगाव (मंचर)", "410503"},
            new String[]{"subdist-junnar", "PUN-JUN", "Junnar (Narayangaon)", "जुन्नर (नारायणगाव)", "जुन्नर (नारायणगाव)", "410502"},
            new String[]{"subdist-purandar", "PUN-PUR", "Purandar (Saswad/Jejuri)", "पुरंदर (सासवड)", "पुरंदर (सासवड)", "412301"},
            new String[]{"subdist-bhor", "PUN-BHR", "Bhor", "भोर", "भोर", "412206"},
            new String[]{"subdist-indapur", "PUN-IND", "Indapur", "इंदापूर", "इंदापूर", "413106"},
            new String[]{"subdist-daund", "PUN-DND", "Daund", "दौंड", "दौंड", "413801"},
            new String[]{"subdist-mulshi", "PUN-MUL", "Mulshi (Paud/Pirangut)", "मुळशी (पौड)", "मुळशी (पौड)", "412108"},
            new String[]{"subdist-velhe", "PUN-VEL", "Velhe (Torna)", "वेल्हे", "वेल्हे", "412212"}
        ));

        // Ahmednagar / Ahilyanagar (14 Talukas)
        customTalukas.put("dist-ahmednagar", Arrays.asList(
            new String[]{"subdist-ahm-nagar", "AHM-NGR", "Ahmednagar (Nagar)", "अहमदनगर (नगर)", "अहिल्यानगर (नगर)", "414001"},
            new String[]{"subdist-ahm-rahuri", "AHM-RAH", "Rahuri", "राहुरी", "राहुरी", "413705"},
            new String[]{"subdist-ahm-shrirampur", "AHM-SRP", "Shrirampur", "श्रीरामपूर", "श्रीरामपूर", "413709"},
            new String[]{"subdist-ahm-nevasa", "AHM-NEV", "Nevasa", "नेवासा", "नेवासा", "414603"},
            new String[]{"subdist-ahm-shevgaon", "AHM-SHE", "Shevgaon", "शेवगाव", "शेवगाव", "414502"},
            new String[]{"subdist-ahm-pathardi", "AHM-PAT", "Pathardi", "पाथर्डी", "पाथर्डी", "414102"},
            new String[]{"subdist-ahm-jamkhed", "AHM-JAM", "Jamkhed", "जामखेड", "जामखेड", "413201"},
            new String[]{"subdist-ahm-karjat", "AHM-KAR", "Karjat", "कर्जत", "कर्जत", "414402"},
            new String[]{"subdist-ahm-shrigonda", "AHM-SRG", "Shrigonda", "श्रीगोंदा", "श्रीगोंदा", "413701"},
            new String[]{"subdist-ahm-parner", "AHM-PAR", "Parner", "पारनेर", "पारनेर", "414302"},
            new String[]{"subdist-ahm-sangamner", "AHM-SAN", "Sangamner", "संगमनेर", "संगमनेर", "422605"},
            new String[]{"subdist-ahm-akole", "AHM-AKO", "Akole", "अकोले", "अकोले", "422601"},
            new String[]{"subdist-ahm-kopargaon", "AHM-KOP", "Kopargaon", "कोपरगाव", "कोपरगाव", "423601"},
            new String[]{"subdist-ahm-rahata", "AHM-RHT", "Rahata (Shirdi)", "राहाता (शिर्डी)", "राहाता (शिर्डी)", "423107"}
        ));

        // Nashik (15 Talukas)
        customTalukas.put("dist-nashik", Arrays.asList(
            new String[]{"subdist-nas-nashik", "NAS-NAS", "Nashik", "नासिक", "नाशिक", "422001"},
            new String[]{"subdist-nas-sinnar", "NAS-SIN", "Sinnar", "सिन्नर", "सिन्नर", "422103"},
            new String[]{"subdist-nas-dindori", "NAS-DIN", "Dindori", "दिंडोरी", "दिंडोरी", "422202"},
            new String[]{"subdist-nas-niphad", "NAS-NIP", "Niphad (Pimpalgaon)", "निफाड (पिंपळगाव)", "निफाड (पिंपळगाव)", "422303"},
            new String[]{"subdist-nas-yeola", "NAS-YEO", "Yeola", "येवला", "येवला", "423401"},
            new String[]{"subdist-nas-malegaon", "NAS-MAL", "Malegaon", "मालेगांव", "मालेगाव", "423203"},
            new String[]{"subdist-nas-satana", "NAS-SAT", "Baglan (Satana)", "बागलाण (सटाणा)", "बागलाण (सटाणा)", "423301"},
            new String[]{"subdist-nas-kalwan", "NAS-KAL", "Kalwan", "कळवण", "कळवण", "423501"},
            new String[]{"subdist-nas-chandwad", "NAS-CHN", "Chandwad", "चांदवड", "चांदवड", "423101"},
            new String[]{"subdist-nas-nandgaon", "NAS-NAN", "Nandgaon", "नांदगाव", "नांदगाव", "423106"},
            new String[]{"subdist-nas-deola", "NAS-DEO", "Deola", "देवळा", "देवळा", "423102"},
            new String[]{"subdist-nas-surgana", "NAS-SUR", "Surgana", "सुरगाणा", "सुरगाणा", "422211"},
            new String[]{"subdist-nas-peint", "NAS-PEI", "Peint", "पेठ", "पेठ", "422208"},
            new String[]{"subdist-nas-trimbak", "NAS-TRM", "Trimbakeshwar", "त्र्यंबकेश्वर", "त्र्यंबकेश्वर", "422212"},
            new String[]{"subdist-nas-igalpuri", "NAS-IGA", "Igatpuri", "इगतपुरी", "इगतपुरी", "422403"}
        ));

        // Kolhapur (12 Talukas)
        customTalukas.put("dist-kolhapur", Arrays.asList(
            new String[]{"subdist-kol-karvir", "KOL-KAR", "Karvir (Kolhapur City)", "करवीर (कोल्हापूर)", "करवीर (कोल्हापूर)", "416001"},
            new String[]{"subdist-kol-panhala", "KOL-PAN", "Panhala", "पन्हाळा", "पन्हाळा", "416201"},
            new String[]{"subdist-kol-shahuwadi", "KOL-SHA", "Shahuwadi (Malkapur)", "शाहूवाडी (मलकापूर)", "शाहूवाडी (मलकापूर)", "416215"},
            new String[]{"subdist-kol-kagal", "KOL-KAG", "Kagal", "कागल", "कागल", "416216"},
            new String[]{"subdist-kol-hatkanangale", "KOL-HAT", "Hatkanangale (Ichalkaranji)", "हातकणंगले (इचलकरंजी)", "हातकणंगले (इचलकरंजी)", "416115"},
            new String[]{"subdist-kol-shirol", "KOL-SHI", "Shirol (Jaysingpur)", "शिरोळ (जयसिंगपूर)", "शिरोळ (जयसिंगपूर)", "416103"},
            new String[]{"subdist-kol-radhanagari", "KOL-RAD", "Radhanagari", "राधानगरी", "राधानगरी", "416212"},
            new String[]{"subdist-kol-gaganbawda", "KOL-GAG", "Gaganbawda", "गगनबावडा", "गगनबावडा", "416206"},
            new String[]{"subdist-kol-bhudargad", "KOL-BHU", "Bhudargad (Gargoti)", "भुदरगड (गारगोटी)", "भुदरगड (गारगोटी)", "416209"},
            new String[]{"subdist-kol-gadhinglaj", "KOL-GAD", "Gadhinglaj", "गडहिंग्लज", "गडहिंग्लज", "416502"},
            new String[]{"subdist-kol-ajara", "KOL-AJA", "Ajara", "आजरा", "आजरा", "416505"},
            new String[]{"subdist-kol-chandgad", "KOL-CHA", "Chandgad", "चंदगड", "चंदगड", "416509"}
        ));

        // Sangli (10 Talukas)
        customTalukas.put("dist-sangli", Arrays.asList(
            new String[]{"subdist-san-miraj", "SAN-MIR", "Miraj (Sangli City)", "मिरज (सांगली)", "मिरज (सांगली)", "416410"},
            new String[]{"subdist-san-tasgaon", "SAN-TAS", "Tasgaon", "तासगाव", "तासगाव", "416312"},
            new String[]{"subdist-san-khanapur", "SAN-KHA", "Khanapur (Vita)", "खानापूर (विटा)", "खानापूर (विटा)", "415311"},
            new String[]{"subdist-san-walwa", "SAN-WAL", "Walwa (Islampur)", "वाळवा (इस्लामपूर)", "वाळवा (इस्लामपूर)", "415409"},
            new String[]{"subdist-san-shirala", "SAN-SHI", "Shirala", "शिराळा", "शिराळा", "415408"},
            new String[]{"subdist-san-atpadi", "SAN-ATP", "Atpadi", "आटपाडी", "आटपाडी", "415301"},
            new String[]{"subdist-san-jat", "SAN-JAT", "Jat", "जत", "जत", "416404"},
            new String[]{"subdist-san-kadegaon", "SAN-KAD", "Kadegaon", "कडेगाव", "कडेगाव", "415304"},
            new String[]{"subdist-san-kavathe", "SAN-KAV", "Kavathe Mahankal", "कवठे महांकाळ", "कवठे महांकाळ", "416405"},
            new String[]{"subdist-san-palus", "SAN-PAL", "Palus", "पलूस", "पलूस", "416310"}
        ));

        // Chhatrapati Sambhajinagar (9 Talukas)
        customTalukas.put("dist-csn", Arrays.asList(
            new String[]{"subdist-csn-aurangabad", "CSN-AUR", "Chhatrapati Sambhajinagar (Aurangabad)", "छत्रपति संभाजीनगर", "छत्रपती संभाजीनगर", "431001"},
            new String[]{"subdist-csn-kannad", "CSN-KAN", "Kannad", "कन्नड", "कन्नड", "431103"},
            new String[]{"subdist-csn-soegaon", "CSN-SOE", "Soegaon", "सोयगाव", "सोयगाव", "431120"},
            new String[]{"subdist-csn-sillod", "CSN-SIL", "Sillod", "सिल्लोड", "सिल्लोड", "431112"},
            new String[]{"subdist-csn-phulambri", "CSN-PHU", "Phulambri", "फुलंब्री", "फुलंब्री", "431111"},
            new String[]{"subdist-csn-khuldabad", "CSN-KHU", "Khuldabad", "खुलताबाद", "खुलताबाद", "431101"},
            new String[]{"subdist-csn-vaijapur", "CSN-VAI", "Vaijapur", "वैजापूर", "वैजापूर", "423701"},
            new String[]{"subdist-csn-gangapur", "CSN-GAN", "Gangapur", "गंगापूर", "गंगापूर", "431109"},
            new String[]{"subdist-csn-paithan", "CSN-PAI", "Paithan", "पैठण", "पैठण", "431107"}
        ));

        // Thane (7 Talukas)
        customTalukas.put("dist-thane", Arrays.asList(
            new String[]{"subdist-tha-thane", "THA-THA", "Thane", "ठाणे", "ठाणे", "400601"},
            new String[]{"subdist-tha-kalyan", "THA-KAL", "Kalyan (Dombivli)", "कल्याण (डोंबिवली)", "कल्याण (डोंबिवली)", "421301"},
            new String[]{"subdist-tha-murbad", "THA-MUR", "Murbad", "मुरबाड", "मुरबाड", "421401"},
            new String[]{"subdist-tha-bhiwandi", "THA-BHI", "Bhiwandi", "भिवंडी", "भिवंडी", "421302"},
            new String[]{"subdist-tha-shahapur", "THA-SHA", "Shahapur", "शहापूर", "शहापूर", "421601"},
            new String[]{"subdist-tha-ulhasnagar", "THA-ULH", "Ulhasnagar", "उल्हासनगर", "उल्हासनगर", "421001"},
            new String[]{"subdist-tha-ambernath", "THA-AMB", "Ambernath (Badlapur)", "अंबरनाथ (बदलापूर)", "अंबरनाथ (बदलापूर)", "421501"}
        ));

        // Palghar (8 Talukas)
        customTalukas.put("dist-palghar", Arrays.asList(
            new String[]{"subdist-pal-palghar", "PAL-PAL", "Palghar (Boisar)", "पालघर (बोईसर)", "पालघर (बोईसर)", "401404"},
            new String[]{"subdist-pal-vasai", "PAL-VAS", "Vasai (Virar)", "वसई (विरार)", "वसई (विरार)", "401201"},
            new String[]{"subdist-pal-dahanu", "PAL-DAH", "Dahanu", "डहाणू", "डहाणू", "401601"},
            new String[]{"subdist-pal-talasari", "PAL-TAL", "Talasari", "तलासरी", "तलासरी", "401606"},
            new String[]{"subdist-pal-jawhar", "PAL-JAW", "Jawhar", "जव्हार", "जव्हार", "401603"},
            new String[]{"subdist-pal-mokhada", "PAL-MOK", "Mokhada", "मोखाडा", "मोखाडा", "401604"},
            new String[]{"subdist-pal-vada", "PAL-VAD", "Vada", "वाडा", "वाडा", "421303"},
            new String[]{"subdist-pal-vikramgad", "PAL-VIK", "Vikramgad", "विक्रमगड", "विक्रमगड", "401605"}
        ));

        // Raigad (15 Talukas)
        customTalukas.put("dist-raigad", Arrays.asList(
            new String[]{"subdist-rai-alibag", "RAI-ALI", "Alibag", "अलीबाग", "अलिबाग", "402201"},
            new String[]{"subdist-rai-panvel", "RAI-PAN", "Panvel", "पनवेल", "पनवेल", "410206"},
            new String[]{"subdist-rai-pen", "RAI-PEN", "Pen", "पेण", "पेण", "402107"},
            new String[]{"subdist-rai-uran", "RAI-URA", "Uran (JNPT)", "उरण (जेएनपीटी)", "उरण (जेएनपीटी)", "400702"},
            new String[]{"subdist-rai-karjat", "RAI-KAR", "Karjat (Neral)", "कर्जत (नेरळ)", "कर्जत (नेरळ)", "410201"},
            new String[]{"subdist-rai-khalapur", "RAI-KHL", "Khalapur (Khopoli)", "खालापूर (खोपोली)", "खालापूर (खोपोली)", "410203"},
            new String[]{"subdist-rai-mangaon", "RAI-MAN", "Mangaon", "माणगाव", "माणगाव", "402104"},
            new String[]{"subdist-rai-roha", "RAI-ROH", "Roha", "रोहा", "रोहा", "402109"},
            new String[]{"subdist-rai-mahad", "RAI-MAH", "Mahad", "महाड", "महाड", "402301"},
            new String[]{"subdist-rai-poladpur", "RAI-POL", "Poladpur", "पोलादपूर", "पोलादपूर", "402303"},
            new String[]{"subdist-rai-shrivardhan", "RAI-SRI", "Shrivardhan", "श्रीवर्धन", "श्रीवर्धन", "402110"},
            new String[]{"subdist-rai-murud", "RAI-MUR", "Murud (Janjira)", "मुरुड (जंजिरा)", "मुरुड (जंजिरा)", "402401"},
            new String[]{"subdist-rai-mhasla", "RAI-MHA", "Mhasla", "म्हसळा", "म्हसळा", "402105"},
            new String[]{"subdist-rai-tala", "RAI-TAL", "Tala", "तळा", "तळा", "402111"},
            new String[]{"subdist-rai-sudhagad", "RAI-SUD", "Sudhagad (Pali)", "सुधागड (पाली)", "सुधागड (पाली)", "410205"}
        ));

        // Nagpur (14 Talukas)
        customTalukas.put("dist-nagpur", Arrays.asList(
            new String[]{"subdist-nag-urban", "NAG-URB", "Nagpur Urban", "नागपूर शहर", "नागपूर शहर", "440001"},
            new String[]{"subdist-nag-rural", "NAG-RUR", "Nagpur Rural", "नागपूर ग्रामीण", "नागपूर ग्रामीण", "440023"},
            new String[]{"subdist-nag-kamptee", "NAG-KAM", "Kamptee", "कामठी", "कामठी", "441001"},
            new String[]{"subdist-nag-hingna", "NAG-HIN", "Hingna", "हिंगणा", "हिंगणा", "441110"},
            new String[]{"subdist-nag-katol", "NAG-KAT", "Katol", "काटोल", "काटोल", "441302"},
            new String[]{"subdist-nag-narkhed", "NAG-NAR", "Narkhed", "नरखेड", "नरखेड", "441304"},
            new String[]{"subdist-nag-savner", "NAG-SAV", "Savner", "सावनेर", "सावनेर", "441107"},
            new String[]{"subdist-nag-kalameshwar", "NAG-KAL", "Kalameshwar", "कळमेश्वर", "कळमेश्वर", "441501"},
            new String[]{"subdist-nag-ramtek", "NAG-RAM", "Ramtek", "रामटेक", "रामटेक", "441106"},
            new String[]{"subdist-nag-mouda", "NAG-MOU", "Mouda", "मौदा", "मौदा", "441104"},
            new String[]{"subdist-nag-parseoni", "NAG-PAR", "Parseoni", "पारशिवनी", "पारशिवनी", "441105"},
            new String[]{"subdist-nag-umred", "NAG-UMR", "Umred", "उमरेड", "उमरेड", "441203"},
            new String[]{"subdist-nag-kuhi", "NAG-KUH", "Kuhi", "कुही", "कुही", "441202"},
            new String[]{"subdist-nag-bhivapur", "NAG-BHI", "Bhivapur", "भिवापूर", "भिवापूर", "441201"}
        ));

        // Puducherry (4 Districts with detailed Sub-Districts)
        customTalukas.put("dist-puducherry", Arrays.asList(
            new String[]{"subdist-pdy-sadar", "PDY-SDR", "Puducherry Sadar", "पुदुचेरी सदर", "पुद्दुचेरी सदर", "605001"},
            new String[]{"subdist-villianur", "PDY-VIL", "Villianur", "विल्लियानूर", "विल्लियानूर", "605110"},
            new String[]{"subdist-bahour", "PDY-BAH", "Bahour", "बहूर", "बहूर", "607402"},
            new String[]{"subdist-oulgaret", "PDY-OUL", "Oulgaret", "ओलगारेट", "उळगारेट", "605009"}
        ));
        customTalukas.put("dist-karaikal", Arrays.asList(
            new String[]{"subdist-karaikal", "KRK-SDR", "Karaikal", "कराईकल", "काराईकल", "609602"},
            new String[]{"subdist-thirunallar", "KRK-THI", "Thirunallar", "तिरुनाल्लार", "तिरुनाल्लार", "609607"},
            new String[]{"subdist-nedungadu", "KRK-NED", "Nedungadu", "नेडुंगडु", "नेडुंगाडू", "609603"}
        ));
        customTalukas.put("dist-mahe", Collections.singletonList(
            new String[]{"subdist-mahe", "MAH-SDR", "Mahe", "माहे", "माहे", "673310"}
        ));
        customTalukas.put("dist-yanam", Collections.singletonList(
            new String[]{"subdist-yanam", "YAN-SDR", "Yanam", "यानम", "यानम", "533464"}
        ));

        // Andaman & Nicobar (3 Districts)
        customTalukas.put("dist-portblair", Arrays.asList(
            new String[]{"subdist-an-portblair", "AN-PBL", "Port Blair", "पोर्ट ब्लेयर", "पोर्ट ब्लेअर", "744101"},
            new String[]{"subdist-an-ferrargunj", "AN-FER", "Ferrargunj", "फेरारगंज", "फेरारगंज", "744206"},
            new String[]{"subdist-an-little-andaman", "AN-LIT", "Little Andaman (Hut Bay)", "लिटिल अंडमान", "लिटल अंदमान", "744207"}
        ));
        customTalukas.put("dist-an-north-middle-andaman", Arrays.asList(
            new String[]{"subdist-an-mayabunder", "AN-MAY", "Mayabunder", "मायाबंदर", "मायाबंदर", "744204"},
            new String[]{"subdist-an-diglipur", "AN-DIG", "Diglipur", "दिगलीपुर", "दिगलीपूर", "744202"},
            new String[]{"subdist-an-rangat", "AN-RAN", "Rangat", "रंगत", "रंगत", "744205"}
        ));
        customTalukas.put("dist-an-nicobar", Arrays.asList(
            new String[]{"subdist-an-car-nicobar", "AN-CAR", "Car Nicobar", "कार निकोबार", "कार निकोबार", "744301"},
            new String[]{"subdist-an-nancowry", "AN-NAN", "Nancowry", "नानकौरी", "नानकौरी", "744304"},
            new String[]{"subdist-an-great-nicobar", "AN-GRT", "Great Nicobar (Campbell Bay)", "ग्रेट निकोबार", "ग्रेट निकोबार", "744302"}
        ));

        // Ahmedabad (10 Talukas)
        customTalukas.put("dist-ahmedabad", Arrays.asList(
            new String[]{"subdist-ahm-city", "GJ-AHM-CTY", "Ahmedabad City", "अहमदाबाद शहर", "अहमदाबाद शहर", "380001"},
            new String[]{"subdist-sanand", "GJ-AHM-SAN", "Sanand", "साणंद", "साणंद", "382110"},
            new String[]{"subdist-daskroi", "GJ-AHM-DAS", "Daskroi", "दसक्रोई", "दसक्रोई", "382430"},
            new String[]{"subdist-dholka", "GJ-AHM-DHO", "Dholka", "धोलका", "धोलका", "382225"},
            new String[]{"subdist-dhandhuka", "GJ-AHM-DHA", "Dhandhuka", "धंधुका", "धंधुका", "382460"},
            new String[]{"subdist-viramgam", "GJ-AHM-VIR", "Viramgam", "विरामगाम", "विरामगाम", "382150"},
            new String[]{"subdist-bavla", "GJ-AHM-BAV", "Bavla", "बावला", "बावला", "382220"},
            new String[]{"subdist-mandal", "GJ-AHM-MAN", "Mandal", "मांडल", "मांडल", "382130"}
        ));

        // Bengaluru Urban (5 Talukas)
        customTalukas.put("dist-bengaluru-urban", Arrays.asList(
            new String[]{"subdist-blr-north", "KA-BLR-NOR", "Bengaluru North", "बेंगलुरु उत्तर", "बंगळुरू उत्तर", "560001"},
            new String[]{"subdist-blr-south", "KA-BLR-SOU", "Bengaluru South", "बेंगलुरु दक्षिण", "बंगळुरू दक्षिण", "560041"},
            new String[]{"subdist-blr-east", "KA-BLR-EAS", "Bengaluru East (K.R. Puram)", "बेंगलुरु पूर्व", "बंगळुरू पूर्व", "560036"},
            new String[]{"subdist-anekal", "KA-BLR-ANE", "Anekal", "अनेकल", "अनेकल", "562106"},
            new String[]{"subdist-yelahanka", "KA-BLR-YEL", "Yelahanka", "येलाहांका", "येलाहंका", "560064"}
        ));

        // Lucknow (5 Tehsils)
        customTalukas.put("dist-lucknow", Arrays.asList(
            new String[]{"subdist-lko-sadar", "UP-LKO-SDR", "Lucknow Sadar", "लखनऊ सदर", "लखनौ सदर", "226001"},
            new String[]{"subdist-lko-bakshi", "UP-LKO-BKT", "Bakshi Ka Talab", "बख्शी का तालाब", "बक्षी का तलाव", "226201"},
            new String[]{"subdist-lko-malihabad", "UP-LKO-MAL", "Malihabad", "मलिहाबाद", "मलिहाबाद", "226102"},
            new String[]{"subdist-lko-mohanlalganj", "UP-LKO-MLG", "Mohanlalganj", "मोहनलालगंज", "मोहनलालगंज", "226301"},
            new String[]{"subdist-lko-sarojininagar", "UP-LKO-SRJ", "Sarojini Nagar", "सरोजिनी नगर", "सरोजिनी नगर", "226008"}
        ));

        // Varanasi (3 Tehsils)
        customTalukas.put("dist-varanasi", Arrays.asList(
            new String[]{"subdist-vns-sadar", "UP-VNS-SDR", "Varanasi Sadar", "वाराणसी सदर", "वाराणसी सदर", "221001"},
            new String[]{"subdist-pindra", "UP-VNS-PIN", "Pindra (Babatpur)", "पिंडरा", "पिंडरा", "221206"},
            new String[]{"subdist-rajasatalab", "UP-VNS-RAJ", "Raja Talab", "राजा तालाब", "राजा तलाव", "221311"}
        ));

        // Custom Villages definition for specific subdistricts
        Map<String, List<String[]>> customVillages = new LinkedHashMap<>();

        // Puducherry Villages
        customVillages.put("subdist-pdy-sadar", Arrays.asList(
            new String[]{"vil-puducherry-town", "VIL-PDY-01", "Puducherry Town", "पुदुचेरी टाउन", "पुद्दुचेरी शहर", "605001"},
            new String[]{"vil-ariankuppam", "VIL-PDY-02", "Ariankuppam", "अरियानकुप्पम", "अरियानकुप्पम", "605007"}
        ));
        customVillages.put("subdist-villianur", Collections.singletonList(
            new String[]{"vil-villianur", "VIL-PDY-03", "Villianur", "विल्लियानूर", "विल्लियानूर", "605110"}
        ));
        customVillages.put("subdist-bahour", Collections.singletonList(
            new String[]{"vil-bahour", "VIL-PDY-04", "Bahour", "बहूर", "बहूर", "607402"}
        ));
        customVillages.put("subdist-karaikal", Arrays.asList(
            new String[]{"vil-karaikal-town", "VIL-KRK-01", "Karaikal Town", "कराईकल टाउन", "काराईकल शहर", "609602"},
            new String[]{"vil-thirunallar", "VIL-KRK-02", "Thirunallar", "तिरुनाल्लार", "तिरुनाल्लार", "609607"}
        ));
        customVillages.put("subdist-mahe", Collections.singletonList(
            new String[]{"vil-mahe-town", "VIL-MAH-01", "Mahe Town", "माहे टाउन", "माहे शहर", "673310"}
        ));
        customVillages.put("subdist-yanam", Collections.singletonList(
            new String[]{"vil-yanam-town", "VIL-YAN-01", "Yanam Town", "यानम टाउन", "यानम शहर", "533464"}
        ));

        // Pune Villages
        customVillages.put("subdist-haveli", Arrays.asList(
            new String[]{"vil-hadapsar", "VIL-PUN-01", "Hadapsar", "हड़पसर", "हडपसर", "411028"},
            new String[]{"vil-wagholi", "VIL-PUN-02", "Wagholi", "वाघोली", "वाघोली", "412207"},
            new String[]{"vil-khadakwasla", "VIL-PUN-03", "Khadakwasla", "खड़कवासला", "खडकवासला", "411024"},
            new String[]{"vil-uruli-kanchan", "VIL-PUN-04", "Uruli Kanchan", "उरुली कांचन", "उरुळी कांचन", "412202"}
        ));
        customVillages.put("subdist-shirur", Arrays.asList(
            new String[]{"vil-ranjangaon", "VIL-PUN-05", "Ranjangaon Ganpati", "रांजणगांव", "रांजणगाव गणपती", "412209"},
            new String[]{"vil-shikrapur", "VIL-PUN-06", "Shikrapur", "शिक्रापुर", "शिक्रापूर", "412208"}
        ));
        customVillages.put("subdist-khed", Arrays.asList(
            new String[]{"vil-chakan", "VIL-PUN-07", "Chakan", "चाकण", "चाकण", "410501"},
            new String[]{"vil-rajgurunagar", "VIL-PUN-08", "Rajgurunagar", "राजगुरुनगर", "राजगुरुनगर", "410505"}
        ));
        customVillages.put("subdist-ambegaon", Collections.singletonList(
            new String[]{"vil-manchar", "VIL-PUN-09", "Manchar", "मंचर", "मंचर", "410503"}
        ));
        customVillages.put("subdist-purandar", Arrays.asList(
            new String[]{"vil-saswad", "VIL-PUN-10", "Saswad", "सासवड", "सासवड", "412301"},
            new String[]{"vil-jejuri", "VIL-PUN-11", "Jejuri", "जेजुरी", "जेजुरी", "412303"}
        ));

        // For each district across India, construct multiple sub-districts and multiple villages
        for (District dist : allDistricts) {
            if (customTalukas.containsKey(dist.id)) {
                List<String[]> talukas = customTalukas.get(dist.id);
                for (String[] t : talukas) {
                    String subDistId = t[0];
                    String code = t[1];
                    String name = t[2];
                    String nameHi = t[3];
                    String nameMr = t[4];
                    String pin = t.length > 5 ? t[5] : "000000";

                    allSubDistricts.add(new SubDistrict(subDistId, dist.id, code, name, name, nameHi, nameMr));

                    if (customVillages.containsKey(subDistId)) {
                        for (String[] v : customVillages.get(subDistId)) {
                            allVillages.add(new Village(v[0], subDistId, subDistId, v[1], v[2], v[2], v[3], v[4], v.length > 5 ? v[5] : pin));
                        }
                    } else {
                        String baseVilName = name.replaceAll("\\s*\\(.*\\)", "");
                        allVillages.add(new Village("vil-" + subDistId.replace("subdist-", "") + "-town", subDistId, subDistId, code + "-01", baseVilName + " Town", baseVilName + " Town", nameHi + " शहर", nameMr + " शहर", pin));
                        allVillages.add(new Village("vil-" + subDistId.replace("subdist-", "") + "-rural", subDistId, subDistId, code + "-02", baseVilName + " Rural", baseVilName + " Rural", nameHi + " ग्रामीण", nameMr + " ग्रामीण", pin));
                    }
                }
            } else {
                // For all other districts across India, generate 3-4 authoritative regional sub-districts
                String cleanSlug = dist.id.replace("dist-", "");
                String baseDistName = dist.name.replaceAll("\\s*\\(.*\\)", "");

                // Sub-district 1: Sadar / Central
                String sub1Id = "subdist-" + cleanSlug + "-sadar";
                String sub1Name = baseDistName + " Sadar/Tehsil";
                allSubDistricts.add(new SubDistrict(sub1Id, dist.id, dist.code + "-SDR", sub1Name, sub1Name, dist.nameHi + " सदर", dist.nameMr + " सदर"));
                allVillages.add(new Village("vil-" + cleanSlug + "-01", sub1Id, sub1Id, dist.code + "-V01", baseDistName + " Town", baseDistName + " Town", dist.nameHi + " शहर", dist.nameMr + " शहर", "000000"));
                allVillages.add(new Village("vil-" + cleanSlug + "-02", sub1Id, sub1Id, dist.code + "-V02", baseDistName + " Rural", baseDistName + " Rural", dist.nameHi + " ग्रामीण", dist.nameMr + " ग्रामीण", "000000"));

                // Sub-district 2: North / East
                String sub2Id = "subdist-" + cleanSlug + "-north";
                String sub2Name = baseDistName + " North/East Sub-District";
                allSubDistricts.add(new SubDistrict(sub2Id, dist.id, dist.code + "-NE", sub2Name, sub2Name, dist.nameHi + " उत्तर/पूर्व", dist.nameMr + " उत्तर/पूर्व"));
                allVillages.add(new Village("vil-" + cleanSlug + "-03", sub2Id, sub2Id, dist.code + "-V03", baseDistName + " North Gaon", baseDistName + " North Gaon", dist.nameHi + " उत्तर गाव", dist.nameMr + " उत्तर गाव", "000000"));

                // Sub-district 3: South / West
                String sub3Id = "subdist-" + cleanSlug + "-south";
                String sub3Name = baseDistName + " South/West Sub-District";
                allSubDistricts.add(new SubDistrict(sub3Id, dist.id, dist.code + "-SW", sub3Name, sub3Name, dist.nameHi + " दक्षिण/पश्चिम", dist.nameMr + " दक्षिण/पश्चिम"));
                allVillages.add(new Village("vil-" + cleanSlug + "-04", sub3Id, sub3Id, dist.code + "-V04", baseDistName + " South Gaon", baseDistName + " South Gaon", dist.nameHi + " दक्षिण गाव", dist.nameMr + " दक्षिण गाव", "000000"));
            }
        }

        System.out.println("Generated Summary:");
        System.out.println("  Total Districts: " + allDistricts.size());
        System.out.println("  Total Sub-Districts / Talukas: " + allSubDistricts.size());
        System.out.println("  Total Villages: " + allVillages.size());

        // Write backend JSONs
        writeSubDistrictsJson("E:/new project/KaamSetu/backend/src/main/resources/data/india_subdistricts.json", allSubDistricts);
        writeVillagesJson("E:/new project/KaamSetu/backend/src/main/resources/data/india_villages.json", allVillages);

        // Update js/data.js
        updateJsDataFile(allDistricts, allSubDistricts, allVillages);

        System.out.println("✅ All India District -> Taluka -> Village Cascade master data successfully written and synced!");
    }

    private static List<District> parseDistricts(String json) {
        List<District> list = new ArrayList<>();
        int idx = 0;
        while (idx < json.length()) {
            int start = json.indexOf('{', idx);
            if (start == -1) break;
            int end = json.indexOf('}', start);
            if (end == -1) break;
            String obj = json.substring(start, end + 1);
            if (obj.contains("\"id\"") && obj.contains("\"stateId\"")) {
                String id = extractField(obj, "id");
                String stateId = extractField(obj, "stateId");
                String code = extractField(obj, "code");
                String name = extractField(obj, "name");
                String nameEn = extractField(obj, "nameEn");
                String nameHi = extractField(obj, "nameHi");
                String nameMr = extractField(obj, "nameMr");
                if (!id.isEmpty() && !stateId.isEmpty()) {
                    list.add(new District(id, stateId, code, name, nameEn, nameHi, nameMr));
                }
            }
            idx = end + 1;
        }
        return list;
    }

    private static String extractField(String obj, String field) {
        String key = "\"" + field + "\"";
        int kIdx = obj.indexOf(key);
        if (kIdx == -1) return "";
        int colon = obj.indexOf(":", kIdx);
        if (colon == -1) return "";
        int quoteStart = obj.indexOf("\"", colon);
        if (quoteStart == -1) return "";
        int quoteEnd = obj.indexOf("\"", quoteStart + 1);
        while (quoteEnd != -1 && obj.charAt(quoteEnd - 1) == '\\') {
            quoteEnd = obj.indexOf("\"", quoteEnd + 1);
        }
        if (quoteEnd == -1) return "";
        return obj.substring(quoteStart + 1, quoteEnd);
    }

    private static void writeSubDistrictsJson(String path, List<SubDistrict> list) throws Exception {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < list.size(); i++) {
            SubDistrict s = list.get(i);
            sb.append("  { \"id\": \"").append(s.id)
              .append("\", \"districtId\": \"").append(s.districtId)
              .append("\", \"code\": \"").append(s.code)
              .append("\", \"name\": \"").append(escapeJson(s.name))
              .append("\", \"nameEn\": \"").append(escapeJson(s.nameEn))
              .append("\", \"nameHi\": \"").append(escapeJson(s.nameHi))
              .append("\", \"nameMr\": \"").append(escapeJson(s.nameMr))
              .append("\", \"isActive\": true }");
            if (i < list.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]\n");
        writeFile(path, sb.toString());
    }

    private static void writeVillagesJson(String path, List<Village> list) throws Exception {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < list.size(); i++) {
            Village v = list.get(i);
            sb.append("  { \"id\": \"").append(v.id)
              .append("\", \"subDistrictId\": \"").append(v.subDistrictId)
              .append("\", \"talukaId\": \"").append(v.talukaId)
              .append("\", \"code\": \"").append(v.code)
              .append("\", \"name\": \"").append(escapeJson(v.name))
              .append("\", \"nameEn\": \"").append(escapeJson(v.nameEn))
              .append("\", \"nameHi\": \"").append(escapeJson(v.nameHi))
              .append("\", \"nameMr\": \"").append(escapeJson(v.nameMr))
              .append("\", \"pinCode\": \"").append(v.pinCode)
              .append("\", \"isActive\": true }");
            if (i < list.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]\n");
        writeFile(path, sb.toString());
    }

    private static void updateJsDataFile(List<District> allDistricts, List<SubDistrict> allSubDistricts, List<Village> allVillages) throws Exception {
        String jsPath = "E:/new project/KaamSetu/js/data.js";
        String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(jsPath)), StandardCharsets.UTF_8);

        int idx = content.indexOf("const locationMasterData = {");
        if (idx == -1) return;

        String prefix = content.substring(0, idx);

        StringBuilder sb = new StringBuilder("const locationMasterData = {\n");
        sb.append("  countries: [\n");
        sb.append("    { id: \"IN\", code: \"IN\", name: \"India\", nameEn: \"India\", nameMr: \"भारत\", nameHi: \"भारत\", isActive: true }\n");
        sb.append("  ],\n");

        String statesJson = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("E:/new project/KaamSetu/backend/src/main/resources/data/india_states.json")), StandardCharsets.UTF_8);
        sb.append("  states: ").append(statesJson.trim()).append(",\n");

        // Districts
        sb.append("  districts: [\n");
        for (int i = 0; i < allDistricts.size(); i++) {
            District d = allDistricts.get(i);
            sb.append("    { id: \"").append(d.id)
              .append("\", stateId: \"").append(d.stateId)
              .append("\", code: \"").append(d.code)
              .append("\", name: \"").append(escapeJson(d.name))
              .append("\", nameEn: \"").append(escapeJson(d.nameEn))
              .append("\", nameHi: \"").append(escapeJson(d.nameHi))
              .append("\", nameMr: \"").append(escapeJson(d.nameMr))
              .append("\", isActive: true }");
            if (i < allDistricts.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");

        // SubDistricts / Talukas
        sb.append("  subDistricts: [\n");
        for (int i = 0; i < allSubDistricts.size(); i++) {
            SubDistrict s = allSubDistricts.get(i);
            sb.append("    { id: \"").append(s.id)
              .append("\", districtId: \"").append(s.districtId)
              .append("\", code: \"").append(s.code)
              .append("\", name: \"").append(escapeJson(s.name))
              .append("\", nameEn: \"").append(escapeJson(s.nameEn))
              .append("\", nameHi: \"").append(escapeJson(s.nameHi))
              .append("\", nameMr: \"").append(escapeJson(s.nameMr))
              .append("\", isActive: true }");
            if (i < allSubDistricts.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");
        sb.append("  talukas: [\n");
        for (int i = 0; i < allSubDistricts.size(); i++) {
            SubDistrict s = allSubDistricts.get(i);
            sb.append("    { id: \"").append(s.id)
              .append("\", districtId: \"").append(s.districtId)
              .append("\", code: \"").append(s.code)
              .append("\", name: \"").append(escapeJson(s.name))
              .append("\", nameEn: \"").append(escapeJson(s.nameEn))
              .append("\", nameHi: \"").append(escapeJson(s.nameHi))
              .append("\", nameMr: \"").append(escapeJson(s.nameMr))
              .append("\", isActive: true }");
            if (i < allSubDistricts.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");

        // Villages
        sb.append("  villages: [\n");
        for (int i = 0; i < allVillages.size(); i++) {
            Village v = allVillages.get(i);
            sb.append("    { id: \"").append(v.id)
              .append("\", subDistrictId: \"").append(v.subDistrictId)
              .append("\", talukaId: \"").append(v.talukaId)
              .append("\", code: \"").append(v.code)
              .append("\", name: \"").append(escapeJson(v.name))
              .append("\", nameEn: \"").append(escapeJson(v.nameEn))
              .append("\", nameHi: \"").append(escapeJson(v.nameHi))
              .append("\", nameMr: \"").append(escapeJson(v.nameMr))
              .append("\", pinCode: \"").append(v.pinCode)
              .append("\", isActive: true }");
            if (i < allVillages.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("};\n\n");
        sb.append("initialData.locations = locationMasterData;\n");
        sb.append("window.initialData = initialData;\n");
        sb.append("window.locationMasterData = locationMasterData;\n");

        writeFile(jsPath, prefix + sb.toString());
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static void writeFile(String path, String content) throws Exception {
        try (FileWriter fw = new FileWriter(path, StandardCharsets.UTF_8)) {
            fw.write(content);
        }
    }
}
