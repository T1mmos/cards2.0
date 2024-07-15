@echo off
pushd "%~dp0"

cd ../cards_solitaireshowdown/target

java -cp "cards_solitaireshowdown-1.0.jar;.\lib\*" gent.timdemey.cards.Start gent.timdemey.cards.SolShowPlugin

popd 