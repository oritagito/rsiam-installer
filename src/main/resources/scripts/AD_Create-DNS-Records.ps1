Param (
    [string]$DB_name,
    [string]$DB_ip,
    [string]$APP1_name,
    [string]$APP1_ip,
    [string]$APP2_name,
    [string]$APP2_ip,
    [string]$CLUSTER_SSO_name,
    [string]$CLUSTER_SSO_ip
)

try
{
    import-module activedirectory
}
catch
{

}

$entryDC = new-object DirectoryServices.DirectoryEntry("")
[string]$FQDN_domain = ($entryDC.distinguishedName.replace("DC=", "").replace(",", "."))

#Creating DNS records
wmic /node:$FQDN_domain /privileges:enable process call create "cmd /c dnscmd . /recordadd $FQDN_domain $DB_name A $DB_ip"
wmic /node:$FQDN_domain /privileges:enable process call create "cmd /c dnscmd . /recordadd $FQDN_domain $APP1_name A $APP1_ip"
wmic /node:$FQDN_domain /privileges:enable process call create "cmd /c dnscmd . /recordadd $FQDN_domain $APP2_name A $APP2_ip"
wmic /node:$FQDN_domain /privileges:enable process call create "cmd /c dnscmd . /recordadd $FQDN_domain $CLUSTER_SSO_name A $CLUSTER_SSO_ip"

