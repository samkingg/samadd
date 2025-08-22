VERSION 5.00
Begin VB.Form frmPOS 
   Caption         =   "نقطة البيع"
   ClientHeight    =   6000
   ClientLeft      =   60
   ClientTop       =   345
   ClientWidth     =   9000
   LinkTopic       =   "frmPOS"
   MDIChild        =   -1  'True
   ScaleHeight     =   6000
   ScaleWidth      =   9000
   StartUpPosition =   2  'CenterScreen
   Begin VB.TextBox txtBarcode 
      Height          =   315
      Left            =   360
      TabIndex        =   0
      Top             =   360
      Width           =   2055
   End
   Begin VB.TextBox txtQty 
      Height          =   315
      Left            =   360
      TabIndex        =   1
      Top             =   840
      Width           =   735
   End
   Begin VB.CommandButton cmdAdd 
      Caption         =   "إضافة"
      Height          =   375
      Left            =   360
      TabIndex        =   2
      Top             =   1320
      Width           =   1095
   End
   Begin VB.ListBox lstCart 
      Height          =   4095
      Left            =   3600
      TabIndex        =   3
      Top             =   360
      Width           =   5175
   End
   Begin VB.CommandButton cmdRemove 
      Caption         =   "حذف المحدد"
      Height          =   375
      Left            =   3600
      TabIndex        =   4
      Top             =   4560
      Width           =   1455
   End
   Begin VB.TextBox txtDiscount 
      Height          =   315
      Left            =   360
      TabIndex        =   5
      Top             =   2160
      Width           =   1095
   End
   Begin VB.Frame fraPayment 
      Caption         =   "الدفع"
      Height          =   1335
      Left            =   360
      TabIndex        =   6
      Top             =   2640
      Width           =   2655
      Begin VB.OptionButton optCash 
         Caption         =   "نقدي"
         Height          =   255
         Left            =   240
         TabIndex        =   7
         Top             =   360
         Value           =   -1  'True
         Width           =   855
      End
      Begin VB.OptionButton optCard 
         Caption         =   "بطاقة"
         Height          =   255
         Left            =   1200
         TabIndex        =   8
         Top             =   360
         Width           =   855
      End
      Begin VB.TextBox txtPaid 
         Height          =   315
         Left            =   240
         TabIndex        =   9
         Top             =   780
         Width           =   1095
      End
      Begin VB.Label lblPaid 
         Caption         =   "المدفوع"
         Height          =   255
         Left            =   1440
         TabIndex        =   10
         Top             =   780
         Width           =   975
      End
   End
   Begin VB.CommandButton cmdPayPrint 
      Caption         =   "دفع وطباعة"
      Height          =   495
      Left            =   360
      TabIndex        =   11
      Top             =   4140
      Width           =   1455
   End
   Begin VB.CommandButton cmdClear 
      Caption         =   "تفريغ"
      Height          =   375
      Left            =   1920
      TabIndex        =   12
      Top             =   4200
      Width           =   975
   End
   Begin VB.Label lblBarcode 
      Caption         =   "باركود/بحث"
      Height          =   255
      Left            =   2460
      TabIndex        =   13
      Top             =   360
      Width           =   1095
   End
   Begin VB.Label lblQty 
      Caption         =   "الكمية"
      Height          =   255
      Left            =   2460
      TabIndex        =   14
      Top             =   840
      Width           =   615
   End
   Begin VB.Label lblDiscount 
      Caption         =   "خصم (عملة)"
      Height          =   255
      Left            =   1620
      TabIndex        =   15
      Top             =   2160
      Width           =   915
   End
   Begin VB.Label lblSubtotal 
      Caption         =   "المجموع: 0"
      Height          =   255
      Left            =   3600
      TabIndex        =   16
      Top             =   5040
      Width           =   1575
   End
   Begin VB.Label lblVAT 
      Caption         =   "الضريبة: 0"
      Height          =   255
      Left            =   5280
      TabIndex        =   17
      Top             =   5040
      Width           =   1575
   End
   Begin VB.Label lblTotal 
      Caption         =   "الإجمالي: 0"
      Height          =   255
      Left            =   6960
      TabIndex        =   18
      Top             =   5040
      Width           =   1815
   End
   Begin VB.Label lblChange 
      Caption         =   "الباقي: 0"
      Height          =   255
      Left            =   360
      TabIndex        =   19
      Top             =   4680
      Width           =   1575
   End
End
Attribute VB_Name = "frmPOS"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private Type SaleLine
	ProductID As Long
	Name As String
	Barcode As String
	Quantity As Double
	UnitPrice As Currency
	VATPercent As Double
End Type

Private Cart() As SaleLine
Private CartCount As Long

Private Sub Form_Load()
	ApplyFormRTL Me
	Cart_Clear
	txtQty.Text = "1"
	txtDiscount.Text = "0"
	txtPaid.Text = "0"
