VERSION 5.00
Begin VB.Form frmLogin 
   Caption         =   "تسجيل الدخول"
   ClientHeight    =   2400
   ClientLeft      =   60
   ClientTop       =   345
   ClientWidth     =   4200
   LinkTopic       =   "frmLogin"
   ScaleHeight     =   2400
   ScaleWidth      =   4200
   StartUpPosition =   2  'CenterScreen
   Begin VB.CommandButton cmdLogin 
      Caption         =   "دخول"
      Height          =   375
      Left            =   1680
      TabIndex        =   2
      Top             =   1800
      Width           =   975
   End
   Begin VB.TextBox txtPassword 
      Height          =   315
      IMEMode         =   3  'DISABLE
      Left            =   360
      PasswordChar    =   "*"
      TabIndex        =   1
      Top             =   1200
      Width           =   2295
   End
   Begin VB.TextBox txtUsername 
      Height          =   315
      Left            =   360
      TabIndex        =   0
      Top             =   600
      Width           =   2295
   End
   Begin VB.Label lblPassword 
      Caption         =   "كلمة المرور"
      Height          =   255
      Left            =   2760
      TabIndex        =   4
      Top             =   1200
      Width           =   1215
   End
   Begin VB.Label lblUsername 
      Caption         =   "اسم المستخدم"
      Height          =   255
      Left            =   2760
      TabIndex        =   3
      Top             =   600
      Width           =   1215
   End
End
Attribute VB_Name = "frmLogin"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private Sub cmdLogin_Click()
	Dim u As String, p As String
	u = Trim$(txtUsername.Text)
	p = Trim$(txtPassword.Text)
	If Len(u) = 0 Or Len(p) = 0 Then
		MsgBox "برجاء إدخال اسم المستخدم وكلمة المرور", vbExclamation, App.Title
		Exit Sub
	End If
	If Database_ValidateUser(u, p) Then
		Unload Me
		Load frmMain
		frmMain.Show
	Else
		MsgBox "بيانات الدخول غير صحيحة", vbCritical, App.Title
	End If
End Sub

Private Sub Form_Load()
	ApplyFormRTL Me
End Sub