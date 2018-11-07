Param (
    [string]$localPath,
    [string]$remotePath
)

$result = Copy-DatastoreItem -Item $localPath -Destination $remotePath
$?