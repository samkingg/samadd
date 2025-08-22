Attribute VB_Name = "modDatabase"
Option Explicit

' References needed:
' - Microsoft ActiveX Data Objects 2.x Library
' - Microsoft ADO Ext. 2.x for DDL and Security (ADOX)

Private Const DATABASE_FOLDER As String = "data"
Private Const DATABASE_FILE As String = "Supermarket.mdb"

Public gConnection As ADODB.Connection

Public Function Database_Ensure() As Boolean
	On Error GoTo EH
	Dim dbPath As String
	Dim dataFolder As String
	
	dataFolder = AppPath & DATABASE_FOLDER
	If Dir$(dataFolder, vbDirectory) = vbNullString Then
		MkDir dataFolder
	End If
	
	dbPath = dataFolder & "\\" & DATABASE_FILE
	If Dir$(dbPath, vbNormal) = vbNullString Then
		If Not Database_Create(dbPath) Then GoTo EH
	End If
	
	If gConnection Is Nothing Then
		Set gConnection = New ADODB.Connection
		gConnection.Open JetConnString(dbPath)
	End If
	
	Database_Ensure = True
	Exit Function
EH:
	Database_Ensure = False
End Function

Private Function Database_Create(ByVal dbPath As String) As Boolean
	On Error GoTo EH
	Dim cat As ADOX.Catalog
	Set cat = New ADOX.Catalog
	
	cat.Create "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & dbPath & ";Jet OLEDB:Engine Type=5;"
	
	Set gConnection = New ADODB.Connection
	gConnection.Open JetConnString(dbPath)
	
	Call CreateSchema
	Call SeedDefaults
	
	Database_Create = True
	Exit Function
EH:
	Database_Create = False
End Function

Private Sub CreateSchema()
	ExecNonQuery "CREATE TABLE Users (" & _
		"UserID AUTOINCREMENT PRIMARY KEY, " & _
		"Username TEXT(50) NOT NULL UNIQUE, " & _
		"PasswordHash TEXT(200) NOT NULL, " & _
		"FullName TEXT(100) NULL, " & _
		"IsAdmin YESNO NOT NULL)"
	
	ExecNonQuery "CREATE TABLE Products (" & _
		"ProductID AUTOINCREMENT PRIMARY KEY, " & _
		"Barcode TEXT(30) NULL, " & _
		"Name TEXT(100) NOT NULL, " & _
		"Category TEXT(50) NULL, " & _
		"Cost CURRENCY NOT NULL, " & _
		"Price CURRENCY NOT NULL, " & _
		"VATPercent DOUBLE DEFAULT 0, " & _
		"Stock DOUBLE DEFAULT 0, " & _
		"IsActive YESNO NOT NULL)"
	
	ExecNonQuery "CREATE INDEX IX_Products_Barcode ON Products(Barcode)"
	ExecNonQuery "CREATE INDEX IX_Products_Name ON Products(Name)"
	
	ExecNonQuery "CREATE TABLE Customers (" & _
		"CustomerID AUTOINCREMENT PRIMARY KEY, " & _
		"Name TEXT(100) NOT NULL, " & _
		"Phone TEXT(50) NULL, " & _
		"Address TEXT(200) NULL, " & _
		"IsActive YESNO NOT NULL)"
	
	ExecNonQuery "CREATE TABLE Sales (" & _
		"SaleID AUTOINCREMENT PRIMARY KEY, " & _
		"SaleNumber TEXT(30) NOT NULL, " & _
		"SaleDate DATETIME NOT NULL, " & _
		"UserID LONG NOT NULL, " & _
		"CustomerID LONG NULL, " & _
		"Subtotal CURRENCY NOT NULL, " & _
		"Discount CURRENCY NOT NULL, " & _
		"VAT CURRENCY NOT NULL, " & _
		"GrandTotal CURRENCY NOT NULL, " & _
		"Paid CURRENCY NOT NULL, " & _
		"Change CURRENCY NOT NULL)"
	ExecNonQuery "CREATE INDEX IX_Sales_SaleNumber ON Sales(SaleNumber)"
	
	ExecNonQuery "CREATE TABLE SaleItems (" & _
		"SaleItemID AUTOINCREMENT PRIMARY KEY, " & _
		"SaleID LONG NOT NULL, " & _
		"ProductID LONG NOT NULL, " & _
		"Quantity DOUBLE NOT NULL, " & _
		"UnitPrice CURRENCY NOT NULL, " & _
		"VATPercent DOUBLE NOT NULL, " & _
		"LineTotal CURRENCY NOT NULL)"
	
	ExecNonQuery "CREATE TABLE Payments (" & _
		"PaymentID AUTOINCREMENT PRIMARY KEY, " & _
		"SaleID LONG NOT NULL, " & _
		"Method TEXT(20) NOT NULL, " & _
		"Amount CURRENCY NOT NULL, " & _
		"Notes TEXT(200) NULL)"
	
	ExecNonQuery "CREATE TABLE InventoryMovements (" & _
		"MovementID AUTOINCREMENT PRIMARY KEY, " & _
		"ProductID LONG NOT NULL, " & _
		"MovementDate DATETIME NOT NULL, " & _
		"Quantity DOUBLE NOT NULL, " & _
		"Reason TEXT(50) NOT NULL)"
End Sub

Private Sub SeedDefaults()
	Dim sql As String
	Dim pwdHash As String
	pwdHash = SimpleHash("admin")
	
	sql = "INSERT INTO Users (Username, PasswordHash, FullName, IsAdmin) VALUES (" & _
		Quote("admin") & ", " & Quote(pwdHash) & ", " & Quote("المشرف") & ", TRUE)"
	ExecNonQuery sql
End Sub

Public Function Database_ValidateUser(ByVal username As String, ByVal password As String) As Boolean
	On Error GoTo EH
	Dim rs As ADODB.Recordset
	Dim sql As String
	Dim pwdHash As String
	pwdHash = SimpleHash(password)
	
	sql = "SELECT UserID FROM Users WHERE Username = " & Quote(username) & " AND PasswordHash = " & Quote(pwdHash)
	Set rs = gConnection.Execute(sql)
	Database_ValidateUser = (Not rs.EOF)
	If Not rs Is Nothing Then rs.Close
	Set rs = Nothing
	Exit Function
EH:
	Database_ValidateUser = False
End Function

Private Function JetConnString(ByVal dbPath As String) As String
	JetConnString = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & dbPath & ";Persist Security Info=False;Jet OLEDB:Database Password="
End Function

Public Sub ExecNonQuery(ByVal sql As String)
	On Error GoTo EH
	If gConnection Is Nothing Then Err.Raise 91, , "Connection not initialized"
	gConnection.Execute sql
	Exit Sub
EH:
	Err.Raise Err.Number, , Err.Description
End Sub

Public Function Quote(ByVal s As String) As String
	Quote = "'" & Replace(s, "'", "''") & "'"
End Function

Private Function SimpleHash(ByVal s As String) As String
	' NOTE: Simple weak hash for demo; replace with better hashing if available
	Dim i As Long
	Dim v As Long
	v = 5381
	For i = 1 To Len(s)
		v = ((v * 33) And &H7FFFFFFF) Xor Asc(Mid$(s, i, 1))
	Next i
	SimpleHash = CStr(v)
End Function