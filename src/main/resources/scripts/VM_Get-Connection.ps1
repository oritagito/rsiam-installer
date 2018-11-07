Param (
    [string]$User,
    [string]$Server,
    [string]$Password
)

$result = Connect-VIServer -Server $Server -User $User -Password $Password -WarningAction SilentlyContinue
$?