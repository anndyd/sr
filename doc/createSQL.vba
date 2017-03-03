Sub createSQL()
    Dim myFile As String, rng As Range, cellValue As Variant, iRow As Integer, j As Integer
    myFile = "insert_emp.sql"
    Open myFile For Output As #1
    
    iRow = 2
    Do While Trim(Cells(iRow, 1).Value) <> ""
        Write #1, "insert into tempempdata (empid,badgeid) values ('" + Trim(Cells(iRow, 2).Value) + "','" + Trim(Cells(iRow, 1).Value) + "');"
        iRow = iRow + 1
    Loop
    
    Close #1
    MsgBox "All done. Exported rows: " + CStr(iRow)
    
End Sub
