VERSION 5.00
Begin VB.Form frmProducts 
   Caption         =   "إدارة الأصناف"
   ClientHeight    =   4800
   ClientLeft      =   60
   ClientTop       =   345
   ClientWidth     =   9000
   LinkTopic       =   "frmProducts"
   MDIChild        =   -1  'True
   ScaleHeight     =   4800
   ScaleWidth      =   9000
   StartUpPosition =   2  'CenterScreen
   Begin VB.TextBox txtProductID 
      Height          =   285
      Left            =   120
      TabIndex        =   12
      Top             =   120
      Visible         =   0   'False
      Width           =   855
   End
   Begin VB.TextBox txtSearch 
      Height          =   315
      Left            =   480
      TabIndex        =   0
      Top             =   480
      Width           =   2655
   End
   Begin VB.ListBox lstResults 
      Height          =   3975
      Left            =   120
      TabIndex        =   11
      Top             =   840
      Width           =   3615
   End
   Begin VB.TextBox txtBarcode 
      Height          =   315
      Left            =   4200
      TabIndex        =   1
      Top             =   840
      Width           =   1935
   End
   Begin VB.TextBox txtName 
      Height          =   315
      Left            =   4200
      TabIndex        =   2
      Top             =   1320
      Width           =   1935
   End
   Begin VB.TextBox txtCategory 
      Height          =   315
      Left            =   4200
      TabIndex        =   3
      Top             =   1800
      Width           =   1935
   End
   Begin VB.TextBox txtCost 
      Height          =   315
      Left            =   4200
      TabIndex        =   4
      Top             =   2280
      Width           =   915
   End
   Begin VB.TextBox txtPrice 
      Height          =   315
      Left            =   5220
      TabIndex        =   5
      Top             =   2280
      Width           =   915
   End
   Begin VB.TextBox txtVAT 
      Height          =   315
      Left            =   4200
      TabIndex        =   6
      Top             =   2760
      Width           =   915
   End
   Begin VB.TextBox txtStock 
      Height          =   315
      Left            =   5220
      TabIndex        =   7
      Top             =   2760
      Width           =   915
   End
   Begin VB.CheckBox chkActive 
      Caption         =   "فعّال"
      Height          =   255
      Left            =   4200
      TabIndex        =   8
      Top             =   3240
      Width           =   975
   End
   Begin VB.CommandButton cmdNew 
      Caption         =   "جديد"
      Height          =   375
      Left            =   4200
      TabIndex        =   9
      Top             =   3720
      Width           =   855
   End
   Begin VB.CommandButton cmdSave 
      Caption         =   "حفظ"
      Height          =   375
      Left            =   5145
      TabIndex        =   10
      Top             =   3720
      Width           =   855
   End
   Begin VB.CommandButton cmdDelete 
      Caption         =   "حذف"
      Height          =   375
      Left            =   6090
      TabIndex        =   13
      Top             =   3720
      Width           =   855
   End
   Begin VB.Label lblSearch 
      Caption         =   "بحث"
      Height          =   255
      Left            =   3180
      TabIndex        =   18
      Top             =   480
      Width           =   495
   End
   Begin VB.Label lblBarcode 
      Caption         =   "باركود"
      Height          =   255
      Left            =   6240
      TabIndex        =   17
      Top             =   840
      Width           =   615
   End
   Begin VB.Label lblName 
      Caption         =   "الاسم"
      Height          =   255
      Left            =   6240
      TabIndex        =   16
      Top             =   1320
      Width           =   615
   End
   Begin VB.Label lblCategory 
      Caption         =   "التصنيف"
      Height          =   255
      Left            =   6240
      TabIndex        =   15
      Top             =   1800
      Width           =   735
   End
   Begin VB.Label lblPricing 
      Caption         =   "التكلفة/السعر"
      Height          =   255
      Left            =   6240
      TabIndex        =   14
      Top             =   2280
      Width           =   1095
   End
   Begin VB.Label lblVATStock 
      Caption         =   "الضريبة/المخزون"
      Height          =   255
      Left            =   6240
      TabIndex        =   19
      Top             =   2760
      Width           =   1215
   End
End
Attribute VB_Name = "frmProducts"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private Sub Form_Load()
	ApplyFormRTL Me
	LoadProducts
End Sub

Private Sub txtSearch_Change()
	LoadProducts
End Sub

Private Sub lstResults_Click()
	LoadSelectedProduct
End Sub

Private Sub cmdNew_Click()
	ClearFields
End Sub

Private Sub cmdSave_Click()
	SaveProduct
End Sub

Private Sub cmdDelete_Click()
	DeleteProduct
End Sub

