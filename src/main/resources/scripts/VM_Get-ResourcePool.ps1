Param (
    [string]$datastore
)

$listPool = (Get-ResourcePool -Location $datastore | Select-Object Parent, Name | Sort-Object Parent | foreach-object { "" + $( $_.Parent ) + "/" + $( $_.Name ) })
$listPool