Attribute VB_Name = "modApp"
Option Explicit

Public AppPath As String

Public Sub Main()
	On Error GoTo EH
	App.Title = "نظام كاشير السوبرماركت"
	AppPath = App.Path
	If Right$(AppPath, 1) <> "\\" Then AppPath = AppPath & "\\"
	
	If Not Database_Ensure() Then
		MsgBox "تعذر تهيئة قاعدة البيانات.", vbCritical, App.Title
		Exit Sub
	End If
	
	Load frmLogin
	frmLogin.Show
	Exit Sub
EH:
	MsgBox "خطأ: " & Err.Description, vbCritical, App.Title
End Sub