# 🎮 دليل بناء APK للعبة الكلمات المتقاطعة العربية
# 🎮 APK Build Guide for Arabic Crossword Game

## 📋 المتطلبات - Requirements

### 🖥️ نظام التشغيل - Operating System
- **Windows 10/11** أو **macOS** أو **Linux**
- **Windows 10/11** or **macOS** or **Linux**

### 🛠️ الأدوات المطلوبة - Required Tools
- **Java Development Kit (JDK) 17 أو أحدث**
- **Java Development Kit (JDK) 17 or later**
- **Android Studio** (مستحسن - recommended)
- **Android SDK** (مطلوب - required)

## 🚀 خطوات البناء - Build Steps

### 1️⃣ تثبيت Java
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# Windows
# Download from: https://adoptium.net/
# macOS
brew install openjdk@17
```

### 2️⃣ تثبيت Android SDK
```bash
# Option 1: Through Android Studio (Recommended)
# Download Android Studio from: https://developer.android.com/studio
# Install and configure Android SDK

# Option 2: Command Line
# Download command line tools from: https://developer.android.com/studio#command-tools
```

### 3️⃣ تعيين متغيرات البيئة - Set Environment Variables
```bash
# Linux/macOS
export ANDROID_HOME=/path/to/your/android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Windows
set ANDROID_HOME=C:\path\to\your\android\sdk
set PATH=%PATH%;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools
```

### 4️⃣ إنشاء ملف local.properties
```bash
# Linux/macOS
echo "sdk.dir=/path/to/your/android/sdk" > local.properties

# Windows
echo sdk.dir=C:\path\to\your\android\sdk > local.properties
```

### 5️⃣ بناء APK - Build APK
```bash
# تنظيف البناء السابق - Clean previous builds
./gradlew clean

# بناء APK للتطوير - Build debug APK
./gradlew assembleDebug

# بناء APK للإنتاج - Build release APK
./gradlew assembleRelease
```

## 📱 مواقع الملفات - File Locations

### APK Files
```
app/build/outputs/apk/debug/app-debug.apk     # للتطوير - Debug version
app/build/outputs/apk/release/app-release.apk # للإنتاج - Release version
```

### Bundle Files (Android App Bundle)
```
app/build/outputs/bundle/release/app-release.aab  # للإنتاج - Release bundle
```

## 🔧 استكشاف الأخطاء - Troubleshooting

### ❌ خطأ: SDK location not found
```bash
# الحل - Solution:
# 1. تأكد من تعيين ANDROID_HOME
#    Make sure ANDROID_HOME is set
# 2. أنشئ ملف local.properties
#    Create local.properties file
# 3. تأكد من وجود Android SDK
#    Ensure Android SDK exists
```

### ❌ خطأ: Java version compatibility
```bash
# الحل - Solution:
# تأكد من استخدام Java 17 أو أحدث
# Make sure you're using Java 17 or later
java -version
```

### ❌ خطأ: Gradle build failed
```bash
# الحل - Solution:
# 1. تأكد من وجود اتصال بالإنترنت
#    Ensure internet connection
# 2. امسح ذاكرة التخزين المؤقت
#    Clear cache
./gradlew clean
./gradlew --stop
```

## 🎯 نصائح للبناء - Build Tips

### ⚡ تسريع البناء - Speed Up Build
```bash
# استخدام Gradle daemon
# Use Gradle daemon
./gradlew --daemon

# بناء متوازي
# Parallel build
./gradlew assembleDebug --parallel

# زيادة ذاكرة JVM
# Increase JVM memory
export GRADLE_OPTS="-Xmx4096m -XX:MaxPermSize=512m"
```

### 🔒 توقيع APK - Sign APK
```bash
# إنشاء keystore
# Create keystore
keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 10000

# توقيع APK
# Sign APK
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key.keystore app-release-unsigned.apk alias_name
```

## 📊 معلومات APK - APK Information

### 📱 مواصفات التطبيق - App Specifications
- **اسم التطبيق**: لعبة الكلمات المتقاطعة العربية
- **App Name**: Arabic Crossword Game
- **حزمة التطبيق**: com.arabiccrossword.game
- **Package**: com.arabiccrossword.game
- **الإصدار**: 1.0.0
- **Version**: 1.0.0
- **الحد الأدنى لـ Android**: API 21 (Android 5.0)
- **Min Android**: API 21 (Android 5.0)
- **الهدف**: API 34 (Android 14)
- **Target**: API 34 (Android 14)

### 🎨 المميزات - Features
- ✅ واجهة عربية كاملة
- ✅ دعم الذكاء الاصطناعي
- ✅ ألغاز متطورة
- ✅ حفظ تلقائي
- ✅ إعدادات متقدمة
- ✅ دعم الوضع المظلم

## 🚀 التثبيت - Installation

### 📱 على الجهاز - On Device
1. **تفعيل المصادر المجهولة** - Enable unknown sources
2. **نقل APK** إلى الجهاز - Transfer APK to device
3. **فتح APK** وتثبيت - Open APK and install
4. **تشغيل التطبيق** - Launch app

### 🔧 عبر ADB - Via ADB
```bash
# تثبيت APK
# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# إعادة تثبيت
# Reinstall
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 📞 الدعم - Support

إذا واجهت أي مشاكل في البناء، يرجى:
If you encounter any build issues, please:

1. **تحقق من المتطلبات** - Check requirements
2. **راجع رسائل الخطأ** - Review error messages
3. **تأكد من إعدادات البيئة** - Verify environment setup
4. **استخدم سكريبت البناء** - Use build script

```bash
# تشغيل سكريبت البناء
# Run build script
./build-apk.sh
```

---

**🎉 تم بناء APK بنجاح! - APK built successfully!**  
**📱 استمتع باللعبة! - Enjoy the game!**