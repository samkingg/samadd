VERSION 5.00
Begin VB.Form frmPurchase 
   Caption         =   "إدخال بضاعة"
   ClientHeight    =   4800
   ClientLeft      =   60
   ClientTop       =   345
   ClientWidth     =   9000
   LinkTopic       =   "frmPurchase"
   MDIChild        =   -1  'True
   ScaleHeight     =   4800
   ScaleWidth      =   9000
   StartUpPosition =   2  'CenterScreen
   Begin VB.TextBox txtBarcode 
      Height          =   315
      Left            =   360
      TabIndex        =   0
      Top             =   480
      Width           =   1935
   End
   Begin VB.TextBox txtQuantity 
      Height          =   315
      Left            =   360
      TabIndex        =   1
      Top             =   960
      Width           =   915
   End
   Begin VB.CommandButton cmdAdd 
      Caption         =   "إضافة للمخزون"
      Height          =   375
      Left            =   360
      TabIndex        =   2
      Top             =   1440
      Width           =   1575
   End
   Begin VB.ListBox lstLog 
      Height          =   3375
      Left            =   3600
      TabIndex        =   3
      Top             =   480
      Width           =   5055
   End
   Begin VB.Label lblBarcode 
      Caption         =   "باركود الصنف"
      Height          =   255
      Left            =   2400
      TabIndex        =   4
      Top             =   480
      Width           =   1095
   End
   Begin VB.Label lblQty 
      Caption         =   "الكمية"
      Height          =   255
      Left            =   2400
      TabIndex        =   5
      Top             =   960
      Width           =   615
   End
End
Attribute VB_Name = "frmPurchase"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private Sub Form_Load()
	ApplyFormRTL Me
End Sub

Private Sub cmdAdd_Click()
	Dim code As String
	Dim qty As Double
	Dim rs As ADODB.Recordset
	Dim sql As String
	code = Trim$(txtBarcode.Text)
	qty = Val(txtQuantity.Text)
	If Len(code) = 0 Or qty <= 0 Then
		MsgBox "أدخل باركود صحيح والكمية", vbExclamation, App.Title
		Exit Sub
	End If
	Set rs = gConnection.Execute("SELECT ProductID, Name, Stock FROM Products WHERE Barcode = " & Quote(code))
	If rs.EOF Then
		MsgBox "لم يتم العثور على الصنف", vbCritical, App.Title
	Else
		ExecNonQuery "UPDATE Products SET Stock = Stock + " & CStr(qty) & " WHERE ProductID = " & rs!ProductID
		ExecNonQuery "INSERT INTO InventoryMovements (ProductID, MovementDate, Quantity, Reason) VALUES (" & rs!ProductID & ", NOW(), " & CStr(qty) & ", 'Purchase')"
		lstLog.AddItem "تمت إضافة " & CStr(qty) & " للوحدات للصنف: " & rs!Name
	End If
	If Not rs Is Nothing Then rs.Close
	Set rs = Nothing
End Sub