/**
 * KaamSetu Multilingual Localization Engine (Day-1 Architecture)
 * Supports: Marathi (मराठी), Hindi (हिंदी), English (en)
 * Complete Key-Based Translations across all 3 Role Dashboards & System UI
 */

const translations = {
  // Common UI Actions & States
  "common.save": { en: "Save", mr: "सेव्ह करा", hi: "सहेजें" },
  "common.cancel": { en: "Cancel", mr: "रद्द करा", hi: "रद्द करें" },
  "common.close": { en: "Close", mr: "बंद करा", hi: "बंद करें" },
  "common.back": { en: "Back", mr: "मागे", hi: "पीछे" },
  "common.next": { en: "Next →", mr: "पुढे जा →", hi: "आगे बढ़ें →" },
  "common.submit": { en: "Submit", mr: "सबमिट करा", hi: "सबमिट करें" },
  "common.loading": { en: "Loading...", mr: "लोड होत आहे...", hi: "लोड हो रहा है..." },
  "common.retry": { en: "Retry", mr: "पुन्हा प्रयत्न करा", hi: "पुनः प्रयास करें" },
  "common.all": { en: "All", mr: "सर्व", hi: "सभी" },
  "common.edit": { en: "Edit", mr: "संपादन", hi: "संपादित करें" },
  "common.delete": { en: "Delete", mr: "हटवा", hi: "हटाएं" },

  // Brand & Global
  "app.name": { en: "KaamSetu", mr: "कामसेतू", hi: "कामसेतु" },
  "app.tagline": {
    en: "Rural & Village Local Jobs Platform",
    mr: "गाव पातळीवरील स्थानिक रोजगार मंच",
    hi: "ग्रामीण व स्थानीय रोजगार मंच"
  },
  
  // Auth & OTP Modal Keys
  "auth.loginTitle": { en: "Mobile Login / Sign Up", mr: "मोबाईल लॉगिन / नोंदणी", hi: "मोबाइल लॉगिन / पंजीकरण" },
  "auth.enterMobile": { en: "Enter 10-digit mobile number", mr: "१० अंकी मोबाईल नंबर टाका", hi: "१० अंकों का मोबाइल नंबर दर्ज करें" },
  "auth.sendOtpBtn": { en: "Send OTP →", mr: "ओटीपी पाठवा →", hi: "ओटीपी भेजें →" },
  "auth.enterOtp": { en: "Enter 6-digit OTP", mr: "६ अंकी ओटीपी टाका", hi: "६ अंकों का ओटीपी दर्ज करें" },
  "auth.verifyBtn": { en: "Verify & Enter →", mr: "पडताळणी करा आणि पुढे जा →", hi: "सत्यापित करें और आगे बढ़ें →" },
  "auth.resendOtp": { en: "Resend OTP", mr: "पुन्हा ओटीपी पाठवा", hi: "पुनः ओटीपी भेजें" },
  "auth.otpSentTo": { en: "OTP sent to", mr: "या क्रमांकावर ओटीपी पाठवला:", hi: "इस नंबर पर ओटीपी भेजा गया:" },
  "auth.logoutBtn": { en: "Log Out", mr: "बाहेर पडा (लॉग आउट)", hi: "लॉग आउट" },
  "auth.onlineStatus": { en: "Live API", mr: "थेट API", hi: "लाइव API" },
  "auth.offlineStatus": { en: "Local Mode", mr: "स्थानिक मोड", hi: "स्थानीय मोड" },
  
  // Onboarding & Role Selection
  "onboarding.welcome": {
    en: "Welcome to KaamSetu",
    mr: "कामसेतू मध्ये आपले स्वागत आहे",
    hi: "कामसेतु में आपका स्वागत है"
  },
  "onboarding.question": {
    en: "What do you want to do?",
    mr: "तुम्हाला काय करायचे आहे?",
    hi: "आप क्या करना चाहते हैं?"
  },
  "role.worker.title": {
    en: "👷 Find Work",
    mr: "👷 मला काम पाहिजे",
    hi: "👷 मुझे काम चाहिए"
  },
  "role.worker.desc": {
    en: "Find local farm, construction, household & repair work nearby",
    mr: "जवळपास शेती, बांधकाम, घरकाम व दुरुस्तीची कामे शोधा",
    hi: "आसपास खेती, निर्माण, घरेलू और मरम्मत का काम खोजें"
  },
  "role.provider.title": {
    en: "👤 Find Workers",
    mr: "👤 मला कामगार पाहिजे",
    hi: "👤 मुझे कामगार चाहिए"
  },
  "role.provider.desc": {
    en: "Hire trusted village workers, farm laborers & skilled helpers",
    mr: "स्थानिक शेतमजूर, कारागीर व मदतनीस त्वरित कामावर घ्या",
    hi: "स्थानीय मजदूर, कारीगर और सहायक तुरंत काम पर रखें"
  },
  "role.admin.title": {
    en: "🛡️ Admin Center",
    mr: "🛡️ प्रशासन केंद्र",
    hi: "🛡️ एडमिन केंद्र"
  },
  
  // Onboarding Steps
  "step.language": { en: "Select Language", mr: "भाषा निवडा", hi: "भाषा चुनें" },
  "step.mobile": { en: "Mobile Number", mr: "मोबाईल नंबर", hi: "मोबाइल नंबर" },
  "step.otp": { en: "Verify OTP", mr: "ओटीपी पडताळणी", hi: "ओटीपी सत्यापन" },
  "step.location": { en: "Select Hierarchical Location", mr: "पदानुक्रमित ठिकाण निवडा", hi: "स्थान पदानुक्रम चुनें" },
  "location.country": { en: "Country", mr: "देश (Country)", hi: "देश (Country)" },
  "location.state": { en: "State", mr: "राज्य (State)", hi: "राज्य (State)" },
  "location.district": { en: "District", mr: "जिल्हा (District)", hi: "जिला (District)" },
  "location.taluka": { en: "Taluka / Tehsil", mr: "तालुका (Taluka)", hi: "तालुका / तहसील" },
  "location.village": { en: "Village", mr: "गाव (Village)", hi: "गाँव (Village)" },
  "location.selectCountry": { en: "Select Country", mr: "देश निवडा (Select Country)", hi: "देश चुनें (Select Country)" },
  "location.selectState": { en: "Select State", mr: "राज्य निवडा (Select State)", hi: "राज्य चुनें (Select State)" },
  "location.selectDistrict": { en: "Select District", mr: "जिल्हा निवडा (Select District)", hi: "जिला चुनें (Select District)" },
  "location.selectTaluka": { en: "Select Taluka", mr: "तालुका निवडा (Select Taluka)", hi: "तालुका चुनें (Select Taluka)" },
  "location.selectVillage": { en: "Select Village", mr: "गाव निवडा (Select Village)", hi: "गाँव चुनें (Select Village)" },
  "location.loading": { en: "Loading locations...", mr: "ठिकाणे लोड होत आहेत...", hi: "स्थान लोड हो रहे हैं..." },
  "location.emptyVillages": { en: "No villages available for this taluka", mr: "या तालुक्यासाठी गावे उपलब्ध नाहीत", hi: "इस तालुका के लिए गाँव उपलब्ध नहीं हैं" },
  "location.errorLoading": { en: "Failed to load locations. Please retry.", mr: "ठिकाणे लोड करण्यात त्रुटी. पुन्हा प्रयत्न करा.", hi: "स्थान लोड करने में विफल। पुनः प्रयास करें।" },
  "location.retry": { en: "Retry", mr: "पुन्हा प्रयत्न करा", hi: "पुनः प्रयास करें" },
  "step.profile": { en: "Basic Profile", mr: "वैयक्तिक माहिती", hi: "व्यक्तिगत जानकारी" },
  "btn.continue": { en: "Continue →", mr: "पुढे जा →", hi: "आगे बढ़ें →" },
  "btn.verify": { en: "Verify & Enter", mr: "पडताळणी करा व सुरू करा", hi: "सत्यापित करें और शुरू करें" },
  "btn.changeRole": { en: "Switch Role", mr: "भूमिका बदला", hi: "भूमिका बदलें" },

  // Navigation Tabs (System Wide)
  "nav.home": { en: "Home", mr: "मुख्य", hi: "होम" },
  "nav.jobs": { en: "Jobs", mr: "कामे", hi: "काम" },
  "nav.myJobs": { en: "My Jobs", mr: "माझी कामे", hi: "मेरे काम" },
  "nav.messages": { en: "Messages", mr: "संदेश", hi: "संदेश" },
  "nav.profile": { en: "Profile", mr: "प्रोफाइल", hi: "प्रोफाइल" },
  "nav.dashboard": { en: "Dashboard", mr: "डॅशबोर्ड", hi: "डैशबोर्ड" },
  "nav.postJob": { en: "Post Job", mr: "काम टाका", hi: "काम डालें" },
  "nav.workers": { en: "Find Workers", mr: "कामगार शोधा", hi: "कामगार खोजें" },
  "nav.applications": { en: "Applications", mr: "आलेले अर्ज", hi: "आवेदन" },
  "nav.admin": { en: "Admin", mr: "प्रशासन", hi: "एडमिन" },

  // Admin Sub-Navigation Keys
  "nav.admin.overview": { en: "Overview", mr: "एकूण आढावा", hi: "अवलोकन" },
  "nav.admin.users": { en: "Users", mr: "वापरकर्ते", hi: "उपयोगकर्ता" },
  "nav.admin.workers": { en: "Workers", mr: "कामगार", hi: "कामगार" },
  "nav.admin.providers": { en: "Providers", mr: "नियोक्ते", hi: "नियोक्ता" },
  "nav.admin.jobs": { en: "Jobs", mr: "कामे", hi: "काम" },
  "nav.admin.reports": { en: "Reports", mr: "तक्रारी", hi: "शिकायतें" },
  "nav.admin.disputes": { en: "Disputes", mr: "वाद", hi: "विवाद" },
  "nav.admin.reviews": { en: "Reviews", mr: "अभिप्राय", hi: "समीक्षाएं" },
  "nav.admin.verification": { en: "Verification", mr: "पडताळणी", hi: "सत्यापन" },
  "nav.admin.notifications": { en: "Notifications", mr: "सूचना", hi: "अधिसूचनाएं" },
  "nav.admin.userMessages": { en: "User Inquiries", mr: "वापरकर्ते संदेश", hi: "उपयोगकर्ता संदेश" },
  "nav.admin.analytics": { en: "Analytics", mr: "विश्लेषण", hi: "विश्लेषण" },
  "nav.admin.security": { en: "Security", mr: "सुरक्षा", hi: "सुरक्षा" },
  "nav.admin.settings": { en: "Settings", mr: "सेटिंग्ज", hi: "सेटिंग्स" },
  "admin.audit.title": { en: "Security Audit Trail", mr: "सुरक्षा ऑडिट ट्रेल", hi: "सुरक्षा ऑडिट ट्रेल" },
  "admin.audit.liveStream": { en: "Live Event Monospace Stream", mr: "थेट इव्हेंट स्ट्रीम", hi: "लाइव इवेंट स्ट्रीम" },

  // Privacy & Progressive Verification (Section 12 & 15)
  "privacy.title": { en: "Privacy & Account Safety", mr: "गोपनीयता व खाते सुरक्षा", hi: "गोपनीयता और खाता सुरक्षा" },
  "privacy.phoneMasked": { en: "Phone numbers remain private until mutual job acceptance", mr: "दोन्ही बाजूंची सहमती होईपर्यंत फोन नंबर सुरक्षित लपवला जातो", hi: "दोनों पक्षों की सहमति तक फोन नंबर सुरक्षित छिपा रहता है" },
  "privacy.phoneToggle": { en: "Hide Phone Number (Masked Contact)", mr: "फोन नंबर लपवा (मास्क्ड संपर्क)", hi: "फोन नंबर छिपाएं (मास्क्ड संपर्क)" },
  "privacy.locationToggle": { en: "Hide Exact GPS (Display Village Only)", mr: "अचूक GPS लपवा (फक्त गाव दाखवा)", hi: "सटीक GPS छिपाएं (केवल गांव दिखाएं)" },
  "privacy.smsAlerts": { en: "Receive Urgent SMS Job Alerts", mr: "तात्काळ कामाचे SMS अलर्ट मिळवा", hi: "तत्काल काम के SMS अलर्ट प्राप्त करें" },
  "privacy.whatsappAlerts": { en: "WhatsApp Notifications", mr: "व्हॉट्सॲप सूचना (WhatsApp)", hi: "व्हाट्सएप सूचनाएं (WhatsApp)" },
  "privacy.deactivate": { en: "Deactivate Account Temporarily", mr: "खाते तात्पुरते गोठवा (Deactivate)", hi: "खाता अस्थायी रूप से निष्क्रिय करें" },
  "privacy.delete": { en: "Delete Account & Anonymize Data", mr: "खाते कायमचे हटवा (Delete Account)", hi: "खाता हमेशा के लिए हटाएं" },
  "privacy.save": { en: "Save Privacy Settings", mr: "गोपनीयता सेटिंग सेव्ह करा", hi: "गोपनीयता सेटिंग सहेजें" },
  "verify.title": { en: "Verification & Trust Badges", mr: "पडताळणी व विश्वास बॅज", hi: "सत्यापन और विश्वास बैज" },
  "verify.mobile": { en: "Mobile OTP Verified", mr: "मोबाईल ओटीपी पडताळणी पूर्ण", hi: "मोबाइल ओटीपी सत्यापित" },
  "verify.location": { en: "Gram Panchayat / Village Verified", mr: "ग्रामपंचायत / गाव पडताळणी", hi: "ग्राम पंचायत / गांव सत्यापित" },
  "verify.kyc": { en: "Aadhaar / ID KYC Verified", mr: "आधार / ओळखपत्र KYC", hi: "आधार / पहचान पत्र KYC" },
  "verify.skill": { en: "Skill & Community Reference", mr: "कौशल्य व स्थानिक संदर्भ", hi: "कौशल और स्थानीय संदर्भ" },
  "verify.trusted": { en: "Trusted Star Member (5+ Jobs)", mr: "विश्वसनीय स्टार सदस्य (५+ कामे)", hi: "विश्वसनीय स्टार सदस्य (५+ काम)" },

  // Job Categories
  "cat.all": { en: "All Work", mr: "सर्व कामे", hi: "सभी काम" },
  "cat.recurring": { en: "🔁 Recurring / Monthly", mr: "🔁 नियमित / मासिक कामे", hi: "🔁 नियमित / मासिक काम" },
  "cat.saved": { en: "❤️ Saved Jobs", mr: "❤️ सेव्ह केलेली कामे", hi: "❤️ सहेजे गए काम" },
  "cat.agriculture": { en: "🌾 Agriculture", mr: "🌾 शेती काम", hi: "🌾 कृषि कार्य" },
  "cat.construction": { en: "🧱 Construction", mr: "🧱 बांधकाम", hi: "🧱 निर्माण कार्य" },
  "cat.household": { en: "🧹 Household", mr: "🧹 घरकाम", hi: "🧹 घरेलू कार्य" },
  "cat.driving": { en: "🚗 Driving/Tractor", mr: "🚗 ड्रायव्हर/ट्रॅक्टर", hi: "🚗 वाहन चालक" },
  "cat.painting": { en: "🎨 Painting", mr: "🎨 रंगकाम", hi: "🎨 रंगाई कार्य" },
  "cat.plumbing": { en: "🔧 Plumbing/Electric", mr: "🔧 प्लंबिंग/इलेक्ट्रिक", hi: "🔧 मरम्मत कार्य" },
  "cat.village": { en: "🏛️ Village Work", mr: "🏛️ ग्रामपंचायत काम", hi: "🏛️ ग्राम पंचायत कार्य" },

  // Provider Types
  "provider.type.farmer": { en: "👨‍🌾 Farmer", mr: "👨‍🌾 शेतकरी", hi: "👨‍🌾 किसान" },
  "provider.type.household": { en: "🏠 Household", mr: "🏠 घरगुती", hi: "🏠 घरेलू" },
  "provider.type.contractor": { en: "🧱 Contractor", mr: "🧱 कंत्राटदार", hi: "🧱 ठेकेदार" },
  "provider.type.panchayat": { en: "🏛️ Gram Panchayat", mr: "🏛️ ग्रामपंचायत", hi: "🏛️ ग्राम पंचायत" },
  "provider.type.business": { en: "🏪 Local Shop/Business", mr: "🏪 स्थानिक व्यवसाय", hi: "🏪 स्थानीय व्यवसाय" },
  "provider.postJob": { en: "Post New Job", mr: "नवीन काम टाका", hi: "नया काम डालें" },
  "provider.findWorkers": { en: "Find Nearby Workers", mr: "जवळपास उपलब्ध कामगार शोधा", hi: "आसपास के कामगार खोजें" },
  "provider.applications": { en: "Applications Received", mr: "आलेले अर्ज", hi: "प्राप्त आवेदन" },

  // Worker Feed & Actions
  "feed.recommended": { en: "Recommended For You", mr: "तुमच्यासाठी शिफारस केलेली कामे", hi: "आपके लिए अनुशंसित काम" },
  "feed.nearby": { en: "Nearby Local Jobs", mr: "जवळपासची सर्व कामे", hi: "आसपास के सभी काम" },
  "filter.radius": { en: "Travel Distance", mr: "प्रवासाचे अंतर", hi: "यात्रा दूरी" },
  
  // Job Statuses & Actions
  "job.apply": { en: "Apply Now", mr: "आता अर्ज करा", hi: "आवेदन करें" },
  "job.applied": { en: "Applied ✓", mr: "अर्ज केला ✓", hi: "आवेदन किया ✓" },
  "job.view": { en: "View Job", mr: "काम पहा", hi: "काम देखें" },
  "job.post": { en: "Post Job", mr: "काम टाका", hi: "काम डालें" },
  "job.save": { en: "♡ Save", mr: "♡ सेव्ह करा", hi: "♡ सहेजें" },
  "job.saved": { en: "♥ Saved", mr: "♥ सेव्ह झाले", hi: "♥ सहेजा गया" },
  "job.workersNeeded": { en: "Workers Needed", mr: "कामगार हवेत", hi: "कामगार चाहिए" },
  "job.dailyWage": { en: "₹/day", mr: "₹/दिवस", hi: "₹/दिन" },
  "job.urgentBadge": { en: "🔴 URGENT", mr: "🔴 तात्काळ", hi: "🔴 तत्काल" },
  "job.filled": { en: "Filled", mr: "जागा भरल्या", hi: "पद भर गए" },
  "job.expired": { en: "Expired", mr: "मुदत संपली", hi: "समय सीमा समाप्त" },
  "job.cancelled": { en: "Cancelled", mr: "रद्द झाले", hi: "रद्द हुआ" },
  "job.inProgress": { en: "In Progress", mr: "काम सुरू", hi: "प्रगति पर" },
  "job.completed": { en: "Completed", mr: "पूर्ण झाले", hi: "पूर्ण हुआ" },
  "job.draft": { en: "Draft", mr: "मसुदा", hi: "ड्राफ्ट" },

  // Worker Confirmation Lifecycle
  "worker.confirmModal.title": { en: "Employer Selected You!", mr: "नियोक्त्याने तुमची निवड केली आहे!", hi: "नियोक्ता ने आपका चयन किया है!" },
  "worker.confirmModal.desc": { en: "Please confirm your availability before the timer expires.", mr: "कृपया वेळेच्या आत कामाची पुष्टी करा.", hi: "कृपया समय समाप्त होने से पहले काम की पुष्टि करें।" },
  "worker.action.confirm": { en: "Accept & Confirm (होय, मी येणार)", mr: "स्वीकार करा (होय, मी येणार)", hi: "स्वीकार करें (हाँ, मैं आऊँगा)" },
  "worker.action.decline": { en: "Decline (नकार द्या)", mr: "नकार द्या", hi: "अस्वीकार करें" },
  "worker.timeout.alert": { en: "Confirmation Window", mr: "पुष्टी करण्याची मुदत", hi: "पुष्टि की समय सीमा" },

  // Job & Assignment Status Badges (camelCase keys — canonical)
  "status.open": { en: "Open", mr: "खुले आहे", hi: "खुला है" },
  "status.filled": { en: "Filled", mr: "जागा भरल्या", hi: "पद भर गए" },
  "status.applied": { en: "Applied", mr: "अर्ज केला", hi: "आवेदन किया" },
  "status.selected": { en: "Selected", mr: "निवड झाली", hi: "चयनित" },
  "status.confirmed": { en: "Confirmed", mr: "पुष्टी झाली", hi: "पुष्टि हुई" },
  "status.declined": { en: "Declined", mr: "नकार दिला", hi: "अस्वीकृत" },
  "status.noResponse": { en: "Timeout", mr: "वेळ संपली", hi: "समय समाप्त" },
  "status.no_response": { en: "Timeout", mr: "वेळ संपली", hi: "समय समाप्त" },
  "status.inProgress": { en: "In Progress", mr: "काम सुरू", hi: "प्रगति पर" },
  "status.in_progress": { en: "In Progress", mr: "काम सुरू", hi: "प्रगति पर" },
  "status.completionRequested": { en: "Completion Requested", mr: "पूर्णतेची विनंती", hi: "पूर्णता अनुरोध" },
  "status.completion_requested": { en: "Completion Requested", mr: "पूर्णतेची विनंती", hi: "पूर्णता अनुरोध" },
  "status.completed": { en: "Completed", mr: "पूर्ण झाले", hi: "पूर्ण हुआ" },
  "status.expired": { en: "Expired", mr: "मुदत संपली", hi: "समय सीमा समाप्त" },
  "status.cancelled": { en: "Cancelled", mr: "रद्द झाले", hi: "रद्द हुआ" },

  // Bilateral Completion & Payment Acknowledgment
  "btn.markCompleted": { en: "Mark Work Completed", mr: "काम पूर्ण झाल्याचे नोंदवा", hi: "काम पूरा हुआ दर्ज करें" },
  "btn.confirmCompleted": { en: "Confirm Completion", mr: "काम पूर्णत्वाची पुष्टी करा", hi: "काम पूरा होने की पुष्टि करें" },
  "payment.ackTitle": { en: "Payment Acknowledgment", mr: "पैसे मिळाल्याची पावती", hi: "भुगतान पावती" },
  "payment.receivedCheckbox": { en: "Payment Received in Full (Cash/UPI) ✓", mr: "पूर्ण पैसे मिळाले (रोख / UPI) ✓", hi: "पूरा भुगतान प्राप्त हुआ (नकद / UPI) ✓" },
  "payment.noHiddenCommission": { en: "No hidden commission during the V1 pilot", mr: "V1 पायलट दरम्यान कोणतेही कमिशन नाही", hi: "V1 पायलट के दौरान कोई कमीशन नहीं" },

  // Multi-Dimensional Ratings
  "rating.overall": { en: "Overall Rating", mr: "एकूण रेटिंग", hi: "कुल रेटिंग" },
  "rating.workQuality": { en: "Work Quality", mr: "कामाचा दर्जा", hi: "काम की गुणवत्ता" },
  "rating.punctuality": { en: "Punctuality", mr: "वेळेवर येणे", hi: "समयबद्धता" },
  "rating.behavior": { en: "Behavior & Respect", mr: "वागणूक व आदर", hi: "व्यवहार व सम्मान" },
  "rating.reliability": { en: "Reliability", mr: "विश्वासार्हता", hi: "विश्वसनीयता" },
  "rating.paymentReliability": { en: "Payment Reliability", mr: "पैसे वेळेवर दिले", hi: "भुगतान विश्वसनीयता" },
  "rating.jobAccuracy": { en: "Job Description Accuracy", mr: "कामाचे योग्य वर्णन", hi: "काम का सही विवरण" },
  "rating.communication": { en: "Communication", mr: "स्पष्ट संवाद", hi: "स्पष्ट संवाद" },
  "rating.rateWorker": { en: "Rate Worker", mr: "कामगाराला अभिप्राय द्या", hi: "कामगार को रेटिंग दें" },
  "rating.rateProvider": { en: "Rate Employer", mr: "नियोक्त्याला अभिप्राय द्या", hi: "नियोक्ता को रेटिंग दें" },

  // Universal Report Action
  "report.title": { en: "Report & Safety Concern", mr: "तक्रार व सुरक्षा नोंद", hi: "शिकायत व सुरक्षा चिंता" },
  "report.reason": { en: "Reason for Report", mr: "तक्रारीचे कारण", hi: "शिकायत का कारण" },
  "report.submit": { en: "Submit Report to Admin", mr: "प्रशासनाला तक्रार पाठवा", hi: "एडमिन को शिकायत भेजें" },
  "report.btn": { en: "🚩 Report", mr: "🚩 तक्रार करा", hi: "🚩 शिकायत करें" },

  // Admin Messaging & Direct Notification
  "admin.sendMessage": { en: "💬 Message", mr: "💬 संदेश पाठवा", hi: "💬 संदेश भेजें" },
  "admin.sendMessageTitle": { en: "Send Admin Message / Notice", mr: "प्रशासकीय संदेश / नोटीस पाठवा", hi: "प्रशासनिक संदेश / नोटिस भेजें" },
  "admin.messageSent": { en: "Admin message sent successfully!", mr: "प्रशासकीय संदेश यशस्वीरीत्या पाठवला!", hi: "प्रशासनिक संदेश सफलतापूर्वक भेजा गया!" },
  "admin.officialNotice": { en: "🛡️ Official Notice from KaamSetu Admin", mr: "🛡️ कामसेतू प्रशासनाकडून अधिकृत सूचना", hi: "🛡️ कामसेतु प्रशासन से आधिकारिक सूचना" },
  "admin.directNotice": { en: "💬 Direct Message from KaamSetu Admin", mr: "💬 कामसेतू प्रशासनाकडून थेट संदेश", hi: "💬 कामसेतु प्रशासन से सीधा संदेश" },
  "admin.replyChat": { en: "💬 Open Chat / Reply", mr: "💬 थेट चॅट उघडा / उत्तर द्या", hi: "💬 चैट खोलें / उत्तर दें" },
  "admin.dismissNotice": { en: "✓ Dismiss", mr: "✓ समजले (Dismiss)", hi: "✓ समझ गया (Dismiss)" },
  "admin.contactAdmin": { en: "Contact Admin", mr: "प्रशासकाशी संपर्क", hi: "एडमिन से संपर्क" },
  "admin.adminSupport": { en: "Admin Support Desk", mr: "कामसेतू प्रशासकीय मदत कक्ष", hi: "कामसेतु प्रशासनिक सहायता कक्ष" },
  "admin.pendingSupportTitle": { en: "⏳ Pending Admin Approval", mr: "⏳ प्रशासकीय मंजुरी प्रलंबित", hi: "⏳ प्रशासनिक अनुमोदन लंबित" },
  "admin.pendingSupportDesc": { en: "Your registration is under verification. You can communicate directly with Admin for support.", mr: "आपली नोंदणी पडताळणीखाली आहे. आवश्यक माहिती किंवा मदतीसाठी आपण प्रशासनाशी येथे थेट संदेश पाठवू शकता.", hi: "आपका पंजीकरण सत्यापन के तहत है। सहायता के लिए आप यहां सीधे एडमिन से संपर्क कर सकते हैं।" },
  "admin.msgInbox": { en: "Admin Messages / Inbox", mr: "प्रशासकीय संदेश इनबॉक्स", hi: "प्रशासनिक संदेश इनबॉक्स" },
  "admin.unread": { en: "unread", mr: "न वाचलेले", hi: "अपठित" },
  "admin.filterAll": { en: "All Users", mr: "सर्व वापरकर्ते", hi: "सभी उपयोगकर्ता" },
  "admin.filterPending": { en: "Pending Users", mr: "प्रलंबित वापरकर्ते", hi: "लंबित उपयोगकर्ता" },
  "admin.filterWorkers": { en: "Workers", mr: "कामगार", hi: "कामगार" },
  "admin.filterProviders": { en: "Job Providers", mr: "नियोक्ते", hi: "नियोक्ता" },
  "admin.searchUsers": { en: "Search users by name, mobile, role...", mr: "नाव, मोबाईल किंवा भूमिकेनुसार शोधा...", hi: "नाम, मोबाइल या भूमिका से खोजें..." },

  // Notifications
  "notification.title": { en: "Notifications", mr: "सूचना", hi: "अधिसूचनाएं" },
  "notification.newJob": { en: "New Job Match", mr: "नवीन काम जुळले", hi: "नया काम मैच" },
  "notification.application": { en: "New Application", mr: "नवीन अर्ज आला", hi: "नया आवेदन प्राप्त" },
  "notification.selection": { en: "Worker Selected", mr: "कामगार निवड", hi: "कामगार चयन" },
  "notification.message": { en: "New Message", mr: "नवीन संदेश", hi: "नया संदेश" },
  "notification.reminder": { en: "Job Reminder", mr: "कामाचे स्मरणपत्र", hi: "काम का रिमाइंड" },
  "notification.ratings": { en: "New Review Received", mr: "नवीन अभिप्राय मिळाला", hi: "नया रिव्यू प्राप्त" },
  "notification.safety": { en: "Safety Alert", mr: "सुरक्षा सूचना", hi: "सुरक्षा अलर्ट" },
  "notification.account": { en: "Account Update", mr: "खाते अपडेट", hi: "खाता अपडेट" },

  // Privacy & Safety
  "privacy.title": { en: "Account & Data Privacy", mr: "खाते व डेटा गोपनीयता", hi: "खाता व डेटा गोपनीयता" },
  "privacy.deactivate": { en: "Deactivate Account", mr: "खाते तात्पुरते बंद करा", hi: "खाता अस्थायी रूप से बंद करें" },
  "privacy.delete": { en: "Request Account Deletion", mr: "खाते कायमचे हटवा", hi: "खाता स्थायी रूप से हटाएं" },
  "privacy.phoneMasked": { en: "Phone number is protected until job confirmation", mr: "कामाची पुष्टी होईपर्यंत फोन नंबर सुरक्षित आहे", hi: "काम की पुष्टि होने तक फ़ोन नंबर सुरक्षित है" },

  // Empty & Error States
  "empty.jobs": { en: "No jobs found in this distance radius. Try expanding radius.", mr: "या अंतरात सध्या कोणतीही कामे उपलब्ध नाहीत. प्रवासाचे अंतर वाढवून पहा.", hi: "इस दूरी में कोई काम नहीं मिला। यात्रा दूरी बढ़ाकर देखें।" },
  "empty.applications": { en: "No applications yet.", mr: "अद्याप कोणतेही अर्ज नाहीत.", hi: "अभी तक कोई आवेदन नहीं।" },
  "empty.messages": { en: "No messages yet.", mr: "अद्याप कोणतेही संदेश नाहीत.", hi: "अभी तक कोई संदेश नहीं।" },
  "empty.notifications": { en: "You're all caught up!", mr: "नवीन कोणत्याही सूचना नाहीत.", hi: "कोई नई अधिसूचना नहीं!" },
  "empty.workers": { en: "No available workers found nearby.", mr: "जवळपास उपलब्ध कामगार आढळले नाहीत.", hi: "आसपास कोई कामगार नहीं मिला।" },
  "empty.saved": { en: "No saved jobs yet. Tap ♡ on a job to save it.", mr: "अद्याप कोणतेही सेव्ह केलेले काम नाही. ♡ टॅप करा.", hi: "अभी कोई सहेजा गया काम नहीं। ♡ दबाएं।" },
  "error.requiredTitle": { en: "Please enter a job title.", mr: "कृपया कामाचे नाव टाका.", hi: "कृपया काम का नाम दर्ज करें।" },
  "error.requiredCategory": { en: "Please select a work category.", mr: "कृपया कामाचा प्रकार निवडा.", hi: "कृपया काम की श्रेणी चुनें।" },
  "error.generic": { en: "An error occurred. Please try again.", mr: "अडचण आली. पुन्हा प्रयत्न करा.", hi: "त्रुटि आई। पुनः प्रयास करें।" },

  // Provider Profile & Preferences
  "provider.profile.title": { en: "Employer Profile", mr: "माझी माहिती (नियोक्ता)", hi: "नियोक्ता प्रोफ़ाइल" },
  "provider.profile.edit": { en: "Edit Profile", mr: "प्रोफाइल संपादित करा", hi: "प्रोफ़ाइल संपादित करें" },
  "provider.profile.save": { en: "Save Changes", mr: "बदल जतन करा", hi: "बदलाव सहेजें" },
  "provider.profile.businessName": { en: "Farm / Business Name", mr: "शेत / व्यवसायाचे नाव", hi: "खेत / व्यवसाय का नाम" },
  "provider.profile.type": { en: "Employer Type", mr: "नियोक्ता प्रकार", hi: "नियोक्ता प्रकार" },
  "provider.profile.description": { en: "Farm Details / Address", mr: "शेती / कामाचे ठिकाण माहिती", hi: "खेत / कार्यस्थल विवरण" },

  // Worker Profile & Availability
  "worker.profile.title": { en: "My Profile", mr: "माझी माहिती", hi: "मेरी प्रोफाइल" },
  "worker.profile.edit": { en: "Edit Profile", mr: "प्रोफाइल संपादित करा", hi: "प्रोफाइल संपादित करें" },
  "worker.profile.save": { en: "Save Changes", mr: "बदल जतन करा", hi: "परिवर्तन सहेजें" },
  "worker.profile.skills": { en: "My Skills", mr: "माझ्या कौशल्य", hi: "मेरे कौशल" },
  "worker.profile.experience": { en: "Experience", mr: "अनुभव", hi: "अनुभव" },
  "worker.profile.rating": { en: "Rating", mr: "रेटिंग", hi: "रेटिंग" },
  "worker.profile.trust": { en: "Trust Index", mr: "विश्वास निर्देशांक", hi: "विश्वास सूचकांक" },
  "worker.profile.minWage": { en: "Min. Daily Wage", mr: "किमान दैनिक मोबदला", hi: "न्यूनतम दैनिक वेतन" },
  "worker.profile.radius": { en: "Travel Radius", mr: "प्रवासाचे अंतर", hi: "यात्रा दूरी" },
  "worker.availability.title": { en: "📅 Weekly Availability", mr: "📅 आठवड्याची उपलब्धता", hi: "📅 साप्ताहिक उपलब्धता" },
  "worker.availability.available": { en: "🟢 Available", mr: "🟢 उपलब्ध", hi: "🟢 उपलब्ध" },
  "worker.availability.unavailable": { en: "🔴 Unavailable", mr: "🔴 अनुपलब्ध", hi: "🔴 अनुपलब्ध" },
  "worker.day.mon": { en: "Mon", mr: "सोम", hi: "सोम" },
  "worker.day.tue": { en: "Tue", mr: "मंगळ", hi: "मंगल" },
  "worker.day.wed": { en: "Wed", mr: "बुध", hi: "बुध" },
  "worker.day.thu": { en: "Thu", mr: "गुरु", hi: "गुरु" },
  "worker.day.fri": { en: "Fri", mr: "शुक्र", hi: "शुक्र" },
  "worker.day.sat": { en: "Sat", mr: "शनि", hi: "शनि" },
  "worker.day.sun": { en: "Sun", mr: "रवि", hi: "रवि" },

  // Notification Center & 7 Event Channels
  "notif.center.title": { en: "Notifications", mr: "सूचना केंद्र", hi: "सूचना केंद्र" },
  "notif.markAllRead": { en: "Mark All Read", mr: "सर्व वाचल्या म्हणून नोंदवा", hi: "सभी पढ़े के रूप में चिह्नित करें" },
  "notif.cat.all": { en: "All", mr: "सर्व", hi: "सभी" },
  "notif.cat.jobs": { en: "Jobs", mr: "नवीन कामे", hi: "नौकरियां" },
  "notif.cat.applications": { en: "Applications", mr: "अर्ज स्थिती", hi: "आवेदन स्थिति" },
  "notif.cat.selections": { en: "Selections", mr: "निवड व पुष्टी", hi: "चयन व पुष्टि" },
  "notif.cat.messages": { en: "Messages", mr: "संदेश", hi: "संदेश" },
  "notif.cat.reminders": { en: "Reminders", mr: "स्मरणपत्रे", hi: "स्मरणपत्र" },
  "notif.cat.ratings": { en: "Ratings & Pay", mr: "अभिप्राय व पैसे", hi: "रेटिंग व भुगतान" },
  "notif.cat.safety": { en: "Safety & Account", mr: "सुरक्षा व खाते", hi: "सुरक्षा व खाता" },
  "notif.prefTitle": { en: "Notification Settings", mr: "सूचना प्राधान्ये", hi: "अधिसूचना सेटिंग्स" },

  // Matching Engine & Bookmarking Keys
  "match.score": { en: "Match", mr: "जुळणी", hi: "मैच" },
  "feed.recommended": { en: "🎯 Recommended For You", mr: "🎯 तुमच्यासाठी खास शिफारसी", hi: "🎯 आपके लिए अनुशंसित" },
  "feed.nearby": { en: "Nearby Open Jobs", mr: "जवळपासची कामे", hi: "आसपास के काम" },
  "btn.saveJob": { en: "Save", mr: "सेव्ह करा", hi: "सहेजें" },
  "btn.saveWorker": { en: "Save Worker", mr: "कामगार सेव्ह करा", hi: "कामगार सहेजें" },
  "nav.savedJobs": { en: "Saved Jobs", mr: "सेव्ह केलेली कामे", hi: "सहेजे गए काम" },
  "nav.savedWorkers": { en: "Saved Workers", mr: "सेव्ह केलेले कामगार", hi: "सहेजे गए कामगार" },

  // Job Detail
  "job.detail.title": { en: "Job Details", mr: "कामाची माहिती", hi: "काम की जानकारी" },
  "job.detail.description": { en: "Job Description", mr: "कामाचे वर्णन", hi: "काम का विवरण" },
  "job.detail.duration": { en: "Duration", mr: "कालावधी", hi: "अवधि" },
  "job.detail.workers": { en: "Workers Required", mr: "हवे असलेले कामगार", hi: "आवश्यक कामगार" },
  "job.detail.start": { en: "Start Date", mr: "सुरुवातीची तारीख", hi: "प्रारंभ तिथि" },
  "job.detail.viewFull": { en: "View Details", mr: "सविस्तर पहा", hi: "विवरण देखें" },

  // Post Job Form Labels
  "postjob.title": { en: "Job Title", mr: "कामाचे नाव", hi: "काम का नाम" },
  "postjob.category": { en: "Work Category", mr: "कामाचा प्रकार", hi: "काम की श्रेणी" },
  "postjob.description": { en: "Job Description", mr: "कामाचे वर्णन", hi: "काम का विवरण" },
  "postjob.location": { en: "Village / Location", mr: "गाव / ठिकाण", hi: "गाँव / स्थान" },
  "postjob.workers": { en: "Workers Needed", mr: "हवे असलेले कामगार", hi: "आवश्यक कामगार" },
  "postjob.wage": { en: "Daily Wage (₹)", mr: "दैनिक मोबदला (₹)", hi: "दैनिक वेतन (₹)" },
  "postjob.startDate": { en: "Start Date", mr: "सुरुवातीची तारीख", hi: "प्रारंभ तिथि" },
  "postjob.duration": { en: "Duration (days)", mr: "कालावधी (दिवस)", hi: "अवधि (दिन)" },
  "postjob.urgent": { en: "Mark as Urgent", mr: "तात्काळ म्हणून नोंदवा", hi: "तत्काल के रूप में चिह्नित करें" },
  "postjob.placeholder.title": { en: "e.g. Farm labor, Construction helper…", mr: "उदा. शेतमजूर, बांधकाम मदतनीस…", hi: "उदा. खेतिहर मजदूर, निर्माण सहायक…" },
  "postjob.placeholder.location": { en: "e.g. Shirur, Saswad…", mr: "उदा. शिरूर, सासवड…", hi: "उदा. शिरूर, सासवड…" },
  "postjob.placeholder.desc": { en: "Describe the work clearly…", mr: "कामाचे स्पष्ट वर्णन करा…", hi: "काम का स्पष्ट विवरण दें…" },
  // Post Job 3-Step Guided Wizard Labels
  "postjob.step1": { en: "1. Work Info", mr: "१. कामाची माहिती", hi: "१. काम की जानकारी" },
  "postjob.step2": { en: "2. Location & Date", mr: "२. गाव व तारीख", hi: "२. गाँव व तारीख" },
  "postjob.step3": { en: "3. Workers & Pay", mr: "३. कामगार व मोबदला", hi: "३. मजदूर व वेतन" },
  "postjob.next": { en: "Next Step →", mr: "पुढील पायरी →", hi: "अगला कदम →" },
  "postjob.prev": { en: "← Previous", mr: "← मागील", hi: "← पिछला" },
  "postjob.taluka": { en: "Taluka", mr: "तालुका", hi: "तहसील" },
  "postjob.district": { en: "District", mr: "जिल्हा", hi: "जिला" },
  "postjob.paymentMode": { en: "Payment Settlement", mr: "पैसे देण्याची पद्धत", hi: "भुगतान का तरीका" },
  "postjob.paymentDirect": { en: "Direct Settlement (Cash / UPI on Site)", mr: "थेट मोबदला (कामाच्या ठिकाणी रोख / UPI)", hi: "सीधा भुगतान (कार्यस्थल पर नकद / UPI)" },

  // Provider Applications & Candidate Review
  "provider.applicants.title": { en: "Applications Received", mr: "आलेले अर्ज", hi: "प्राप्त आवेदन" },
  "provider.applicants.select": { en: "Select Worker", mr: "कामगार निवडा", hi: "कामगार चुनें" },
  "provider.applicants.empty": { en: "No applications yet. Post a job to receive applications.", mr: "अद्याप कोणतेही अर्ज आलेले नाहीत. काम टाकल्यानंतर अर्ज येतील.", hi: "अभी कोई आवेदन नहीं। काम डालने के बाद आवेदन मिलेंगे।" },
  "provider.applicants.selectedBadge": { en: "Selection Sent (Pending Confirmation)", mr: "निवड पाठवली (पुष्टीची प्रतीक्षा)", hi: "चयन भेजा (पुष्टि की प्रतीक्षा)" },
  "provider.callWorker": { en: "📞 Call Worker", mr: "📞 फोन करा", hi: "📞 कॉल करें" },
  "provider.confirmedTeam": { en: "Confirmed Workers Team", mr: "पुष्टी झालेले कामगार", hi: "पुष्ट कामगार दल" },
  "worker.appliedToast": { en: "Applied successfully!", mr: "अर्ज यशस्वीरीत्या सादर केला!", hi: "आवेदन सफलतापूर्वक जमा हुआ!" },
  "worker.confirmedToast": { en: "Job confirmed! Slot secured.", mr: "कामाची पुष्टी झाली! जागा निश्चित केली.", hi: "काम की पुष्टि हुई! स्लॉट सुरक्षित।" },
  "worker.declinedToast": { en: "Job declined.", mr: "काम नाकारले.", hi: "काम अस्वीकार किया।" },
  "worker.profileUpdatedToast": { en: "Profile updated successfully!", mr: "माहिती यशस्वीरीत्या अद्यतनित केली!", hi: "प्रोफाइल सफलतापूर्वक अपडेट हुई!" },

  // Auth & Admin Approval Lifecycle Keys
  "auth.login": { en: "Login", mr: "लॉगिन करा", hi: "लॉगिन करें" },
  "auth.register": { en: "Create Account / Register", mr: "नवीन खाते तयार करा / नोंदणी", hi: "नया खाता बनाएं / पंजीकरण" },
  "auth.username": { en: "Username", mr: "युझरनेम (Username)", hi: "यूज़रनेम (Username)" },
  "auth.fullName": { en: "Full Name", mr: "पूर्ण नाव", hi: "पूरा नाम" },
  "auth.email": { en: "Email Address", mr: "ईमेल पत्ता (Email)", hi: "ईमेल पता (Email)" },
  "auth.password": { en: "Password", mr: "पासवर्ड (Password)", hi: "पासवर्ड (Password)" },
  "auth.confirmPassword": { en: "Confirm Password", mr: "पासवर्ड पुन्हा टाका", hi: "पासवर्ड दोबारा डालें" },
  "auth.selectRole": { en: "Select Account Role", mr: "खात्याची भूमिका निवडा", hi: "खाते की भूमिका चुनें" },
  "auth.sendOtp": { en: "Send OTP", mr: "ओटीपी पाठवा", hi: "ओटीपी भेजें" },
  "auth.verifyMobile": { en: "Verify Mobile", mr: "मोबाईल पडताळणी करा", hi: "मोबाइल सत्यापित करें" },
  "auth.mobileVerified": { en: "📱 Mobile Verified", mr: "📱 मोबाईल पडताळणी पूर्ण", hi: "📱 मोबाइल सत्यापित" },
  "auth.sendVerificationEmail": { en: "Send Verification Email", mr: "ईमेल पडताळणी लिंक पाठवा", hi: "सत्यापन ईमेल भेजें" },
  "auth.emailVerified": { en: "✉️ Email Verified", mr: "✉️ ईमेल पडताळणी पूर्ण", hi: "✉️ ईमेल सत्यापित" },
  "auth.pendingApproval": { en: "Your account is waiting for administrator approval.", mr: "तुमचे खाते प्रशासकीय मान्यतेसाठी प्रलंबित आहे.", hi: "आपका खाता व्यवस्थापक की स्वीकृति के लिए प्रतीक्षारत है।" },
  "auth.registrationSubmitted": { en: "Registration submitted! Verification complete, awaiting Admin approval.", mr: "नोंदणी सादर झाली! पडताळणी पूर्ण, आता ॲडमिन मंजुरीची प्रतीक्षा आहे.", hi: "पंजीकरण प्रस्तुत! सत्यापन पूर्ण, अब व्यवस्थापक स्वीकृति की प्रतीक्षा है।" },
  "auth.invalidCredentials": { en: "Invalid username or password.", mr: "अवैध युझरनेम किंवा पासवर्ड.", hi: "अमान्य यूज़रनेम या पासवर्ड।" },
  "auth.logout": { en: "Logout", mr: "बाहेर पडा (Logout)", hi: "लॉगआउट करें" },
  "auth.gender": { en: "Gender", mr: "लिंग (Gender)", hi: "लिंग (Gender)" },
  "gender.male": { en: "Male", mr: "पुरुष (Male)", hi: "पुरुष (Male)" },
  "gender.female": { en: "Female", mr: "महिला (Female)", hi: "महिला (Female)" },
  "gender.other": { en: "Other", mr: "इतर (Other)", hi: "अन्य (Other)" },
  "role.worker": { en: "Worker", mr: "कामगार", hi: "कामगार" },
  "role.provider": { en: "Employer", mr: "नियोक्ता", hi: "नियोक्ता" },
  "admin.pendingUsers": { en: "Pending User Approvals", mr: "प्रलंबित खाती व मंजुरी", hi: "लंबित खाते व स्वीकृति" },
  "admin.approve": { en: "✓ Approve", mr: "✓ मंजूर करा", hi: "✓ स्वीकृत करें" },
  "admin.reject": { en: "✕ Reject", mr: "✕ नाकारा", hi: "✕ अस्वीकार करें" },
  "admin.userApprovedToast": { en: "User account approved successfully!", mr: "वापरकर्ता खाते यशस्वीरीत्या मंजूर केले!", hi: "उपयोगकर्ता खाता सफलतापूर्वक स्वीकृत हुआ!" },
  "admin.userRejectedToast": { en: "User account rejected.", mr: "वापरकर्ता खाते नाकारले.", hi: "उपयोगकर्ता खाता अस्वीकार किया गया।" },
  "admin.userApprovedNoticeTitle": { en: "🎉 Account Successfully Verified & Approved!", mr: "🎉 खाते यशस्वीरीत्या पडताळले व मंजूर केले!", hi: "🎉 खाता सफलतापूर्वक सत्यापित एवं स्वीकृत हुआ!" },
  "admin.userApprovedNoticeDesc": { en: "User: {name} (@{username}) | Role: {role} | Status: VERIFIED & ACTIVE", mr: "वापरकर्ता: {name} (@{username}) | भूमिका: {role} | स्थिती: पडताळणी पूर्ण व सक्रिय", hi: "उपयोगकर्ता: {name} (@{username}) | भूमिका: {role} | स्थिति: सत्यापित व सक्रिय" },
  "admin.userApprovedNoticeLoginReady": { en: "Login Ready: This user can now immediately log in using their valid username/mobile and password.", mr: "लॉगिन सज्ज: हा वापरकर्ता आता वैध युझरनेम किंवा मोबाईल आणि पासवर्डने थेट लॉग इन करू शकतो.", hi: "लॉगिन तैयार: यह उपयोगकर्ता अब अपने वैध उपयोगकर्ता नाम/मोबाइल और पासवर्ड से तुरंत लॉगिन कर सकता है।" },

  // Hierarchical Location Selector Keys (Complete India Administrative Master)
  "location.country": { en: "Country", mr: "देश (Country)", hi: "देश (Country)" },
  "location.state": { en: "State / UT", mr: "राज्य (State)", hi: "राज्य (State)" },
  "location.district": { en: "District", mr: "जिल्हा (District)", hi: "जिला (District)" },
  "location.taluka": { en: "Taluka / Tehsil / Sub-District", mr: "तालुका (Taluka)", hi: "तहसील / उप-जिला" },
  "location.village": { en: "Village", mr: "गाव (Village)", hi: "गाँव (Village)" },
  "location.selectCountry": { en: "Select Country", mr: "देश निवडा", hi: "देश चुनें" },
  "location.selectState": { en: "Select State / UT", mr: "राज्य निवडा", hi: "राज्य चुनें" },
  "location.selectDistrict": { en: "Select District", mr: "जिल्हा निवडा", hi: "जिला चुनें" },
  "location.selectTaluka": { en: "Select Taluka / Tehsil", mr: "तालुका निवडा", hi: "तहसील चुनें" },
  "location.selectVillage": { en: "Select Village", mr: "गाव निवडा", hi: "गाँव चुनें" },
  "location.searchVillage": { en: "Search village within taluka...", mr: "तालुक्यात गाव शोधा...", hi: "तहसील में गाँव खोजें..." },
  "location.loading": { en: "Loading locations...", mr: "ठिकाणे लोड होत आहेत...", hi: "स्थान लोड हो रहे हैं..." },
  "location.emptyVillages": { en: "No villages available for this taluka", mr: "या तालुक्यासाठी गावे उपलब्ध नाहीत", hi: "इस तहसील के लिए गाँव उपलब्ध नहीं हैं" },
  "location.errorLoading": { en: "Error loading locations", mr: "ठिकाणे लोड करण्यात त्रुटी", hi: "स्थान लोड करने में त्रुटि" },
  "location.retry": { en: "Retry", mr: "पुन्हा प्रयत्न करा", hi: "पुनः प्रयास करें" },

  // Work Lifecycle & Status Keys
  "job.create": { en: "Create Work", mr: "नवीन काम पोस्ट करा", hi: "नया काम पोस्ट करें" },
  "job.createdSuccessfully": { en: "Work created successfully!", mr: "काम यशस्वीरीत्या तयार केले!", hi: "काम सफलतापूर्वक बनाया गया!" },
  "job.apply": { en: "Apply for Work", mr: "कामासाठी अर्ज करा", hi: "काम के लिए आवेदन करें" },
  "job.applied": { en: "Applied", mr: "अर्ज केला (Applied)", hi: "आवेदन किया (Applied)" },
  "job.full": { en: "🔒 Work Full / Closed", mr: "🔒 जागा पूर्ण भरल्या (Work Full)", hi: "🔒 पद पूर्ण भर चुके (Work Full)" },
  "job.expired": { en: "⏰ Expired", mr: "⏰ मुदत संपली (Expired)", hi: "⏰ समय सीमा समाप्त" },
  "job.selected": { en: "✓ Selected", mr: "✓ निवड झाली (Selected)", hi: "✓ चयन हुआ (Selected)" },
  "job.completed": { en: "✅ Completed", mr: "✅ काम पूर्ण झाले (Completed)", hi: "✅ काम पूरा हुआ (Completed)" },
  "job.statusUpdated": { en: "Status Updated", mr: "स्थिती अद्यतनित झाली", hi: "स्थिति अपडेट हुई" },
  "job.deadline": { en: "Application Deadline", mr: "अर्जाची अंतिम तारीख (Deadline)", hi: "आवेदन की अंतिम तिथि (Deadline)" },
  "job.workModel": { en: "Work Model", mr: "कामाचे स्वरूप (Work Model)", hi: "कार्य का प्रकार (Work Model)" },
  "job.onetime": { en: "⚡ One-Time Work", mr: "⚡ एकवेळ काम (One-Time)", hi: "⚡ एकमुश्त कार्य (One-Time)" },
  "job.daily": { en: "📅 Daily Work", mr: "📅 दैनंदिन काम (Daily)", hi: "📅 दैनिक कार्य (Daily)" },
  "job.recurring": { en: "🔁 Recurring / Monthly", mr: "🔁 नियमित / मासिक काम", hi: "🔁 आवर्ती / मासिक कार्य" },
  "filter.category": { en: "Select Work Category", mr: "कामाचा प्रकार निवडा (Select Work)", hi: "काम का प्रकार चुनें" },
  "filter.taluka": { en: "Select Taluka", mr: "तालुका निवडा (Select Taluka)", hi: "तालुका चुनें" },

  // Status Labels
  "status.draft": { en: "Draft", mr: "मसुदा (Draft)", hi: "प्रारूप (Draft)" },
  "status.open": { en: "🟢 Open", mr: "🟢 खुले (Open)", hi: "🟢 खुला (Open)" },
  "status.applications": { en: "🟡 Applications", mr: "🟡 अर्ज सुरू (Applications)", hi: "🟡 आवेदन जारी" },
  "status.full": { en: "🔵 Full", mr: "🔵 जागा पूर्ण (Full)", hi: "🔵 पद पूर्ण (Full)" },
  "status.assigned": { en: "🟣 Assigned", mr: "🟣 काम वाटप झाले (Assigned)", hi: "🟣 काम सौंपा गया" },
  "status.inProgress": { en: "🟠 In Progress", mr: "🟠 काम सुरू आहे (In Progress)", hi: "🟠 कार्य प्रगति पर है" },
  "status.completed": { en: "✅ Completed", mr: "✅ पूर्ण (Completed)", hi: "✅ पूर्ण (Completed)" },
  "status.expired": { en: "⏰ Expired", mr: "⏰ मुदत संपली (Expired)", hi: "⏰ समय समाप्त" },
  "status.cancelled": { en: "❌ Cancelled", mr: "❌ रद्द (Cancelled)", hi: "❌ रद्द (Cancelled)" },
  "status.archived": { en: "📦 Archived", mr: "📦 संग्रहित (Archived)", hi: "📦 संग्रहीत (Archived)" },

  // Work Types
  "worktype.houseCleaning": { en: "House Cleaning", mr: "घर स्वच्छता (House Cleaning)", hi: "घर की सफाई" },
  "worktype.houseHelp": { en: "House Help", mr: "घरकाम मदतनीस (House Help)", hi: "घरेलू सहायक" },
  "worktype.roadCleaning": { en: "Road Cleaning", mr: "रस्ता स्वच्छता (Road Cleaning)", hi: "सड़क सफाई" },
  "worktype.roadMaintenance": { en: "Road Maintenance", mr: "रस्ता देखभाल (Road Maintenance)", hi: "सड़क मरम्मत" },
  "worktype.gardening": { en: "Gardening", mr: "बागकाम (Gardening)", hi: "बागवानी" },
  "worktype.agriculturalLabour": { en: "Agricultural Labour", mr: "शेती काम / शेतमजूर", hi: "कृषि मजदूर" },
  "worktype.constructionHelper": { en: "Construction Helper", mr: "बांधकाम मदतनीस", hi: "निर्माण सहायक" },
  "worktype.painting": { en: "Painting", mr: "रंगकाम (Painting)", hi: "पेंटिंग / रंगाई" },
  "worktype.plumbingHelper": { en: "Plumbing Helper", mr: "प्लंबिंग मदतनीस", hi: "नलसाजी सहायक" },
  "worktype.electricianHelper": { en: "Electrician Helper", mr: "इलेक्ट्रिशियन मदतनीस", hi: "बिजली सहायक" },
  "worktype.generalLabour": { en: "General Labour", mr: "सर्वसाधारण मजूर (General Labour)", hi: "सामान्य मजदूर" },
  "worktype.other": { en: "Other Work", mr: "इतर काम (Other)", hi: "अन्य कार्य" },

  // Facilities & Timing
  "facility.title": { en: "Facilities / Benefits", mr: "मिळणाऱ्या सुविधा (Facilities)", hi: "सुविधाएं (Facilities)" },
  "facility.tea": { en: "☕ Tea Provided", mr: "☕ चहा दिला जाईल", hi: "☕ चाय उपलब्ध" },
  "facility.lunch": { en: "🍱 Lunch Provided", mr: "🍱 दुपारचे जेवण दिले जाईल", hi: "🍱 दोपहर का भोजन उपलब्ध" },
  "facility.water": { en: "💧 Drinking Water", mr: "💧 पिण्याचे स्वच्छ पाणी", hi: "💧 पीने का पानी" },
  "facility.transport": { en: "🚌 Transport Provided", mr: "🚌 ने-आण सोय (Transport)", hi: "🚌 परिवहन सुविधा" },
  "facility.accommodation": { en: "🏠 Accommodation", mr: "🏠 राहण्याची सोय", hi: "🏠 ठहरने की व्यवस्था" },
  "facility.safetyEquipment": { en: "🦺 Safety Equipment", mr: "🦺 सुरक्षा साधने पुरवली जातील", hi: "🦺 सुरक्षा उपकरण उपलब्ध" },
  "facility.other": { en: "✨ Other Facility", mr: "✨ इतर सुविधा", hi: "✨ अन्य सुविधा" },
  "facility.details": { en: "Additional Facility Details", mr: "सुविधांविषयी अधिक माहिती", hi: "अतिरिक्त सुविधा विवरण" },

  "timing.title": { en: "Work Timing & Breaks", mr: "कामाची वेळ व सुट्ट्या", hi: "काम का समय और अवकाश" },
  "timing.start": { en: "Start Time", mr: "सुरू होण्याची वेळ", hi: "प्रारंभ समय" },
  "timing.end": { en: "End Time", mr: "संपण्याची वेळ", hi: "समाप्ति समय" },
  "timing.hours": { en: "Working Hours (Hours/Day)", mr: "कामाचे तास (तास/दिवस)", hi: "कार्य के घंटे (घंटे/दिन)" },
  "timing.lunchBreak": { en: "Lunch Break", mr: "जेवणाची सुट्टी (Lunch Break)", hi: "भोजन अवकाश" },
  "timing.teaBreak": { en: "Tea Break", mr: "चहाची सुट्टी (Tea Break)", hi: "चाय अवकाश" },
  "timing.otherBreak": { en: "Other Break", mr: "इतर विश्रांती (Other Break)", hi: "अन्य अवकाश" },

  "overtime.title": { en: "Overtime / Extra Work", mr: "ओव्हरटाईम / जास्तीचे काम", hi: "अतिरिक्त कार्य / ओवरटाइम" },
  "overtime.available": { en: "Overtime Available", mr: "ओव्हरटाईम उपलब्ध आहे", hi: "ओवरटाइम उपलब्ध है" },
  "overtime.rate": { en: "Overtime Rate (₹/hour)", mr: "ओव्हरटाईम दर (₹/तास)", hi: "ओवरटाइम दर (₹/घंटा)" },
  "overtime.hours": { en: "Overtime Hours", mr: "ओव्हरटाईम तास", hi: "ओवरटाइम घंटे" },
  "overtime.amount": { en: "Overtime Amount", mr: "ओव्हरटाईम रक्कम", hi: "ओवरटाइम राशि" },

  // Payment Keys
  "payment.title": { en: "Payment Details", mr: "मजुरी व पेमेंट तपशील", hi: "भुगतान विवरण" },
  "payment.base": { en: "Base Daily Wage", mr: "मूळ रोजंदारी (Base Wage)", hi: "मूल दैनिक मजदूरी" },
  "payment.unit": { en: "Payment Unit", mr: "पेमेंट स्वरूप", hi: "भुगतान इकाई" },
  "payment.additional": { en: "Additional Allowance", mr: "अतिरिक्त भत्ता / रक्कम", hi: "अतिरिक्त भत्ता" },
  "payment.total": { en: "Total Payment", mr: "एकूण देय रक्कम (Total Amount)", hi: "कुल भुगतान राशि" },
  "payment.pending": { en: "⏳ Payment Pending", mr: "⏳ मजुरी देणे बाकी (Pending)", hi: "⏳ भुगतान लंबित" },
  "payment.completed": { en: "💵 Payment Completed", mr: "💵 मजुरी अदा केली (Paid)", hi: "💵 भुगतान संपन्न" },
  "payment.confirmed": { en: "✓ Payment Confirmed", mr: "✓ पेमेंट निश्चित झाले", hi: "✓ भुगतान की पुष्टि" },
  "payment.acknowledged": { en: "✓ Payment Acknowledged", mr: "✓ मजुरी मिळाली", hi: "✓ भुगतान प्राप्त हुआ" },
  "payment.disputed": { en: "⚠️ Payment Disputed", mr: "⚠️ पेमेंट तंटा (Disputed)", hi: "⚠️ भुगतान विवादित" },
  "payment.confirmBtn": { en: "Confirm & Pay Wage", mr: "मजुरी पुष्टी करा व द्या", hi: "मजदूरी की पुष्टि करें" },

  // Rating & Review Keys
  "rating.title": { en: "Ratings & Reviews", mr: "रेटिंग व अभिप्राय (Reviews)", hi: "रेटिंग और समीक्षाएँ" },
  "rating.available": { en: "⭐ Rating Window Open", mr: "⭐ रेटिंग उपलब्ध आहे", hi: "⭐ रेटिंग विंडो खुली है" },
  "rating.pending": { en: "⭐ Rating Pending", mr: "⭐ अभिप्राय देणे बाकी", hi: "⭐ रेटिंग लंबित" },
  "rating.submit": { en: "Submit Rating & Review", mr: "रेटिंग व अभिप्राय सबमिट करा", hi: "रेटिंग और समीक्षा प्रस्तुत करें" },
  "rating.completed": { en: "✓ Rating Submitted", mr: "✓ रेटिंग नोंदवली गेली", hi: "✓ रेटिंग दर्ज हुई" },
  "rating.submitted": { en: "Review submitted successfully!", mr: "अभिप्राय यशस्वीरीत्या सेव्ह झाला!", hi: "समीक्षा सफलतापूर्वक दर्ज हुई!" },
  "rating.workManagement": { en: "Work Management", mr: "काम नियोजन व व्यवस्थापन", hi: "कार्य प्रबंधन" },
  "rating.behavior": { en: "Behavior & Respect", mr: "वागणूक व सन्मान", hi: "व्यवहार और सम्मान" },
  "rating.paymentExperience": { en: "Payment Experience", mr: "वेळेवर मजुरी अनुभव", hi: "भुगतान अनुभव" },
  "rating.timeManagement": { en: "Time Management", mr: "वेळेचे पालन", hi: "समय पालन" },
  "rating.quality": { en: "Work Quality", mr: "कामाचा दर्जा (Quality)", hi: "काम की गुणवत्ता" },
  "rating.attendance": { en: "Attendance & Punctuality", mr: "उपस्थिती व वक्तशीरपणा", hi: "उपस्थिति और समयबद्धता" },
  "rating.reliability": { en: "Reliability & Trust", mr: "विश्वासार्हता (Reliability)", hi: "विश्वसनीयता" },
  "rating.skill": { en: "Skill & Expertise", mr: "कौशल्य व कार्यक्षमता", hi: "कौशल और प्रवीणता" },
  "rating.overallExperience": { en: "Overall Experience", mr: "एकूण अनुभव (Overall)", hi: "समग्र अनुभव" },
  "rating.totalRatings": { en: "ratings", mr: "रेटिंग्स (Ratings)", hi: "रेटिंग्स" },
  "rating.reviews": { en: "Public Reviews", mr: "सार्वजनिक अभिप्राय", hi: "सार्वजनिक समीक्षाएं" },

  // Role & Dashboard Labels
  "provider.selectWorker": { en: "Select Worker", mr: "कामगार निवडा (Select)", hi: "मजदूर चुनें" },
  "provider.myWorks": { en: "My Works", mr: "माझी पोस्ट केलेली कामे (My Works)", hi: "मेरे पोस्ट किए गए कार्य" },
  "worker.application": { en: "Work Application", mr: "कामाचा अर्ज", hi: "कार्य आवेदन" },
  "worker.myApplications": { en: "My Applications", mr: "माझे कामाचे अर्ज", hi: "मेरे आवेदन" },
  "worker.workHistory": { en: "Work History", mr: "कामाचा इतिहास व रेकॉर्ड", hi: "कार्य इतिहास" },
  "admin.jobs": { en: "All Platform Works", mr: "सर्व नोंदणीकृत कामे (All Works)", hi: "सभी पंजीकृत कार्य" },

  // Header Navigation & Role Switcher
  "landing.navRoles": { en: "👥 Workers & Employers", mr: "👥 कामगार व मालक", hi: "👥 मजदूर व नियोक्ता" },
  "landing.navHowItWorks": { en: "⚙️ How It Works?", mr: "⚙️ कसे काम करते?", hi: "⚙️ कैसे काम करता है?" },
  "landing.navCategories": { en: "🌾 Work Types", mr: "🌾 काम प्रकार", hi: "🌾 कार्य प्रकार" },
  "landing.navImpact": { en: "📊 Our Impact", mr: "📊 आमचा प्रभाव", hi: "📊 हमारा प्रभाव" },
  "role.workerBtn": { en: "👷 Worker", mr: "👷 कामगार", hi: "👷 मजदूर" },
  "role.providerBtn": { en: "👨‍🌾 Employer", mr: "👨‍🌾 नियोक्ता", hi: "👨‍🌾 नियोक्ता" },

  // Theme Toggle (Day / Night Mode)
  "theme.dayMode": { en: "Day Mode", mr: "डे मोड", hi: "दिन मोड" },
  "theme.nightMode": { en: "Night Mode", mr: "नाईट मोड", hi: "रात मोड" },
  "theme.switchToDay": { en: "Switch to Day Mode", mr: "डे मोड सुरू करा", hi: "डे मोड शुरू करें" },
  "theme.switchToNight": { en: "Switch to Night Mode", mr: "नाईट मोड सुरू करा", hi: "नाईट मोड शुरू करें" },
  "theme.dayToast": { en: "☀️ Day Mode Active", mr: "☀️ डे मोड सुरू केला", hi: "☀️ दिन मोड सक्रिय किया" },
  "theme.nightToast": { en: "🌙 Night Mode Active", mr: "🌙 नाईट मोड सुरू केला", hi: "🌙 रात मोड सक्रिय किया" },

  // Hero Section
  "landing.heroBadge": { en: "🌾 Maharashtra Rural Employment Digital Platform", mr: "🌾 महाराष्ट्र ग्रामीण रोजगार डिजिटल मंच", hi: "🌾 महाराष्ट्र ग्रामीण रोजगार डिजिटल मंच" },
  "landing.serverConnecting": { en: "Connecting to server...", mr: "सर्व्हर कनेक्ट करत आहे...", hi: "सर्वर से कनेक्ट हो रहा है..." },
  "landing.serverConnected": { en: "Server Connected (Live Sync)", mr: "सर्व्हर कनेक्टेड (Live Sync)", hi: "सर्वर कनेक्टेड (Live Sync)" },
  "landing.heroTitle1": { en: "Trusted Village-Level", mr: "गाव पातळीवरील विश्वासार्ह", hi: "ग्राम स्तर पर विश्वसनीय" },
  "landing.heroTitle2": { en: "Local Jobs Marketplace", mr: "स्थानिक रोजगार मंच", hi: "स्थानीय रोजगार मंच" },
  "landing.heroSubtitle": {
    en: "Direct work for registered workers and instant skilled labor for farmers and employers — 0% commission, direct phone contact, and on-time wages.",
    mr: "नोंदणीकृत कामगारांना थेट काम आणि शेतकऱ्यांना/मालकांना त्वरित कुशल मजूर — ०% कमिशन, थेट फोन संपर्क, आणि वेळेवर मजुरी.",
    hi: "पंजीकृत मजदूरों को सीधा काम और किसानों/मालिकों को तुरंत कुशल मजदूर — ०% कमीशन, सीधा फोन संपर्क, और समय पर मजदूरी।"
  },
  "landing.zeroCommissionPill": { en: "0% Commission", mr: "०% कमिशन", hi: "०% कमीशन" },
  "landing.registerCta": { en: "Create New Account / Register", mr: "नवीन खाते तयार करा / नोंदणी", hi: "नया खाता बनाएं / पंजीकरण" },
  "landing.loginCta": { en: "Direct Login", mr: "थेट लॉगिन करा", hi: "सीधा लॉगिन करें" },

  // Hero Value Chips
  "landing.featNoCut": { en: "0% Commission (No Cut)", mr: "०% कमिशन (No Cut)", hi: "०% कमीशन (कोई कटौती नहीं)" },
  "landing.featPhoneCall": { en: "Direct Phone Call", mr: "थेट मालकांशी फोन कॉल", hi: "मालिक से सीधा फोन कॉल" },
  "landing.featAgriWork": { en: "Farm & Local Work", mr: "शेती व स्थानिक कामे", hi: "खेती व स्थानीय काम" },
  "landing.featCashWage": { en: "Direct Wage on Work Day", mr: "कामाच्या दिवशी थेट मजुरी", hi: "काम के दिन सीधी मजदूरी" },
  "landing.liveUpdates": { en: "Live Updates", mr: "थेट अपडेट्स", hi: "लाइव अपडेट्स" },

  // Top Impact Stats
  "landing.statWorkers": { en: "Registered Local Workers", mr: "नोंदणीकृत स्थानिक कामगार", hi: "पंजीकृत स्थानीय मजदूर" },
  "landing.liveRegistration": { en: "Live Active Registration", mr: "थेट लाइव्ह नोंदणी", hi: "सीधा लाइव पंजीकरण" },
  "landing.statProviders": { en: "Farmers & Business Owners", mr: "शेतकरी व उद्योग मालक", hi: "किसान व उद्योग मालिक" },
  "landing.activeEmployers": { en: "Active Employers", mr: "सक्रिय नियोक्ते", hi: "सक्रिय नियोक्ता" },
  "landing.statVillages": { en: "Pune Rural Villages (Shirur, Ranjangaon, Saswad)", mr: "पुणे ग्रामीण गावे (शिरूर, रांजणगाव, सासवड)", hi: "पुणे ग्रामीण गाँव (शिरूर, रांजणगाव, सासवड)" },
  "landing.talukaCoverage": { en: "Taluka-wise Coverage", mr: "तालुका-निहाय कव्हरेज", hi: "तालुका-वार कवरेज" },
  "landing.statCommission": { en: "Zero Commission (100% Direct Wages)", mr: "शून्य कमिशन (१००% थेट मजुरी)", hi: "शून्य कमीशन (१००% सीधी मजदूरी)" },
  "landing.freeForever": { en: "Free Forever", mr: "कायमस्वरूपी मोफत", hi: "सदा के लिए मुफ्त" },

  // Role Selection Cards
  "landing.roleTag": { en: "CHOOSE YOUR ROLE", mr: "भूमिका निवडा (CHOOSE YOUR ROLE)", hi: "भूमिका चुनें (CHOOSE YOUR ROLE)" },
  "landing.roleTitle": { en: "What do you want to do?", mr: "तुम्हाला काय करायचे आहे?", hi: "आप क्या करना चाहते हैं?" },
  "landing.roleDesc": {
    en: "Join KaamSetu as a worker or job provider and connect directly at the village level.",
    mr: "कामगार किंवा रोजगारदाता म्हणून कामसेतूमध्ये सामील व्हा आणि स्थानिक पातळीवर थेट जोडले जा.",
    hi: "मजदूर या नियोक्ता के रूप में कामसेतु से जुड़ें और ग्रामीण स्तर पर सीधे जुड़ें।"
  },
  "landing.roleWorkerPill": { en: "Job Seeker • Worker", mr: "काम शोधणारे • Job Seeker", hi: "काम खोजने वाले • Job Seeker" },
  "landing.roleWorkerTitle": { en: "I Need Work (WORKER)", mr: "मला काम पाहिजे (WORKER)", hi: "मुझे काम चाहिए (WORKER)" },
  "landing.roleWorkerDesc": {
    en: "Get daily farm, construction, tractor driving, or local wage jobs nearby.",
    mr: "शेती, बांधकाम, ट्रॅक्टर चालवणे किंवा इतर स्थानिक रोजंदारीची कामे मिळवा.",
    hi: "खेती, निर्माण, ट्रैक्टर चलाना या अन्य स्थानीय दिहाड़ी काम पाएं।"
  },
  "landing.workerCheck1": { en: "Guaranteed daily work right in your village", mr: "गावातच रोजची खात्रीशीर कामे", hi: "गाँव में ही दैनिक पक्का काम" },
  "landing.workerCheck2": { en: "Direct phone calls with verified employers", mr: "मालकांशी थेट फोनवरून बोला", hi: "मालिकों से सीधे फोन पर बात करें" },
  "landing.workerCheck3": { en: "100% cash / direct payment, zero commission", mr: "१००% रोख / थेट मजुरी, कोणतेही कमिशन नाही", hi: "१००% नकद / सीधा भुगतान, कोई कमीशन नहीं" },
  "landing.workerCheck4": { en: "Safe and verified workplace assignments", mr: "सुरक्षित व प्रशासकीय पडताळणी झालेले काम", hi: "सुरक्षित और सत्यापित कार्य" },
  "landing.workerRegisterBtn": { en: "Register as Worker ➔", mr: "कामगार म्हणून नोंदणी करा ➔", hi: "मजदूर के रूप में पंजीकरण करें ➔" },
  "landing.roleProviderPill": { en: "Employer • Farm Owner", mr: "रोजगारदाता • Employer", hi: "रोजगारदाता • Employer" },
  "landing.roleProviderTitle": { en: "I Need Workers (JOB PROVIDER)", mr: "मला कामगार पाहिजे (JOB PROVIDER)", hi: "मुझे मजदूर चाहिए (JOB PROVIDER)" },
  "landing.roleProviderDesc": {
    en: "Quickly hire skilled local workers for agriculture, construction, or businesses.",
    mr: "शेतीतील कामांसाठी, बांधकामासाठी किंवा कारखान्यासाठी त्वरित कुशल मजूर मिळवा.",
    hi: "खेती, निर्माण या कारखाने के लिए तुरंत कुशल मजदूर पाएं।"
  },
  "landing.providerCheck1": { en: "Post a new job in 30 seconds", mr: "३० सेकंदात नवीन काम पोस्ट करा", hi: "३० सेकंड में नया काम पोस्ट करें" },
  "landing.providerCheck2": { en: "Nearby local workers within 5 to 25 km", mr: "५ ते २५ किमी मधील जवळचे स्थानिक मजूर", hi: "५ से २५ किमी के नजदीकी स्थानीय मजदूर" },
  "landing.providerCheck3": { en: "Verified and trusted village workers", mr: "पडताळणी झालेले आणि विश्वासार्ह कामगार", hi: "सत्यापित और विश्वसनीय कामगार" },
  "landing.providerCheck4": { en: "Direct contact, free and simple to use", mr: "थेट संपर्क, मोफत आणि सुलभ वापर", hi: "सीधा संपर्क, मुफ्त और आसान उपयोग" },
  "landing.providerRegisterBtn": { en: "Register as Farmer/Employer ➔", mr: "शेतकरी/मालक म्हणून नोंदणी करा ➔", hi: "किसान/मालिक के रूप में पंजीकरण करें ➔" },

  // How It Works (3 Steps)
  "landing.howItWorksTag": { en: "HOW IT WORKS", mr: "कार्यपद्धती (HOW IT WORKS)", hi: "कार्यप्रणाली (HOW IT WORKS)" },
  "landing.howItWorksTitle": { en: "How KaamSetu Works?", mr: "कामसेतू कसे काम करते?", hi: "कामसेतु कैसे काम करता है?" },
  "landing.howItWorksDesc": {
    en: "Find work or hire workers in just 3 simple steps.",
    mr: "फक्त ३ सोप्या पायऱ्यांमध्ये काम मिळवा किंवा मजुरांशी संपर्क साधा.",
    hi: "केवल ३ आसान चरणों में काम पाएं या मजदूरों से संपर्क करें।"
  },
  "landing.step1Title": { en: "Registration & Verification", mr: "नोंदणी व पडताळणी", hi: "पंजीकरण और सत्यापन" },
  "landing.step1Desc": {
    en: "Create an account in 1 minute using mobile number and verified OTP.",
    mr: "मोबाईल नंबर आणि रिअल-टाईम ईमेल OTP च्या साहाय्याने १ मिनिटात खाते तयार करा.",
    hi: "मोबाइल नंबर और ओटीपी की मदद से १ मिनट में खाता बनाएं।"
  },
  "landing.step2Title": { en: "Find Work / Post Work", mr: "काम शोधा / पोस्ट करा", hi: "काम खोजें / काम पोस्ट करें" },
  "landing.step2Desc": {
    en: "Workers choose local jobs and employers post jobs for workers instantly.",
    mr: "कामगारांनी स्थानिक कामे निवडावीत आणि मालकांनी मजुरांसाठी त्वरित काम पोस्ट करावे.",
    hi: "मजदूर स्थानीय काम चुनें और मालिक तुरंत काम पोस्ट करें।"
  },
  "landing.step3Title": { en: "Direct Contact & Wages", mr: "थेट संपर्क व मजुरी", hi: "सीधा संपर्क और मजदूरी" },
  "landing.step3Desc": {
    en: "Speak directly on phone without middlemen, complete work, and receive full wages on the work day.",
    mr: "मध्यस्थांशिवाय फोनवर थेट संवाद साधा, कामावर जा आणि कामाच्या दिवशी थेट मजुरी मिळवा.",
    hi: "बिचौलियों के बिना सीधे फोन पर बात करें, काम करें और उसी दिन पूरी मजदूरी पाएं।"
  },

  // Interactive Wage Calculator
  "landing.calcTag": { en: "WAGE ESTIMATOR", mr: "झटपट मजुरी व उपलब्ध मजूर गणक (WAGE ESTIMATOR)", hi: "मजदूरी दर अनुमान (WAGE ESTIMATOR)" },
  "landing.calcTitle": { en: "Check Wage Rates in Your Village", mr: "तुमच्या गावातील मजुरीचा दर तपासा", hi: "अपने गाँव में मजदूरी दर जांचें" },
  "landing.calcDesc": {
    en: "Select village and work type to view real-time daily wages, available workers, and response times.",
    mr: "गाव व कामाचा प्रकार निवडा आणि सरासरी मजुरी, उपलब्ध कामगार व प्रतिसाद वेळ त्वरित पाहा.",
    hi: "गाँव और काम का प्रकार चुनें और औसत मजदूरी, उपलब्ध मजदूर व प्रतिक्रिया समय देखें।"
  },
  "landing.calcVillageLabel": { en: "📍 Select Village / Taluka", mr: "📍 गाव / तालुका निवडा", hi: "📍 गाँव / तालुका चुनें" },
  "landing.calcJobLabel": { en: "🌾 Select Work Type", mr: "🌾 कामाचा प्रकार निवडा", hi: "🌾 काम का प्रकार चुनें" },
  "landing.calcDisclaimer": {
    en: "Direct calculations based on registered workers within local 5 to 15 km radius.",
    mr: "स्थानिक ५ ते १५ किमी परिसरातील प्रत्यक्ष नोंदणीकृत मजुरांच्या आधारे थेट गणना.",
    hi: "स्थानीय ५ से १५ किमी क्षेत्र में पंजीकृत मजदूरों के आधार पर सीधी गणना।"
  },
  "landing.calcMarketEstimate": { en: "Local Market Rate Estimate", mr: "स्थानिक बाजारभाव अंदाज", hi: "स्थानीय बाजार भाव अनुमान" },
  "landing.calcEstDailyRate": { en: "Estimated Daily Wage:", mr: "अंदाजे रोजंदारी दर:", hi: "अनुमानित दैनिक दर:" },
  "landing.calcWorkersAvailable": { en: "Available Workers", mr: "उपलब्ध मजूर", hi: "उपलब्ध मजदूर" },
  "landing.calcResponseTime": { en: "Response Time", mr: "प्रतिसाद वेळ", hi: "प्रतिक्रिया समय" },

  // Categories
  "landing.catTag": { en: "CATEGORIES", mr: "काम प्रकार (CATEGORIES)", hi: "कार्य प्रकार (CATEGORIES)" },
  "landing.catTitle": { en: "What Jobs Are Available?", mr: "कोणकोणती कामे उपलब्ध आहेत?", hi: "कौन से काम उपलब्ध हैं?" },
  "landing.catDesc": {
    en: "Categorized farm, technical, and local jobs available across villages.",
    mr: "गावातील सर्व प्रकारची शेती, तांत्रिक आणि स्थानिक कामांचे वर्गीकरण.",
    hi: "गाँव के सभी प्रकार के कृषि, तकनीकी और स्थानीय कार्यों का वर्गीकरण।"
  },
  "cat.name.agri": { en: "Agriculture & Farm Labor", mr: "शेती व कृषी मजूर", hi: "कृषि व खेत मजदूर" },
  "cat.name.tractor": { en: "Tractor & Machinery", mr: "ट्रॅक्टर व मशिनरी", hi: "ट्रैक्टर व मशीनरी" },
  "cat.name.construction": { en: "Construction & Mason", mr: "बांधकाम व गवंडी", hi: "निर्माण व राजमिस्त्री" },
  "cat.name.electric": { en: "Electrician & Plumber", mr: "इलेक्ट्रिशियन व प्लंबर", hi: "इलेक्ट्रीशियन व प्लंबर" },
  "cat.name.driver": { en: "Driver & Transport", mr: "वाहन चालक व वाहतूक", hi: "ड्राइवर व परिवहन" },
  "cat.name.dairy": { en: "Dairy & Livestock", mr: "दुग्ध व्यवसाय व पशुपालन", hi: "डेयरी व पशुपालन" },
  "cat.name.helper": { en: "Helper & Loading", mr: "मदतनीस व हमाली", hi: "सहायक व हम्माली" },
  "cat.name.carpenter": { en: "Carpenter & Fabrication", mr: "सुतारकाम व फॅब्रिकेशन", hi: "बढ़ईगीरी व वेल्डिंग" },

  // Calculator Dropdown Options
  "calc.opt.agri": { en: "Agriculture, Weeding & Harvesting", mr: "शेती, खुरपणी व कापणी", hi: "खेती, निराई व कटाई" },
  "calc.opt.tractor": { en: "Tractor & Machinery Operator", mr: "ट्रॅक्टर व मशिनरी चालक", hi: "ट्रैक्टर व मशीनरी चालक" },
  "calc.opt.construction": { en: "Construction & Masonry", mr: "बांधकाम व गवंडी काम", hi: "निर्माण व राजमिस्त्री कार्य" },
  "calc.opt.helper": { en: "Helper & Goods Transport", mr: "मदतनीस व मालवाहतूक", hi: "सहायक व माल परिवहन" },

  // Trust & Security
  "landing.trustTag": { en: "TRUST & SECURITY", mr: "सुरक्षा व विश्वास (TRUST & SECURITY)", hi: "सुरक्षा व विश्वास (TRUST & SECURITY)" },
  "landing.trustTitle": { en: "100% Safe & Transparent Platform", mr: "१००% सुरक्षित व पारदर्शक मंच", hi: "१००% सुरक्षित व पारदर्शी मंच" },
  "landing.trust1Title": { en: "Administrative Verification", mr: "प्रशासकीय पडताळणी", hi: "प्रशासनिक सत्यापन" },
  "landing.trust1Desc": { en: "All worker and employer accounts are verified and approved by admin.", mr: "सर्व कामगार आणि मालकांची खाती Admin कडून पडताळून मंजूर केली जातात.", hi: "सभी मजदूर और मालिक खाते एडमिन द्वारा सत्यापित और स्वीकृत होते हैं।" },
  "landing.trust2Title": { en: "Real-Time Email & Mobile OTP", mr: "रिअल-टाईम ईमेल OTP", hi: "रियल-टाइम ईमेल OTP" },
  "landing.trust2Desc": { en: "Secure Google SMTP verification, preventing spam and fake profiles.", mr: "Google SMTP द्वारे सुरक्षित पडताळणी, खोट्या प्रोफाईल्सना पूर्णपणे बंदी.", hi: "Google SMTP द्वारा सुरक्षित सत्यापन, फर्जी प्रोफाइल पर पूरी रोक।" },
  "landing.trust3Title": { en: "Hyper-Local GPS Radius", mr: "हायपर-लोकल GPS", hi: "हाइपर-लोकल GPS" },
  "landing.trust3Desc": { en: "Shows jobs within 5, 10, or 25 km, saving travel time and expense.", mr: "५, १० किंवा २५ किमी अंतरावरीलच कामे दाखवून प्रवास वेळ आणि खर्च वाचवतो.", hi: "५, १० या २५ किमी के भीतर काम दिखाकर यात्रा समय व खर्च बचाता है।" },
  "landing.trust4Title": { en: "Zero Percent Commission", mr: "शून्य टक्के कमिशन", hi: "शून्य प्रतिशत कमीशन" },
  "landing.trust4Desc": { en: "100% wages paid directly into workers' hands with no deductions.", mr: "कामगारांची मजुरी १००% थेट त्यांच्या हातात, कोणतीही कपात नाही.", hi: "मजदूरों की मजदूरी १००% सीधे उनके हाथों में, कोई कटौती नहीं।" },

  // FAQ
  "landing.faqTag": { en: "FREQUENTLY ASKED QUESTIONS", mr: "वारंवार विचारले जाणारे प्रश्न (FAQ)", hi: "अक्सर पूछे जाने वाले प्रश्न (FAQ)" },
  "landing.faqTitle": { en: "Have Any Questions?", mr: "काही शंका आहेत का?", hi: "कोई सवाल है?" },
  "landing.faqDesc": { en: "Answers to common questions about using KaamSetu in villages.", mr: "कामसेतू वापरण्याबाबत ग्रामस्थांना पडणारे नेहमीचे प्रश्न व त्यांची उत्तरे.", hi: "कामसेतु के उपयोग के बारे में अक्सर पूछे जाने वाले प्रश्न और उत्तर।" },
  "landing.faq1Q": { en: "1. Is there any fee or commission to use KaamSetu?", mr: "१. कामसेतू ॲप वापरण्यासाठी काही शुल्क किंवा कमिशन आहे का?", hi: "१. क्या कामसेतु ऐप का उपयोग करने के लिए कोई शुल्क या कमीशन है?" },
  "landing.faq1A": { en: "No! KaamSetu is a 100% free platform for village workers and farmers. Wages are paid directly to the worker with zero middleman commission.", mr: "नाही! कामसेतू हा गावपातळीवरील कामगार आणि शेतकऱ्यांसाठी १००% मोफत मंच आहे. मजुरीची रक्कम थेट कामगाराच्या हातात दिली जाते, यामध्ये कोणतेही मध्यस्थ कमिशन आकारले जात नाही.", hi: "नहीं! कामसेतु ग्रामीण मजदूरों और किसानों के लिए १००% मुफ्त मंच है। मजदूरी सीधे मजदूर को दी जाती है, कोई बिचौलिया कमीशन नहीं है।" },
  "landing.faq2Q": { en: "2. How is safety and verification handled?", mr: "२. कामगारांची खात्री व सुरक्षा कशी केली जाते?", hi: "२. सुरक्षा और सत्यापन कैसे किया जाता है?" },
  "landing.faq2A": { en: "Every user verifies mobile and email with real-time OTP. Profiles are then approved by administrative checks before account activation.", mr: "प्रत्येक नवीन युझरची नोंदणी झाल्यावर ईमेल व मोबाईल OTP पडताळणी होते, त्यानंतर स्थानिक ॲडमिन कडून प्रोफाईल मंजूर (Approved) झाल्यानंतरच खाते सक्रिय केले जाते.", hi: "प्रत्येक नए उपयोगकर्ता का मोबाइल और ईमेल ओटीपी से सत्यापन होता है, फिर एडमिन सत्यापन के बाद खाता सक्रिय होता है।" },
  "landing.faq3Q": { en: "3. How quickly do employers find workers?", mr: "३. शेतकऱ्यांना मजूर किती वेळात मिळतात?", hi: "३. किसानों को मजदूर कितनी देर में मिलते हैं?" },
  "landing.faq3A": { en: "Once posted, nearby workers within 5 to 25 km are notified immediately. Most employers start receiving direct phone calls within 15 to 30 minutes.", mr: "काम पोस्ट केल्यानंतर ५ ते २५ किमी परिसरातील जवळच्या सर्व नोंदणीकृत कामगारांना थेट सूचना जाते. बहुतांश शेतकऱ्यांना १५ ते ३० मिनिटांत थेट फोन कॉल्स येण्यास सुरुवात होते.", hi: "काम पोस्ट करने के बाद ५ से २५ किमी के सभी पंजीकृत मजदूरों को सूचना जाती है। अधिकांश किसानों को १५ से ३० मिनट में सीधे फोन आने लगते हैं।" },
  "landing.faq4Q": { en: "4. What if I don't own a smartphone?", mr: "४. माझ्याकडे स्मार्टफोन नसल्यास काय करावे?", hi: "४. अगर मेरे पास स्मार्टफोन नहीं है तो क्या करें?" },
  "landing.faq4A": { en: "Register once through a local Setu Kendra, village representative, or friend, and you can receive jobs directly via phone calls.", mr: "गावातील सेतू केंद्र, ग्रामपंचायत प्रतिनिधी किंवा गावातील सहकाऱ्यांच्या मदतीने एकदा नोंदणी करून तुम्ही थेट फोन कॉलद्वारे कामे मिळवू शकता.", hi: "गाँव के सेतु केंद्र, ग्राम पंचायत प्रतिनिधि या मित्र की मदद से एक बार पंजीकरण कराएं और सीधे फोन कॉल से काम पाएं।" },

  // Impact
  "landing.impactTag": { en: "OUR IMPACT", mr: "आमचा प्रभाव (OUR IMPACT)", hi: "हमारा प्रभाव (OUR IMPACT)" },
  "landing.impactTitle": { en: "Digital Empowerment for Rural Maharashtra", mr: "महाराष्ट्रातील ग्रामीण अर्थव्यवस्थेला डिजिटल बळ", hi: "महाराष्ट्र की ग्रामीण अर्थव्यवस्था को डिजिटल संबल" },
  "landing.impactDesc": { en: "See how KaamSetu is changing the lives of farmers and workers across villages:", mr: "कामसेतूच्या माध्यमातून गावोगावी शेतकरी व कामगारांचे जीवन कसे बदलत आहे पहा:", hi: "देखें कामसेतु से गाँव-गाँव में किसानों और मजदूरों का जीवन कैसे बदल रहा है:" },
  "landing.impact1Title": { en: "Timely Farm Work & Harvesting", mr: "वेळेवर शेतीकामे व कापणी", hi: "समय पर कृषि कार्य व कटाई" },
  "landing.impact1Desc": { en: "Avoided crop losses for farmers during onion, soybean, and sugarcane harvesting seasons.", mr: "कांदा, सोयाबीन व ऊस तोडणीच्या हंगामात मजुरांअभावी होणारे शेतकऱ्यांचे मोठे नुकसान टळले.", hi: "प्याज, सोयाबीन और गन्ना कटाई के मौसम में मजदूरों की कमी से होने वाले बड़े नुकसान से बचाव हुआ।" },
  "landing.impact2Title": { en: "100% Direct Wages (Zero Middlemen)", mr: "१००% थेट मजुरी (Zero Middlemen)", hi: "१००% सीधी मजदूरी (Zero Middlemen)" },
  "landing.impact2Desc": { en: "No contractor cuts. Direct phone contact between farmer and laborer with on-time cash/UPI wage payment.", mr: "कोणतीही मुकादम कट किंवा कमिशन नाही. शेतकरी ते मजूर थेट फोन संपर्क आणि वेळेवर रोख/UPI मोबदला.", hi: "कोई ठेकेदार कटौती या कमीशन नहीं। किसान से मजदूर का सीधा फोन संपर्क और समय पर नकद/UPI भुगतान।" },
  "landing.impact3Title": { en: "Dignified Work in Own Village", mr: "गावातच सन्मानजनक रोजगार", hi: "गाँव में ही सम्मानजनक रोजगार" },
  "landing.impact3Desc": { en: "Preventing migration to cities by providing regular jobs within a 5 to 15 km local radius.", mr: "शहराकडे होणारे स्थलांतर रोखून मजुरांना त्यांच्या स्वतःच्या गावात व ५ ते २५ किमी परिसरात नियमित काम.", hi: "शहरों की ओर पलायन रोककर मजदूरों को अपने ही गाँव और ५ से २५ किमी क्षेत्र में नियमित काम।" },
  "landing.impact4Title": { en: "Safe & Trusted Environment", mr: "सुरक्षित व खात्रीशीर मंच", hi: "सुरक्षित और विश्वसनीय मंच" },
  "landing.impact4Desc": { en: "Admin and OTP verification ensure reliability, dispute resolution, and a safe work environment.", mr: "स्थानिक ॲडमिन व ओटीपी पडताळणीमुळे विश्वासार्हता, वाद निवारण व सुरक्षित कामाचे वातावरण.", hi: "स्थानीय एडमिन और ओटीपी सत्यापन से विश्वसनीयता, विवाद समाधान और सुरक्षित कार्य वातावरण।" },

  // Testimonials
  "landing.testimonialTag": { en: "COMMUNITY VOICES", mr: "ग्रामस्थांचे अनुभव (COMMUNITY VOICES)", hi: "ग्रामीण अनुभव (COMMUNITY VOICES)" },
  "landing.testimonialTitle": { en: "What Our Users Say", mr: "आमचे युझर्स काय म्हणतात?", hi: "हमारे उपयोगकर्ता क्या कहते हैं?" },
  "landing.test1Quote": { en: "\"With KaamSetu, I get agricultural work every day in Shirur. No more waiting at village squares hoping for jobs.\"", mr: "\"कामसेतू ॲपमुळे मला रोज शिरूर भागात शेतीची कामे मिळतात. कामाच्या शोधात चौकात ताटकळत उभे राहण्याची गरज उरली नाही.\"", hi: "\"कामसेतु ऐप से मुझे शिरूर क्षेत्र में रोज काम मिलता है। अब काम की तलाश में चौराहे पर खड़े रहने की जरूरत नहीं रही।\"" },
  "landing.test1Author": { en: "Ramesh Pawar", mr: "रमेश पवार", hi: "रमेश पवार" },
  "landing.test1Role": { en: "Agricultural Worker • Shirur, Pune", mr: "शेती कामगार • शिरूर, पुणे", hi: "कृषि मजदूर • शिरूर, पुणे" },
  "landing.test2Quote": { en: "\"During onion and sugarcane harvesting, I found 5 local workers with just one tap. No middleman commission was paid.\"", mr: "\"कांद्याच्या आणि उसाच्या कापणीच्या वेळी एका क्लिकवर ५ स्थानिक मजूर मिळाले. मध्यस्थाला कमिशन देण्याची गरज पडली नाही.\"", hi: "\"प्याज और गन्ने की कटाई के समय एक क्लिक में ५ स्थानीय मजदूर मिले। किसी बिचौलिये को कमीशन नहीं देना पड़ा।\"" },
  "landing.test2Author": { en: "Balasaheb Patil", mr: "बाळासाहेब पाटील", hi: "बाळासाहेब पाटिल" },
  "landing.test2Role": { en: "Farmer & Orchard Owner • Ranjangaon", mr: "शेतकरी व बागायतदार • रांजणगाव", hi: "किसान व बागवान • रांजणगाव" },

  // Helpline Banner
  "landing.helpTitle": { en: "Need Help or Guidance?", mr: "मदत किंवा थेट मार्गदर्शन हवे आहे का?", hi: "मदद या सीधा मार्गदर्शन चाहिए?" },
  "landing.helpDesc": { en: "Our 24x7 support team for farmers and workers is always at your service.", mr: "आमची २४x७ शेतकरी व कामगार सहाय्यता टीम तुमच्या सेवेत तत्पर आहे.", hi: "हमारी २४x७ किसान व मजदूर सहायता टीम आपकी सेवा में सदैव तत्पर है।" },
  "landing.tollFree": { en: "📞 1800-233-5678 (Toll Free)", mr: "📞 १८००-२३३-५६७८ (टोल फ्री)", hi: "📞 १८००-२३३-५६७८ (टोल फ्री)" },
  "landing.whatsappSupport": { en: "💬 WhatsApp Support", mr: "💬 WhatsApp सहाय्यता", hi: "💬 WhatsApp सहायता" },

  // Final Banner & Footer
  "landing.finalCtaTitle": { en: "Join the KaamSetu Family Today!", mr: "आजच कामसेतू परिवारात सामील व्हा!", hi: "आज ही कामसेतु परिवार से जुड़ें!" },
  "landing.finalCtaDesc": { en: "Maharashtra's leading digital platform connecting village workers and farmers.", mr: "गाव पातळीवर कामगार व शेतकरी/मालकांना जोडणारा महाराष्ट्राचा अग्रगण्य डिजिटल मंच.", hi: "ग्रामीण स्तर पर मजदूरों और किसानों को जोड़ने वाला महाराष्ट्र का अग्रणी डिजिटल मंच।" },
  "landing.footerAdminLogin": { en: "Admin Login", mr: "प्रशासन लॉगिन", hi: "एडमिन लॉगिन" },
  "landing.footerCopyright": { en: "© 2026 KaamSetu Platform • Pune Rural Belt Local Jobs Initiative • All rights reserved.", mr: "© २०२६ कामसेतू मंच (KaamSetu) • पुणे ग्रामीण स्थानिक रोजगार उपक्रम • सर्व हक्क राखीव", hi: "© २०२६ कामसेतु मंच (KaamSetu) • पुणे ग्रामीण स्थानीय रोजगार पहल • सर्वाधिकार सुरक्षित" },

  // Admin Dashboard Overview & Navigation
  "admin.pendingBannerTitle": { en: "Pending User Approvals ({count} Accounts)", mr: "नवीन वापरकर्ता नोंदणी मंजुरी प्रलंबित ({count} खाती)", hi: "लंबित उपयोगकर्ता पंजीकरण स्वीकृति ({count} खाते)" },
  "admin.pendingBannerDesc": { en: "New workers and employers have registered. Review and approve accounts to activate them.", mr: "नवीन कामगार व नियोक्त्यांनी नोंदणी केली आहे. त्यांची खाती सक्रिय करण्यासाठी मंजुरी द्या.", hi: "नए कामगारों व नियोक्ताओं ने पंजीकरण किया है। उनके खाते सक्रिय करने हेतु स्वीकृति दें।" },
  "admin.pendingBannerBtn": { en: "👉 Review & Approve ({count})", mr: "👉 खाती तपासा व मंजूर करा ({count})", hi: "👉 खाते जांचें व स्वीकृत करें ({count})" },
  "admin.kpi.pending": { en: "Pending Approvals", mr: "प्रलंबित मंजुरी", hi: "लंबित स्वीकृति" },
  "admin.kpi.actionRequired": { en: "⚠️ Action Required", mr: "⚠️ मंजुरी आवश्यक", hi: "⚠️ कार्रवाई आवश्यक" },
  "admin.kpi.allApproved": { en: "✓ All Approved", mr: "✓ सर्व मंजूर", hi: "✓ सभी स्वीकृत" },
  "admin.kpi.workersTrend": { en: "↑ +8.4% this week", mr: "↑ +8.4% या आठवड्यात", hi: "↑ +8.4% इस सप्ताह" },
  "admin.kpi.providersTrend": { en: "↑ +14.2% this week", mr: "↑ +14.2% या आठवड्यात", hi: "↑ +14.2% इस सप्ताह" },
  "admin.kpi.userMessages": { en: "User Inquiries", mr: "वापरकर्ते संदेश", hi: "उपयोगकर्ता संदेश" },
  "admin.kpi.newMessages": { en: "new messages", mr: "नवीन संदेश", hi: "नए संदेश" },
  "admin.kpi.allUpdated": { en: "✓ All up to date", mr: "✓ सर्व अद्यतनित", hi: "✓ सभी अद्यतित" },
  "admin.kpi.urgentJobs": { en: "⚡ 42 Urgent", mr: "⚡ 42 तातडीचे", hi: "⚡ 42 तुरंत" },
  "admin.kpi.targetMet": { en: "✓ Target > 90%", mr: "✓ Target > 90%", hi: "✓ Target > 90%" },
  "admin.kpi.actionNeeded": { en: "⚠️ Action Needed", mr: "⚠️ आवश्यक कारवाई", hi: "⚠️ आवश्यक कार्रवाई" },
  "admin.kpi.allResolved": { en: "✓ All Resolved", mr: "✓ सर्व निकाली", hi: "✓ सभी निपटाए" },
  "admin.quickActionsTitle": { en: "⚡ Quick Action Center", mr: "⚡ जलद प्रशासकीय कृती (Quick Action Center)", hi: "⚡ त्वरित प्रशासनिक कार्य (Quick Action Center)" },
  "admin.action.pendingApproval": { en: "New Account Approvals", mr: "नवीन खाते मंजुरी", hi: "नए खाते स्वीकृति" },
  "admin.action.trustLadder": { en: "User Trust Directory", mr: "वापरकर्ता विश्वास शिडी", hi: "उपयोगकर्ता विश्वास सूची" },
  "admin.action.jobsControl": { en: "Jobs & Moderation", mr: "कामे व देखरेख", hi: "कार्य व निगरानी" },
  "admin.action.disputes": { en: "Dispute Resolution", mr: "तक्रार निवारण", hi: "शिकायत निवारण" },
  "admin.action.broadcast": { en: "Public Announcements", mr: "सार्वजनिक घोषणा", hi: "सार्वजनिक घोषणा" },
  "admin.action.logout": { en: "Logout", mr: "बाहेर पडा (Logout)", hi: "लॉगआउट (Logout)" },
  "admin.reportsTitle": { en: "Recent Dispute Tickets", mr: "अलीकडील नोंदवलेल्या तक्रारी (Recent Dispute Tickets)", hi: "हालिया दर्ज शिकायतें (Recent Dispute Tickets)" },
  "admin.viewAll": { en: "View All", mr: "सर्व पहा", hi: "सभी देखें" },
  "admin.detailedCase": { en: "Detailed Case", mr: "सविस्तर केस", hi: "विस्तृत केस" },
  "admin.reporter": { en: "Reporter:", mr: "तक्रारदार:", hi: "शिकायतकर्ता:" },
  "admin.takeDecision": { en: "Take Decision", mr: "निर्णय घ्या", hi: "निर्णय लें" },
  "admin.noReports": { en: "No open dispute reports.", mr: "कोणतीही नवीन तक्रार नाही.", hi: "कोई नई शिकायत नहीं है।" },
  "admin.pendingSubtitle": { en: "Verification and Account Approval Queue", mr: "नवीन कामगार व नियोक्त्यांची पडताळणी व खाते मंजुरी रांग", hi: "सत्यापन एवं खाता स्वीकृति कतार" },
  "admin.verificationAlert": { en: "Administrative Verification: Newly registered accounts require admin approval before using the platform.", mr: "प्रशासकीय पडताळणी: नवीन नोंदणीकृत खात्यांना प्लॅटफॉर्म वापरण्यापूर्वी ॲडमिन मंजुरी आवश्यक आहे.", hi: "प्रशासनिक सत्यापन: नए पंजीकृत खातों को प्लेटफॉर्म उपयोग से पूर्व एडमिन स्वीकृति अनिवार्य है।" },
  "admin.noPending": { en: "No pending registrations!", mr: "कोणतीही नोंदणी प्रलंबित नाही!", hi: "कोई पंजीकरण लंबित नहीं है!" },
  "admin.allApprovedDesc": { en: "All new users have been approved.", mr: "सर्व नवीन वापरकर्ते मंजूर झालेले आहेत.", hi: "सभी नए उपयोगकर्ता स्वीकृत हैं।" },
  "admin.viewProfile": { en: "👤 View Profile", mr: "👤 प्रोफाइल", hi: "👤 प्रोफ़ाइल" },
  "admin.refresh": { en: "🔄 Refresh", mr: "🔄 रिफ्रेश", hi: "🔄 रिफ्रेश" },
  "admin.usersSubtitle": { en: "User Management, Pending Approvals & Trust Directory", mr: "वापरकर्ता व्यवस्थापन, प्रलंबित मंजुरी व विश्वास शिडी", hi: "उपयोगकर्ता प्रबंधन, लंबित स्वीकृति व विश्वास सूची" },
  "admin.allUsers": { en: "All Users", mr: "सर्व वापरकर्ते", hi: "सभी उपयोगकर्ता" },
  "admin.jobsSubtitle": { en: "Marketplace Jobs & Moderation", mr: "कामांची नोंद व थेट नियंत्रण", hi: "कार्य सूची एवं नियंत्रण" },
  "admin.postJobBtn": { en: "➕ Post New Job", mr: "➕ नवीन काम टाका", hi: "➕ नया काम पोस्ट करें" },
  "admin.allStatus": { en: "All Status", mr: "सर्व स्थिती (All Status)", hi: "सभी स्थिति (All Status)" },
  "admin.moderateJob": { en: "🚫 Cancel / Moderate", mr: "🚫 रद्द / नियंत्रण", hi: "🚫 रद्द / नियंत्रण" },
  "admin.searchProviders": { en: "Search provider by name or village...", mr: "नियोक्ता नाव किंवा गाव शोधा...", hi: "नियोक्ता नाम या गाँव खोजें..." },
  "admin.searchJobs": { en: "Search job title, village, or employer...", mr: "कामाचे नाव, गाव किंवा नियोक्त्याचे नाव शोधा...", hi: "काम का नाम, गाँव या नियोक्ता खोजें..." },
  "admin.securitySubtitle": { en: "Immutable Security & Audit Trail", mr: "अपरिवर्तनीय सुरक्षा व ऑडिट नोंदी", hi: "अपरिवर्तनीय सुरक्षा एवं ऑडिट लॉग" },

  // Worker Dashboard Keys
  "worker.availableJobs": { en: "Available Jobs", mr: "उपलब्ध कामे", hi: "उपलब्ध कार्य" },
  "worker.myApplications": { en: "My Applications", mr: "माझे अर्ज", hi: "मेरे आवेदन" },
  "worker.confirmedJobs": { en: "Confirmed Jobs", mr: "निश्चित कामे", hi: "स्वीकृत कार्य" },
  "worker.expectedWage": { en: "Expected Wage", mr: "अपेक्षित रोजंदारी", hi: "अपेक्षित मजदूरी" },
  "worker.nextDayRating": { en: "🔔 New Rating Available", mr: "🔔 नवीन रेटिंग उपलब्ध", hi: "🔔 नई रेटिंग उपलब्ध" },
  "worker.rateProvider": { en: "⭐ Rate Provider", mr: "⭐ नियोक्त्याला रेटिंग द्या", hi: "⭐ नियोक्ता को रेटिंग दें" },
  "worker.workPrompt": { en: "Your work is complete. Please rate the employer.", mr: "तुमचे काम पूर्ण झाले आहे. कृपया नियोक्त्याला रेटिंग द्या.", hi: "आपका काम पूरा हो चुका है। कृपया नियोक्ता को रेटिंग दें।" },

  // Provider Dashboard Keys
  "provider.myJobs": { en: "Posted Jobs", mr: "पोस्ट केलेली कामे", hi: "पोस्ट किए गए कार्य" },
  "provider.applicants": { en: "Applicants", mr: "अर्जदार मजूर", hi: "आवेदक मजदूर" },
  "provider.confirmed": { en: "Confirmed", mr: "पुष्टी (Confirmed)", hi: "पुष्ट (Confirmed)" },
  "provider.reliability": { en: "Reliability", mr: "विश्वासार्हता", hi: "विश्वसनीयता" },
  "provider.postJobBtn": { en: "➕ Post New Job", mr: "➕ नवीन काम पोस्ट करा", hi: "➕ नया काम पोस्ट करें" },
  "provider.noJobsYet": { en: "No jobs posted yet", mr: "कोणतेही काम पोस्ट केलेले नाही", hi: "अभी तक कोई काम पोस्ट नहीं किया गया" },
  "provider.noJobsDesc": { en: "You can quickly post worker requirements for agricultural or local tasks.", mr: "आपण शेती किंवा स्थानिक कामासाठी कामगारांची मागणी त्वरित पोस्ट करू शकता.", hi: "आप कृषि अथवा स्थानीय कार्यों के लिए तुरंत मजदूरों की मांग पोस्ट कर सकते हैं।" },
  "provider.postFirstJob": { en: "➕ Post Your First Job", mr: "➕ पहिले काम पोस्ट करा", hi: "➕ पहला काम पोस्ट करें" },
  "provider.rateWorker": { en: "⭐ Rate Worker", mr: "⭐ कामगाराला रेटिंग द्या", hi: "⭐ मजदूर को रेटिंग दें" }
};

