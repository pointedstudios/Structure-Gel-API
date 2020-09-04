# Get arguments
$MCGradleArg = $args[0]

if ($MCGradleArg -ne "FromHub")
{
    # Clear the screen
    Clear-Host

    # Get current PowerShell title (Windows Only)
    if ($PSVersionTable.Platform -eq "Win32NT")
    {
        $MCCurrentTitle = [System.Console]::Title
    }

    $MCGradleAuthor = "Jonathing"
    $MCGradleVersion = "0.5.4"

    # Print script information
    $MCGradleGreeting1 = "MCGradle Scripts by " + $MCGradleAuthor
    $MCGradleGreeting2 = "Version " + $MCGradleVersion
    Write-Host $MCGradleGreeting1
    Write-Host $MCGradleGreeting2
    Write-Host ""

    # Check for update
    Set-Location ..
    & '.\PowerShell\internal\check_update.ps1' $MCGradleVersion

    # Go to root project directory
    Set-Location ..

    # Get Forge mod name
    & '.\Scripts\PowerShell\internal\get_mod_name.ps1'
    $MCProjectName = Get-Content '.\Scripts\PowerShell\internal\MODNAME'
    Remove-Item '.\Scripts\PowerShell\internal\MODNAME'
}

# Set the title of the Windows PowerShell or PowerShell Core console
$MCGradleTitle = $MCProjectName + ": Build Project"
[System.Console]::Title = $MCGradleTitle

# Build the project
$MCTaskMessage = "Building " + $MCProjectName + "..."
Write-Host $MCTaskMessage
Write-Host ""
.\gradlew build --warning-mode none
[System.Console]::Title = $MCGradleTitle
Write-Host ""
$MCExitMessage = "Finished building " + $MCProjectName + "."
Write-Host $MCExitMessage
$MCExitMessage2 = "If the build was successful, the output should be located under build\libs"
Write-Host $MCExitMessage2

if ($PSVersionTable.Platform -eq "Win32NT")
{
    Write-Host "Would you like to open the build\libs folder now? " -ForegroundColor Yellow -NoNewline
    Write-Host "[ y/N ] " -ForegroundColor Yellow -NoNewline
    $Readhost = Read-Host
    Switch ($ReadHost)
    {
        Y { $MCHasConfirmed = 1}
        Default { $MCHasConfirmed = 0}
    }

    if ($MCHasConfirmed -eq 1)
    {
        start .\build\libs
    }

    Write-Host ""
}

# END OF SCRIPT
Pause

if ($MCGradleArg -eq "FromHub")
{
    # Set the title of the Windows PowerShell or PowerShell Core console
    $MCGradleTitle = $MCProjectName + ": MCGradle Scripts Hub"
    [System.Console]::Title = $MCGradleTitle
}
else
{
    Write-Host "Quitting MCGradle Scripts..." -ForegroundColor Red

    # Return to scripts directory
    if ($MCGradleArg -eq "FromCMD")
    {
        Set-Location '.\Scripts\Windows\'
    }
    else
    {
        Set-Location '.\Scripts\PowerShell\'
    }

    # Revert PowerShell title (Windows Only)
    if ($PSVersionTable.Platform -eq "Win32NT")
    {
        [System.Console]::Title = $MCCurrentTitle
    }
    else
    {
        [System.Console]::Title = ""
    }
}

if ($MCGradleArg -ne "FromCMD")
{
    Write-Host ""
}
exit 0
