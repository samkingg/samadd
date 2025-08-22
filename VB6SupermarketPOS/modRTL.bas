Attribute VB_Name = "modRTL"
Option Explicit

Private Declare Function GetWindowLong Lib "user32" Alias "GetWindowLongA" (ByVal hwnd As Long, ByVal nIndex As Long) As Long
Private Declare Function SetWindowLong Lib "user32" Alias "SetWindowLongA" (ByVal hwnd As Long, ByVal nIndex As Long, ByVal dwNewLong As Long) As Long

Private Const GWL_EXSTYLE As Long = (-20)
Private Const WS_EX_LAYOUTRTL As Long = &H400000
Private Const WS_EX_RTLREADING As Long = &H2000
Private Const WS_EX_NOINHERITLAYOUT As Long = &H100000

Public Sub ApplyFormRTL(f As Form)
	Dim ex As Long
	ex = GetWindowLong(f.hwnd, GWL_EXSTYLE)
	ex = ex Or WS_EX_LAYOUTRTL Or WS_EX_RTLREADING
	ex = ex And (Not WS_EX_NOINHERITLAYOUT)
	SetWindowLong f.hwnd, GWL_EXSTYLE, ex
End Sub