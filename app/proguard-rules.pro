# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep our API interfaces
-keep interface com.example.fishingforecastappstav.data.remote.WeatherApi { *; }
-keep class com.example.fishingforecastappstav.data.remote.dto.OpenMeteoResponse { *; }
-keep class com.example.fishingforecastappstav.data.remote.dto.OpenMeteoCurrent { *; }

# Keep our data classes used with Gson
-keep class com.example.fishingforecastappstav.mainScreen.FishDetail { *; }
-keep class com.example.fishingforecastappstav.mainScreen.Fish { *; }
-keep class com.example.fishingforecastappstav.mainScreen.GuideCategory { *; }
-keep class com.example.fishingforecastappstav.mainScreen.GuideDetail { *; }
