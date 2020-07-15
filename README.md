# cards2.0
A pluggable card game engine created by Tim De Mey coming with a plugin supporting a remake of MSN Messenger's Solitaire Showdown

![Screenshot Solitaire Showdown](screenshots/solshow_3d.png)

## Project Goal
Back in the early nillies people could play Solitaire Showdown, a game included in the chat application MSN Messenger - later renamed to Windows Live Messenger. As Microsoft pulled the plug, this game is no longer available to the public, or at least not in its original form. 

This project's goal is to replicate the original as close as possible. [This](screenshots/solshow_orig_game.jpg?raw=true) is a screenshot of the original game.

The game is still unfinished as specific required parts are not yet (fully) developed. Some examples:
* Score system
* Sounds
* UI must be reworked to resemble the original game

## A pluggable card game engine

The main engine found in [cards_framework](../master/cards_framework) must be started with a [plugin](../master/cards_framework/src/main/java/src/gent/timdemey/cards/ICardPlugin.java) in order to run a game. Currently, two plugins are supported: 
* Solitaire (single player - [screenshot](screenshots/solitaire_590x445.png?raw=true) - [plugin](../master/cards_solitaire/src/main/java/src/gent/timdemey/cards/SolitairePlugin.java))
* Solitaire Showdown (1vs1 multiplayer - [animated GIF](screenshots/solitaireshowdown_05212020.gif?raw=true) - [plugin](../master/cards_solitaireshowdown/src/main/java/src/gent/timdemey/cards/SolShowPlugin.java))

## Sprites

All in-game sprites were made by me. For example, all cards were made in Paint.NET, using a bunch of layers to switch between red and black colored card elements, to switch the card value, to add a gloss etc. Then, I saved 52 times to a PNG file, each time with different layers switched on and off, resulting in 52 cards in PNG format. Infact, this was the first thing done before writing any code.

The cards are designed to replicate the cards as seen in the [original Solitaire Showdown game](screenshots/solshow_orig_game.jpg?raw=true).

## Project History

* Around 2015 I started with cards1.0. It used vectorial cards which was slow to render. The architecture was bad. In the end, this project was abandoned and it never produced a working game.
* Development on cards2.0 started somewhere in october 2018 and was initially focused at making a standard, single player Solitaire game in order to have most of the UI stuff working, but already with Solitaire Showdown in mind.
* Starting late 2018, netcode was added, after which the work on the primary goal, Solitaire Showdown, was started. 
* After a long period of doing nothing, I continued somewhere around January 2020, refactoring the complete codebase and throwing away unnecessary garbage. When that was done, support for undoing command chains was added. (In multiplayer the server may not accept commands that are already executed client-side. They are executed client-side and corrected afterwards if necessary, to ensure a smooth gameplay that never blocks).
* The hardest part was then done. Support for lobbies was added, I completed the game logic, it all started coming together around April 2020. (see [animated GIF 04/16/2020](screenshots/solitaireshowdown_04162020.gif?raw=true)). 
* May 2020: Better handling of leaving players, lost connections, full lobbies, general cleanup of TCP connections in all these cases. Added score system. Support for animations other than cards. Added more card sprites for both front and back, and card stacks. (see [animated GIF 05/21/2020](screenshots/solitaireshowdown_05212020.gif?raw=true))
* June 2020: The entire scalable component and scalable resource system has been reworked. Resources are now preloaded before the game is started, and scaled resources are being cached. This results in a more instant UI update when the game is rescaled and all resources are only scaled once in a particular dimension, even if they are being used by multiple components. Up to this point, only images were supported as a "scalable resource". Support was added for text: the font itself is the scalable resource here. The first use case for scalable text was added to Solitaire Showdown, and is seen in the animation that shows the increment in score, when laying down a card on a cardstack. (see [animated GIF 06/29/2020](screenshots/solitaireshowdown_06292020.gif?raw=true))
* July 2020: Support for animations spanning resizements was added, so you can resize the window while animations are ongoing. This is the last part in making the game truely resizable at any moment. A counter for the SPECIAL stack in Solitaire Showdown was added as well. Font are handled better in terms of resizing the game.

## Latest animated GIF

![Screenshot Solitaire Showdown](screenshots/solitaireshowdown_07152020.gif)
