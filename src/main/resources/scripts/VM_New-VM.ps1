Param (
    [string]$datacenter,
    [string]$cluster,
    [string]$datastore,
    [string]$vmHost,
    [string]$resourcePool,
    [string]$parentResourcePool,
    [string]$folderName,
    [string]$parentFolderName,
    [string]$guestId,
    [string]$networkName,
    [string]$name,
    [string]$numCpu,
    [string]$diskGB,
    [string]$memoryGB,
    [string]$newResourcePool,
    [string]$newFolder
)

If ($newResourcePool)
{
    $resourcePoolWhere = Get-ResourcePool -Id (Get-ResourcePool -Location $cluster | Select-Object * | where { ($_.Name -eq $resourcePool) -and ($_.Parent -like $parentResourcePool) }).ID
    $newResourcePoolObject = New-ResourcePool -Location $resourcePoolWhere -Name $newResourcePool -CpuExpandableReservation $true -CpuReservationMhz 0 -CpuSharesLevel Normal -MemExpandableReservation $true -MemReservationGB 0 -MemSharesLevel Normal -CpuLimitMhz -1
}
Else
{
    $newResourcePoolObject = Get-ResourcePool -Id (Get-ResourcePool -Location $cluster | Select-Object * | where { ($_.Name -eq $resourcePool) -and ($_.Parent -like $parentResourcePool) }).ID
}

If ($newFolder)
{
    $folderWhere = Get-Folder -Id (Get-Folder | Select-Object * | where { ($_.Name -eq $folderName) -and ($_.Parent -like $parentFolderName) }).ID
    $newFolderObject = New-Folder -Location $folderWhere -Name $newFolder
}
Else
{
    $newFolderObject = Get-Folder -Id (Get-Folder | Select-Object * | where { ($_.Name -eq $folderName) -and ($_.Parent -like $parentFolderName) }).ID
}

$newVM = New-VM -Name $name -VMHost $vmHost -ResourcePool $newResourcePoolObject -Datastore $datastore -NumCpu $numCpu -MemoryGB $memoryGB -DiskGB $diskGB -DiskStorageFormat Thin -GuestId $guestId -NetworkName $networkName -Location $newFolderObject

$macVM = (Get-NetworkAdapter -VM $newVM).MacAddress
$macVM