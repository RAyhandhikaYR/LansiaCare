# Lansia Care ProGuard Rules

# Keep all model classes
-keep class com.lansiacare.data.entities.** { *; }

# Keep Repository classes
-keep class com.lansiacare.data.repository.** { *; }

# Keep ViewModel classes
-keep class com.lansiacare.ui.**.ViewModel { *; }

# Room specific rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# WorkManager rules
-keep class androidx.work.impl.WorkDatabase { *; }
-keep class androidx.work.impl.model.** { *; }

# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep custom classes used with Gson
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Material Design Components
-keep class com.google.android.material.** { *; }

# Remove logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}