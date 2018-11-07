try
{
    import-module activedirectory
}
catch
{
    Write-Output "[ERROR] Failed to import 'import-module activedirectory'";
}

try
{
    $user_josso = (get-aduser RSauth).DistinguishedName
}
catch
{
    $user_josso = $false
}

$user_josso