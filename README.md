# cards2.0
Remake of MSN's Solitaire Showdown

Hi there.

I'm Tim De Mey, a software engineer developing in Java, C#, and some C++ as well.

The goal of this one-man project is to recreate MSN's Solitaire Showdown which you could play back in the days around 2000 - 2005 within the Windows Live Messenger application. It's a fun project written in Java, but with a lot of attention to detail and smooth gameplay. I once started with this 5 years ago, but the architecture was bad, it had ugly cards, I used vectorial cards and the SVG library was really slow, and in the end I just didn't continue on it. This is cards2.0, which has the same goal as the first iteration, but the remake should resemble the original game, and I'll finish it. The approach is more like "think a little, implement the idea, think again, refactor a little when needed". Only when the architecture doesn't support some feature, I'd go into refactor mode to build 
support for it. Don't waste time on improving the architecture when it won't be necessary.

Development started somewhere in october 2018 and was initially focused at making a standard, single-player Solitaire game in order to have most of the UI stuff working. Starting late 2018, that was done, and I continued with adding netcode, after which the work on the actual game, Solitaire Showdown, was started. 

At april 2020, the game looks like this but is not yet finished:

![Screenshot Solitaire Showdown](screenshots/solitaireshowdown_04162020.gif)

In above screenshot you see 2 players each having their own game instance running, being connected to a LAN server that one of the players is currently hosting. 

The same card game engine can be used to drive a different card game, e.g. single player solitaire:

![Screenshot Solitaire](screenshots/solitaire_590x445.png)

For even more screenshots (in which you can see the development progress as well), see the screenshots folder.

All in-game sprites were made by me. For example, all cards were made in Paint.NET, using a bunch of layers to switch between red and black colored card elements, to switch the card value, to add a gloss etc. Then, I saved 52 times to a PNG file, each time with different layers switched on and off, resulting in 52 cards in PNG format. Infact, this was the first thing done before writing any code.

Currently supported games / plugins:
- Solitaire (single player)
- Solitaire Showdown (2 players)

Enjoy.
