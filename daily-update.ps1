# Requires: Git for Windows. Run this script from INSIDE your repo root (Pahana-Edu).
# Usage: Right–click > Run with PowerShell, or: powershell -ExecutionPolicy Bypass -File .\daily-update.ps1 "feat: update customer UI"
param(
    [string]$Message = ""
)

# Move to the script's directory (repo root assumed)
Set-Location -Path $PSScriptRoot

# Detect default base branch
$baseBranch = "develop"
$hasDevelop = git branch -a | Select-String -Quiet "remotes/origin/develop"
if (-not $hasDevelop) { $baseBranch = "main" }

git fetch origin

# Make sure we have the latest base
git checkout $baseBranch 2>$null
git pull --ff-only origin $baseBranch

# Daily branch name e.g., feature/2025-08-18-daily-update
$today = Get-Date -Format 'yyyy-MM-dd'
$branch = "feature/$today-daily-update"

# Create or switch to the daily branch
$exists = git branch --list $branch
if (-not $exists) {
  git checkout -b $branch $baseBranch
} else {
  git checkout $branch
  git rebase $baseBranch
}

# Stage changes
git add -A

# Check if there is something to commit
$diff = git status --porcelain
if (-not $diff) {
  Write-Host "No changes to commit. Make code changes in pahana-edu-service/ or pahana-edu-web/ and run again." -ForegroundColor Yellow
  exit 0
}

# Commit
if (-not $Message -or $Message.Trim().Length -eq 0) {
  $Message = Read-Host "Enter a meaningful commit message (e.g., 'feat: add order total calculation')"
}
git commit -m $Message

# Push
git push -u origin $branch

# Print PR link and open browser for convenience
$repoUrl = "https://github.com/MadushiWeerasinghe/Pahana-Edu"
$prUrl = "$repoUrl/compare/$branch?expand=1"
Write-Host "`nBranch pushed: $branch" -ForegroundColor Green
Write-Host "Open Pull Request: $prUrl"
Start-Process $prUrl
