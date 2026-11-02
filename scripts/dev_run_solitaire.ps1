# Builds the framework + Solitaire plugin and starts the game (development use)
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot

mvn -q -B -f "$root\cards_framework\pom.xml" install -DskipTests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

mvn -q -B -f "$root\cards_solitaire\pom.xml" install -DskipTests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$target = "$root\cards_solitaire\target"
java -cp "$target\cards_solitaire-1.0.jar;$target\lib\*" gent.timdemey.cards.Start gent.timdemey.cards.SolitairePlugin
exit $LASTEXITCODE
