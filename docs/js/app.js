/* ═══════════════════════════════════════════════
   WinRescue — Site JS (i18n + animations)
   ═══════════════════════════════════════════════ */

// ─── Translations ───
const translations = {
  fr: {
    "nav.features": "Fonctionnalites",
    "nav.howItWorks": "Comment ca marche",
    "nav.scripts": "Scripts",
    "nav.requirements": "Pre-requis",
    "nav.download": "Telecharger",

    "hero.badge": "Open Source \u00b7 GPL-3.0",
    "hero.title1": "Votre smartphone Android",
    "hero.title2": "devient un outil de recuperation",
    "hero.subtitle": "WinRescue transforme votre Android roote en clavier USB virtuel pour reparer, recuperer et administrer n'importe quel PC Windows \u2014 sans cle USB bootable.",
    "hero.cta": "Telecharger",
    "hero.source": "Voir le code",
    "hero.stat1": "Scripts",
    "hero.stat2": "Plateformes",
    "hero.stat3": "Cle USB requise",

    "features.badge": "Fonctionnalites",
    "features.title": "Tout ce dont vous avez besoin",
    "features.subtitle": "22 scripts Windows via USB HID + 8 scripts Android via shell root, guides par un wizard interactif.",
    "features.f1.title": "Windows Recovery",
    "features.f1.desc": "22 scripts pour reinitialiser mots de passe, reparer le boot, recuperer des fichiers, activer le mode sans echec et plus.",
    "features.f1.tag": "Win10 \u00b7 Win11",
    "features.f2.title": "Android Maintenance",
    "features.f2.desc": "8 scripts pour vider le cache, changer les DNS, desactiver le bloatware, calibrer la batterie et diagnostiquer le systeme.",
    "features.f2.tag": "Shell root",
    "features.f3.title": "Wizard interactif",
    "features.f3.desc": "Guide step-by-step avec images d'aide, preview des commandes, barre de progression et confirmation a chaque etape.",
    "features.f3.tag": "Step-by-step",
    "features.f4.title": "USB HID natif",
    "features.f4.desc": "Emule un clavier physique via le protocole HID standard. Fonctionne meme sur l'ecran de login et dans WinRE.",
    "features.f4.tag": "QWERTY \u00b7 AZERTY",
    "features.f5.title": "Dashboard KPI",
    "features.f5.desc": "Vue d'ensemble avec statut root, connexion USB, nombre de scripts par categorie et filtres par OS.",
    "features.f5.tag": "Temps reel",
    "features.f6.title": "Bilingue FR/EN",
    "features.f6.desc": "Interface complete en francais et anglais. Detecte automatiquement la langue du systeme ou laisse le choix dans les parametres.",
    "features.f6.tag": "i18n",

    "how.badge": "Simple",
    "how.title": "Comment ca marche",
    "how.subtitle": "Trois etapes pour recuperer n'importe quel PC Windows.",
    "how.step1.title": "Branchez",
    "how.step1.desc": "Connectez votre Android au PC cible via un cable USB-C OTG. WinRescue detecte automatiquement la connexion.",
    "how.step2.title": "Selectionnez",
    "how.step2.desc": "Choisissez le script adapte a votre situation : reset mot de passe, reparation boot, recuperation fichiers...",
    "how.step3.title": "Executez",
    "how.step3.desc": "Le wizard vous guide etape par etape. WinRescue envoie les frappes clavier automatiquement au PC.",

    "scripts.badge": "30+ scripts",
    "scripts.title": "Scripts disponibles",
    "scripts.w1": "Reset mot de passe",
    "scripts.w2": "Reset PIN Windows Hello",
    "scripts.w3": "Recuperation fichiers",
    "scripts.w4": "Reinitialisation usine",
    "scripts.w5": "Creation compte admin",
    "scripts.w6": "Reparation boot MBR/BCD",
    "scripts.w7": "Reparation SFC/DISM",
    "scripts.w8": "Mode sans echec + 15 autres",
    "scripts.a1": "Vider le cache",
    "scripts.a2": "Reset WiFi/Bluetooth",
    "scripts.a3": "Calibrer la batterie",
    "scripts.a4": "Changer les DNS",
    "scripts.a5": "Diagnostic systeme",
    "scripts.a6": "Desactiver bloatware + 2 autres",
    "scripts.easy": "Facile",
    "scripts.medium": "Moyen",
    "scripts.advanced": "Avance",
    "scripts.mixed": "Mix",

    "req.badge": "Configuration",
    "req.title": "Pre-requis",
    "req.android": "API 26 minimum. Teste sur Unihertz Titan 2, Google Pixel, OnePlus.",
    "req.root": "Necessaire pour creer le peripherique USB HID virtuel (/dev/hidg0) via configfs.",
    "req.kernel": "Kernel HID",
    "req.kernelDesc": "CONFIG_USB_CONFIGFS_F_HID=y compile dans le kernel ou module libcomposite.",
    "req.cable": "Cable USB-C OTG",
    "req.cableDesc": "Cable de donnees (pas charge uniquement). USB-C vers USB-A ou USB-C vers USB-C.",
    "req.warning": "Non compatible : Samsung (TEE whitelist), Xiaomi (configfs restrictif), Huawei (bootloader verrouille).",

    "dl.title": "Pret a recuperer ?",
    "dl.subtitle": "Clonez le depot, buildez et installez en quelques minutes.",
    "dl.github": "Voir sur GitHub",

    "footer.legal": "Usage legal uniquement. L'auteur decline toute responsabilite pour tout usage illicite."
  },
  en: {
    "nav.features": "Features",
    "nav.howItWorks": "How it works",
    "nav.scripts": "Scripts",
    "nav.requirements": "Requirements",
    "nav.download": "Download",

    "hero.badge": "Open Source \u00b7 GPL-3.0",
    "hero.title1": "Your Android smartphone",
    "hero.title2": "becomes a recovery tool",
    "hero.subtitle": "WinRescue turns your rooted Android into a virtual USB keyboard to repair, recover and administer any Windows PC \u2014 no bootable USB key needed.",
    "hero.cta": "Download",
    "hero.source": "View source",
    "hero.stat1": "Scripts",
    "hero.stat2": "Platforms",
    "hero.stat3": "USB key needed",

    "features.badge": "Features",
    "features.title": "Everything you need",
    "features.subtitle": "22 Windows scripts via USB HID + 8 Android scripts via root shell, guided by an interactive wizard.",
    "features.f1.title": "Windows Recovery",
    "features.f1.desc": "22 scripts to reset passwords, repair boot, recover files, enable safe mode and more.",
    "features.f1.tag": "Win10 \u00b7 Win11",
    "features.f2.title": "Android Maintenance",
    "features.f2.desc": "8 scripts to clear cache, change DNS, disable bloatware, calibrate battery and run system diagnostics.",
    "features.f2.tag": "Root shell",
    "features.f3.title": "Interactive Wizard",
    "features.f3.desc": "Step-by-step guide with hint images, command preview, progress bar and confirmation at each step.",
    "features.f3.tag": "Step-by-step",
    "features.f4.title": "Native USB HID",
    "features.f4.desc": "Emulates a physical keyboard via the standard HID protocol. Works even on the login screen and in WinRE.",
    "features.f4.tag": "QWERTY \u00b7 AZERTY",
    "features.f5.title": "KPI Dashboard",
    "features.f5.desc": "Overview with root status, USB connection, script count by category and OS filters.",
    "features.f5.tag": "Real-time",
    "features.f6.title": "Bilingual FR/EN",
    "features.f6.desc": "Full interface in French and English. Auto-detects system language or lets you choose in settings.",
    "features.f6.tag": "i18n",

    "how.badge": "Simple",
    "how.title": "How it works",
    "how.subtitle": "Three steps to recover any Windows PC.",
    "how.step1.title": "Connect",
    "how.step1.desc": "Plug your Android into the target PC via a USB-C OTG cable. WinRescue auto-detects the connection.",
    "how.step2.title": "Select",
    "how.step2.desc": "Choose the right script for your situation: password reset, boot repair, file recovery...",
    "how.step3.title": "Execute",
    "how.step3.desc": "The wizard guides you step by step. WinRescue sends keystrokes automatically to the PC.",

    "scripts.badge": "30+ scripts",
    "scripts.title": "Available scripts",
    "scripts.w1": "Password reset",
    "scripts.w2": "Windows Hello PIN reset",
    "scripts.w3": "File recovery",
    "scripts.w4": "Factory reset",
    "scripts.w5": "Create admin account",
    "scripts.w6": "Boot repair MBR/BCD",
    "scripts.w7": "SFC/DISM repair",
    "scripts.w8": "Safe mode + 15 more",
    "scripts.a1": "Clear cache",
    "scripts.a2": "Reset WiFi/Bluetooth",
    "scripts.a3": "Battery calibration",
    "scripts.a4": "Change DNS",
    "scripts.a5": "System diagnostics",
    "scripts.a6": "Disable bloatware + 2 more",
    "scripts.easy": "Easy",
    "scripts.medium": "Medium",
    "scripts.advanced": "Advanced",
    "scripts.mixed": "Mix",

    "req.badge": "Setup",
    "req.title": "Requirements",
    "req.android": "API 26 minimum. Tested on Unihertz Titan 2, Google Pixel, OnePlus.",
    "req.root": "Required to create the virtual USB HID device (/dev/hidg0) via configfs.",
    "req.kernel": "HID Kernel",
    "req.kernelDesc": "CONFIG_USB_CONFIGFS_F_HID=y compiled in the kernel or libcomposite module.",
    "req.cable": "USB-C OTG Cable",
    "req.cableDesc": "Data cable (not charge-only). USB-C to USB-A or USB-C to USB-C.",
    "req.warning": "Not compatible: Samsung (TEE whitelist), Xiaomi (restrictive configfs), Huawei (locked bootloader).",

    "dl.title": "Ready to recover?",
    "dl.subtitle": "Clone the repo, build and install in minutes.",
    "dl.github": "View on GitHub",

    "footer.legal": "Legal use only. The author disclaims all liability for any illegal use."
  }
};

