VERSION 5.00
Begin VB.MDIForm frmMain 
   BackColor       =   &H8000000C&
   Caption         =   "نظام كاشير السوبرماركت"
   ClientHeight    =   6000
   ClientLeft      =   60
   ClientTop       =   360
   ClientWidth     =   9000
   LinkTopic       =   "MDIForm1"
   StartUpPosition =   2  'CenterScreen
   Begin VB.Menu mnuFile 
      Caption         =   "ملف"
      Begin VB.Menu mnuPOS 
         Caption         =   "نقطة البيع"
      End
      Begin VB.Menu mnuProducts 
         Caption         =   "الأصناف"
      End
      Begin VB.Menu mnuPurchase 
         Caption         =   "إدخال بضاعة"
      End
      Begin VB.Menu mnuCustomers 
         Caption         =   "العملاء"
      End
      Begin VB.Menu mnuInvoices 
         Caption         =   "الفواتير"
      End
      Begin VB.Menu mnuReports 
         Caption         =   "التقارير"
      End
      Begin VB.Menu mnuSep1 
         Caption         =   "-"
      End
      Begin VB.Menu mnuExit 
         Caption         =   "خروج"
      End
   End
End
Attribute VB_Name = "frmMain"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private Sub MDIForm_Load()
	ApplyFormRTL Me
End Sub

Private Sub mnuExit_Click()
	Unload Me
End Sub

Private Sub mnuProducts_Click()
	Load frmProducts
	frmProducts.MDIChild = True
	frmProducts.Show
End Sub

Private Sub mnuCustomers_Click()
	Load frmCustomers
	frmCustomers.MDIChild = True
	frmCustomers.Show
End Sub

Private Sub mnuPurchase_Click()
	Load frmPurchase
	frmPurchase.MDIChild = True
	frmPurchase.Show
End Sub

Private Sub mnuInvoices_Click()
	Load frmInvoices
	frmInvoices.MDIChild = True
	frmInvoices.Show
End Sub