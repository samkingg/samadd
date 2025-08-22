# Supermarket POS (Arabic) - VB6 + Access

## المتطلبات
- نظام Windows مع Visual Basic 6.0 (SP6)
- Microsoft Jet 4.0 OLE DB Provider
- مراجع VB6 التالية:
  - Microsoft ActiveX Data Objects 2.8 Library
  - Microsoft ADO Ext. 2.8 for DDL and Security

## نظرة عامة
نظام كاشير سوبرماركت باللغة العربية مبني بـ Visual Basic 6 وقاعدة بيانات Microsoft Access. يدعم إنشاء قاعدة البيانات تلقائياً عند التشغيل الأول، وتقديم واجهة RTL، وتسجيل الدخول، والقائمة الرئيسية. سيتم إضافة إدارة الأصناف ونقطة البيع لاحقاً.

## التشغيل
1. افتح المشروع `VB6SupermarketPOS/VB6SupermarketPOS.vbp` في VB6.
2. من Project -> References تأكد من تفعيل ADO 2.8 و ADOX 2.8.
3. شغّل المشروع (F5). سيتم إنشاء قاعدة البيانات تلقائياً داخل مجلد `data/` باسم `Supermarket.mdb`.
   - اسم المستخدم الافتراضي: `admin`
   - كلمة المرور الافتراضية: `admin`

## هيكل المشروع
- VB6SupermarketPOS/
  - VB6SupermarketPOS.vbp
  - modApp.bas
  - modDatabase.bas
  - modRTL.bas
  - frmLogin.frm
  - frmMain.frm (MDI)
  - data/ (يُنشأ تلقائياً)

## المزايا المخطط لها
- إدارة الأصناف (إضافة/تعديل/حذف/بحث)
- شاشة نقطة بيع مع سلة، خصومات، ضريبة، وباركود
- إدارة العملاء ومدفوعات وطباعة فواتير
- تقارير أساسية للمبيعات والمخزون

## ملاحظات
- المشروع مُهيأ للاتجاه من اليمين لليسار على مستوى النوافذ.
- تم فصل منطق قاعدة البيانات في `modDatabase.bas` بما في ذلك إنشاء الجداول.
- يمكن تعديل سلسلة الاتصال في `modDatabase.bas` إن لزم.