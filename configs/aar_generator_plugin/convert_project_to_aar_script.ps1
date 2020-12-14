param(
    [Parameter(Mandatory=$true)][string]$projectDir,
    [Parameter(Mandatory=$true)][string]$projectName,
    [Parameter(Mandatory=$true)][string]$projectFlavour,
    [Parameter(Mandatory=$true)][string]$projectMilestones
)

Write-Host "==========================================="
Write-Host "====== Running 'ProjectToAar' Script ======"
Write-Host "==========================================="
Write-Host "====== Script Config ======"
Write-Host "project_dir=$projectDir"
Write-Host "project_name=$projectName"
Write-Host "project_flavour=$projectFlavour"
Write-Host "project_milestones=$projectMilestones"
Write-Host "==========================="
Write-Host ""

#implementation project(':engine_support') to
#implementation 'ru.aar_generator:$project_name_$project_milestones_$project_flavour.engine_support:0.0.1'

$implementation="implementation"
$api="api"
$testImplementation="testImplementation"

$aarName="'ru.aar_generator:" + $projectName + "_" + $projectMilestones + "_" + $projectFlavour + "."
$aarVersion=":0.0.1'"

#project(':
$project_1="project(':"
$project_1_match="project\(':" <# Команда -match требует экранирования скобок #>
#project( ':
$project_2="project( ':"
$project_2_match="project\( ':" <# Команда -match требует экранирования скобок #>
#project(":
$project_3='project(":'
$project_3_match='project\(":' <# Команда -match требует экранирования скобок #>
#project(path: ':
$project_4="project(path: ':"
$project_4_match="project\(path: ':" <# Команда -match требует экранирования скобок #>

function Get-ReplaceImplementation(
        [parameter(Mandatory=$true)]$valExtGradleFile,
        [parameter(Mandatory=$true)]$mainReplacer
) {
    $resultFile = [System.Collections.Generic.List[string]]::new()
    $content = ($valExtGradleFile | Get-Content)

    foreach ($line in $content) {
        $modifierLine = ""
        if($line -match "$mainReplacer $project_1_match") {
            #implementation|api|testImplementation project(':project')
            $modifierLine = $line.Replace("$mainReplacer $project_1", "$mainReplacer $aarName")
        } elseif ($line -match "$mainReplacer $project_2_match") {
            #implementation|api|testImplementation project( ':project')
            $modifierLine = $line.Replace("$mainReplacer $project_2", "$mainReplacer $aarName")
        } elseif ($line -match "$mainReplacer $project_3_match") {
            #implementation|api|testImplementation project(":project")
            $modifierLine = $line.Replace("$mainReplacer $project_3", "$mainReplacer $aarName")
        } elseif ($line -match "$mainReplacer $project_4_match") {
            #implementation|api|testImplementation project(path: ':project')
            $modifierLine = $line.Replace("$mainReplacer $project_4", "$mainReplacer $aarName")
        } else {
            $modifierLine = $line
        }

        if ($modifierLine -match "$mainReplacer '") {
            if($modifierLine -match "'\)") {
                #')
                $resultFile.Add($modifierLine.Replace("')", $aarVersion))
            } elseif($modifierLine -match """\)") {
                #")
                $resultFile.Add($modifierLine.Replace(""")", $aarVersion))
            } elseif($modifierLine -match "' \)") {
                #' )
                $resultFile.Add($modifierLine.Replace("' )", $aarVersion))
            }
            else {
                $resultFile.Add($modifierLine)
            }
        } else {
            $resultFile.Add($modifierLine)
        }
    }

    # ====== Debug: "Show Result File" ======
#    $resultFileDebug = [System.Collections.Generic.List[string]]::new()
#    foreach ($line in $resultFile) {
#        $resultFileDebug.Add($line + "`r`n")
#    }
#    Write-Host $resultFileDebug
#    $resultFileDebug.Clear()
#    $resultFile.Clear()

    # ====== Release: "Write Result File on Disk" ======
        $resultFile | Set-Content $valExtGradleFile.FullName
        $resultFile.Clear()
}

Get-ChildItem -Path $ProjectDir -Filter build.gradle -Recurse | where {-not $_.psiscontainer} | Foreach-Object {
    $output = "Converting: " + $_.FullName + " ... "

    Get-ReplaceImplementation $_ $implementation
    Get-ReplaceImplementation $_ $api
    Get-ReplaceImplementation $_ $testImplementation

    $output += "DONE!"
    Write-Output $output
}