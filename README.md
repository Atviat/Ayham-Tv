# Ayham TV — Android TV Launcher

مشروع Launcher أصلي (Kotlin + Jetpack Compose) لأجهزة Android TV، بهوية سوداء وبنفسجية.
الحزمة: `com.ayham.tv` — الحد الأدنى Android 5 (API 21) — الهدف Android 14 (API 34).

> **الحالة:** المرحلة 1. الكود مكتوب لكن **لم يتم بناؤه ولا اختباره على تلفاز حقيقي بعد**.
> أول بناء فعلي سيتم على GitHub Actions. راجع `docs/FEATURES.md` للقائمة الصادقة للميزات.

## ماذا يوجد في المرحلة 1

- شاشة رئيسية: الساعة والتاريخ، المفضلة، آخر التطبيقات، كل التطبيقات، اختصارات (الإعدادات، الواي فاي، البلوتوث).
- اكتشاف التطبيقات المثبتة وتشغيلها.
- المفضلة: زر **Menu** في الريموت (أو ضغط مطوّل على OK) يضيف التطبيق أو يزيله.
- تنقل كامل بالريموت مع تمييز العنصر المحدد.
- عربي وإنجليزي حسب لغة النظام (RTL تلقائي).
- تسجيل التطبيق كـ Home Launcher.

## البناء على GitHub (بدون كمبيوتر)

الملف `.github/workflows/build.yml` يبني APK عند كل رفع، أو يدوياً:
**Actions ← Build APK ← Run workflow**. بعد النجاح ستجد APK ضمن **Artifacts** في أسفل صفحة التشغيل.

للإصدارات: ادفع Tag مثل `v0.1.0` (أو شغّل Release APK يدوياً) فينتج APK ويُنشر في Releases.

### توقيع الإصدارات (اختياري)

أضف في **Settings ← Secrets and variables ← Actions** هذه الأسرار:
`AYHAM_KEYSTORE_BASE64`, `AYHAM_KEYSTORE_PASSWORD`, `AYHAM_KEY_ALIAS`, `AYHAM_KEY_PASSWORD`.
بدونها يُبنى APK تجريبي (debug) قابل للتثبيت. لا توضع أي مفاتيح داخل المستودع.

## إدارة المشروع من الآيفون

1. أنشئ مستودعاً باسم `Ayham-TV` على github.com من Safari.
2. استخدم ملف **bootstrap.yml** المرفق: افتح المستودع ← **Add file ← Create new file**، اكتب الاسم
   `.github/workflows/bootstrap.yml` والصق محتوى الملف، ثم **Commit changes**.
3. اذهب إلى **Actions ← Bootstrap Ayham TV ← Run workflow**. سيُنشئ كل ملفات المشروع في المستودع ويبني أول APK.
4. لتعديل أي ملف لاحقاً: افتحه في GitHub واضغط أيقونة القلم، عدّل، ثم Commit.

## التثبيت على تلفاز Xiaomi

1. على التلفاز ثبّت تطبيق **Downloader** من متجر Google Play.
2. من الآيفون: افتح صفحة تشغيل البناء ← نزّل الـ Artifact، أو ضع APK في Release وانسخ رابط التحميل المباشر.
3. في Downloader أدخل الرابط ونزّل الملف، ووافق على السماح بالتثبيت من هذا المصدر.
4. افتح **Ayham TV** من قائمة التطبيقات.

### جعله الشاشة الرئيسية

بعض أجهزة Xiaomi تُبقي واجهة PatchWall ولا تعرض اختيار Launcher. جرّب زر **Home** بعد التثبيت؛ إن ظهر اختيار فاختر Ayham TV.
إن لم يظهر، فالتطبيق يعمل بشكل طبيعي كتطبيق تفتحه بنفسك، وتغيير الافتراضي يحتاج ADB من كمبيوتر.

## هيكل المشروع

```
app/src/main/java/com/ayham/tv/
  MainActivity.kt        نقطة الدخول
  HomeViewModel.kt       حالة الشاشة (MVVM)
  data/AppRepository.kt  اكتشاف وتشغيل التطبيقات
  data/Prefs.kt          DataStore: المفضلة وآخر التطبيقات
  ui/HomeScreen.kt       الواجهة
  ui/Theme.kt            الألوان
```
