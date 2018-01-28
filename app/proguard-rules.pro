# amap
-keep class com.amap.api.location.** { *; }
-keep class com.amap.api.fence.** { *; }
-keep class com.autonavi.aps.amapapi.model.** { *; }

-dontwarn retrofit2.**
-dontwarn io.reactivex.**

-keep class android.support.** { *; }

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# bmob
-ignorewarnings
-keepattributes Signature,*Annotation*
-dontwarn cn.bmob.v3.**
-keep class cn.bmob.v3.** { *; }
-keep class * extends cn.bmob.v3.BmobObject { *; }
-dontwarn org.apache.http.**
-keep class org.apache.http.** { *; }
-dontwarn android.net.http.**
-keep class android.net.http.**{ *; }
-dontwarn okio.**
-keep class okio.** { *; }
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-dontwarn rx.**
-keep class rx.** { *; }

# ViewHolder
-keepclassmembers class * extends **.radapter.RViewHolder {
    public <init>(android.view.View);
}