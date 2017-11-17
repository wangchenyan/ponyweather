-keepattributes Signature, InnerClasses, LineNumberTable

# android-support
-dontwarn android.support.**
-keep class android.support.** { *; }

# app
-keep class me.wcy.weather.utils.proguard.NoProGuard { *; }
-keep class * extends me.wcy.weather.utils.proguard.NoProGuard { *; }

# amap
-keep class com.amap.api.location.** { *; }
-keep class com.amap.api.fence.** { *; }
-keep class com.autonavi.aps.amapapi.model.** { *; }

# rxjava2
-dontwarn io.reactivex.**
-keep class io.reactivex.** { *; }

# retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# okio
-dontwarn okio.**
-keep class okio.** { *; }

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# bmob
-dontwarn cn.bmob.v3.**
-keep class cn.bmob.v3.** { *; }