// ─── i18n Engine ───
let currentLang = "fr";

function setLanguage(lang) {
  currentLang = lang;
  document.documentElement.lang = lang;
  localStorage.setItem("winrescue-lang", lang);

  const elements = document.querySelectorAll("[data-i18n]");
  elements.forEach(function(el) {
    var key = el.getAttribute("data-i18n");
    if (translations[lang] && translations[lang][key]) {
      el.textContent = translations[lang][key];
    }
  });

  var flag = document.getElementById("langFlag");
  if (flag) {
    flag.textContent = lang === "fr" ? "EN" : "FR";
  }
}

function toggleLanguage() {
  setLanguage(currentLang === "fr" ? "en" : "fr");
}

// ─── Tabs ───
function initTabs() {
  var tabBtns = document.querySelectorAll(".tab-btn");
  tabBtns.forEach(function(btn) {
    btn.addEventListener("click", function() {
      var tab = btn.getAttribute("data-tab");

      tabBtns.forEach(function(b) { b.classList.remove("active"); });
      btn.classList.add("active");

      document.querySelectorAll(".tab-panel").forEach(function(p) { p.classList.remove("active"); });
      var target = document.getElementById("tab-" + tab);
      if (target) target.classList.add("active");
    });
  });
}

// ─── Scroll Animations ───
function initAnimations() {
  var observer = new IntersectionObserver(function(entries) {
    entries.forEach(function(entry) {
      if (entry.isIntersecting) {
        entry.target.classList.add("visible");
        observer.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1, rootMargin: "0px 0px -40px 0px" });

  document.querySelectorAll("[data-animate]").forEach(function(el) {
    observer.observe(el);
  });
}

// ─── Mobile Menu ───
function initMobileMenu() {
  var btn = document.getElementById("mobileMenuBtn");
  var links = document.getElementById("navLinks");

  if (!btn || !links) return;

  btn.addEventListener("click", function() {
    links.classList.toggle("open");
    btn.classList.toggle("active");
  });

  links.querySelectorAll("a").forEach(function(a) {
    a.addEventListener("click", function() {
      links.classList.remove("open");
      btn.classList.remove("active");
    });
  });
}

// ─── Navbar Scroll ───
function initNavbar() {
  var navbar = document.getElementById("navbar");
  if (!navbar) return;

  window.addEventListener("scroll", function() {
    if (window.scrollY > 20) {
      navbar.style.background = "rgba(8, 12, 24, 0.95)";
    } else {
      navbar.style.background = "rgba(8, 12, 24, 0.85)";
    }
  }, { passive: true });
}

// ─── Smooth Scroll ───
function initSmoothScroll() {
  document.querySelectorAll('a[href^="#"]').forEach(function(a) {
    a.addEventListener("click", function(e) {
      var target = document.querySelector(a.getAttribute("href"));
      if (target) {
        e.preventDefault();
        target.scrollIntoView({ behavior: "smooth", block: "start" });
      }
    });
  });
}

// ─── Init ───
document.addEventListener("DOMContentLoaded", function() {
  // Detect saved or browser language
  var saved = localStorage.getItem("winrescue-lang");
  if (saved && translations[saved]) {
    setLanguage(saved);
  } else {
    var browserLang = navigator.language.slice(0, 2);
    setLanguage(browserLang === "fr" ? "fr" : "en");
  }

  // Language toggle
  var langBtn = document.getElementById("langToggle");
  if (langBtn) langBtn.addEventListener("click", toggleLanguage);

  initTabs();
  initAnimations();
  initMobileMenu();
  initNavbar();
  initSmoothScroll();
});
