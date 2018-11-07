Param (
    [string]$macDb,
    [string]$macApp1,
    [string]$macApp2,
    [string]$isoPath
)

$foundDb = Get-VM -Id (Get-VM | Get-NetworkAdapter | Where-Object { $_.MacAddress -like $macDb }).ParentId
$foundApp1 = Get-VM -Id (Get-VM | Get-NetworkAdapter | Where-Object { $_.MacAddress -like $macApp1 }).ParentId
$foundApp2 = Get-VM -Id (Get-VM | Get-NetworkAdapter | Where-Object { $_.MacAddress -like $macApp2 }).ParentId

New-CDDrive -VM $foundDb -ISOPath $isoPath -StartConnected:$True -Confirm:$false
New-CDDrive -VM $foundApp1 -ISOPath $isoPath -StartConnected:$True -Confirm:$false
New-CDDrive -VM $foundApp2 -ISOPath $isoPath -StartConnected:$True -Confirm:$false

Start-vm -VM $foundDb
Start-vm -VM $foundApp1
Start-vm -VM $foundApp2