class I18nManager {
  constructor() {
    let savedLang = 'mr';
    try {
      savedLang = (window.SafeStorage ? window.SafeStorage.getItem("kaamsetu_lang") : localStorage.getItem("kaamsetu_lang")) || "mr";
    } catch (e) {
      savedLang = 'mr';
    }
    this.currentLang = savedLang; // Default Marathi (गाव पातळीवर सोपे)
  }

  setLanguage(lang) {
    if (["en", "mr", "hi"].includes(lang)) {
      this.currentLang = lang;
      try {
        if (window.SafeStorage) {
          window.SafeStorage.setItem("kaamsetu_lang", lang);
        } else {
          localStorage.setItem("kaamsetu_lang", lang);
        }
      } catch (e) {}
      document.documentElement.lang = lang;
      this.updateDOM();
      // Instantly re-render the entire active application view
      if (window.renderApp) {
        window.renderApp();
      }
    }
  }

  t(key, fallback = "") {
    if (translations[key] && translations[key][this.currentLang]) {
      return translations[key][this.currentLang];
    }
    return fallback || key;
  }

  updateDOM() {
    document.querySelectorAll("[data-i18n]").forEach(el => {
      const key = el.getAttribute("data-i18n");
      const translation = this.t(key);
      if (translation) {
        if (el.tagName === "INPUT" || el.tagName === "TEXTAREA") {
          el.placeholder = translation;
        } else if (el.children.length === 0) {
          el.textContent = translation;
        } else {
          // If the element has child nodes (e.g., icons or badges), update only the inner text span or text node
          const textSpan = el.querySelector("span:not(.nav-tab-icon):not(.admin-tab-badge):not(.badge)");
          if (textSpan) {
            textSpan.textContent = translation;
          } else {
            el.innerHTML = translation;
          }
        }
      }
    });

    // Update active state in language selector (pills and dropdown)
    const langSelect = document.getElementById("header-lang-select");
    if (langSelect) {
      langSelect.value = this.currentLang;
    }

    document.querySelectorAll(".lang-option").forEach(btn => {
      if (btn.getAttribute("data-lang") === this.currentLang) {
        btn.classList.add("active");
      } else {
        btn.classList.remove("active");
      }
    });
  }
}

window.i18n = new I18nManager();
