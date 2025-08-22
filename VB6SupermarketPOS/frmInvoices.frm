VERSION 5.00
Begin VB.Form frmInvoices 
   Caption         =   "قائمة الفواتير"
   ClientHeight    =   4800
   ClientLeft      =   60
   ClientTop       =   345
   ClientWidth     =   9000
   LinkTopic       =   "frmInvoices"
   MDIChild        =   -1  'True
   ScaleHeight     =   4800
   ScaleWidth      =   9000
   StartUpPosition =   2  'CenterScreen
   Begin VB.TextBox txtFrom 
      Height          =   315
      Left            =   360
      TabIndex        =   0
      Top             =   480
      Width           =   1455
   End
   Begin VB.TextBox txtTo 
      Height          =   315
      Left            =   360
      TabIndex        =   1
      Top             =   960
      Width           =   1455
   End
   Begin VB.CommandButton cmdLoad 
      Caption         =   "تحميل"
      Height          =   375
      Left            =   1980
      TabIndex        =   2
      Top             =   720
      Width           =   975
   End
   Begin VB.ListBox lstInvoices 
      Height          =   3495
      Left            =   360
      TabIndex        =   3
      Top             =   1440
      Width           =   8295
   End
   Begin VB.Label lblFrom 
      Caption         =   "من (YYYY-MM-DD)"
      Height          =   255
      Left            =   1860
      TabIndex        =   4
      Top             =   480
      Width           =   1455
   End
   Begin VB.Label lblTo 
      Caption         =   "إلى (YYYY-MM-DD)"
      Height          =   255
      Left            =   1860
      TabIndex        =   5
      Top             =   960
      Width           =   1455
   End
End
Attribute VB_Name = "frmInvoices"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private Sub Form_Load()
	ApplyFormRTL Me
	Dim today As String
	today = Format$(Date, "yyyy-mm-dd")
	txtFrom.Text = today
	txtTo.Text = today
	LoadInvoices
End Sub

Private Sub cmdLoad_Click()
	LoadInvoices
End Sub

Private Sub LoadInvoices()
	Dim rs As ADODB.Recordset
	Dim sql As String
	Dim d1 As String, d2 As String
	d1 = txtFrom.Text
	d2 = txtTo.Text
	If Len(d1) = 0 Or Len(d2) = 0 Then Exit Sub
	sql = "SELECT SaleID, SaleNumber, SaleDate, GrandTotal FROM Sales WHERE SaleDate >= " & Quote(d1 & " 00:00:00") & _
		" AND SaleDate <= " & Quote(d2 & " 23:59:59") & " ORDER BY SaleDate DESC"
	Set rs = gConnection.Execute(sql)
	lstInvoices.Clear
	Do While Not rs.EOF
		lstInvoices.AddItem CStr(rs!SaleID) & " - " & rs!SaleNumber & " - " & Format$(rs!SaleDate, "yyyy-mm-dd hh:nn") & " - " & FormatCurrency(rs!GrandTotal)
		rs.MoveNext
	Loop
	If Not rs Is Nothing Then rs.Close
	Set rs = Nothing
End Sub