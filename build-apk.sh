#!/bin/bash

# Arabic Crossword Game - APK Build Script
# لعبة الكلمات المتقاطعة العربية - سكريبت بناء APK

echo "🎮 بناء لعبة الكلمات المتقاطعة العربية - Building Arabic Crossword Game"
echo "=================================================="

# Check if Android SDK is available
if [ -z "$ANDROID_HOME" ]; then
    echo "❌ خطأ: متغير ANDROID_HOME غير محدد"
    echo "Error: ANDROID_HOME environment variable is not set"
    echo ""
    echo "📋 للحل، قم بتعيين متغير ANDROID_HOME:"
    echo "To fix this, set the ANDROID_HOME environment variable:"
    echo ""
    echo "export ANDROID_HOME=/path/to/your/android/sdk"
    echo "export PATH=\$PATH:\$ANDROID_HOME/tools:\$ANDROID_HOME/platform-tools"
    echo ""
    echo "🔧 أو قم بإنشاء ملف local.properties:"
    echo "Or create a local.properties file:"
    echo ""
    echo "echo 'sdk.dir=/path/to/your/android/sdk' > local.properties"
    echo ""
    exit 1
fi

echo "✅ Android SDK found at: $ANDROID_HOME"
echo ""

# Clean previous builds
echo "🧹 تنظيف البناء السابق - Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "🔨 بناء APK للتطوير - Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 تم بناء APK بنجاح! - APK built successfully!"
    echo "📱 موقع الملف - File location:"
    echo "   app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📊 حجم الملف - File size:"
    ls -lh app/build/outputs/apk/debug/app-debug.apk
    echo ""
    echo "🚀 يمكنك الآن تثبيت APK على جهاز Android"
    echo "You can now install the APK on an Android device"
else
    echo ""
    echo "❌ فشل في بناء APK - Failed to build APK"
    echo "🔍 تحقق من رسائل الخطأ أعلاه"
    echo "Check the error messages above"
    exit 1
fi