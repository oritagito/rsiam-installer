Param (
    [string]$datacenter
)

$ListCluster = (Get-Cluster -Location $datacenter | Sort-Object Name).Name
$ListCluster