End Sub

Private Sub cmdAdd_Click()
	AddByBarcode Trim$(txtBarcode.Text), Val(txtQty.Text)
End Sub

Private Sub cmdRemove_Click()
	If lstCart.ListIndex < 0 Then Exit Sub
	Cart_RemoveAt lstCart.ListIndex + 1
	RefreshCartList
	RecomputeTotals
End Sub

Private Sub cmdClear_Click()
	Cart_Clear
	RefreshCartList
	RecomputeTotals
End Sub

Private Sub cmdPayPrint_Click()
	If CartCount = 0 Then
		MsgBox "السلة فارغة", vbExclamation, App.Title
		Exit Sub
	End If
	Dim subtotal As Currency, vat As Currency, discount As Currency, grand As Currency
	ComputeTotals subtotal, vat, discount, grand
	Dim paid As Currency
	paid = CCur(Val(txtPaid.Text))
	If optCash.Value And paid < grand Then
		MsgBox "المبلغ المدفوع أقل من الإجمالي", vbExclamation, App.Title
		Exit Sub
	End If
	Dim saleId As Long
	If Not SaveSale(subtotal, discount, vat, grand, paid, saleId) Then
		MsgBox "تعذر حفظ الفاتورة", vbCritical, App.Title
		Exit Sub
	End If
	PrintReceipt saleId, subtotal, discount, vat, grand, paid
	Cart_Clear
	RefreshCartList
	RecomputeTotals
	MsgBox "تم الدفع والحفظ بنجاح", vbInformation, App.Title
End Sub

Private Sub txtPaid_Change()
	Dim subtotal As Currency, vat As Currency, discount As Currency, grand As Currency
	ComputeTotals subtotal, vat, discount, grand
	Dim paid As Currency
	paid = CCur(Val(txtPaid.Text))
	lblChange.Caption = "الباقي: " & FormatCurrency(paid - grand)
End Sub

Private Sub AddByBarcode(ByVal code As String, ByVal qty As Double)
	If Len(code) = 0 Or qty <= 0 Then Exit Sub
	Dim rs As ADODB.Recordset
	Dim sql As String
	sql = "SELECT ProductID, Name, Barcode, Price, VATPercent FROM Products WHERE IsActive = TRUE AND (Barcode = " & Quote(code) & _
		" OR Name LIKE " & Quote("%" & code & "%") & ")"
	Set rs = gConnection.Execute(sql)
	If rs.EOF Then
		MsgBox "الصنف غير موجود", vbCritical, App.Title
		Exit Sub
	End If
	Dim line As SaleLine
	line.ProductID = rs!ProductID
	line.Name = rs!Name
	line.Barcode = NzText(rs!Barcode)
	line.Quantity = qty
	line.UnitPrice = rs!Price
	line.VATPercent = NzNum(rs!VATPercent)
	Cart_Add line
	If Not rs Is Nothing Then rs.Close
	Set rs = Nothing
	RefreshCartList
	RecomputeTotals
End Sub

Private Sub RefreshCartList()
	Dim i As Long
	lstCart.Clear
	For i = 1 To CartCount
		Dim lineTotal As Currency
		lineTotal = CCur(Cart(i).Quantity) * Cart(i).UnitPrice
		lstCart.AddItem Cart(i).Name & " x " & CStr(Cart(i).Quantity) & " @ " & FormatCurrency(Cart(i).UnitPrice) & " = " & FormatCurrency(lineTotal)
	Next i
End Sub

Private Sub RecomputeTotals()
	Dim subtotal As Currency, vat As Currency, discount As Currency, grand As Currency
	ComputeTotals subtotal, vat, discount, grand
	lblSubtotal.Caption = "المجموع: " & FormatCurrency(subtotal)
	lblVAT.Caption = "الضريبة: " & FormatCurrency(vat)
	lblTotal.Caption = "الإجمالي: " & FormatCurrency(grand)
	Dim paid As Currency
	paid = CCur(Val(txtPaid.Text))
	lblChange.Caption = "الباقي: " & FormatCurrency(paid - grand)
End Sub

Private Sub ComputeTotals(ByRef subtotal As Currency, ByRef vat As Currency, ByRef discount As Currency, ByRef grand As Currency)
	Dim i As Long
	subtotal = 0
	vat = 0
	For i = 1 To CartCount
		Dim lineSub As Currency
		lineSub = CCur(Cart(i).Quantity) * Cart(i).UnitPrice
		subtotal = subtotal + lineSub
		vat = vat + (lineSub * CCur(Cart(i).VATPercent) / 100)
	Next i
	discount = CCur(Val(txtDiscount.Text))
	If discount < 0 Then discount = 0
	grand = subtotal - discount + vat
	If grand < 0 Then grand = 0
End Sub

