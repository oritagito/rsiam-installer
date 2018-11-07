$ListFolder = Get-Folder | Select-Object Parent, Name | Sort-Object Parent | foreach-object { "" + $( $_.Parent ) + "/" + $( $_.Name ) }
$ListFolder