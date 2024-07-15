@echo off
pushd "%~dp0"

cd ../cards_solitaire/target

java -cp "cards_solitaire-1.0.jar;.\lib\*" gent.timdemey.cards.Start gent.timdemey.cards.SolitairePlugin

popd 