Private Function SaveSale(ByVal subtotal As Currency, ByVal discount As Currency, ByVal vat As Currency, ByVal grand As Currency, ByVal paid As Currency, ByRef saleId As Long) As Boolean
	On Error GoTo EH
	Dim saleNumber As String
	saleNumber = "S" & Format$(Now, "yyyymmddhhnnss")
	ExecNonQuery "INSERT INTO Sales (SaleNumber, SaleDate, UserID, CustomerID, Subtotal, Discount, VAT, GrandTotal, Paid, Change) VALUES (" & _
		Quote(saleNumber) & ", NOW(), 1, NULL, " & _
		CStr(subtotal) & ", " & CStr(discount) & ", " & CStr(vat) & ", " & CStr(grand) & ", " & CStr(paid) & ", " & CStr(paid - grand) & ")"
	Dim rs As ADODB.Recordset
	Set rs = gConnection.Execute("SELECT @@IDENTITY AS NewID")
	If rs.EOF Then GoTo EH
	saleId = rs!NewID
	If Not rs Is Nothing Then rs.Close
	Set rs = Nothing
	Dim i As Long
	For i = 1 To CartCount
		Dim lineSub As Currency
		lineSub = CCur(Cart(i).Quantity) * Cart(i).UnitPrice
		ExecNonQuery "INSERT INTO SaleItems (SaleID, ProductID, Quantity, UnitPrice, VATPercent, LineTotal) VALUES (" & _
			CStr(saleId) & ", " & CStr(Cart(i).ProductID) & ", " & CStr(Cart(i).Quantity) & ", " & CStr(Cart(i).UnitPrice) & ", " & CStr(Cart(i).VATPercent) & ", " & CStr(lineSub) & ")"
		ExecNonQuery "UPDATE Products SET Stock = Stock - " & CStr(Cart(i).Quantity) & " WHERE ProductID = " & CStr(Cart(i).ProductID)
		ExecNonQuery "INSERT INTO InventoryMovements (ProductID, MovementDate, Quantity, Reason) VALUES (" & CStr(Cart(i).ProductID) & ", NOW(), -" & CStr(Cart(i).Quantity) & ", 'Sale')"
	Next i
	Dim method As String
	method = IIf(optCash.Value, "Cash", "Card")
	ExecNonQuery "INSERT INTO Payments (SaleID, Method, Amount, Notes) VALUES (" & CStr(saleId) & ", " & Quote(method) & ", " & CStr(paid) & ", NULL)"
	SaveSale = True
	Exit Function
EH:
	SaveSale = False
End Function

Private Sub PrintReceipt(ByVal saleId As Long, ByVal subtotal As Currency, ByVal discount As Currency, ByVal vat As Currency, ByVal grand As Currency, ByVal paid As Currency)
	On Error Resume Next
	Printer.FontSize = 10
	Printer.Print "===== فاتورة بيع ====="
	Printer.Print "رقم: " & CStr(saleId) & "    التاريخ: " & Format$(Now, "yyyy-mm-dd hh:nn")
	Printer.Print String$(32, "-")
	Dim i As Long
	For i = 1 To CartCount
		Printer.Print Left$(Cart(i).Name, 20) & " x" & FormatNumber(Cart(i).Quantity, 2) & _
			" @" & FormatCurrency(Cart(i).UnitPrice) & " = " & FormatCurrency(CCur(Cart(i).Quantity) * Cart(i).UnitPrice)
	Next i
	Printer.Print String$(32, "-")
	Printer.Print "المجموع: " & FormatCurrency(subtotal)
	Printer.Print "الخصم: " & FormatCurrency(discount)
	Printer.Print "الضريبة: " & FormatCurrency(vat)
	Printer.Print "الإجمالي: " & FormatCurrency(grand)
	Printer.Print "المدفوع: " & FormatCurrency(paid)
	Printer.Print "الباقي: " & FormatCurrency(paid - grand)
	Printer.EndDoc
End Sub

Private Sub Cart_Clear()
	CartCount = 0
	Erase Cart
End Sub

Private Sub Cart_Add(ByRef line As SaleLine)
	CartCount = CartCount + 1
	ReDim Preserve Cart(1 To CartCount) As SaleLine
	Cart(CartCount) = line
End Sub

Private Sub Cart_RemoveAt(ByVal index1 As Long)
	Dim i As Long
	If index1 < 1 Or index1 > CartCount Then Exit Sub
	For i = index1 To CartCount - 1
		Cart(i) = Cart(i + 1)
	Next i
	CartCount = CartCount - 1
	If CartCount = 0 Then
		Erase Cart
	Else
		ReDim Preserve Cart(1 To CartCount) As SaleLine
	End If
End Sub

Private Function NzText(v) As String
	If IsNull(v) Then NzText = "" Else NzText = CStr(v)
End Function

Private Function NzNum(v) As Double
	If IsNull(v) Then NzNum = 0 Else NzNum = CDbl(v)
End Function