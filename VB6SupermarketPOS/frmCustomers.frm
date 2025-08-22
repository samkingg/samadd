VERSION 5.00
Begin VB.Form frmCustomers 
   Caption         =   "إدارة العملاء"
   ClientHeight    =   4200
   ClientLeft      =   60
   ClientTop       =   345
   ClientWidth     =   8000
   LinkTopic       =   "frmCustomers"
   MDIChild        =   -1  'True
   ScaleHeight     =   4200
   ScaleWidth      =   8000
   StartUpPosition =   2  'CenterScreen
   Begin VB.TextBox txtCustomerID 
      Height          =   285
      Left            =   120
      TabIndex        =   10
      Top             =   120
      Visible         =   0   'False
      Width           =   855
   End
   Begin VB.TextBox txtSearch 
      Height          =   315
      Left            =   480
      TabIndex        =   0
      Top             =   480
      Width           =   2415
   End
   Begin VB.ListBox lstResults 
      Height          =   3375
      Left            =   120
      TabIndex        =   9
      Top             =   840
      Width           =   3375
   End
   Begin VB.TextBox txtName 
      Height          =   315
      Left            =   3900
      TabIndex        =   1
      Top             =   840
      Width           =   1935
   End
   Begin VB.TextBox txtPhone 
      Height          =   315
      Left            =   3900
      TabIndex        =   2
      Top             =   1320
      Width           =   1935
   End
   Begin VB.TextBox txtAddress 
      Height          =   855
      Left            =   3900
      MultiLine       =   -1  'True
      TabIndex        =   3
      Top             =   1800
      Width           =   1935
   End
   Begin VB.CheckBox chkActive 
      Caption         =   "فعّال"
      Height          =   255
      Left            =   3900
      TabIndex        =   4
      Top             =   2760
      Width           =   975
   End
   Begin VB.CommandButton cmdNew 
      Caption         =   "جديد"
      Height          =   375
      Left            =   3900
      TabIndex        =   5
      Top             =   3240
      Width           =   855
   End
   Begin VB.CommandButton cmdSave 
      Caption         =   "حفظ"
      Height          =   375
      Left            =   4845
      TabIndex        =   6
      Top             =   3240
      Width           =   855
   End
   Begin VB.CommandButton cmdDelete 
      Caption         =   "حذف"
      Height          =   375
      Left            =   5790
      TabIndex        =   7
      Top             =   3240
      Width           =   855
   End
   Begin VB.Label lblSearch 
      Caption         =   "بحث"
      Height          =   255
      Left            =   2940
      TabIndex        =   11
      Top             =   480
      Width           =   495
   End
End
Attribute VB_Name = "frmCustomers"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private Sub Form_Load()
	ApplyFormRTL Me
	LoadCustomers
End Sub

Private Sub txtSearch_Change()
	LoadCustomers
End Sub

Private Sub lstResults_Click()
	LoadSelectedCustomer
End Sub

Private Sub cmdNew_Click()
	ClearFields
End Sub

Private Sub cmdSave_Click()
	SaveCustomer
End Sub

Private Sub cmdDelete_Click()
	DeleteCustomer
End Sub

Private Sub LoadCustomers()
	Dim rs As ADODB.Recordset
	Dim sql As String
	Dim filter As String
	filter = Trim$(txtSearch.Text)
	sql = "SELECT CustomerID, Name, Phone FROM Customers WHERE 1=1"
	If Len(filter) > 0 Then
		sql = sql & " AND (Name LIKE " & Quote("%" & filter & "%") & _
			" OR Phone LIKE " & Quote("%" & filter & "%") & ")"
	End If
	sql = sql & " ORDER BY Name"
	Set rs = gConnection.Execute(sql)
	lstResults.Clear
	Do While Not rs.EOF
		lstResults.AddItem rs!Name & IIf(Nz(rs!Phone) <> "", " (" & rs!Phone & ")", "")
		lstResults.ItemData(lstResults.NewIndex) = rs!CustomerID
		rs.MoveNext
	Loop
	If Not rs Is Nothing Then rs.Close
	Set rs = Nothing
End Sub

Private Sub LoadSelectedCustomer()
	Dim id As Long
	Dim rs As ADODB.Recordset
	Dim sql As String
	If lstResults.ListIndex < 0 Then Exit Sub
	id = lstResults.ItemData(lstResults.ListIndex)
	sql = "SELECT * FROM Customers WHERE CustomerID=" & CStr(id)
	Set rs = gConnection.Execute(sql)
	If Not rs.EOF Then
		txtCustomerID.Text = CStr(rs!CustomerID)
		txtName.Text = NzText(rs!Name)
		txtPhone.Text = NzText(rs!Phone)
		txtAddress.Text = NzText(rs!Address)
		chkActive.Value = IIf(rs!IsActive, 1, 0)
	End If
	If Not rs Is Nothing Then rs.Close
	Set rs = Nothing
End Sub

Private Sub SaveCustomer()
	Dim isNew As Boolean
	Dim sql As String
	Dim activeVal As String
	isNew = (Len(Trim$(txtCustomerID.Text)) = 0)
	activeVal = IIf(chkActive.Value = 1, "TRUE", "FALSE")
	If isNew Then
		sql = "INSERT INTO Customers (Name, Phone, Address, IsActive) VALUES (" & _
			Quote(txtName.Text) & ", " & _
			IIf(Len(Trim$(txtPhone.Text))>0, Quote(txtPhone.Text), "NULL") & ", " & _
			IIf(Len(Trim$(txtAddress.Text))>0, Quote(txtAddress.Text), "NULL") & ", " & _
			activeVal & ")"
		ExecNonQuery sql
	Else
		sql = "UPDATE Customers SET " & _
			"Name = " & Quote(txtName.Text) & ", " & _
			"Phone = " & IIf(Len(Trim$(txtPhone.Text))>0, Quote(txtPhone.Text), "NULL") & ", " & _
			"Address = " & IIf(Len(Trim$(txtAddress.Text))>0, Quote(txtAddress.Text), "NULL") & ", " & _
			"IsActive = " & activeVal & _
			" WHERE CustomerID = " & txtCustomerID.Text
		ExecNonQuery sql
	End If
	LoadCustomers
	ClearFields
End Sub

Private Sub DeleteCustomer()
	If Len(Trim$(txtCustomerID.Text)) = 0 Then Exit Sub
	ExecNonQuery "DELETE FROM Customers WHERE CustomerID = " & txtCustomerID.Text
	LoadCustomers
	ClearFields
End Sub

Private Sub ClearFields()
	txtCustomerID.Text = ""
	txtName.Text = ""
	txtPhone.Text = ""
	txtAddress.Text = ""
	chkActive.Value = 1
End Sub

Private Function NzText(v) As String
	If IsNull(v) Then NzText = "" Else NzText = CStr(v)
End Function

Private Function Nz(v) As String
	If IsNull(v) Then Nz = "" Else Nz = CStr(v)
End Function