Private Sub LoadProducts()
	Dim rs As ADODB.Recordset
	Dim sql As String
	Dim filter As String
	filter = Trim$(txtSearch.Text)
	sql = "SELECT ProductID, Name, Barcode FROM Products WHERE 1=1"
	If Len(filter) > 0 Then
		sql = sql & " AND (Name LIKE " & Quote("%" & filter & "%") & _
			" OR Barcode LIKE " & Quote("%" & filter & "%") & ")"
	End If
	sql = sql & " ORDER BY Name"
	Set rs = gConnection.Execute(sql)
	lstResults.Clear
	Do While Not rs.EOF
		lstResults.AddItem rs!Name & IIf(Nz(rs!Barcode) <> "", " (" & rs!Barcode & ")", "")
		lstResults.ItemData(lstResults.NewIndex) = rs!ProductID
		rs.MoveNext
	Loop
	If Not rs Is Nothing Then rs.Close
	Set rs = Nothing
End Sub

Private Sub LoadSelectedProduct()
	Dim id As Long
	Dim rs As ADODB.Recordset
	Dim sql As String
	If lstResults.ListIndex < 0 Then Exit Sub
	id = lstResults.ItemData(lstResults.ListIndex)
	sql = "SELECT * FROM Products WHERE ProductID=" & CStr(id)
	Set rs = gConnection.Execute(sql)
	If Not rs.EOF Then
		txtProductID.Text = CStr(rs!ProductID)
		txtBarcode.Text = NzText(rs!Barcode)
		txtName.Text = NzText(rs!Name)
		txtCategory.Text = NzText(rs!Category)
		txtCost.Text = CStr(NzNum(rs!Cost))
		txtPrice.Text = CStr(NzNum(rs!Price))
		txtVAT.Text = CStr(NzNum(rs!VATPercent))
		txtStock.Text = CStr(NzNum(rs!Stock))
		chkActive.Value = IIf(rs!IsActive, 1, 0)
	End If
	If Not rs Is Nothing Then rs.Close
	Set rs = Nothing
End Sub

Private Sub SaveProduct()
	Dim isNew As Boolean
	Dim sql As String
	Dim activeVal As String
	isNew = (Len(Trim$(txtProductID.Text)) = 0)
	activeVal = IIf(chkActive.Value = 1, "TRUE", "FALSE")
	If isNew Then
		sql = "INSERT INTO Products (Barcode, Name, Category, Cost, Price, VATPercent, Stock, IsActive) VALUES (" & _
			IIf(Len(Trim$(txtBarcode.Text))>0, Quote(txtBarcode.Text), "NULL") & ", " & _
			Quote(txtName.Text) & ", " & _
			IIf(Len(Trim$(txtCategory.Text))>0, Quote(txtCategory.Text), "NULL") & ", " & _
			CStr(Val(txtCost.Text)) & ", " & _
			CStr(Val(txtPrice.Text)) & ", " & _
			CStr(Val(txtVAT.Text)) & ", " & _
			CStr(Val(txtStock.Text)) & ", " & _
			activeVal & ")"
		ExecNonQuery sql
	Else
		sql = "UPDATE Products SET " & _
			"Barcode = " & IIf(Len(Trim$(txtBarcode.Text))>0, Quote(txtBarcode.Text), "NULL") & ", " & _
			"Name = " & Quote(txtName.Text) & ", " & _
			"Category = " & IIf(Len(Trim$(txtCategory.Text))>0, Quote(txtCategory.Text), "NULL") & ", " & _
			"Cost = " & CStr(Val(txtCost.Text)) & ", " & _
			"Price = " & CStr(Val(txtPrice.Text)) & ", " & _
			"VATPercent = " & CStr(Val(txtVAT.Text)) & ", " & _
			"Stock = " & CStr(Val(txtStock.Text)) & ", " & _
			"IsActive = " & activeVal & _
			" WHERE ProductID = " & txtProductID.Text
		ExecNonQuery sql
	End If
	LoadProducts
	ClearFields
End Sub

Private Sub DeleteProduct()
	If Len(Trim$(txtProductID.Text)) = 0 Then Exit Sub
	ExecNonQuery "DELETE FROM Products WHERE ProductID = " & txtProductID.Text
	LoadProducts
	ClearFields
End Sub

Private Sub ClearFields()
	txtProductID.Text = ""
	txtBarcode.Text = ""
	txtName.Text = ""
	txtCategory.Text = ""
	txtCost.Text = "0"
	txtPrice.Text = "0"
	txtVAT.Text = "0"
	txtStock.Text = "0"
	chkActive.Value = 1
End Sub

Private Function NzText(v) As String
	If IsNull(v) Then NzText = "" Else NzText = CStr(v)
End Function

Private Function NzNum(v) As Double
	If IsNull(v) Then NzNum = 0 Else NzNum = CDbl(v)
End Function