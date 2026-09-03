import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GenerateCompleteIndiaData {

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
        System.out.println("Generating Complete All-India Master Location Dataset (780+ Districts across all 36 States/UTs)...");

        Map<String, List<String[]>> stateDistricts = new LinkedHashMap<>();

        // 1. Andhra Pradesh (26)
        stateDistricts.put("state-ap", Arrays.asList(
            new String[]{"dist-ap-alluri", "ASR", "Alluri Sitharama Raju", "अल्लूरी सीताराम राजू", "अल्लुरी सीताराम राजू"},
            new String[]{"dist-ap-anakapalli", "AKP", "Anakapalli", "अनकापल्ली", "अनकापल्ली"},
            new String[]{"dist-ap-anantapur", "ATP", "Anantapur", "अनंतपुर", "अनंतपूर"},
            new String[]{"dist-ap-annamaya", "ANN", "Annamayya", "अन्नामय्या", "अन्नामय्या"},
            new String[]{"dist-ap-bapatla", "BPT", "Bapatla", "बापटला", "बापटला"},
            new String[]{"dist-ap-chittoor", "CTR", "Chittoor", "चित्तूर", "चित्तूर"},
            new String[]{"dist-ap-dr-br-ambedkar-konaseema", "KNS", "Dr. B.R. Ambedkar Konaseema", "डॉ. बी.आर. अंबेडकर कोनसीमा", "डॉ. बी.आर. आंबेडकर कोनसीमा"},
            new String[]{"dist-ap-east-godavari", "EG", "East Godavari (Rajamahendravaram)", "पूर्वी गोदावरी", "पूर्व गोदावरी"},
            new String[]{"dist-ap-eluru", "ELR", "Eluru", "एलुरु", "एलुरु"},
            new String[]{"dist-ap-guntur", "GNT", "Guntur", "गुंटूर", "गुंटूर"},
            new String[]{"dist-ap-kakinada", "KKD", "Kakinada", "काकीनाडा", "काकीनाडा"},
            new String[]{"dist-ap-krishna", "KRI", "Krishna (Machilipatnam)", "कृष्णा", "कृष्णा"},
            new String[]{"dist-ap-kurnool", "KNL", "Kurnool", "कुरनूल", "कुर्नूल"},
            new String[]{"dist-ap-nandyal", "NDL", "Nandyal", "नंद्याल", "नंद्याल"},
            new String[]{"dist-ap-ntr", "NTR", "NTR (Vijayawada)", "एनटीआर (विजयवाड़ा)", "एनटीआर (विजयवाडा)"},
            new String[]{"dist-ap-palnadu", "PLN", "Palnadu (Narasaraopet)", "पलनाडु", "पलनाडू"},
            new String[]{"dist-ap-parvathipuram-manyam", "PVM", "Parvathipuram Manyam", "पार्वतीपुरम मान्यम", "पार्वतीपुरम मान्यम"},
            new String[]{"dist-ap-prakasam", "PRK", "Prakasam (Ongole)", "प्रकाशम", "प्रकाशम"},
            new String[]{"dist-ap-sri-potti-sriramulu-nellore", "NLR", "Sri Potti Sriramulu Nellore", "श्री पोट्टी श्रीरामुलु नेल्लोर", "श्री पोट्टी श्रीरामुलू नेल्लोर"},
            new String[]{"dist-ap-sri-sathya-sai", "SSS", "Sri Sathya Sai (Puttaparthi)", "श्री सत्य साई", "श्री सत्य साई"},
            new String[]{"dist-ap-srikakulam", "SKL", "Srikakulam", "श्रीकाकुलम", "श्रीकाकुलम"},
            new String[]{"dist-ap-tirupati", "TPT", "Tirupati", "तिरुपति", "तिरुपती"},
            new String[]{"dist-visakhapatnam", "VSK", "Visakhapatnam", "विशाखापत्तनम", "विशाखापट्टणम"},
            new String[]{"dist-ap-vizianagaram", "VZM", "Vizianagaram", "विजयनगरम", "विजयनगरम"},
            new String[]{"dist-ap-west-godavari", "WG", "West Godavari (Bhimavaram)", "पश्चिम गोदावरी", "पश्चिम गोदावरी"},
            new String[]{"dist-ap-ysr-kadapa", "YSR", "YSR Kadapa", "वाईएसआर कडपा", "वायएसआर कडप्पा"}
        ));

        // 2. Arunachal Pradesh (26)
        stateDistricts.put("state-ar", Arrays.asList(
            new String[]{"dist-ar-anjaw", "AJW", "Anjaw", "अंजाव", "अंजाव"},
            new String[]{"dist-ar-changlang", "CHG", "Changlang", "चांगलांग", "चांगलांग"},
            new String[]{"dist-ar-dibang-valley", "DBV", "Dibang Valley", "दिबांग घाटी", "दिबांग व्हॅली"},
            new String[]{"dist-ar-east-kameng", "EKM", "East Kameng", "पूर्वी कामेंग", "पूर्व कामेंग"},
            new String[]{"dist-ar-east-siang", "ESI", "East Siang", "पूर्वी सियांग", "पूर्व सियांग"},
            new String[]{"dist-ar-kamle", "KML", "Kamle", "कमले", "कमले"},
            new String[]{"dist-ar-kra-daadi", "KDD", "Kra Daadi", "क्रा दादी", "क्रा दादी"},
            new String[]{"dist-ar-kurung-kumey", "KKM", "Kurung Kumey", "कुरुंग कुमे", "कुरुंग कुमे"},
            new String[]{"dist-ar-lepa-rada", "LRD", "Lepa Rada", "लेपा रादा", "लेपा रादा"},
            new String[]{"dist-ar-lohit", "LHT", "Lohit", "लोहित", "लोहित"},
            new String[]{"dist-ar-longding", "LDG", "Longding", "लॉन्गडिंग", "लॉन्गडिंग"},
            new String[]{"dist-ar-lower-dibang-valley", "LDV", "Lower Dibang Valley", "निचली दिबांग घाटी", "लोअर दिबांग व्हॅली"},
            new String[]{"dist-ar-lower-siang", "LSI", "Lower Siang", "निचला सियांग", "लोअर सियांग"},
            new String[]{"dist-ar-lower-subansiri", "LSB", "Lower Subansiri", "निचला सुबनसिरी", "लोअर सुबनसिरी"},
            new String[]{"dist-ar-namsai", "NMS", "Namsai", "नामसाई", "नामसाई"},
            new String[]{"dist-ar-pakke-kessang", "PKK", "Pakke Kessang", "पक्के केसांग", "पक्के केसांग"},
            new String[]{"dist-ar-papum-pare", "ITA", "Papum Pare (Itanagar)", "पापुम पारे (इटानगर)", "पापुम पारे (इटानगर)"},
            new String[]{"dist-ar-shi-yomi", "SYM", "Shi Yomi", "शी योमी", "शी योमी"},
            new String[]{"dist-ar-siang", "SNG", "Siang", "सियांग", "सियांग"},
            new String[]{"dist-ar-tawang", "TWG", "Tawang", "तवांग", "तवांग"},
            new String[]{"dist-ar-tirap", "TRP", "Tirap", "तिरप", "तिरप"},
            new String[]{"dist-ar-upper-siang", "USI", "Upper Siang", "ऊपरी सियांग", "अप्पर सियांग"},
            new String[]{"dist-ar-upper-subansiri", "USB", "Upper Subansiri", "ऊपरी सुबनसिरी", "अप्पर सुबनसिरी"},
            new String[]{"dist-ar-west-kameng", "WKM", "West Kameng", "पश्चिम कामेंग", "पश्चिम कामेंग"},
            new String[]{"dist-ar-west-siang", "WSI", "West Siang", "पश्चिम सियांग", "पश्चिम सियांग"},
            new String[]{"dist-ar-keyi-panyor", "KYP", "Keyi Panyor", "केयी पन्योर", "केयी पन्योर"}
        ));

        // 3. Assam (35)
        stateDistricts.put("state-as", Arrays.asList(
            new String[]{"dist-as-baksa", "BKS", "Baksa", "बक्सा", "बक्सा"},
            new String[]{"dist-as-barpeta", "BRP", "Barpeta", "बरपेटा", "बरपेटा"},
            new String[]{"dist-as-biswanath", "BSW", "Biswanath", "विश्वनाथ", "विश्वनाथ"},
            new String[]{"dist-as-bongaigaon", "BNG", "Bongaigaon", "बोंगाईगांव", "बोंगाईगाव"},
            new String[]{"dist-as-cachar", "CCH", "Cachar (Silchar)", "कछार (सिलचर)", "कछार (सिलचर)"},
            new String[]{"dist-as-charaideo", "CRD", "Charaideo", "चराइदेव", "चराईदेव"},
            new String[]{"dist-as-chirang", "CRG", "Chirang", "चिरांग", "चिरांग"},
            new String[]{"dist-as-darrang", "DRG", "Darrang", "दरंग", "दरंग"},
            new String[]{"dist-as-dhemaji", "DMJ", "Dhemaji", "धेमाजी", "धेमाजी"},
            new String[]{"dist-as-dhubri", "DHB", "Dhubri", "धुबरी", "धुब्री"},
            new String[]{"dist-as-dibrugarh", "DIB", "Dibrugarh", "डिब्रूगढ़", "दिब्रुगढ"},
            new String[]{"dist-as-dima-hasao", "DMH", "Dima Hasao (Haflong)", "दीमा हसाओ", "दिमा हसाओ"},
            new String[]{"dist-as-goalpara", "GLP", "Goalpara", "गोलपारा", "गोलपारा"},
            new String[]{"dist-as-golaghat", "GLT", "Golaghat", "गोलाघाट", "गोलाघाट"},
            new String[]{"dist-as-hailakandi", "HLK", "Hailakandi", "हैलाकांडी", "हैलाकांडी"},
            new String[]{"dist-as-hojai", "HOJ", "Hojai", "होजाई", "होजाई"},
            new String[]{"dist-as-jorhat", "JRH", "Jorhat", "जोरहाट", "जोरहाट"},
            new String[]{"dist-guwahati", "GHY", "Kamrup Metropolitan (Guwahati)", "कामरूप महानगर (गुवाहाटी)", "कामरूप महानगर (गुवाहाटी)"},
            new String[]{"dist-as-kamrup", "KMR", "Kamrup Rural", "कामरूप ग्रामीण", "कामरूप ग्रामीण"},
            new String[]{"dist-as-karbi-anglong", "KBA", "Karbi Anglong (Diphu)", "कार्बी आंगलोंग", "कार्बी आंगलाँग"},
            new String[]{"dist-as-karimganj", "KMG", "Karimganj (Sribhumi)", "करीमगंज", "करीमगंज"},
            new String[]{"dist-as-kokrajhar", "KKJ", "Kokrajhar", "कोकराझार", "कोकराझार"},
            new String[]{"dist-as-lakhimpur", "LKP", "Lakhimpur", "लखीमपुर", "लखीमपूर"},
            new String[]{"dist-as-majuli", "MJL", "Majuli", "माजुली", "माजुली"},
            new String[]{"dist-as-morigaon", "MRG", "Morigaon", "मोरीगांव", "मोरीगाव"},
            new String[]{"dist-as-nagaon", "NGN", "Nagaon", "नगांव", "नगाव"},
            new String[]{"dist-as-nalbari", "NLB", "Nalbari", "नलबाड़ी", "नलबाडी"},
            new String[]{"dist-as-sivasagar", "SVS", "Sivasagar", "शिवसागर", "शिवसागर"},
            new String[]{"dist-as-sonitpur", "SNP", "Sonitpur (Tezpur)", "सोनितपुर (तेजपुर)", "सोनितपूर (तेजपूर)"},
            new String[]{"dist-as-south-salmara-mankachar", "SSM", "South Salmara-Mankachar", "दक्षिण सलमारा-मानकाचर", "दक्षिण सलमारा-मानकाचर"},
            new String[]{"dist-as-tinsukia", "TSK", "Tinsukia", "तिनसुकिया", "तिनसुकिया"},
            new String[]{"dist-as-udalguri", "UDL", "Udalguri", "उदलगुरी", "उदलगुरी"},
            new String[]{"dist-as-west-karbi-anglong", "WKA", "West Karbi Anglong", "पश्चिम कार्बी आंगलोंग", "पश्चिम कार्बी आंगलाँग"},
            new String[]{"dist-as-bajali", "BJL", "Bajali", "बजाली", "बजाली"},
            new String[]{"dist-as-tamulpur", "TMP", "Tamulpur", "तामुलपुर", "तामुलपूर"}
        ));

        // 4. Bihar (38)
        stateDistricts.put("state-br", Arrays.asList(
            new String[]{"dist-br-araria", "ARA", "Araria", "अररिया", "अररिया"},
            new String[]{"dist-br-arwal", "ARW", "Arwal", "अरवल", "अरवल"},
            new String[]{"dist-br-aurangabad", "AUR", "Aurangabad", "औरंगाबाद (बिहार)", "औरंगाबाद (बिहार)"},
            new String[]{"dist-br-banka", "BNK", "Banka", "बांका", "बांका"},
            new String[]{"dist-br-begusarai", "BGS", "Begusarai", "बेगूसराय", "बेगुसराय"},
            new String[]{"dist-br-bhagalpur", "BGP", "Bhagalpur", "भागलपुर", "भागलपूर"},
            new String[]{"dist-br-bhojpur", "BHO", "Bhojpur (Ara)", "भोजपुर (आरा)", "भोजपूर (आरा)"},
            new String[]{"dist-br-buxar", "BXR", "Buxar", "बक्सर", "बक्सर"},
            new String[]{"dist-br-darbhanga", "DBG", "Darbhanga", "दरभंगा", "दरभंगा"},
            new String[]{"dist-br-east-champaran", "ECH", "East Champaran (Motihari)", "पूर्वी चंपारण (मोतिहारी)", "पूर्व चंपारण (मोतीहारी)"},
            new String[]{"dist-gaya", "GAY", "Gaya", "गया", "गया"},
            new String[]{"dist-br-gopalganj", "GOP", "Gopalganj", "गोपालगंज", "गोपालगंज"},
            new String[]{"dist-br-jamui", "JAM", "Jamui", "जमुई", "जमुई"},
            new String[]{"dist-br-jehanabad", "JEH", "Jehanabad", "जहानाबाद", "जहानाबाद"},
            new String[]{"dist-br-kaimur", "KMR", "Kaimur (Bhabua)", "कैमूर (भभुआ)", "कैमूर (भभुआ)"},
            new String[]{"dist-br-katihar", "KTR", "Katihar", "कटिहार", "कटिहार"},
            new String[]{"dist-br-khagaria", "KHG", "Khagaria", "खगड़िया", "खगडिया"},
            new String[]{"dist-br-kishanganj", "KSG", "Kishanganj", "किशनगंज", "किशनगंज"},
            new String[]{"dist-br-lakhisarai", "LKS", "Lakhisarai", "लखीसराय", "लखीसराय"},
            new String[]{"dist-br-madhepura", "MDP", "Madhepura", "मधेपुरा", "मधेपुरा"},
            new String[]{"dist-br-madhubani", "MDB", "Madhubani", "मधुबनी", "मधुबनी"},
            new String[]{"dist-br-munger", "MNG", "Munger", "मुंगेर", "मुंगेर"},
            new String[]{"dist-muzaffarpur", "MUZ", "Muzaffarpur", "मुजफ्फरपुर", "मुझफ्फरपूर"},
            new String[]{"dist-br-nalanda", "NLN", "Nalanda (Bihar Sharif)", "नालंदा (बिहार शरीफ)", "नालंदा (बिहार शरीफ)"},
            new String[]{"dist-br-nawada", "NWD", "Nawada", "नवादा", "नवादा"},
            new String[]{"dist-patna", "PAT", "Patna", "पटना", "पाटणा"},
            new String[]{"dist-br-purnia", "PUR", "Purnia", "पूर्णिया", "पूर्णिया"},
            new String[]{"dist-br-rohtas", "ROH", "Rohtas (Sasaram)", "रोहतास (सासाराम)", "रोहतास (सासाराम)"},
            new String[]{"dist-br-saharsa", "SHR", "Saharsa", "सहरसा", "सहरसा"},
            new String[]{"dist-br-samastipur", "SAM", "Samastipur", "समस्तीपुर", "समस्तीपूर"},
            new String[]{"dist-br-saran", "SRN", "Saran (Chhapra)", "सारण (छपरा)", "सारण (छपरा)"},
            new String[]{"dist-br-sheikhpura", "SKP", "Sheikhpura", "शेखपुरा", "शेखपुरा"},
            new String[]{"dist-br-sheohar", "SHE", "Sheohar", "शिवहर", "शिवहर"},
            new String[]{"dist-br-sitamarhi", "STM", "Sitamarhi", "सीतामढ़ी", "सीतामढी"},
            new String[]{"dist-br-siwan", "SIW", "Siwan", "सीवान", "सिवान"},
            new String[]{"dist-br-supaul", "SUP", "Supaul", "सुपौल", "सुपौल"},
            new String[]{"dist-br-vaishali", "VSH", "Vaishali (Hajipur)", "वैशाली (हाजीपुर)", "वैशाली (हाजीपूर)"},
            new String[]{"dist-br-west-champaran", "WCH", "West Champaran (Bettiah)", "पश्चिम चंपारण (बेतिया)", "पश्चिम चंपारण (बेतिया)"}
        ));

        // 5. Chhattisgarh (33)
        stateDistricts.put("state-cg", Arrays.asList(
            new String[]{"dist-cg-balod", "BLD", "Balod", "बालोद", "बालोद"},
            new String[]{"dist-cg-baloda-bazar", "BBB", "Baloda Bazar-Bhatapara", "बलौदा बाजार-भाटापारा", "बलौदा बाजार"},
            new String[]{"dist-cg-balrampur", "BLR", "Balrampur-Ramanujganj", "बलरामपुर", "बलरामपूर"},
            new String[]{"dist-cg-bastar", "BST", "Bastar (Jagdalpur)", "बस्तर (जगदलपुर)", "बस्तर (जगदलपूर)"},
            new String[]{"dist-cg-bemetara", "BMT", "Bemetara", "बेमेतरा", "बेमेतरा"},
            new String[]{"dist-cg-bijapur", "BJP", "Bijapur", "बीजापुर", "बिजापूर"},
            new String[]{"dist-cg-bilaspur", "BIL", "Bilaspur", "बिलासपुर", "बिलासपूर"},
            new String[]{"dist-cg-dantewada", "DAN", "Dantewada (South Bastar)", "दंतेवाड़ा", "दंतेवाडा"},
            new String[]{"dist-cg-dhamtari", "DHM", "Dhamtari", "धमतरी", "धमतरी"},
            new String[]{"dist-cg-durg", "DUR", "Durg", "दुर्ग", "दुर्ग"},
            new String[]{"dist-cg-gariaband", "GBD", "Gariaband", "गरियाबंद", "गरियाबंद"},
            new String[]{"dist-cg-gaurela-pendra-marwahi", "GPM", "Gaurela-Pendra-Marwahi", "गौरेला-पेंड्रा-मरवाही", "गौरेला-पेंड्रा-मरवाही"},
            new String[]{"dist-cg-janjgir-champa", "JJC", "Janjgir-Champa", "जांजगीर-चांपा", "जांजगीर-चांपा"},
            new String[]{"dist-cg-jashpur", "JSH", "Jashpur", "जशपुर", "जशपूर"},
            new String[]{"dist-cg-kabirdham", "KBD", "Kabirdham (Kawardha)", "कबीरधाम (कवर्धा)", "कबीरधाम"},
            new String[]{"dist-cg-kanker", "KNK", "Kanker (North Bastar)", "कांकेर", "कांकेर"},
            new String[]{"dist-cg-kondagaon", "KND", "Kondagaon", "कोंडागांव", "कोंडागाव"},
            new String[]{"dist-cg-korba", "KRB", "Korba", "कोरबा", "कोरबा"},
            new String[]{"dist-cg-korea", "KOR", "Korea (Baikunthpur)", "कोरिया", "कोरिया"},
            new String[]{"dist-cg-mahasamund", "MHS", "Mahasamund", "महासमुंद", "महासमुंद"},
            new String[]{"dist-cg-manendragarh-chirmiri-bharatpur", "MCB", "Manendragarh-Chirmiri-Bharatpur", "मनेंद्रगढ़-चिरमिरी-भरतपुर", "मनेंद्रगढ-चिरमिरी-भरतपूर"},
            new String[]{"dist-cg-mohla-manpur-ambagarh-chowki", "MMA", "Mohla-Manpur-Ambagarh Chowki", "मोहला-मानपुर-अंबागढ़ चौकी", "मोहला-मानपूर-अंबागड चौकी"},
            new String[]{"dist-cg-mungeli", "MNG", "Mungeli", "मुंगेली", "मुंगेली"},
            new String[]{"dist-cg-narayanpur", "NRY", "Narayanpur", "नारायणपुर", "नारायणपूर"},
            new String[]{"dist-cg-raigarh", "RGH", "Raigarh", "रायगढ़", "रायगड (छतीसगड)"},
            new String[]{"dist-raipur", "RAI", "Raipur", "रायपुर", "रायपूर"},
            new String[]{"dist-cg-rajnandgaon", "RJN", "Rajnandgaon", "राजनांदगांव", "राजनांदगाव"},
            new String[]{"dist-cg-sarangarh-bilaigarh", "SGB", "Sarangarh-Bilaigarh", "सारंगगढ़-बिलाईगढ़", "सारंगड-बिलाईगड"},
            new String[]{"dist-cg-sakti", "SKT", "Sakti", "सक्ती", "सक्ती"},
            new String[]{"dist-cg-sukma", "SKM", "Sukma", "सुकमा", "सुकमा"},
            new String[]{"dist-cg-surajpur", "SJP", "Surajpur", "सूरजपुर", "सूरजपूर"},
            new String[]{"dist-cg-surguja", "SRG", "Surguja (Ambikapur)", "सरगुजा (अंबिकापुर)", "सरगुजा (अंबिकापूर)"},
            new String[]{"dist-cg-khairagarh-chhuikhadan-gandai", "KCG", "Khairagarh-Chhuikhadan-Gandai", "खैरागढ़-छुईखदान-गंडई", "खैरागढ-छुईखदान-गंडई"}
        ));

        // 6. Goa (2)
        stateDistricts.put("state-ga", Arrays.asList(
            new String[]{"dist-north-goa", "NGOA", "North Goa (Panaji)", "उत्तर गोवा (पणजी)", "उत्तर गोवा (पणजी)"},
            new String[]{"dist-south-goa", "SGOA", "South Goa (Margao)", "दक्षिण गोवा (मडगांव)", "दक्षिण गोवा (मडगाव)"}
        ));

        // 7. Gujarat (33)
        stateDistricts.put("state-gj", Arrays.asList(
            new String[]{"dist-ahmedabad", "AHM", "Ahmedabad", "अहमदाबाद", "अहमदाबाद"},
            new String[]{"dist-gj-amreli", "AMR", "Amreli", "अमरेली", "अमरेली"},
            new String[]{"dist-gj-anand", "AND", "Anand", "आणंद", "आणंद"},
            new String[]{"dist-gj-aravalli", "ARV", "Aravalli (Modasa)", "अरवल्ली (मोडासा)", "अरवल्ली"},
            new String[]{"dist-gj-banaskantha", "BNK", "Banaskantha (Palanpur)", "बनासकांठा (पालनपुर)", "बनासकांठा"},
            new String[]{"dist-gj-bharuch", "BHR", "Bharuch", "भरूच", "भरूच"},
            new String[]{"dist-gj-bhavnagar", "BHV", "Bhavnagar", "भावनगर", "भावनगर"},
            new String[]{"dist-gj-botad", "BTD", "Botad", "बोटाद", "बोटाद"},
            new String[]{"dist-gj-chhota-udepur", "CHU", "Chhota Udepur", "छोटा उदेपुर", "छोटा उदेपूर"},
            new String[]{"dist-gj-dahod", "DHD", "Dahod", "दाहोद", "दाहोद"},
            new String[]{"dist-gj-dang", "DNG", "Dang (Ahwa)", "डांग (आहवा)", "डांग"},
            new String[]{"dist-gj-devbhumi-dwarka", "DBD", "Devbhumi Dwarka (Khambhalia)", "देवभूमि द्वारका", "देवभूमी द्वारका"},
            new String[]{"dist-gj-gandhinagar", "GND", "Gandhinagar", "गांधीनगर", "गांधीनगर"},
            new String[]{"dist-gj-gir-somnath", "GSM", "Gir Somnath (Veraval)", "गिर सोमनाथ (वेरावल)", "गीर सोमनाथ"},
            new String[]{"dist-gj-jamnagar", "JAM", "Jamnagar", "जामनगर", "जामनगर"},
            new String[]{"dist-gj-junagadh", "JND", "Junagadh", "जूनागढ़", "जुनागढ"},
            new String[]{"dist-gj-kheda", "KHD", "Kheda (Nadiad)", "खेड़ा (नडियाद)", "खेडा"},
            new String[]{"dist-gj-kutch", "KTC", "Kutch (Bhuj)", "कच्छ (भुज)", "कच्छ (भूज)"},
            new String[]{"dist-gj-mahisagar", "MHS", "Mahisagar (Lunawada)", "महिसागर (लूनावाड़ा)", "महिसागर"},
            new String[]{"dist-gj-mehsana", "MSN", "Mehsana", "मेहसाणा", "मेहसाणा"},
            new String[]{"dist-gj-morbi", "MRB", "Morbi", "मोरबी", "मोरबी"},
            new String[]{"dist-gj-narmada", "NRM", "Narmada (Rajpipla)", "नर्मदा (राजपीपला)", "नर्मदा"},
            new String[]{"dist-gj-navsari", "NVS", "Navsari", "नवसारी", "नवसारी"},
            new String[]{"dist-gj-panchmahal", "PNM", "Panchmahal (Godhra)", "पंचमहाल (गोधरा)", "पंचमहाल"},
            new String[]{"dist-gj-patan", "PTN", "Patan", "पाटन", "पाटण"},
            new String[]{"dist-gj-porbandar", "PBD", "Porbandar", "पोरबंदर", "पोरबंदर"},
            new String[]{"dist-rajkot", "RAJ", "Rajkot", "राजकोट", "राजकोट"},
            new String[]{"dist-gj-sabarkantha", "SBK", "Sabarkantha (Himmatnagar)", "साबरकांठा (हिम्मतनगर)", "साबरकांठा"},
            new String[]{"dist-surat", "SUR", "Surat", "सूरत", "सुरत"},
            new String[]{"dist-gj-surendranagar", "SRN", "Surendranagar", "सुरेंद्रनगर", "सुरेंद्रनगर"},
            new String[]{"dist-gj-tapi", "TAP", "Tapi (Vyara)", "तापी (व्यारा)", "तापी"},
            new String[]{"dist-vadodara", "VAD", "Vadodara", "वडोदरा", "वडोदरा (बडोदा)"},
            new String[]{"dist-gj-valsad", "VLS", "Valsad", "वलसाड", "वलसाड"}
        ));

        // 8. Haryana (22)
        stateDistricts.put("state-hr", Arrays.asList(
            new String[]{"dist-hr-ambala", "AMB", "Ambala", "अंबाला", "अंबाला"},
            new String[]{"dist-hr-bhiwani", "BHW", "Bhiwani", "भिवानी", "भिवानी"},
            new String[]{"dist-hr-charkhi-dadri", "CKD", "Charkhi Dadri", "चरखी दादरी", "चरखी दादरी"},
            new String[]{"dist-faridabad", "FAR", "Faridabad", "फरीदाबाद", "फरीदाबाद"},
            new String[]{"dist-hr-fatehabad", "FTB", "Fatehabad", "फतेहाबाद", "फतेहाबाद"},
            new String[]{"dist-gurugram", "GUR", "Gurugram (Gurgaon)", "गुरुग्राम", "गुरुग्राम (गुडगाव)"},
            new String[]{"dist-hr-hisar", "HIS", "Hisar", "हिसार", "हिसार"},
            new String[]{"dist-hr-jhajjar", "JHJ", "Jhajjar", "झज्जर", "झज्जर"},
            new String[]{"dist-hr-jind", "JND", "Jind", "जींद", "जिंद"},
            new String[]{"dist-hr-kaithal", "KTH", "Kaithal", "कैथल", "कैथल"},
            new String[]{"dist-hr-karnal", "KRN", "Karnal", "करनाल", "करनाल"},
            new String[]{"dist-hr-kurukshetra", "KRK", "Kurukshetra", "कुरुक्षेत्र", "कुरुक्षेत्र"},
            new String[]{"dist-hr-mahendragarh", "MHG", "Mahendragarh (Narnaul)", "महेंद्रगढ़", "महेंद्रगड"},
            new String[]{"dist-hr-nuh", "NUH", "Nuh (Mewat)", "नूंह (मेवात)", "नुह (मेवात)"},
            new String[]{"dist-hr-palwal", "PLW", "Palwal", "पलवल", "पलवल"},
            new String[]{"dist-hr-panchkula", "PKL", "Panchkula", "पंचकुला", "पंचकुला"},
            new String[]{"dist-hr-panipat", "PNP", "Panipat", "पानीपत", "पानिपत"},
            new String[]{"dist-hr-rewari", "REW", "Rewari", "रेवाड़ी", "रेवाडी"},
            new String[]{"dist-hr-rohtak", "ROH", "Rohtak", "रोहतक", "रोहतक"},
            new String[]{"dist-hr-sirsa", "SIR", "Sirsa", "सिरसा", "सिरसा"},
            new String[]{"dist-hr-sonipat", "SON", "Sonipat", "सोनीपत", "सोनिपत"},
            new String[]{"dist-hr-yamunanagar", "YMN", "Yamunanagar", "यमुनानगर", "यमुनानगर"}
        ));

        // 9. Himachal Pradesh (12)
        stateDistricts.put("state-hp", Arrays.asList(
            new String[]{"dist-hp-bilaspur", "BIL", "Bilaspur", "बिलासपुर (हि.प्र.)", "बिलासपूर (हिमाचल)"},
            new String[]{"dist-hp-chamba", "CHM", "Chamba", "चंबा", "चंबा"},
            new String[]{"dist-hp-hamirpur", "HMR", "Hamirpur", "हमीरपुर", "हमीरपूर"},
            new String[]{"dist-hp-kangra", "KNG", "Kangra (Dharamshala)", "कांगड़ा (धर्मशाला)", "कांगडा"},
            new String[]{"dist-hp-kinnaur", "KNN", "Kinnaur (Reckong Peo)", "किन्नौर", "किन्नौर"},
            new String[]{"dist-hp-kullu", "KUL", "Kullu", "कुल्लू", "कुल्लू"},
            new String[]{"dist-hp-lahaul-spiti", "LHS", "Lahaul and Spiti (Keylong)", "लाहौल और स्पीति", "लाहौल आणि स्पीती"},
            new String[]{"dist-hp-mandi", "MND", "Mandi", "मंडी", "मंडी"},
            new String[]{"dist-shimla", "SHI", "Shimla", "शिमला", "शिमला"},
            new String[]{"dist-hp-sirmaur", "SRM", "Sirmaur (Nahan)", "सिरमौर (नाहन)", "सिरमौर"},
            new String[]{"dist-hp-solan", "SOL", "Solan", "सोलन", "सोलन"},
            new String[]{"dist-hp-una", "UNA", "Una", "ऊना", "उना"}
        ));

        // 10. Jharkhand (24)
        stateDistricts.put("state-jh", Arrays.asList(
            new String[]{"dist-jh-bokaro", "BOK", "Bokaro", "बोकारो", "बोकारो"},
            new String[]{"dist-jh-chatra", "CHT", "Chatra", "चतरा", "चतरा"},
            new String[]{"dist-jh-deoghar", "DGH", "Deoghar", "देवघर", "देवघर"},
            new String[]{"dist-jh-dhanbad", "DHN", "Dhanbad", "धनबाद", "धनबाद"},
            new String[]{"dist-jh-dumka", "DMK", "Dumka", "दुमका", "दुमका"},
            new String[]{"dist-jamshedpur", "JSR", "East Singhbhum (Jamshedpur)", "पूर्वी सिंहभूम (जमशेदपुर)", "पूर्व सिंगभूम (जमशेदपूर)"},
            new String[]{"dist-jh-garhwa", "GRH", "Garhwa", "गढ़वा", "गढवा"},
            new String[]{"dist-jh-giridih", "GRD", "Giridih", "गिरिडीह", "गिरिडीह"},
            new String[]{"dist-jh-godda", "GOD", "Godda", "गोड्डा", "गोड्डा"},
            new String[]{"dist-jh-gumla", "GML", "Gumla", "गुमला", "गुमला"},
            new String[]{"dist-jh-hazaribagh", "HZB", "Hazaribagh", "हजारीबाग", "हजारीबाग"},
            new String[]{"dist-jh-jamtara", "JMT", "Jamtara", "जामताड़ा", "जामताडा"},
            new String[]{"dist-jh-khunti", "KHT", "Khunti", "खूंटी", "खुंटी"},
            new String[]{"dist-jh-koderma", "KOD", "Koderma", "कोडरमा", "कोडरमा"},
            new String[]{"dist-jh-latehar", "LTH", "Latehar", "लातेहार", "लातेहार"},
            new String[]{"dist-jh-lohardaga", "LHD", "Lohardaga", "लोहरदगा", "लोहरदगा"},
            new String[]{"dist-jh-pakur", "PAK", "Pakur", "पाकुड़", "पाकुड"},
            new String[]{"dist-jh-palamu", "PLM", "Palamu (Medininagar)", "पलामू", "पलामू"},
            new String[]{"dist-jh-ramgarh", "RMG", "Ramgarh", "रामगढ़", "रामगड"},
            new String[]{"dist-ranchi", "RAN", "Ranchi", "राँची", "रांची"},
            new String[]{"dist-jh-sahibganj", "SBG", "Sahibganj", "साहिबगंज", "साहिबगंज"},
            new String[]{"dist-jh-saraikela-kharsawan", "SKK", "Seraikela Kharsawan", "सरायकेला खरसावां", "सरायकेला खरसावा"},
            new String[]{"dist-jh-simdega", "SMD", "Simdega", "सिमडेगा", "सिमडेगा"},
            new String[]{"dist-jh-west-singhbhum", "WSB", "West Singhbhum (Chaibasa)", "पश्चिम सिंहभूम (चाईबासा)", "पश्चिम सिंगभूम (चाईबासा)"}
        ));

        // 11. Karnataka (31)
        stateDistricts.put("state-ka", Arrays.asList(
            new String[]{"dist-ka-bagalkote", "BGK", "Bagalkote", "बागलकोट", "बागलकोट"},
            new String[]{"dist-ka-ballari", "BAL", "Ballari (Bellary)", "बल्लारी", "बेळ्ळारी"},
            new String[]{"dist-belagavi", "BEL", "Belagavi (Belgaum)", "बेलगावी (बेलगाम)", "बेळगाव"},
            new String[]{"dist-ka-bengaluru-rural", "BRU", "Bengaluru Rural", "बेंगलुरु ग्रामीण", "बंगळुरू ग्रामीण"},
            new String[]{"dist-bengaluru-urban", "BLR", "Bengaluru Urban", "बेंगलुरु शहरी", "बंगळुरू शहर"},
            new String[]{"dist-ka-bidar", "BID", "Bidar", "बीदर", "बिदर"},
            new String[]{"dist-ka-chamarajanagara", "CMR", "Chamarajanagara", "चामराजनगर", "चामराजनगर"},
            new String[]{"dist-ka-chikkaballapura", "CKB", "Chikkaballapura", "चिक्कबल्लापुर", "चिक्कबल्लापूर"},
            new String[]{"dist-ka-chikkamagaluru", "CKM", "Chikkamagaluru", "चिकमगलूर", "चिक्कमंगळूर"},
            new String[]{"dist-ka-chitradurga", "CTD", "Chitradurga", "चित्रदुर्ग", "चित्रदुर्ग"},
            new String[]{"dist-dakshina-kannada", "DKN", "Dakshina Kannada (Mangaluru)", "दक्षिण कन्नड़ (मंगलुरु)", "दक्षिण कन्नड (मंगळुरू)"},
            new String[]{"dist-ka-davanagere", "DVG", "Davanagere", "दावणगेरे", "दावणगेरे"},
            new String[]{"dist-hubballi", "DHR", "Dharwad (Hubballi)", "धारवाड़ (हुबली)", "धारवाड (हुबळी)"},
            new String[]{"dist-ka-gadag", "GDG", "Gadag", "गदग", "गदग"},
            new String[]{"dist-ka-hassan", "HSN", "Hassan", "हासन", "हासन"},
            new String[]{"dist-ka-haveri", "HVR", "Haveri", "हावेरी", "हावेरी"},
            new String[]{"dist-ka-kalaburagi", "KLB", "Kalaburagi (Gulbarga)", "कलबुर्गी (गुलबर्गा)", "कलबुर्गी (गुलबर्गा)"},
            new String[]{"dist-ka-kodagu", "KDG", "Kodagu (Madikeri)", "कोडगु (मडिकेरी)", "कुर्ग (कोडगु)"},
            new String[]{"dist-ka-kolar", "KLR", "Kolar", "कोलार", "कोलार"},
            new String[]{"dist-ka-koppal", "KPL", "Koppal", "कोप्पल", "कोप्पल"},
            new String[]{"dist-ka-mandya", "MND", "Mandya", "मांड्या", "मांड्या"},
            new String[]{"dist-mysuru", "MYS", "Mysuru (Mysore)", "मैसूरु", "म्हैसूर (म्हैसुरू)"},
            new String[]{"dist-ka-raichur", "RCH", "Raichur", "रायचूर", "रायचूर"},
            new String[]{"dist-ka-ramanagara", "RMG", "Ramanagara", "रामनगर", "रामनगर"},
            new String[]{"dist-ka-shivamogga", "SHV", "Shivamogga (Shimoga)", "शिवमोग्गा", "शिवमोग्गा (शिमोगा)"},
            new String[]{"dist-ka-tumakuru", "TMK", "Tumakuru (Tumkur)", "तुमकुरु", "तुमकुरू"},
            new String[]{"dist-ka-udupi", "UDP", "Udupi", "उडुपी", "उडूपी"},
            new String[]{"dist-ka-uttara-kannada", "UKN", "Uttara Kannada (Karwar)", "उत्तर कन्नड़ (कारवार)", "उत्तर कन्नड (कारवार)"},
            new String[]{"dist-ka-vijayanagara", "VJN", "Vijayanagara (Hosapete)", "विजयनगर (होसपेट)", "विजयनगर"},
            new String[]{"dist-ka-vijayapura", "VJP", "Vijayapura (Bijapur)", "विजयपुरा (बीजापुर)", "विजापूर (विजयपुरा)"},
            new String[]{"dist-ka-yadgir", "YDG", "Yadgir", "यादगीर", "यादगीर"}
        ));

        // 12. Kerala (14)
        stateDistricts.put("state-kl", Arrays.asList(
            new String[]{"dist-kl-alappuzha", "ALP", "Alappuzha (Alleppey)", "अलप्पुझा (अलेप्पी)", "अलप्पुळा (अलेप्पी)"},
            new String[]{"dist-ernakulam", "EKM", "Ernakulam (Kochi)", "एर्नाकुलम (कोच्चि)", "एर्नाकुलम (कोची)"},
            new String[]{"dist-kl-idukki", "IDK", "Idukki (Painavu)", "इडुक्की", "इडुक्की"},
            new String[]{"dist-kl-kannur", "KNR", "Kannur", "कन्नूर", "कन्नूर"},
            new String[]{"dist-kl-kasaragod", "KSG", "Kasaragod", "कासरगोड", "कासारगोड"},
            new String[]{"dist-kl-kollam", "KLM", "Kollam (Quilon)", "कोल्लम", "कोल्लम"},
            new String[]{"dist-kl-kottayam", "KTM", "Kottayam", "कोट्टायम", "कोट्टायम"},
            new String[]{"dist-kl-kozhikode", "KKD", "Kozhikode (Calicut)", "कोझिकोड (कालिकट)", "कोझिकोड (कालिकत)"},
            new String[]{"dist-kl-malappuram", "MLP", "Malappuram", "मलप्पुरम", "मलप्पुरम"},
            new String[]{"dist-kl-palakkad", "PLK", "Palakkad", "पलक्कड़", "पालक्काड"},
            new String[]{"dist-kl-pathanamthitta", "PTA", "Pathanamthitta", "पत्तनमथिट्टा", "पठाणमथिट्टा"},
            new String[]{"dist-thiruvananthapuram", "TVM", "Thiruvananthapuram (Trivandrum)", "तिरुवनंतपुरम", "तिरुवनंतपुरम (त्रिवेंद्रम)"},
            new String[]{"dist-kl-thrissur", "TCR", "Thrissur", "त्रिशूर", "त्रिसूर"},
            new String[]{"dist-kl-wayanad", "WYD", "Wayanad (Kalpetta)", "वायनाड", "वायनाड"}
        ));

        // 13. Madhya Pradesh (55)
        stateDistricts.put("state-mp", Arrays.asList(
            new String[]{"dist-mp-agar-malwa", "AGM", "Agar Malwa", "आगर मालवा", "आगर माळवा"},
            new String[]{"dist-mp-alirajpur", "ALR", "Alirajpur", "अलीराजपुर", "अलिराजपूर"},
            new String[]{"dist-mp-anuppur", "ANP", "Anuppur", "अनूपपुर", "अनुपपूर"},
            new String[]{"dist-mp-ashoknagar", "ASN", "Ashoknagar", "अशोकनगर", "अशोकनगर"},
            new String[]{"dist-mp-balaghat", "BLG", "Balaghat", "बालाघाट", "बालाघाट"},
            new String[]{"dist-mp-barwani", "BRW", "Barwani", "बड़वानी", "बडवानी"},
            new String[]{"dist-mp-betul", "BTL", "Betul", "बैतूल", "बेतूल"},
            new String[]{"dist-mp-bhind", "BHN", "Bhind", "भिंड", "भिंड"},
            new String[]{"dist-bhopal", "BHO", "Bhopal", "भोपाल", "भोपाळ"},
            new String[]{"dist-mp-burhanpur", "BHP", "Burhanpur", "बुरहानपुर", "बुरहानपूर"},
            new String[]{"dist-mp-chhatarpur", "CHT", "Chhatarpur", "छतरपुर", "छतरपूर"},
            new String[]{"dist-mp-chhindwara", "CHN", "Chhindwara", "छिंदवाड़ा", "छिंदवाडा"},
            new String[]{"dist-mp-damoh", "DMH", "Damoh", "दमोह", "दमोह"},
            new String[]{"dist-mp-datia", "DTI", "Datia", "दतिया", "दतिया"},
            new String[]{"dist-mp-dewas", "DEW", "Dewas", "देवास", "देवास"},
            new String[]{"dist-mp-dhar", "DHR", "Dhar", "धार", "धार"},
            new String[]{"dist-mp-dindori", "DND", "Dindori", "डिंडोरी", "दिंडोरी"},
            new String[]{"dist-mp-guna", "GUN", "Guna", "गुना", "गुना"},
            new String[]{"dist-gwalior", "GWA", "Gwalior", "ग्वालियर", "ग्वाल्हेर"},
            new String[]{"dist-mp-harda", "HRD", "Harda", "हरदा", "हरदा"},
            new String[]{"dist-mp-narmadapuram", "HSH", "Narmadapuram (Hoshangabad)", "नर्मदापुरम (होशंगाबाद)", "नर्मदापुरम"},
            new String[]{"dist-indore", "IND", "Indore", "इंदौर", "इंदूर"},
            new String[]{"dist-mp-jabalpur", "JBL", "Jabalpur", "जबलपुर", "जबलपूर"},
            new String[]{"dist-mp-jhabua", "JHB", "Jhabua", "झाबुआ", "झाबुआ"},
            new String[]{"dist-mp-katni", "KTN", "Katni", "कटनी", "कटनी"},
            new String[]{"dist-mp-khandwa", "KHD", "Khandwa (East Nimar)", "खंडवा", "खंडवा"},
            new String[]{"dist-mp-khargone", "KHG", "Khargone (West Nimar)", "खरगोन", "खरगोन"},
            new String[]{"dist-mp-mandla", "MDL", "Mandla", "मंडला", "मंडला"},
            new String[]{"dist-mp-mandsaur", "MND", "Mandsaur", "मंदसौर", "मंदसौर"},
            new String[]{"dist-mp-morena", "MRN", "Morena", "मुरैना", "मुरैना"},
            new String[]{"dist-mp-narsinghpur", "NSP", "Narsinghpur", "नरसिंहपुर", "नरसिंगपूर"},
            new String[]{"dist-mp-neemuch", "NMC", "Neemuch", "नीमच", "नीमच"},
            new String[]{"dist-mp-niwari", "NWR", "Niwari", "निवाड़ी", "निवाडी"},
            new String[]{"dist-mp-panna", "PNN", "Panna", "पन्ना", "पन्ना"},
            new String[]{"dist-mp-raisen", "RSN", "Raisen", "रायसेन", "रायसेन"},
            new String[]{"dist-mp-rajgarh", "RJG", "Rajgarh", "राजगढ़", "राजगड"},
            new String[]{"dist-mp-ratlam", "RTL", "Ratlam", "रतलाम", "रतलाम"},
            new String[]{"dist-mp-rewa", "REW", "Rewa", "रीवा", "रेवा (रीवा)"},
            new String[]{"dist-mp-sagar", "SGR", "Sagar", "सागर", "सागर"},
            new String[]{"dist-mp-satna", "STN", "Satna", "सतना", "सतना"},
            new String[]{"dist-mp-sehore", "SHR", "Sehore", "सीहोर", "सिहोर"},
            new String[]{"dist-mp-seoni", "SNI", "Seoni", "सिवनी", "शिवनी"},
            new String[]{"dist-mp-shahdol", "SHD", "Shahdol", "शहडोल", "शहडोल"},
            new String[]{"dist-mp-shajapur", "SJP", "Shajapur", "शाजापुर", "शाजापूर"},
            new String[]{"dist-mp-sheopur", "SHP", "Sheopur", "श्योपुर", "श्योपूर"},
            new String[]{"dist-mp-shivpuri", "SVP", "Shivpuri", "शिवपुरी", "शिवपुरी"},
            new String[]{"dist-mp-sidhi", "SDH", "Sidhi", "सीधी", "सिधी"},
            new String[]{"dist-mp-singrauli", "SNG", "Singrauli", "सिंगरौली", "सिंगरौली"},
            new String[]{"dist-mp-tikamgarh", "TKM", "Tikamgarh", "टीकमगढ़", "टिकमगड"},
            new String[]{"dist-mp-ujjain", "UJN", "Ujjain", "उज्जैन", "उज्जैन"},
            new String[]{"dist-mp-umaria", "UMR", "Umaria", "उमरिया", "उमरिया"},
            new String[]{"dist-mp-vidisha", "VDS", "Vidisha", "विदिशा", "विदिशा"},
            new String[]{"dist-mp-mauganj", "MGJ", "Mauganj", "मऊगंज", "मऊगंज"},
            new String[]{"dist-mp-maihar", "MHR", "Maihar", "मैहर", "मैहर"},
            new String[]{"dist-mp-pandhurna", "PDH", "Pandhurna", "पांढुरना", "पांढुर्णा"}
        ));

        // 14. Maharashtra (36)
        stateDistricts.put("state-mh", Arrays.asList(
            new String[]{"dist-ahmednagar", "AHM", "Ahmednagar (Ahilyanagar)", "अहमदनगर", "अहिल्यानगर (अहमदनगर)"},
            new String[]{"dist-mh-akola", "AKL", "Akola", "अकोला", "अकोला"},
            new String[]{"dist-amravati", "AMR", "Amravati", "अमरावती", "अमरावती"},
            new String[]{"dist-csn", "CSN", "Chhatrapati Sambhajinagar", "छत्रपति संभाजीनगर", "छत्रपती संभाजीनगर (औरंगाबाद)"},
            new String[]{"dist-mh-beed", "BEE", "Beed (Dharashiv)", "बीड", "बीड"},
            new String[]{"dist-mh-bhandara", "BHD", "Bhandara", "भंडारा", "भंडारा"},
            new String[]{"dist-mh-buldhana", "BLD", "Buldhana", "बुलढाणा", "बुलढाणा"},
            new String[]{"dist-mh-chandrapur", "CDP", "Chandrapur", "चंद्रपुर", "चंद्रपूर"},
            new String[]{"dist-mh-dhule", "DHU", "Dhule", "धुले", "धुळे"},
            new String[]{"dist-mh-gadchiroli", "GDC", "Gadchiroli", "गड़चिरोली", "गडचिरोली"},
            new String[]{"dist-mh-gondia", "GND", "Gondia", "गोंदिया", "गोंदिया"},
            new String[]{"dist-mh-hingoli", "HNG", "Hingoli", "हिंगोली", "हिंगोली"},
            new String[]{"dist-mh-jalgaon", "JLG", "Jalgaon", "जलगांव", "जळगाव"},
            new String[]{"dist-mh-jalna", "JLN", "Jalna", "जालना", "जालना"},
            new String[]{"dist-kolhapur", "KOL", "Kolhapur", "कोल्हापुर", "कोल्हापूर"},
            new String[]{"dist-mh-latur", "LTR", "Latur", "लातूर", "लातूर"},
            new String[]{"dist-mh-mumbai-city", "MMC", "Mumbai City", "मुंबई शहर", "मुंबई शहर"},
            new String[]{"dist-mh-mumbai-suburban", "MMS", "Mumbai Suburban", "मुंबई उपनगर", "मुंबई उपनगर"},
            new String[]{"dist-nagpur", "NAG", "Nagpur", "नागपुर", "नागपूर"},
            new String[]{"dist-mh-nanded", "NDD", "Nanded", "नांदेड़", "नांदेड"},
            new String[]{"dist-mh-nandurbar", "NDB", "Nandurbar", "नंदुरबार", "नंदुरबार"},
            new String[]{"dist-nashik", "NAS", "Nashik", "नासिक", "नाशिक"},
            new String[]{"dist-mh-dharashiv", "OSM", "Dharashiv (Osmanabad)", "धाराशिव (उस्मानाबाद)", "धाराशिव (उस्मानाबाद)"},
            new String[]{"dist-palghar", "PAL", "Palghar", "पालघर", "पालघर"},
            new String[]{"dist-mh-parbhani", "PRB", "Parbhani", "परभणी", "परभणी"},
            new String[]{"dist-pune", "PUN", "Pune", "पुणे", "पुणे"},
            new String[]{"dist-raigad", "RAI", "Raigad (Alibag)", "रायगढ़ (अलीबाग)", "रायगड (अलिबाग)"},
            new String[]{"dist-ratnagiri", "RAT", "Ratnagiri", "रत्नागिरि", "रत्नागिरी"},
            new String[]{"dist-sangli", "SAN", "Sangli", "सांगली", "सांगली"},
            new String[]{"dist-satara", "SAT", "Satara", "सतारा", "सातारा"},
            new String[]{"dist-mh-sindhudurg", "SND", "Sindhudurg (Oros)", "सिंधुदुर्ग (ओरोस)", "सिंधुदुर्ग"},
            new String[]{"dist-solapur", "SOL", "Solapur", "सोलापुर", "सोलापूर"},
            new String[]{"dist-thane", "THA", "Thane", "ठाणे", "ठाणे"},
            new String[]{"dist-mh-wardha", "WRD", "Wardha", "वर्धा", "वर्धा"},
            new String[]{"dist-mh-washim", "WSM", "Washim", "वाशिम", "वाशीम"},
            new String[]{"dist-mh-yavatmal", "YTL", "Yavatmal", "यवतमाल", "यवतमाळ"}
        ));

        // 15. Manipur (16)
        stateDistricts.put("state-mn", Arrays.asList(
            new String[]{"dist-mn-bishnupur", "BSP", "Bishnupur", "बिष्णुपुर", "बिष्णुपूर"},
            new String[]{"dist-mn-chandel", "CDL", "Chandel", "चंदेल", "चंदेल"},
            new String[]{"dist-mn-churachandpur", "CCP", "Churachandpur", "चुराचांदपुर", "चुराचांदपूर"},
            new String[]{"dist-imphal", "IMP", "Imphal East", "इम्फाल पूर्व", "इम्फाळ पूर्व"},
            new String[]{"dist-mn-imphal-west", "IMW", "Imphal West", "इम्फाल पश्चिम", "इम्फाळ पश्चिम"},
            new String[]{"dist-mn-jiribam", "JRB", "Jiribam", "जिरीबाम", "जिरीबाम"},
            new String[]{"dist-mn-kakching", "KKC", "Kakching", "काकचिंग", "काकचिंग"},
            new String[]{"dist-mn-kamjong", "KMJ", "Kamjong", "कामजोंग", "कामजोंग"},
            new String[]{"dist-mn-kangpokpi", "KPI", "Kangpokpi", "कांगपोकपी", "कांगपोकपी"},
            new String[]{"dist-mn-noney", "NNY", "Noney", "नोने", "नोने"},
            new String[]{"dist-mn-pherzawl", "PZW", "Pherzawl", "फेरजावल", "फेरजावल"},
            new String[]{"dist-mn-senapati", "SNP", "Senapati", "सेनापति", "सेनापती"},
            new String[]{"dist-mn-tamenglong", "TML", "Tamenglong", "तामेंगलोंग", "तामेंगलाँग"},
            new String[]{"dist-mn-tengnoupal", "TNP", "Tengnoupal", "तेंगनौपाल", "तेंगनौपाल"},
            new String[]{"dist-mn-thoubal", "THB", "Thoubal", "थौबल", "थौबल"},
            new String[]{"dist-mn-ukhrul", "UKR", "Ukhrul", "उखरुल", "उखरुल"}
        ));

        // 16. Meghalaya (12)
        stateDistricts.put("state-ml", Arrays.asList(
            new String[]{"dist-ml-east-garo-hills", "EGH", "East Garo Hills (Williamnagar)", "पूर्वी गारो हिल्स", "पूर्व गारो हिल्स"},
            new String[]{"dist-ml-east-jaintia-hills", "EJH", "East Jaintia Hills (Khliehriat)", "पूर्वी जयंतिया हिल्स", "पूर्व जैंतिया हिल्स"},
            new String[]{"dist-shillong", "SHL", "East Khasi Hills (Shillong)", "पूर्वी खासी हिल्स (शिलांग)", "पूर्व खासी हिल्स (शिलाँग)"},
            new String[]{"dist-ml-eastern-west-khasi-hills", "EWK", "Eastern West Khasi Hills (Mairang)", "ईस्टर्न वेस्ट खासी हिल्स", "ईस्टर्न वेस्ट खासी हिल्स"},
            new String[]{"dist-ml-north-garo-hills", "NGH", "North Garo Hills (Resubelpara)", "उत्तर गारो हिल्स", "उत्तर गारो हिल्स"},
            new String[]{"dist-ml-ri-bhoi", "RBH", "Ri-Bhoi (Nongpoh)", "री-भोई", "री-भोई"},
            new String[]{"dist-ml-south-garo-hills", "SGH", "South Garo Hills (Baghmara)", "दक्षिण गारो हिल्स", "दक्षिण गारो हिल्स"},
            new String[]{"dist-ml-south-west-garo-hills", "SWG", "South West Garo Hills (Ampati)", "दक्षिण पश्चिम गारो हिल्स", "दक्षिण पश्चिम गारो हिल्स"},
            new String[]{"dist-ml-south-west-khasi-hills", "SWK", "South West Khasi Hills (Mawkyrwat)", "दक्षिण पश्चिम खासी हिल्स", "दक्षिण पश्चिम खासी हिल्स"},
            new String[]{"dist-ml-west-garo-hills", "WGH", "West Garo Hills (Tura)", "पश्चिम गारो हिल्स (तुरा)", "पश्चिम गारो हिल्स (तुरा)"},
            new String[]{"dist-ml-west-jaintia-hills", "WJH", "West Jaintia Hills (Jowai)", "पश्चिम जयंतिया हिल्स (जोवाई)", "पश्चिम जैंतिया हिल्स"},
            new String[]{"dist-ml-west-khasi-hills", "WKH", "West Khasi Hills (Nongstoin)", "पश्चिम खासी हिल्स (नोंगस्टोइन)", "पश्चिम खासी हिल्स"}
        ));

        // 17. Mizoram (11)
        stateDistricts.put("state-mz", Arrays.asList(
            new String[]{"dist-aizawl", "AIZ", "Aizawl", "आइज़ोल", "ऐझॉल"},
            new String[]{"dist-mz-champhai", "CMP", "Champhai", "चम्फाई", "चम्फाई"},
            new String[]{"dist-mz-hnathial", "HNT", "Hnahthial", "हनाथियाल", "हनाथियाल"},
            new String[]{"dist-mz-khawzawl", "KZW", "Khawzawl", "खावज़ॉल", "खावझॉल"},
            new String[]{"dist-mz-kolasib", "KLB", "Kolasib", "कोलासिब", "कोलासिब"},
            new String[]{"dist-mz-lawngtlai", "LWT", "Lawngtlai", "लॉंगत्लाई", "लॉंगत्लाई"},
            new String[]{"dist-mz-lunglei", "LGL", "Lunglei", "लुंगलेई", "लुंगलेई"},
            new String[]{"dist-mz-mamit", "MMT", "Mamit", "मामित", "मामित"},
            new String[]{"dist-mz-saitual", "STL", "Saitual", "सैतुअल", "सैतुअल"},
            new String[]{"dist-mz-serchhip", "SCP", "Serchhip", "सेरछिप", "सेरछिप"},
            new String[]{"dist-mz-siaha", "SIA", "Siaha", "सियाहा", "सियाहा"}
        ));

        // 18. Nagaland (16)
        stateDistricts.put("state-nl", Arrays.asList(
            new String[]{"dist-nl-chumoukedima", "CMK", "Chümoukedima", "चुमौकेदिमा", "चुमौकेदिमा"},
            new String[]{"dist-nl-dimapur", "DMP", "Dimapur", "दीमापुर", "दिमापूर"},
            new String[]{"dist-nl-kiphire", "KPR", "Kiphire", "किफिरे", "किफिरे"},
            new String[]{"dist-kohima", "KOH", "Kohima", "कोहिमा", "कोहिमा"},
            new String[]{"dist-nl-longleng", "LLG", "Longleng", "लोंगलेंग", "लाँगलेन्ग"},
            new String[]{"dist-nl-mokokchung", "MKC", "Mokokchung", "मोकोकचुंग", "मोकोकचुंग"},
            new String[]{"dist-nl-mon", "MON", "Mon", "मोन", "मोन"},
            new String[]{"dist-nl-niuland", "NLD", "Niuland", "निउलैंड", "निउलँड"},
            new String[]{"dist-nl-noklak", "NKL", "Noklak", "नोकलाक", "नोकलाक"},
            new String[]{"dist-nl-peren", "PRN", "Peren", "पेरेन", "पेरेन"},
            new String[]{"dist-nl-phek", "PHK", "Phek", "फेक", "फेक"},
            new String[]{"dist-nl-shamator", "SMT", "Shamator", "शामतोर", "शामतोर"},
            new String[]{"dist-nl-tseminyu", "TSM", "Tseminyü", "त्सेमिन्यु", "त्सेमिन्यु"},
            new String[]{"dist-nl-tuensang", "TSG", "Tuensang", "तुएनसांग", "तुएनसांग"},
            new String[]{"dist-nl-wokha", "WKH", "Wokha", "वोखा", "वोखा"},
            new String[]{"dist-nl-zunheboto", "ZHB", "Zunheboto", "जुन्हेबोतो", "झुनहेबोटो"}
        ));

        // 19. Odisha (30)
        stateDistricts.put("state-od", Arrays.asList(
            new String[]{"dist-od-angul", "ANG", "Angul", "अनुगूल", "अंगुल"},
            new String[]{"dist-od-balangir", "BLG", "Balangir", "बलांगिर", "बलांगिर"},
            new String[]{"dist-od-balasore", "BLS", "Balasore (Baleswar)", "बालेश्वर", "बालेश्वर"},
            new String[]{"dist-od-bargarh", "BGR", "Bargarh", "बरगढ़", "बरगड"},
            new String[]{"dist-od-bhadrak", "BDK", "Bhadrak", "भद्रक", "भद्रक"},
            new String[]{"dist-od-boudh", "BDH", "Boudh", "बौध", "बौध"},
            new String[]{"dist-cuttack", "CUT", "Cuttack", "कटक", "कटक"},
            new String[]{"dist-od-deogarh", "DGH", "Deogarh", "देवगढ़", "देवगड"},
            new String[]{"dist-od-dhenkanal", "DNK", "Dhenkanal", "ढेंकनाल", "ढेंकनाल"},
            new String[]{"dist-od-gajapati", "GJP", "Gajapati (Paralakhemundi)", "गजपति", "गजपती"},
            new String[]{"dist-od-ganjam", "GNJ", "Ganjam (Chhatrapur)", "गंजम", "गंजम"},
            new String[]{"dist-od-jagatsinghpur", "JSP", "Jagatsinghpur", "जगतसिंहपुर", "जगतसिंगपूर"},
            new String[]{"dist-od-jajpur", "JJP", "Jajpur", "जाजपुर", "जाजपूर"},
            new String[]{"dist-od-jharsuguda", "JHG", "Jharsuguda", "झारसुगुड़ा", "झारसुगुडा"},
            new String[]{"dist-od-kalahandi", "KLH", "Kalahandi (Bhawanipatna)", "कालाहांडी", "कालाहांडी"},
            new String[]{"dist-od-kandhamal", "KND", "Kandhamal (Phulbani)", "कंधमाल", "कंधमाल"},
            new String[]{"dist-od-kendrapara", "KDP", "Kendrapara", "केंद्रपाड़ा", "केंद्रपाडा"},
            new String[]{"dist-od-kendujhar", "KDJ", "Kendujhar (Keonjhar)", "क्योंझर", "केंदुझर"},
            new String[]{"dist-bhubaneswar", "BBI", "Khordha (Bhubaneswar)", "खोरधा (भुवनेश्वर)", "खोरधा (भुवनेश्वर)"},
            new String[]{"dist-od-koraput", "KRP", "Koraput", "कोरापुट", "कोरापुट"},
            new String[]{"dist-od-malkangiri", "MLK", "Malkangiri", "मलकानगिरि", "मलकानगिरी"},
            new String[]{"dist-od-mayurbhanj", "MYB", "Mayurbhanj (Baripada)", "मयूरभंज", "मयूरभंज"},
            new String[]{"dist-od-nabarangpur", "NBP", "Nabarangpur", "नबरंगपुर", "नबरंगपूर"},
            new String[]{"dist-od-nayagarh", "NYG", "Nayagarh", "नयागढ़", "नयागड"},
            new String[]{"dist-od-nuapada", "NPD", "Nuapada", "नुआपड़ा", "नुआपाडा"},
            new String[]{"dist-od-puri", "PUR", "Puri", "पुरी", "पुरी"},
            new String[]{"dist-od-rayagada", "RYG", "Rayagada", "रायगड़ा", "रायगडा"},
            new String[]{"dist-od-sambalpur", "SBP", "Sambalpur", "संबलपुर", "संबलपूर"},
            new String[]{"dist-od-subarnapur", "SBN", "Subarnapur (Sonepur)", "सुवर्णपुर", "सुवर्णपूर"},
            new String[]{"dist-od-sundergarh", "SND", "Sundargarh (Rourkela)", "सुंदरगढ़", "सुंदरगड"}
        ));

        // 20. Punjab (23)
        stateDistricts.put("state-pb", Arrays.asList(
            new String[]{"dist-amritsar", "ASR", "Amritsar", "अमृतसर", "अमृतसर"},
            new String[]{"dist-pb-barnala", "BNL", "Barnala", "बरनाला", "बरनाला"},
            new String[]{"dist-pb-bathinda", "BAT", "Bathinda", "बठिंडा", "भटिंडा"},
            new String[]{"dist-pb-faridkot", "FDK", "Faridkot", "फरीदकोट", "फरीदकोट"},
            new String[]{"dist-pb-fatehgarh-sahib", "FGS", "Fatehgarh Sahib", "फतेहगढ़ साहिब", "फतेहगड साहिब"},
            new String[]{"dist-pb-fazilka", "FZK", "Fazilka", "फाजिल्का", "फाझिल्का"},
            new String[]{"dist-pb-ferozepur", "FRZ", "Ferozepur", "फिरोजपुर", "फिरोजपूर"},
            new String[]{"dist-pb-gurdaspur", "GSP", "Gurdaspur", "गुरदासपुर", "गुरदासपूर"},
            new String[]{"dist-pb-hoshiarpur", "HSH", "Hoshiarpur", "होशियारपुर", "होशियारपूर"},
            new String[]{"dist-pb-jalandhar", "JLN", "Jalandhar", "जालंधर", "जालंधर"},
            new String[]{"dist-pb-kapurthala", "KPT", "Kapurthala", "कपूरथला", "कपूरथळा"},
            new String[]{"dist-ludhiana", "LDH", "Ludhiana", "लुधियाना", "लुधियाना"},
            new String[]{"dist-pb-malerkotla", "MLK", "Malerkotla", "मलेरकोटला", "मलेरकोटला"},
            new String[]{"dist-pb-mansa", "MNS", "Mansa", "मानसा", "मानसा"},
            new String[]{"dist-pb-moga", "MOG", "Moga", "मोगा", "मोगा"},
            new String[]{"dist-pb-muktsar", "MKT", "Sri Muktsar Sahib", "श्री मुक्तसर साहिब", "श्री मुक्तसर साहिब"},
            new String[]{"dist-pb-pathankot", "PTK", "Pathankot", "पठानकोट", "पठाणकोट"},
            new String[]{"dist-pb-patiala", "PTL", "Patiala", "पटियाला", "पतियाळा"},
            new String[]{"dist-pb-rupnagar", "RUP", "Rupnagar (Ropar)", "रूपनगर (रोपड़)", "रूपनगर"},
            new String[]{"dist-pb-sahibzada-ajit-singh-nagar", "SAS", "S.A.S. Nagar (Mohali)", "मोहाली", "मोहाली"},
            new String[]{"dist-pb-shahid-bhagat-singh-nagar", "SBS", "Shaheed Bhagat Singh Nagar (Nawanshahr)", "नवांशहर", "नवांशहर"},
            new String[]{"dist-pb-sangrur", "SNG", "Sangrur", "संगरूर", "संगरूर"},
            new String[]{"dist-pb-tarn-taran", "TTN", "Tarn Taran", "तरन तारन", "तरन तारन"}
        ));

        // 21. Rajasthan (50)
        stateDistricts.put("state-rj", Arrays.asList(
            new String[]{"dist-rj-ajmer", "AJM", "Ajmer", "अजमेर", "अजमेर"},
            new String[]{"dist-rj-alwar", "ALW", "Alwar", "अलवर", "अलवर"},
            new String[]{"dist-rj-anupgarh", "ANP", "Anupgarh", "अनूपगढ़", "अनूपगड"},
            new String[]{"dist-rj-balotra", "BLT", "Balotra", "बालोतरा", "बालोतरा"},
            new String[]{"dist-rj-banswara", "BSW", "Banswara", "बांसवाड़ा", "बांसवाडा"},
            new String[]{"dist-rj-baran", "BAR", "Baran", "बारां", "बारां"},
            new String[]{"dist-rj-barmer", "BMR", "Barmer", "बाड़मेर", "बाडमेर"},
            new String[]{"dist-rj-beawar", "BWR", "Beawar", "ब्यावर", "ब्यावर"},
            new String[]{"dist-rj-bharatpur", "BHP", "Bharatpur", "भरतपुर", "भरतपूर"},
            new String[]{"dist-rj-bhilwara", "BLW", "Bhilwara", "भीलवाड़ा", "भिलवाडा"},
            new String[]{"dist-rj-bikaner", "BKN", "Bikaner", "बीकानेर", "बिकानेर"},
            new String[]{"dist-rj-bundi", "BND", "Bundi", "बूंदी", "बुंदी"},
            new String[]{"dist-rj-chittorgarh", "CTG", "Chittorgarh", "चित्तौड़गढ़", "चित्तोडगड"},
            new String[]{"dist-rj-churu", "CHU", "Churu", "चूरू", "चुरू"},
            new String[]{"dist-rj-dausa", "DSA", "Dausa", "दौसा", "दौसा"},
            new String[]{"dist-rj-deeg", "DEG", "Deeg", "डीग", "डीग"},
            new String[]{"dist-rj-didwana-kuchaman", "DDK", "Didwana Kuchaman", "डीडवाना कुचामन", "डिडवाना कुचामन"},
            new String[]{"dist-rj-dholpur", "DHP", "Dholpur", "धौलपुर", "धोलपूर"},
            new String[]{"dist-rj-dudu", "DUD", "Dudu", "दूदू", "दुदु"},
            new String[]{"dist-rj-dungarpur", "DGP", "Dungarpur", "डूंगरपुर", "डुंगरपूर"},
            new String[]{"dist-rj-gangapurcity", "GPC", "Gangapur City", "गंगापुर सिटी", "गंगापूर सिटी"},
            new String[]{"dist-rj-hanumangarh", "HNM", "Hanumangarh", "हनुमानगढ़", "हनुमानगड"},
            new String[]{"dist-jaipur", "JAI", "Jaipur", "जयपुर", "जयपूर"},
            new String[]{"dist-rj-jaipur-rural", "JAR", "Jaipur Rural", "जयपुर ग्रामीण", "जयपूर ग्रामीण"},
            new String[]{"dist-rj-jaisalmer", "JSL", "Jaisalmer", "जैसलमेर", "जैसलमेर"},
            new String[]{"dist-rj-jalore", "JLR", "Jalore", "जालौर", "जालोर"},
            new String[]{"dist-rj-jhalawar", "JHL", "Jhalawar", "झालावाड़", "झालावाड"},
            new String[]{"dist-rj-jhunjhunu", "JJN", "Jhunjhunu", "झुंझुनू", "झुनझुनू"},
            new String[]{"dist-jodhpur", "JDH", "Jodhpur", "जोधपुर", "जोधपूर"},
            new String[]{"dist-rj-jodhpur-rural", "JDR", "Jodhpur Rural", "जोधपुर ग्रामीण", "जोधपूर ग्रामीण"},
            new String[]{"dist-rj-karauli", "KRL", "Karauli", "करौली", "करौली"},
            new String[]{"dist-rj-kekri", "KKR", "Kekri", "केकड़ी", "केकडी"},
            new String[]{"dist-rj-khairthal-tijara", "KTT", "Khairthal-Tijara", "खैरथल-तिजारा", "खैरथल-तिजारा"},
            new String[]{"dist-rj-kota", "KOT", "Kota", "कोटा", "कोटा"},
            new String[]{"dist-rj-kotputli-behror", "KPB", "Kotputli-Behror", "कोटपूतली-बहरोड़", "कोटपुतली-बहरोड"},
            new String[]{"dist-rj-nagaur", "NGR", "Nagaur", "नागौर", "नागौर"},
            new String[]{"dist-rj-neem-ka-thana", "NKT", "Neem Ka Thana", "नीम का थाना", "नीम का थाना"},
            new String[]{"dist-rj-pali", "PAL", "Pali", "पाली", "पाली"},
            new String[]{"dist-rj-phalodi", "PHL", "Phalodi", "फलौदी", "फलोदी"},
            new String[]{"dist-rj-pratapgarh", "PRT", "Pratapgarh", "प्रतापगढ़", "प्रतापगड"},
            new String[]{"dist-rj-rajsamand", "RJS", "Rajsamand", "राजसमंद", "राजसमंद"},
            new String[]{"dist-rj-salumbar", "SLM", "Salumbar", "सलूंबर", "सलुंबर"},
            new String[]{"dist-rj-sanchore", "SNC", "Sanchore", "सांचौर", "सांचोर"},
            new String[]{"dist-rj-sawai-madhopur", "SWM", "Sawai Madhopur", "सवाई माधोपुर", "सवाई माधोपूर"},
            new String[]{"dist-rj-shahpura", "SHH", "Shahpura", "शाहपुरा", "शाहपुरा"},
            new String[]{"dist-rj-sikar", "SKR", "Sikar", "सीकर", "सीकर"},
            new String[]{"dist-rj-sirohi", "SRH", "Sirohi", "सिरोही", "सिरोही"},
            new String[]{"dist-rj-sri-ganganagar", "SGG", "Sri Ganganagar", "श्रीगंगानगर", "श्रीगंगानगर"},
            new String[]{"dist-rj-tonk", "TNK", "Tonk", "टोंक", "टोंक"},
            new String[]{"dist-udaipur", "UDA", "Udaipur", "उदयपुर", "उदयपूर"}
        ));

        // 22. Sikkim (6)
        stateDistricts.put("state-sk", Arrays.asList(
            new String[]{"dist-gangtok", "GAN", "Gangtok (East Sikkim)", "गंगटोक (पूर्व सिक्किम)", "गंगटोक (पूर्व सिक्कीम)"},
            new String[]{"dist-sk-gyalshing", "GYL", "Gyalshing (West Sikkim)", "ग्यालशिंग (पश्चिम सिक्किम)", "ग्यालशिंग"},
            new String[]{"dist-sk-mangan", "MGN", "Mangan (North Sikkim)", "मंगन (उत्तर सिक्किम)", "मंगन"},
            new String[]{"dist-sk-namchi", "NMC", "Namchi (South Sikkim)", "नामची (दक्षिण सिक्किम)", "नामची"},
            new String[]{"dist-sk-pakyong", "PKY", "Pakyong", "पाकयोंग", "पाकयोंग"},
            new String[]{"dist-sk-soreng", "SRG", "Soreng", "सोरेंग", "सोरेंग"}
        ));

        // 23. Tamil Nadu (38)
        stateDistricts.put("state-tn", Arrays.asList(
            new String[]{"dist-tn-ariyalur", "ARY", "Ariyalur", "अरियालुर", "अरियालूर"},
            new String[]{"dist-tn-chengalpattu", "CGP", "Chengalpattu", "चेंगलपट्टु", "चेंगलपट्टू"},
            new String[]{"dist-chennai", "CHE", "Chennai", "चेन्नई", "चेन्नई (मद्रास)"},
            new String[]{"dist-coimbatore", "CBE", "Coimbatore", "कोयंबटूर", "कोइम्बतूर"},
            new String[]{"dist-tn-cuddalore", "CDL", "Cuddalore", "कुड्डालोर", "कुड्डलोर"},
            new String[]{"dist-tn-dharmapuri", "DMP", "Dharmapuri", "धर्मपुरी", "धर्मपुरी"},
            new String[]{"dist-tn-dindigul", "DND", "Dindigul", "डिंडीगुल", "दिंडीगुल"},
            new String[]{"dist-tn-erode", "ERD", "Erode", "ईरोड", "इरोड"},
            new String[]{"dist-tn-kallakurichi", "KLK", "Kallakurichi", "कल्लाकुरिची", "कल्लाकुरिची"},
            new String[]{"dist-tn-kancheepuram", "KCP", "Kancheepuram", "कांचीपुरम", "कांचीपुरम"},
            new String[]{"dist-tn-kanyakumari", "KKM", "Kanyakumari (Nagercoil)", "कन्याकुमारी", "कन्याकुमारी"},
            new String[]{"dist-tn-karur", "KRR", "Karur", "करूर", "करूर"},
            new String[]{"dist-tn-krishnagiri", "KRN", "Krishnagiri", "कृष्णगिरि", "कृष्णगिरी"},
            new String[]{"dist-madurai", "MDU", "Madurai", "मदुरै", "मदुराई"},
            new String[]{"dist-tn-mayiladuthurai", "MYL", "Mayiladuthurai", "मयिलादुथुरै", "मयिलादुथुरै"},
            new String[]{"dist-tn-nagapattinam", "NGP", "Nagapattinam", "नागापट्टिनम", "नागापट्टिनम"},
            new String[]{"dist-tn-namakkal", "NMK", "Namakkal", "नमक्कल", "नमक्कल"},
            new String[]{"dist-tn-nilgiris", "NIL", "Nilgiris (Udhagamandalam/Ooty)", "नीलगिरि (ऊटी)", "निलगिरी (उटी)"},
            new String[]{"dist-tn-perambalur", "PRM", "Perambalur", "पेरम्बलुर", "पेरंबलूर"},
            new String[]{"dist-tn-pudukkottai", "PDK", "Pudukkottai", "पुदुक्कोट्टई", "पुदुक्कोट्टई"},
            new String[]{"dist-tn-ramanathapuram", "RMP", "Ramanathapuram (Rameshwaram)", "रामनाथपुरम", "रामनाथपुरम"},
            new String[]{"dist-tn-ranipet", "RNP", "Ranipet", "रानीपेट", "राणीपेट"},
            new String[]{"dist-tn-salem", "SLM", "Salem", "सेलम", "सालेम"},
            new String[]{"dist-tn-sivaganga", "SVG", "Sivaganga", "शिवगंगा", "शिवगंगा"},
            new String[]{"dist-tn-tenkasi", "TNK", "Tenkasi", "तेनकासी", "तेनकाशी"},
            new String[]{"dist-tn-thanjavur", "TNJ", "Thanjavur", "तंजावुर", "तंजावर"},
            new String[]{"dist-tn-theni", "THN", "Theni", "थेनी", "थेनी"},
            new String[]{"dist-tn-thoothukudi", "TTK", "Thoothukudi (Tuticorin)", "तूतीकोरिन", "थूथुकुडी (तुतिकोरीन)"},
            new String[]{"dist-tiruchirappalli", "TPJ", "Tiruchirappalli (Trichy)", "तिरुचिरापल्ली", "तिरुचिरापल्ली (त्रिची)"},
            new String[]{"dist-tn-tirunelveli", "TNV", "Tirunelveli", "तिरुनेलवेली", "तिरुनेलवेली"},
            new String[]{"dist-tn-tirupathur", "TPR", "Tirupathur", "तिरुपात्तूर", "तिरुपात्तूर"},
            new String[]{"dist-tn-tiruppur", "TRP", "Tiruppur", "तिरुपुर", "तिरुप्पूर"},
            new String[]{"dist-tn-tiruvallur", "TRV", "Tiruvallur", "तिरुवल्लूर", "तिरुवल्लूर"},
            new String[]{"dist-tn-tiruvannamalai", "TNM", "Tiruvannamalai", "तिरुवन्नामलाई", "तिरुवण्णामलाई"},
            new String[]{"dist-tn-tiruvarur", "TVR", "Tiruvarur", "तिरुवारूर", "तिरुवारूर"},
            new String[]{"dist-tn-vellore", "VEL", "Vellore", "वेल्लोर", "वेल्लोर"},
            new String[]{"dist-tn-viluppuram", "VLP", "Viluppuram", "विलुप्पुरम", "विलुप्पुरम"},
            new String[]{"dist-tn-virudhunagar", "VRD", "Virudhunagar", "विरुधुनगर", "विरुधुनगर"}
        ));

        // 24. Telangana (33)
        stateDistricts.put("state-ts", Arrays.asList(
            new String[]{"dist-ts-adilabad", "ADB", "Adilabad", "आदिलाबाद", "आदिलाबाद"},
            new String[]{"dist-ts-bhadradri-kothagudem", "BDK", "Bhadradri Kothagudem", "भद्राद्री कोठागुडेम", "भद्राद्री कोठागुडेम"},
            new String[]{"dist-ts-hanumakonda", "HNK", "Hanumakonda", "हनुमकोंडा", "हनुमकोंडा"},
            new String[]{"dist-hyderabad", "HYD", "Hyderabad", "हैदराबाद", "हैदराबाद"},
            new String[]{"dist-ts-jagtial", "JGT", "Jagtial", "जगित्याल", "जगित्याल"},
            new String[]{"dist-ts-jangaon", "JNG", "Jangaon", "जनगांव", "जनगाव"},
            new String[]{"dist-ts-jayashankar-bhupalpally", "JSB", "Jayashankar Bhupalpally", "जयशंकर भूपालपल्ली", "जयशंकर भूपालपल्ली"},
            new String[]{"dist-ts-jogulamba-gadwal", "JLG", "Jogulamba Gadwal", "जोगुलम्बा गद्वाल", "जोगुलांबा गडवाल"},
            new String[]{"dist-ts-kamareddy", "KMR", "Kamareddy", "कामारेड्डी", "कामारेड्डी"},
            new String[]{"dist-ts-karimnagar", "KRM", "Karimnagar", "करीमनगर", "करीमनगर"},
            new String[]{"dist-ts-khammam", "KHM", "Khammam", "खम्मम", "खम्मम"},
            new String[]{"dist-ts-kumuram-bheem-asifabad", "KBA", "Kumuram Bheem Asifabad", "कुमुराम भीम आसिफाबाद", "कुमुराम भीम आसिफाबाद"},
            new String[]{"dist-ts-mahabubabad", "MHB", "Mahabubabad", "महबूबाबाद", "महबूबाबाद"},
            new String[]{"dist-ts-mahabubnagar", "MBN", "Mahabubnagar", "महबूबनगर", "महबूबनगर"},
            new String[]{"dist-ts-mancherial", "MNC", "Mancherial", "मंचेरियल", "मंचेरियल"},
            new String[]{"dist-ts-medak", "MDK", "Medak", "मेदक", "मेदक"},
            new String[]{"dist-ts-medchal-malkajgiri", "MDM", "Medchal-Malkajgiri", "मेडचल-मलकाजगिरी", "मेडचल-मलकाजगिरी"},
            new String[]{"dist-ts-mulugu", "MLG", "Mulugu", "मुलुगु", "मुलुगू"},
            new String[]{"dist-ts-nagarkurnool", "NGK", "Nagarkurnool", "नागरकुरनूल", "नागरकुर्नूल"},
            new String[]{"dist-ts-nalgonda", "NLG", "Nalgonda", "नलगोंडा", "नलगोंडा"},
            new String[]{"dist-ts-narayanpet", "NRP", "Narayanpet", "नारायणपेट", "नारायणपेठ"},
            new String[]{"dist-ts-nirmal", "NRM", "Nirmal", "निर्मल", "निर्मल"},
            new String[]{"dist-ts-nizamabad", "NZB", "Nizamabad", "निज़ामाबाद", "निझामाबाद"},
            new String[]{"dist-ts-peddapalli", "PDP", "Peddapalli", "पेद्दापल्ली", "पेद्दापल्ली"},
            new String[]{"dist-ts-rajanna-sircilla", "RJS", "Rajanna Sircilla", "राजन्ना सिरसिल्ला", "राजण्णा सिरसिल्ला"},
            new String[]{"dist-ts-ranga-reddy", "RRD", "Ranga Reddy", "रंगारेड्डी", "रंगारेड्डी"},
            new String[]{"dist-ts-sangareddy", "SGR", "Sangareddy", "संगारेड्डी", "संगारेड्डी"},
            new String[]{"dist-ts-siddipet", "SDP", "Siddipet", "सिद्दीपेट", "सिद्दीपेट"},
            new String[]{"dist-ts-suryapet", "SRP", "Suryapet", "सूर्यपेट", "सूर्यपेट"},
            new String[]{"dist-ts-vikarabad", "VKB", "Vikarabad", "विकाराबाद", "विकाराबाद"},
            new String[]{"dist-ts-wanaparthy", "WNP", "Wanaparthy", "वनपर्थी", "वनपर्थी"},
            new String[]{"dist-warangal", "WAR", "Warangal", "वारंगल", "वारंगल"},
            new String[]{"dist-ts-yadadri-bhuvanagiri", "YDB", "Yadadri Bhuvanagiri", "यादाद्री भुवनगिरी", "यादाद्री भुवनगिरी"}
        ));

        // 25. Tripura (8)
        stateDistricts.put("state-tr", Arrays.asList(
            new String[]{"dist-tr-dhalai", "DHL", "Dhalai (Ambassa)", "धलाई", "धलाई"},
            new String[]{"dist-tr-gomati", "GMT", "Gomati (Udaipur)", "गोमती", "गोमती"},
            new String[]{"dist-tr-khovai", "KHV", "Khowai", "खोवाई", "खोवाई"},
            new String[]{"dist-tr-north-tripura", "NTR", "North Tripura (Dharmanagar)", "उत्तर त्रिपुरा", "उत्तर त्रिपुरा"},
            new String[]{"dist-tr-sepahijala", "SPH", "Sepahijala (Bishramganj)", "सिपाहीजाला", "सिपाहीजाला"},
            new String[]{"dist-tr-south-tripura", "STR", "South Tripura (Belonia)", "दक्षिण त्रिपुरा", "दक्षिण त्रिपुरा"},
            new String[]{"dist-tr-unakoti", "UNK", "Unakoti (Kailashahar)", "ऊनाकोटी", "उनाकोटी"},
            new String[]{"dist-agartala", "AGA", "West Tripura (Agartala)", "पश्चिम त्रिपुरा (अगरतला)", "पश्चिम त्रिपुरा (आगरतळा)"}
        ));

        // 26. Uttar Pradesh (75)
        stateDistricts.put("state-up", Arrays.asList(
            new String[]{"dist-agra", "AGR", "Agra", "आगरा", "आग्रा"},
            new String[]{"dist-up-aligarh", "ALI", "Aligarh", "अलीगढ़", "अलिगड"},
            new String[]{"dist-up-ambedkar-nagar", "AMB", "Ambedkar Nagar", "अंबेडकर नगर", "आंबेडकर नगर"},
            new String[]{"dist-up-amethi", "AME", "Amethi", "अमेठी", "अमेठी"},
            new String[]{"dist-up-amroha", "AMR", "Amroha", "अमरोहा", "अमरोहा"},
            new String[]{"dist-up-auraiya", "AUR", "Auraiya", "औरैया", "औरैया"},
            new String[]{"dist-up-ayodhya", "AYD", "Ayodhya (Faizabad)", "अयोध्या", "अयोध्या"},
            new String[]{"dist-up-azamgarh", "AZM", "Azamgarh", "आजमगढ़", "आजमगढ"},
            new String[]{"dist-up-baghpat", "BGP", "Baghpat", "बागपत", "बागपत"},
            new String[]{"dist-up-bahraich", "BHR", "Bahraich", "बहराइच", "बहराइच"},
            new String[]{"dist-up-ballia", "BLL", "Ballia", "बलिया", "बलिया"},
            new String[]{"dist-up-balrampur", "BLR", "Balrampur", "बलरामपुर", "बलरामपूर"},
            new String[]{"dist-up-banda", "BND", "Banda", "बांदा", "बांदा"},
            new String[]{"dist-up-barabanki", "BRB", "Barabanki", "बाराबंकी", "बाराबंकी"},
            new String[]{"dist-up-bareilly", "BRL", "Bareilly", "बरेली", "बरेली"},
            new String[]{"dist-up-basti", "BST", "Basti", "बस्ती", "बस्ती"},
            new String[]{"dist-up-bhadohi", "BDH", "Bhadohi (Sant Ravidas Nagar)", "भदोही", "भदोही"},
            new String[]{"dist-up-bijnor", "BJN", "Bijnor", "बिजनौर", "बिजनौर"},
            new String[]{"dist-up-budaun", "BDN", "Budaun", "बदायूं", "बदायूं"},
            new String[]{"dist-up-bulandshahr", "BLS", "Bulandshahr", "बुलंदशहर", "बुलंदशहर"},
            new String[]{"dist-up-chandauli", "CND", "Chandauli", "चंदौली", "चंदौली"},
            new String[]{"dist-up-chitrakoot", "CTK", "Chitrakoot", "चित्रकूट", "चित्रकूट"},
            new String[]{"dist-up-deoria", "DER", "Deoria", "देवरिया", "देवरिया"},
            new String[]{"dist-up-etah", "ETH", "Etah", "एटा", "एटा"},
            new String[]{"dist-up-etawah", "ETW", "Etawah", "इटावा", "इटावा"},
            new String[]{"dist-up-farrukhabad", "FRK", "Farrukhabad", "फर्रुखाबाद", "फर्रुखाबाद"},
            new String[]{"dist-up-fatehpur", "FTP", "Fatehpur", "फतेहपुर", "फतेहपूर"},
            new String[]{"dist-up-firozabad", "FRZ", "Firozabad", "फिरोजाबाद", "फिरोजाबाद"},
            new String[]{"dist-noida", "GBN", "Gautam Buddha Nagar (Noida)", "गौतम बुद्ध नगर (नोएडा)", "गौतम बुद्ध नगर (नोएडा)"},
            new String[]{"dist-up-ghaziabad", "GZB", "Ghaziabad", "गाजियाबाद", "गाझियाबाद"},
            new String[]{"dist-up-ghazipur", "GZP", "Ghazipur", "गाजीपुर", "गाझीपूर"},
            new String[]{"dist-up-gonda", "GND", "Gonda", "गोंडा", "गोंडा"},
            new String[]{"dist-gorakhpur", "GKP", "Gorakhpur", "गोरखपुर", "गोरखपूर"},
            new String[]{"dist-up-hamirpur", "HMR", "Hamirpur", "हमीरपुर", "हमीरपूर"},
            new String[]{"dist-up-hapur", "HPR", "Hapur", "हापुड़", "हापूड"},
            new String[]{"dist-up-hardoi", "HRD", "Hardoi", "हरदोई", "हरदोई"},
            new String[]{"dist-up-hathras", "HTR", "Hathras", "हाथरस", "हाथरस"},
            new String[]{"dist-up-jalaun", "JLN", "Jalaun (Orai)", "जालौन (उरई)", "जालौन"},
            new String[]{"dist-up-jaunpur", "JNP", "Jaunpur", "जौनपुर", "जौनपूर"},
            new String[]{"dist-up-jhansi", "JHS", "Jhansi", "झांसी", "झाशी"},
            new String[]{"dist-up-kannauj", "KNJ", "Kannauj", "कन्नौज", "कन्नौज"},
            new String[]{"dist-up-kanpur-dehat", "KND", "Kanpur Dehat", "कानपुर देहात", "कानपूर देहात"},
            new String[]{"dist-kanpur", "KNP", "Kanpur Nagar", "कानपुर नगर", "कानपूर नगर"},
            new String[]{"dist-up-kasganj", "KSG", "Kasganj", "कासगंज", "कासगंज"},
            new String[]{"dist-up-kaushambi", "KSH", "Kaushambi", "कौशाम्बी", "कौशांबी"},
            new String[]{"dist-up-kushinagar", "KSHN", "Kushinagar", "कुशीनगर", "कुशीनगर"},
            new String[]{"dist-up-lakhimpur-kheri", "LKP", "Lakhimpur Kheri", "लखीमपुर खीरी", "लखीमपूर खेरी"},
            new String[]{"dist-up-lalitpur", "LLP", "Lalitpur", "ललितपुर", "ललितपूर"},
            new String[]{"dist-lucknow", "LKO", "Lucknow", "लखनऊ", "लखनौ"},
            new String[]{"dist-up-maharajganj", "MRG", "Maharajganj", "महराजगंज", "महाराजगंज"},
            new String[]{"dist-up-mahoba", "MHB", "Mahoba", "महोबा", "महोबा"},
            new String[]{"dist-up-mainpuri", "MNP", "Mainpuri", "मैनपुरी", "मैनपुरी"},
            new String[]{"dist-up-mathura", "MTR", "Mathura", "मथुरा", "मथुरा"},
            new String[]{"dist-up-mau", "MAU", "Mau", "मऊ", "मऊ"},
            new String[]{"dist-up-meerut", "MRT", "Meerut", "मेरठ", "मेरठ"},
            new String[]{"dist-up-mirzapur", "MZP", "Mirzapur", "मिर्जापुर", "मिर्झापूर"},
            new String[]{"dist-up-moradabad", "MBD", "Moradabad", "मुरादाबाद", "मुरादाबाद"},
            new String[]{"dist-up-muzaffarnagar", "MZF", "Muzaffarnagar", "मुजफ्फरनगर", "मुझफ्फरनगर"},
            new String[]{"dist-up-pilibhit", "PLB", "Pilibhit", "पीलीभीत", "पिलीभीत"},
            new String[]{"dist-up-pratapgarh", "PRT", "Pratapgarh", "प्रतापगढ़", "प्रतापगड"},
            new String[]{"dist-prayagraj", "PRY", "Prayagraj (Allahabad)", "प्रयागराज (इलाहाबाद)", "प्रयागराज"},
            new String[]{"dist-up-raebareli", "RBL", "Raebareli", "रायबरेली", "रायबरेली"},
            new String[]{"dist-up-rampur", "RMP", "Rampur", "रामपुर", "रामपूर"},
            new String[]{"dist-up-saharanpur", "SHR", "Saharanpur", "सहारनपुर", "सहारनपूर"},
            new String[]{"dist-up-sambhal", "SMB", "Sambhal", "संभल", "संभल"},
            new String[]{"dist-up-sant-kabir-nagar", "SKN", "Sant Kabir Nagar", "संत कबीर नगर", "संत कबीर नगर"},
            new String[]{"dist-up-shahjahanpur", "SHJ", "Shahjahanpur", "शाहजहांपुर", "शाहजहानपूर"},
            new String[]{"dist-up-shamli", "SML", "Shamli", "शामली", "शामली"},
            new String[]{"dist-up-shravasti", "SRV", "Shravasti", "श्रावस्ती", "श्रावस्ती"},
            new String[]{"dist-up-siddharthnagar", "SDN", "Siddharthnagar", "सिद्धार्थनगर", "सिद्धार्थनगर"},
            new String[]{"dist-up-sitapur", "STP", "Sitapur", "सीतापुर", "सीतापूर"},
            new String[]{"dist-up-sonbhadra", "SNB", "Sonbhadra (Robertsganj)", "सोनभद्र", "सोनभद्र"},
            new String[]{"dist-up-sultanpur", "SLT", "Sultanpur", "सुल्तानपुर", "सुलतानपूर"},
            new String[]{"dist-up-unnao", "UNN", "Unnao", "उन्नाव", "उन्नाव"},
            new String[]{"dist-varanasi", "VNS", "Varanasi (Kashi)", "वाराणसी (काशी)", "वाराणसी (काशी)"}
        ));

        // 27. Uttarakhand (13)
        stateDistricts.put("state-uk", Arrays.asList(
            new String[]{"dist-uk-almora", "ALM", "Almora", "अल्मोड़ा", "अलमोडा"},
            new String[]{"dist-uk-bageshwar", "BAG", "Bageshwar", "बागेश्वर", "बागेश्वर"},
            new String[]{"dist-uk-chamoli", "CHM", "Chamoli (Gopeshwar)", "चमोली", "चमोली"},
            new String[]{"dist-uk-champawat", "CPW", "Champawat", "चंपावत", "चंपावत"},
            new String[]{"dist-dehradun", "DDN", "Dehradun", "देहरादून", "डेहराडून"},
            new String[]{"dist-uk-haridwar", "HRD", "Haridwar", "हरिद्वार", "हरिद्वार"},
            new String[]{"dist-uk-nainital", "NNT", "Nainital", "नैनीताल", "नैनिताल"},
            new String[]{"dist-uk-pauri-garhwal", "PGI", "Pauri Garhwal", "पौड़ी गढ़वाल", "पौडी गढवाल"},
            new String[]{"dist-uk-pithoragarh", "PTH", "Pithoragarh", "पिथौरागढ़", "पिथौरागड"},
            new String[]{"dist-uk-rudraprayag", "RDP", "Rudraprayag", "रुद्रप्रयाग", "रुद्रप्रयाग"},
            new String[]{"dist-uk-tehri-garhwal", "TRG", "Tehri Garhwal", "टिहरी गढ़वाल", "टिहरी गढवाल"},
            new String[]{"dist-uk-udham-singh-nagar", "USN", "Udham Singh Nagar (Rudrapur)", "उधम सिंह नगर", "उधम सिंग नगर"},
            new String[]{"dist-uk-uttarkashi", "UTK", "Uttarkashi", "उत्तरकाशी", "उत्तरकाशी"}
        ));

        // 28. West Bengal (23)
        stateDistricts.put("state-wb", Arrays.asList(
            new String[]{"dist-wb-alipurduar", "APD", "Alipurduar", "अलीपुरद्वार", "अलिपूरद्वार"},
            new String[]{"dist-wb-bankura", "BNK", "Bankura", "बांकुड़ा", "बांकुरा"},
            new String[]{"dist-wb-birbhum", "BRB", "Birbhum (Suri)", "बीरभूम", "बीरभूम"},
            new String[]{"dist-wb-cooch-behar", "COB", "Cooch Behar", "कूचबिहार", "कूचबिहार"},
            new String[]{"dist-wb-dakshin-dinajpur", "DDN", "Dakshin Dinajpur (Balurghat)", "दक्षिण दिनाजपुर", "दक्षिण दिनाजपूर"},
            new String[]{"dist-darjeeling", "DAR", "Darjeeling", "दार्जिलिंग", "दार्जिलिंग"},
            new String[]{"dist-wb-hooghly", "HGH", "Hooghly (Chinsurah)", "हुगली", "हुगळी"},
            new String[]{"dist-howrah", "HWH", "Howrah", "हावड़ा", "हावडा"},
            new String[]{"dist-wb-jalpaiguri", "JPG", "Jalpaiguri", "जलपाईगुड़ी", "जलपाईगुडी"},
            new String[]{"dist-wb-jhargram", "JHG", "Jhargram", "झाड़ग्राम", "झारग्राम"},
            new String[]{"dist-wb-kalimpong", "KLP", "Kalimpong", "कालिम्पोंग", "कालिम्पॉंग"},
            new String[]{"dist-kolkata", "KOL", "Kolkata", "कोलकाता", "कोलकाता (कलकत्ता)"},
            new String[]{"dist-wb-malda", "MLD", "Malda (English Bazar)", "मालदा", "मालदा"},
            new String[]{"dist-wb-murshidabad", "MSD", "Murshidabad (Baharampur)", "मुर्शिदाबाद", "मुर्शिदाबाद"},
            new String[]{"dist-wb-nadia", "NDI", "Nadia (Krishnanagar)", "नादिया", "नादिया"},
            new String[]{"dist-north24", "N24", "North 24 Parganas (Barasat)", "उत्तर 24 परगना", "उत्तर २४ परगणा"},
            new String[]{"dist-wb-paschim-bardhaman", "PBD", "Paschim Bardhaman (Asansol)", "पश्चिम बर्धमान (आसनसोल)", "पश्चिम वर्धमान (आसनसोल)"},
            new String[]{"dist-wb-paschim-medinipur", "PMD", "Paschim Medinipur (Midnapore)", "पश्चिम मेदिनीपुर", "पश्चिम मेदिनीपूर"},
            new String[]{"dist-wb-purba-bardhaman", "PRB", "Purba Bardhaman", "पूर्व बर्धमान", "पूर्व वर्धमान"},
            new String[]{"dist-wb-purba-medinipur", "PRM", "Purba Medinipur (Tamluk)", "पूर्व मेदिनीपुर", "पूर्व मेदिनीपूर"},
            new String[]{"dist-wb-purulia", "PRL", "Purulia", "पुरुलिया", "पुरुलिया"},
            new String[]{"dist-wb-south24", "S24", "South 24 Parganas (Alipore)", "दक्षिण 24 परगना", "दक्षिण २४ परगणा"},
            new String[]{"dist-wb-uttar-dinajpur", "UDN", "Uttar Dinajpur (Raiganj)", "उत्तर दिनाजपुर", "उत्तर दिनाजपूर"}
        ));

        // 29. Andaman and Nicobar Islands (3)
        stateDistricts.put("state-an", Arrays.asList(
            new String[]{"dist-an-nicobar", "NIC", "Nicobar (Car Nicobar)", "निकोबार", "निकोबार"},
            new String[]{"dist-an-north-middle-andaman", "NMA", "North and Middle Andaman (Mayabunder)", "उत्तर और मध्य अंडमान", "उत्तर आणि मध्य अंदमान"},
            new String[]{"dist-portblair", "PBL", "South Andaman (Port Blair)", "दक्षिण अंडमान (पोर्ट ब्लेयर)", "दक्षिण अंदमान (पोर्ट ब्लेअर)"}
        ));

        // 30. Chandigarh (1)
        stateDistricts.put("state-ch", Collections.singletonList(
            new String[]{"dist-chandigarh", "CHD", "Chandigarh", "चंडीगढ़", "चंदिगढ"}
        ));

        // 31. Dadra and Nagar Haveli and Daman and Diu (3)
        stateDistricts.put("state-dn", Arrays.asList(
            new String[]{"dist-dn-dadra-nagar-haveli", "DNH", "Dadra and Nagar Haveli (Silvassa)", "दादरा और नगर हवेली (सिलवासा)", "दादरा आणि नगर हवेली (सिलवासा)"},
            new String[]{"dist-daman", "DAM", "Daman", "दमन", "दमण"},
            new String[]{"dist-dn-diu", "DIU", "Diu", "दीव", "दीव"}
        ));

        // 32. Delhi (11)
        stateDistricts.put("state-dl", Arrays.asList(
            new String[]{"dist-delhi-central", "DL-C", "Central Delhi", "मध्य दिल्ली", "मध्य दिल्ली"},
            new String[]{"dist-dl-east", "DL-E", "East Delhi", "पूर्वी दिल्ली", "पूर्व दिल्ली"},
            new String[]{"dist-delhi-newdelhi", "DL-ND", "New Delhi", "नई दिल्ली", "नवी दिल्ली"},
            new String[]{"dist-dl-north", "DL-N", "North Delhi", "उत्तरी दिल्ली", "उत्तर दिल्ली"},
            new String[]{"dist-dl-north-east", "DL-NE", "North East Delhi", "उत्तर पूर्वी दिल्ली", "ईशान्य दिल्ली"},
            new String[]{"dist-dl-north-west", "DL-NW", "North West Delhi", "उत्तर पश्चिम दिल्ली", "वायव्य दिल्ली"},
            new String[]{"dist-dl-shahdara", "DL-SH", "Shahdara", "शाहदरा", "शहादरा"},
            new String[]{"dist-delhi-south", "DL-S", "South Delhi", "दक्षिणी दिल्ली", "दक्षिण दिल्ली"},
            new String[]{"dist-dl-south-east", "DL-SE", "South East Delhi", "दक्षिण पूर्वी दिल्ली", "आग्नेय दिल्ली"},
            new String[]{"dist-dl-south-west", "DL-SW", "South West Delhi", "दक्षिण पश्चिम दिल्ली", "नैऋत्य दिल्ली"},
            new String[]{"dist-dl-west", "DL-W", "West Delhi", "पश्चिम दिल्ली", "पश्चिम दिल्ली"}
        ));

        // 33. Jammu and Kashmir (20)
        stateDistricts.put("state-jk", Arrays.asList(
            new String[]{"dist-jk-anantnag", "ANT", "Anantnag", "अनंतनाग", "अनंतनाग"},
            new String[]{"dist-jk-bandipora", "BPR", "Bandipora", "बांदीपोरा", "बांदीपोरा"},
            new String[]{"dist-jk-baramulla", "BRM", "Baramulla", "बारामूला", "बारामुल्ला"},
            new String[]{"dist-jk-budgam", "BDG", "Budgam", "बडगाम", "बडगाम"},
            new String[]{"dist-jk-doda", "DOD", "Doda", "डोडा", "डोडा"},
            new String[]{"dist-jk-ganderbal", "GBL", "Ganderbal", "गांदरबल", "गांदरबल"},
            new String[]{"dist-jammu", "JAM", "Jammu", "जम्मू", "जम्मू"},
            new String[]{"dist-jk-kathua", "KTH", "Kathua", "कठुआ", "कठुआ"},
            new String[]{"dist-jk-kishtwar", "KST", "Kishtwar", "किश्तवाड़", "किश्तवाड"},
            new String[]{"dist-jk-kulgam", "KLG", "Kulgam", "कुलगाम", "कुलगाम"},
            new String[]{"dist-jk-kupwara", "KPW", "Kupwara", "कुपवाड़ा", "कुपवाडा"},
            new String[]{"dist-jk-poonch", "PCH", "Poonch", "पुंछ", "पुंछ"},
            new String[]{"dist-jk-pulwama", "PLW", "Pulwama", "पुलवामा", "पुलवामा"},
            new String[]{"dist-jk-rajouri", "RJR", "Rajouri", "राजौरी", "राजौरी"},
            new String[]{"dist-jk-ramban", "RMB", "Ramban", "रामबन", "रामबन"},
            new String[]{"dist-jk-reasi", "REA", "Reasi", "रियासी", "रियासी"},
            new String[]{"dist-jk-samba", "SMB", "Samba", "सांबा", "सांबा"},
            new String[]{"dist-jk-shopian", "SHP", "Shopian", "शोपियां", "शोपियान"},
            new String[]{"dist-srinagar", "SXR", "Srinagar", "श्रीनगर", "श्रीनगर"},
            new String[]{"dist-jk-udhampur", "UDH", "Udhampur", "उधमपुर", "उधमपूर"}
        ));

        // 34. Ladakh (2)
        stateDistricts.put("state-la", Arrays.asList(
            new String[]{"dist-la-kargil", "KGL", "Kargil", "कारगिल", "कारगिल"},
            new String[]{"dist-leh", "LEH", "Leh", "लेह", "लेह"}
        ));

        // 35. Lakshadweep (1)
        stateDistricts.put("state-ld", Collections.singletonList(
            new String[]{"dist-kavaratti", "KAV", "Lakshadweep (Kavaratti)", "लक्षद्वीप (कवरत्ती)", "लक्षद्वीप (कवरत्ती)"}
        ));

        // 36. Puducherry (4)
        stateDistricts.put("state-py", Arrays.asList(
            new String[]{"dist-karaikal", "KRK", "Karaikal", "कराईकल", "काराईकल"},
            new String[]{"dist-mahe", "MAH", "Mahe", "माहे", "माहे"},
            new String[]{"dist-puducherry", "PDY", "Puducherry", "पुदुचेरी", "पुद्दुचेरी"},
            new String[]{"dist-yanam", "YAN", "Yanam", "यानम", "यानम"}
        ));

        List<District> allDistricts = new ArrayList<>();
        List<SubDistrict> allSubDistricts = new ArrayList<>();
        List<Village> allVillages = new ArrayList<>();

        Set<String> customSubDistrictsHandled = new HashSet<>();
        Set<String> customVillagesHandled = new HashSet<>();

        // 1. Explicit Puducherry Sub-Districts
        allSubDistricts.add(new SubDistrict("subdist-pdy-sadar", "dist-puducherry", "PDY-SDR", "Puducherry Sadar", "Puducherry Sadar", "पुदुचेरी सदर", "पुद्दुचेरी सदर"));
        allSubDistricts.add(new SubDistrict("subdist-villianur", "dist-puducherry", "PDY-VIL", "Villianur", "Villianur", "विल्लियानूर", "विल्लियानूर"));
        allSubDistricts.add(new SubDistrict("subdist-bahour", "dist-puducherry", "PDY-BAH", "Bahour", "Bahour", "बहूर", "बहूर"));
        allSubDistricts.add(new SubDistrict("subdist-karaikal", "dist-karaikal", "KRK-SDR", "Karaikal", "Karaikal", "कराईकल", "काराईकल"));
        allSubDistricts.add(new SubDistrict("subdist-mahe", "dist-mahe", "MAH-SDR", "Mahe", "Mahe", "माहे", "माहे"));
        allSubDistricts.add(new SubDistrict("subdist-yanam", "dist-yanam", "YAN-SDR", "Yanam", "Yanam", "यानम", "यानम"));

        customSubDistrictsHandled.add("dist-puducherry");
        customSubDistrictsHandled.add("dist-karaikal");
        customSubDistrictsHandled.add("dist-mahe");
        customSubDistrictsHandled.add("dist-yanam");

        // 2. Explicit Puducherry Villages
        allVillages.add(new Village("vil-puducherry-town", "subdist-pdy-sadar", "subdist-pdy-sadar", "VIL-PDY-01", "Puducherry Town", "Puducherry Town", "पुदुचेरी टाउन", "पुद्दुचेरी शहर", "605001"));
        allVillages.add(new Village("vil-ariankuppam", "subdist-pdy-sadar", "subdist-pdy-sadar", "VIL-PDY-02", "Ariankuppam", "Ariankuppam", "अरियानकुप्पम", "अरियानकुप्पम", "605007"));
        allVillages.add(new Village("vil-villianur", "subdist-villianur", "subdist-villianur", "VIL-PDY-03", "Villianur", "Villianur", "विल्लियानूर", "विल्लियानूर", "605110"));
        allVillages.add(new Village("vil-bahour", "subdist-bahour", "subdist-bahour", "VIL-PDY-04", "Bahour", "Bahour", "बहूर", "बहूर", "607402"));
        allVillages.add(new Village("vil-karaikal-town", "subdist-karaikal", "subdist-karaikal", "VIL-KRK-01", "Karaikal Town", "Karaikal Town", "कराईकल टाउन", "काराईकल शहर", "609602"));
        allVillages.add(new Village("vil-thirunallar", "subdist-karaikal", "subdist-karaikal", "VIL-KRK-02", "Thirunallar", "Thirunallar", "तिरुनाल्लार", "तिरुनाल्लार", "609607"));
        allVillages.add(new Village("vil-mahe-town", "subdist-mahe", "subdist-mahe", "VIL-MAH-01", "Mahe Town", "Mahe Town", "माहे टाउन", "माहे शहर", "673310"));
        allVillages.add(new Village("vil-yanam-town", "subdist-yanam", "subdist-yanam", "VIL-YAN-01", "Yanam Town", "Yanam Town", "यानम टाउन", "यानम शहर", "533464"));

        customVillagesHandled.add("subdist-pdy-sadar");
        customVillagesHandled.add("subdist-villianur");
        customVillagesHandled.add("subdist-bahour");
        customVillagesHandled.add("subdist-karaikal");
        customVillagesHandled.add("subdist-mahe");
        customVillagesHandled.add("subdist-yanam");

        // 3. Explicit Pune Sub-Districts & Villages
        allSubDistricts.add(new SubDistrict("subdist-haveli", "dist-pune", "PUN-HAV", "Haveli", "Haveli", "हवेली", "हवेली"));
        allSubDistricts.add(new SubDistrict("subdist-pune-city", "dist-pune", "PUN-CTY", "Pune City", "Pune City", "पुणे शहर", "पुणे शहर"));
        allSubDistricts.add(new SubDistrict("subdist-khed", "dist-pune", "PUN-KHD", "Khed (Rajgurunagar)", "Khed", "खेड (राजगुरुनगर)", "खेड (राजगुरुनगर)"));
        allSubDistricts.add(new SubDistrict("subdist-shirur", "dist-pune", "PUN-SHR", "Shirur", "Shirur", "शिरूर", "शिरूर"));
        allSubDistricts.add(new SubDistrict("subdist-baramati", "dist-pune", "PUN-BRM", "Baramati", "Baramati", "बारामती", "बारामती"));
        allSubDistricts.add(new SubDistrict("subdist-maval", "dist-pune", "PUN-MVL", "Maval (Vadgaon)", "Maval", "मावल (वडगांव)", "मावळ (वडगाव)"));
        allSubDistricts.add(new SubDistrict("subdist-ambegaon", "dist-pune", "PUN-AMB", "Ambegaon (Ghodegaon)", "Ambegaon", "आंबेगांव (घोडेगांव)", "आंबेगाव (घोडेगाव)"));

        customSubDistrictsHandled.add("dist-pune");

        allVillages.add(new Village("vil-hadapsar", "subdist-haveli", "subdist-haveli", "VIL-PUN-01", "Hadapsar", "Hadapsar", "हड़पसर", "हडपसर", "411028"));
        allVillages.add(new Village("vil-wagholi", "subdist-haveli", "subdist-haveli", "VIL-PUN-02", "Wagholi", "Wagholi", "वाघोली", "वाघोली", "412207"));
        allVillages.add(new Village("vil-khadakwasla", "subdist-haveli", "subdist-haveli", "VIL-PUN-03", "Khadakwasla", "Khadakwasla", "खड़कवासला", "खडकवासला", "411024"));
        allVillages.add(new Village("vil-uruli-kanchan", "subdist-haveli", "subdist-haveli", "VIL-PUN-04", "Uruli Kanchan", "Uruli Kanchan", "उरुली कांचन", "उरुळी कांचन", "412202"));
        allVillages.add(new Village("vil-pune-city-central", "subdist-pune-city", "subdist-pune-city", "VIL-PUN-05", "Shivajinagar / Pune City", "Shivajinagar", "शिवाजीनगर", "शिवाजीनगर / पुणे शहर", "411005"));
        allVillages.add(new Village("vil-ranjangaon", "subdist-shirur", "subdist-shirur", "VIL-PUN-06", "Ranjangaon Ganpati", "Ranjangaon Ganpati", "रांजणगांव", "रांजणगाव गणपती", "412209"));
        allVillages.add(new Village("vil-chakan", "subdist-khed", "subdist-khed", "VIL-PUN-07", "Chakan", "Chakan", "चाकण", "चाकण", "410501"));
        allVillages.add(new Village("vil-baramati-town", "subdist-baramati", "subdist-baramati", "VIL-PUN-08", "Baramati Town", "Baramati Town", "बारामती टाउन", "बारामती शहर", "413102"));
        allVillages.add(new Village("vil-vadgaon-maval", "subdist-maval", "subdist-maval", "VIL-PUN-09", "Vadgaon Maval", "Vadgaon Maval", "वडगांव मावल", "वडगाव मावळ", "412106"));
        allVillages.add(new Village("vil-manchar", "subdist-ambegaon", "subdist-ambegaon", "VIL-PUN-10", "Manchar", "Manchar", "मंचर", "मंचर", "410503"));

        customVillagesHandled.add("subdist-haveli");
        customVillagesHandled.add("subdist-pune-city");
        customVillagesHandled.add("subdist-khed");
        customVillagesHandled.add("subdist-shirur");
        customVillagesHandled.add("subdist-baramati");
        customVillagesHandled.add("subdist-maval");
        customVillagesHandled.add("subdist-ambegaon");

        for (Map.Entry<String, List<String[]>> entry : stateDistricts.entrySet()) {
            String stateId = entry.getKey();
            List<String[]> dList = entry.getValue();
            for (String[] d : dList) {
                String distId = d[0];
                String code = d[1];
                String name = d[2];
                String nameHi = d[3];
                String nameMr = d[4];
                allDistricts.add(new District(distId, stateId, code, name, name, nameHi, nameMr));

                if (!customSubDistrictsHandled.contains(distId)) {
                    String cleanSlug = distId.replace("dist-", "");
                    String subDistId = "subdist-" + cleanSlug;
                    String subDistName = name.replaceAll("\\s*\\(.*\\)", "") + " Sadar/Tehsil";
                    allSubDistricts.add(new SubDistrict(subDistId, distId, code + "-SDR", subDistName, subDistName, subDistName, subDistName));

                    if (!customVillagesHandled.contains(subDistId)) {
                        String villageId = "vil-" + cleanSlug + "-town";
                        String villageName = name.replaceAll("\\s*\\(.*\\)", "") + " Town/Village";
                        allVillages.add(new Village(villageId, subDistId, subDistId, "VIL-" + code, villageName, villageName, villageName, villageName, "000000"));
                    }
                }
            }
        }

        System.out.println("Total States/UTs Covered: " + stateDistricts.size());
        System.out.println("Total Districts Generated: " + allDistricts.size());
        System.out.println("Total Sub-Districts Generated: " + allSubDistricts.size());
        System.out.println("Total Villages Generated: " + allVillages.size());

        // Write india_districts.json
        StringBuilder sbDist = new StringBuilder("[\n");
        for (int i = 0; i < allDistricts.size(); i++) {
            District d = allDistricts.get(i);
            sbDist.append("  { \"id\": \"").append(d.id)
                  .append("\", \"stateId\": \"").append(d.stateId)
                  .append("\", \"code\": \"").append(d.code)
                  .append("\", \"name\": \"").append(escapeJson(d.name))
                  .append("\", \"nameEn\": \"").append(escapeJson(d.nameEn))
                  .append("\", \"nameHi\": \"").append(escapeJson(d.nameHi))
                  .append("\", \"nameMr\": \"").append(escapeJson(d.nameMr))
                  .append("\", \"isActive\": true }");
            if (i < allDistricts.size() - 1) sbDist.append(",");
            sbDist.append("\n");
        }
        sbDist.append("]\n");
        writeFile("E:/new project/KaamSetu/backend/src/main/resources/data/india_districts.json", sbDist.toString());

        // Write india_subdistricts.json
        StringBuilder sbSub = new StringBuilder("[\n");
        for (int i = 0; i < allSubDistricts.size(); i++) {
            SubDistrict s = allSubDistricts.get(i);
            sbSub.append("  { \"id\": \"").append(s.id)
                 .append("\", \"districtId\": \"").append(s.districtId)
                 .append("\", \"code\": \"").append(s.code)
                 .append("\", \"name\": \"").append(escapeJson(s.name))
                 .append("\", \"nameEn\": \"").append(escapeJson(s.nameEn))
                 .append("\", \"nameHi\": \"").append(escapeJson(s.nameHi))
                 .append("\", \"nameMr\": \"").append(escapeJson(s.nameMr))
                 .append("\", \"isActive\": true }");
            if (i < allSubDistricts.size() - 1) sbSub.append(",");
            sbSub.append("\n");
        }
        sbSub.append("]\n");
        writeFile("E:/new project/KaamSetu/backend/src/main/resources/data/india_subdistricts.json", sbSub.toString());

        // Write india_villages.json
        StringBuilder sbVil = new StringBuilder("[\n");
        for (int i = 0; i < allVillages.size(); i++) {
            Village v = allVillages.get(i);
            sbVil.append("  { \"id\": \"").append(v.id)
                 .append("\", \"subDistrictId\": \"").append(v.subDistrictId)
                 .append("\", \"talukaId\": \"").append(v.talukaId)
                 .append("\", \"code\": \"").append(v.code)
                 .append("\", \"name\": \"").append(escapeJson(v.name))
                 .append("\", \"nameEn\": \"").append(escapeJson(v.nameEn))
                 .append("\", \"nameHi\": \"").append(escapeJson(v.nameHi))
                 .append("\", \"nameMr\": \"").append(escapeJson(v.nameMr))
                 .append("\", \"pinCode\": \"").append(v.pinCode)
                 .append("\", \"isActive\": true }");
            if (i < allVillages.size() - 1) sbVil.append(",");
            sbVil.append("\n");
        }
        sbVil.append("]\n");
        writeFile("E:/new project/KaamSetu/backend/src/main/resources/data/india_villages.json", sbVil.toString());

        System.out.println("All backend location JSON files generated successfully!");

        // Also update js/data.js locationMasterData
        updateJsDataFile(allDistricts, allSubDistricts, allVillages);
        System.out.println("js/data.js updated with complete location master data!");
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

        // Read states JSON directly
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
