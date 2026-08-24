param(
    [string]$HistoryRoot = "$env:APPDATA\Code\User\History",
    [string]$ProjectRoot = "C:\Users\burak\OneDrive\Masaüstü\project\randevu-panel"
)

$srcRoot = Join-Path $ProjectRoot "src"

# URL decode function
function ConvertFrom-UrlEncoded {
    param([string]$EncodedString)
    [System.Net.WebUtility]::UrlDecode($EncodedString)
}

# Find all entries.json files
Write-Host "Searching for entries.json files in: $HistoryRoot" -ForegroundColor Cyan
$entriesFiles = Get-ChildItem -Path $HistoryRoot -Recurse -Filter "entries.json" -ErrorAction SilentlyContinue

Write-Host "Found $($entriesFiles.Count) entries.json files" -ForegroundColor Green

$filesToRestore = @()

foreach ($entriesFile in $entriesFiles) {
    try {
        $content = Get-Content -Path $entriesFile.FullName -Raw | ConvertFrom-Json
        $historyDir = $entriesFile.Directory.FullName
        
        if ($content -is [System.Object[]]) {
            $entries = $content
        } else {
            $entries = @($content)
        }
        
        # Process each entry
        foreach ($entry in $entries) {
            if ($entry.resource) {
                $decodedPath = ConvertFrom-UrlEncoded $entry.resource
                # Remove file:/// prefix
                $decodedPath = $decodedPath -replace '^file:///', ''
                
                # Check if it's a src/ file and in one of our target folders
                if ($decodedPath -match '\\src\\(features|layout|shared|components)\\' -or $decodedPath -match '/src/(features|layout|shared|components)/') {
                    $filesToRestore += @{
                        HistoryPath = $historyDir
                        OriginalPath = $decodedPath
                        Entry = $entry
                        EntriesFile = $entriesFile.FullName
                    }
                }
            }
        }
    } catch {
        Write-Host "Error processing $($entriesFile.FullName): $_" -ForegroundColor Yellow
    }
}

Write-Host "`nFound $($filesToRestore.Count) files to potentially restore" -ForegroundColor Green

# Group by original path to keep only latest
$filesByPath = @{}
foreach ($file in $filesToRestore) {
    $key = $file.OriginalPath.ToLower()
    if (-not $filesByPath[$key] -or $filesByPath[$key].Entry.timestamp -lt $file.Entry.timestamp) {
        $filesByPath[$key] = $file
    }
}

Write-Host "After deduplication: $($filesByPath.Count) unique files to restore" -ForegroundColor Green

# Restore files
$restoreCount = 0
foreach ($key in $filesByPath.Keys) {
    $file = $filesByPath[$key]
    $originalPath = $file.OriginalPath
    
    # Find the numbered file in history directory
    $historyFiles = Get-ChildItem -Path $file.HistoryPath -Filter "*" -ErrorAction SilentlyContinue | 
                    Where-Object { $_.Name -match '^\w+\.(tsx?|jsx?)$' } |
                    Sort-Object LastWriteTime -Descending
    
    if ($historyFiles.Count -eq 0) {
        Write-Host "No history files found in $($file.HistoryPath)" -ForegroundColor Yellow
        continue
    }
    
    # Get the latest file (most recent)
    $latestHistoryFile = $historyFiles[0]
    
    # Extract target path from original path (normalize path separators)
    $targetPath = $originalPath -replace '^file:///', '' -replace '\?.*$', ''
    $targetPath = $targetPath -replace '\\', '/'
    
    # Extract relative path from src/
    if ($targetPath -match '.+/src/(.+)$') {
        $relativePath = $matches[1]
        $targetFile = Join-Path $srcRoot $relativePath
    } else {
        Write-Host "Could not extract relative path from: $targetPath" -ForegroundColor Yellow
        continue
    }
    
    # Create target directory
    $targetDir = Split-Path -Parent $targetFile
    if (-not (Test-Path $targetDir)) {
        New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
        Write-Host "Created directory: $targetDir" -ForegroundColor Gray
    }
    
    # Copy file
    try {
        Copy-Item -Path $latestHistoryFile.FullName -Destination $targetFile -Force
        Write-Host "✓ Restored: $(Split-Path -Leaf $targetFile)" -ForegroundColor Green
        $restoreCount++
    } catch {
        Write-Host "✗ Failed to copy $($latestHistoryFile.FullName) to $targetFile : $_" -ForegroundColor Red
    }
}

Write-Host "`nRestored $restoreCount files successfully!" -ForegroundColor Cyan

# Stage and commit changes
if ($restoreCount -gt 0) {
    Write-Host "`nStaging changes..." -ForegroundColor Cyan
    Push-Location $ProjectRoot
    git add src/
    git commit -m "Restore all features, layout, shared components and hooks`n`nCo-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>"
    Pop-Location
    Write-Host "Committed successfully!" -ForegroundColor Green
}
