# Chat Game Fontificator [[Download](../../raw/master/jar/ChatGameFontificator.jar)] 

The Chat Game Fontificator is a Twitch chat display that visually imitates the text boxes from various video games. Its intended use is as a part of a Twitch video game stream in place of less pretty chat windows.

It's a Java application archive, so you'll need the JRE to run it, but it's likely you already have that. If you don't, you can get it [here](https://java.com/en/download/). Install the JRE, and then you can run this program.

Here are some screenshots of some of just a few of the preset game styles:

<img src="http://www.glitchcog.com/cgf/cgf_ct.png" width="120" alt="Chrono Trigger"> <img src="http://www.glitchcog.com/cgf/cgf_cc.png" width="120" alt="Chrono Cross"> <img src="http://www.glitchcog.com/cgf/cgf_eb_mint.png" width="120" alt="Earthbound"> <img src="http://www.glitchcog.com/cgf/cgf_eb_saturn.png" width="120" alt="Earthbound (Mr. Saturn)"> <img src="http://www.glitchcog.com/cgf/cgf_ff6.png" width="120" alt="Final Fantasy">

<img src="http://www.glitchcog.com/cgf/cgf_dw.png" width="120" alt="Dragon Warrior"> <img src="http://www.glitchcog.com/cgf/cgf_pkmn.png" width="120" alt="Pokemon Red/Blue"> <img src="http://www.glitchcog.com/cgf/cgf_pkmn_frlg.png" width="120" alt="Pokemon Fire Red/Leaf Green"> <img src="http://www.glitchcog.com/cgf/cgf_metroid.png" width="120" alt="Metroid"> <img src="http://www.glitchcog.com/cgf/cgf_metroid_mb.png" width="120" alt="Metroid (Mother Brain)">

<img src="http://www.glitchcog.com/cgf/cgf_smb1.png" width="120" alt="Super Mario Bros."> <img src="http://www.glitchcog.com/cgf/cgf_smb3.png" width="120" alt="Super Mario Bros. 3"> <img src="http://www.glitchcog.com/cgf/cgf_smb3_letter.png" width="120" alt="Super Mario Bros. 3 Princess Letter"> <img src="http://www.glitchcog.com/cgf/cgf_zelda1.png" width="120" alt="The Legend of Zelda"> <img src="http://www.glitchcog.com/cgf/cgf_zelda3.png" width="120" alt="Zelda: LttP">

Follow me on Twitter [@glitchcog](https://twitter.com/glitchcog) for updates whenever I add new games.

Enjoy!

*****

## Quick-start Guide

1. Get the program: [[Download](../../raw/master/jar/ChatGameFontificator.jar)]
2. Run ChatGameFontificator.jar, probably by double clicking it, or possibly by right-clicking it and selecting to run it. It should look something like the picture below, with the two windows, one for the chat display, one for configuration. If you close the configuration window, just single click anywhere inside the chat display window to bring it back. If it doesn't run, you may need to download and install the [JRE](https://java.com/en/download/).
<img src="http://www.glitchcog.com/cgf/cgf.png" alt="Chat Game Fontificator">
3. Enter your credentials: the Twitch user name you want to join the chat with, and your [OAuth token](http://www.twitchapps.com/tmi/). [What is an OAuth token?](http://oauth.net/about/)
4. Enter the channel you want to join (the # is optional, but Twitch channels should be in all lowercase, but that is done invisibly and automatically if you're connecting to the Twitch IRC server).
5. Make the chat look how you want it to look. Either select something from the Presets menu, or use the Fontificator Configuration window tabs to configure to your heart's content. There are many more games available on the Font/Border tab that don't have representation in the Preset menu.
6. Click connect and enjoy.

*****

## More Information for the Especially Curious

* Why did you create this?

I made it mostly for personal use, but decided to just put it out there in the hope that others would find it useful. I think it's ugly to put a plain-looking chat into a Twitch stream, so I made this.

* Isn't it sort of stupid to put the chat in the stream when it's right there next to the stream already?

Yeah, I have tremendous sympathy for that notion. But a chat that isn't displayed in the video is fleeting, so when you watch a stream re-run, it just looks like a crazy person talking to him or herself. And it gives you a better feel for the infamous Twitch stream delay since you can see exactly when the post got to the streamer. And, the sorts of games I like have 4:3 aspect ratios, so there's wasted space anyhow.

* Do you stream? If so, do you eat your own dog food?

Yes, I stream, although I haven't for a long time because I was spending my free time coding this. If you're interested in seeing this chat program in action, please come watch me here: [GlitchCog at Twitch.tv](http://www.twitch.tv/glitchcog/profile).

* How did you choose these games? What about this other game?

The selection of games is informed by what I like and want to stream myself, what I could find images of fonts for, and the random whims of my OCD while putting the font sprite sheets together. I'll likely keep adding more in the future. Please feel free to message me on Twitch if you'd like to request a game for a future update, and I'll see if I can make it happen.

* What's the difference between these two fonts?

Sometimes not much. Dragon Warrior, for example, just changed a couple letters slightly throughout the series on the NES. Why did I bother with these subtle changes? I'm afraid I have no valid reason other than a sense of completeness. There are additional characters that aren't typically featured in the fonts of older games, so whenever necessary I drew my own dollar signs and tildes, trying my best to match the style of the font.

* Where did you get these font and border images?

I don't own the copyright to the fonts or border art contained in this program, nor do I have permission to use them from the copyright holders. I ripped some out of ROM images of the games, took screenshots of name-entry screens, searched all over the Internet for elusive screenshots that happened to display dialog containing some rare character I was still missing, and found some others searching through sprite rip websites.

* I can't connect. What's wrong?

Maybe your connection to the Internet is down? Check all your connection properties for typos. If everything looks okay, check the output logs on the bottom of the Connection tab. Google something that looks like it might be your error for advice. If there's something wrong with your OAuth Token, you'll get a message that says, ":tmi.twitch.tv NOTICE * :Login unsuccessful".

* I can't post to the chat. What's wrong?

Nothing is wrong; the Chat Game Fontificator is purely for displaying the chat. Because it has such extensive visual configuration options, I didn't want to include even more features beyond this core purpose. You can simultaneously log into the Twitch chat via the stream page, or with a third program like [Chatty](http://getchatty.sourceforge.net/), which is a fully featured IRC client.

* Why don't I see any messages?

First, make sure messages are actually being posted to the chat. Only messages posted after you connect will be displayed. If you still don't see any messages, it could be that you have the options configured in such a way to hide them. Some possibilities include:

  -You're not really connected, or you're expecting to see messages that were posted before you connected. Just connect to the channel and wait, and future messages will be posted.

  -You're not really connected to the correct channel. Make sure what you have in the channel input field is just the username of the Twitch channel you're trying to connect to. If you have anything but just the channel name, or if there is a typo in it, you'll be "connected" to something that's not really the channel you're trying to connect to.

  -The text and the background (or chroma color) are the same. Change the colors to something else.

  -The font is too large or too spaced out, or the border is too big, given the chat window size, to have room for even a single character. Try reducing the sizes, or making the border offset negative.

  -There are only a few messages, and the border offsets are negative numbers. The messages may be out of bounds of the window. Try reducing the border offset values.

  -The message speed is too slow for the number of messages flooding into the channel, which in turn empties out the full message queue faster than any messages can be displayed. Increase the message speed and the queue size. Also consider opting to forgo including join messages.

  -All the messages you're posting are in violation of your message censorship rules. The easiest way to check this is to disable all censorship in the Message menu, Message Management popup.

* Can I use the Fontificator with non-Twitch IRC channels?

Yes, just change the host on the Connection tab to a different server. Twitch-specific features like emotes won't work though.

* Are there any known bugs?

  - When anti-aliasing is turned on, it leaves little flecks all over the chat window. Anti-aliasing is applied to the whole sheet of characters and components of the border, so if there are pixels from a character adjacent to another cell of the grid, the anti-aliasing bleeds a little over into that next cell. This bug affects any fonts that don't have at least a single pixel buffer of space around their letters. Unfortunately, if anti-aliasing is applied to each character individually as the image is drawn, the program runs very slowly. One possible solution would be to cache these individually anti-aliased characters, but that might be a little messy to implement. I won't resort to that until I've attempted to fix it some other way, perhaps drawing each layer (background, border, and text) aliased separately, and then applying the anti-aliasing to the combination of just those three images.

  - A message consisting of a single period will not show up in the chat program. I'm not sure why this happens. The message appears to never be received by the program. It might be on Twitch's end, or in the PircBot IRC library this program uses.

  - In some rare unknown circumstance (possibly related to changing the rate at which messages are drawn?) the framerate can drop, making the message roll-out look choppy. Typically restarting will fix this.

  - Messages from massively popular streams like GamesDoneQuick don't show up in the chat window. I suspect Twitch uses a different protocol for massively populated streams, but I'm not sure. GamesDoneQuick is the only channel I've seen this happen with.

* How do I use a custom font?

Select Custom... from the font dropdown menu and select your font sheet image. This image must be a PNG containing a grid of uniformly sized rectangular cells, 8 cells wide by 12 cells tall. Each cell should contain in order all the ASCII characters from 32 (space) to 127 (delete). Click the Select Missing Character button for an example of this. The degree to which a pixel is pure white is the degree to which it is tinted by the Text Tint color, and any transparent or partially transparent pixels will show through to the border or background color the appropriate amount. [This image (the Chrono Trigger font) is an example of one that will work.](../../raw/master/src/main/resources/fonts/ct_font.png)

* How do I use a custom border?

Select Custom... from the border dropdown menu and select your border sheet image. This image must be a PNG containing a grid of uniformly sized rectangular cells, 3 cells wide by 3 cells tall. This image represents a small version of a border, which can be expanded by tiling the top-middle, middle-left, middle-right, and bottom-middle edge cells. The center cell is tiled on the inside of the border horizontally and vertically. The degree to which a pixel is pure white is the degree to which it is tinted by the Border Tint color, and any transparent or partially transparent pixels will show through to the background color the appropriate amount. [This image (a Metroid border) is an example of one that will work.](../../raw/master/src/main/resources/borders/metroid_border.png)

* What if I want to start with an existing font or border, rather than create a custom one from whole cloth?

All the font and border images are available as part of the source code on this site. Modify them as you see fit, and then load them into the program as a custom font or border. To save them from the GitHub source code repository, you have to click on the font, then save the image from the button in the upper left that says Raw, or from the image that is displayed on that page. If you right-click the image name from the repository and select Save-As, you will be saving the HTML page that GitHub uses to represent that image, not the image itself. Another consideration is that the characters of many of the existing fonts are colored white against a transparent background, so some image viewing or editing programs will display these images as just a blank white picture. If this happens to you, you may need a more sophisticated program to work on them. Here are links to the repository locations containing the existing font and border images: [Fonts](https://github.com/GlitchCog/ChatGameFontificator/tree/master/src/main/resources/fonts) | [Borders](https://github.com/GlitchCog/ChatGameFontificator/tree/master/src/main/resources/borders)

* Why didn't you tell me there were more game fonts available than just those in the Preset menu?

I'm sorry, I should have mentioned that you can use the Font/Border tab to select fonts from some games that aren't even represented with a preset option. Making presets is more tedious than you might think because I take care to try to match every aspect of the game's dialog, including pixel-perfect kerning, line spacing, text positioning and coloration. I don't always get it exactly correct, but many of the presets represent exactly how the text would appear if it were actually in the game.

* Could I please see an exhaustive list of all the game fonts represented in this program?

  [[Image of all the available fonts](../../raw/master/cgf_fonts_example.png)] 

  - 7th Dragon (Dialog)
  - 7th Dragon (Name)
  - 7th Saga (Battle)
  - ActRaiser
  - ActRaiser 2
  - ActRaiser 2 Password
  - ActRaiser 2 Score
  - Animal Crossing
  - Ape Escape (Credits)
  - Bahamut Lagoon
  - Batman (NES)
  - Batman Returns (NES)
  - Bionic Commando
  - Blaster Master
  - Blaster Master (Credits)
  - Breath of Fire
  - Breath of Fire II
  - Bucky O'Hare
  - Castlevania
  - Castlevania II: Simon's Quest
  - Castlevania III: Dracula's Curse
  - Super Castlevania IV
  - Castlevania: Symphony of the Night
  - Cave Story Plus
  - Chibi Robo
  - Chip 'n Dale Rescue Rangers
  - Chrono Cross
  - Chrono Trigger
  - Clash at Demonhead
  - Super C
  - Crusader of Centry
  - Crystalis
  - Duck Hunt
  - DuckTales (NES)
  - Donkey Kong
  - Donkey Kong Jr.
  - Donkey Kong Jr. Math
  - Donkey Kong '94
  - Donkey Kong Country
  - DOS
  - Double Dragon
  - Double Dragon 2
  - Double Dragon 3
  - Dr. Mario
  - Dragon Warrior
  - Dragon Warrior II
  - Dragon Quest I.II (SFC)
  - Dragon Warrior III
  - Dragon Warrior III (GBC) Dialog
  - Dragon Warrior III (GBC) Fight
  - Dragon Quest III (SFC)
  - Dragon Warrior IV
  - Dragon Warrior VII Fight
  - Dragon Quest Heroes: Rocket Slime
  - Dragon Quest IX
  - Drill Dozer
  - EarthBound Zero
  - EarthBound Zero Bold
  - EarthBound
  - EarthBound Mr. Saturn
  - Ecco the Dolphin
  - Ecco the Dolphin Small
  - Excite Bike
  - Mother 3
  - Faxanadu
  - Final Fantasy
  - Final Fantasy II
  - Final Fantasy Dawn of Souls
  - Final Fantasy IV
  - Final Fantasy VI
  - Final Fantasy VI (Battle)
  - Final Fantasy VII
  - Final Fantasy IX
  - Final Fantasy Mystic Quest (Dialog)
  - Final Fantasy Mystic Quest (HUD)
  - Final Fantasy Tactics Advance (Dialog)
  - Final Fantasy Tactics Advance (Menu)
  - Freedom Planet
  - Gemfire (NES)
  - Ghosts n Goblins
  - Golden Sun
  - Golden Sun (Battle)
  - Harvest Moon: Friends of Mineral Town
  - Harvest Moon: The Tale of Two Towns
  - Holy Diver
  - Super Jeopardy!
  - Kero Blaster
  - Kid Icarus
  - The Lost Vikings
  - Little Nemo: The Dream Master
  - Lufia II: Rise of the Sinistrals
  - Lunar: Silver Star Story Complete
  - Final Fantasy Adventure
  - Secret of Mana
  - Legend of Mana
  - Super Mario Bros.
  - Super Mario Bros. 2
  - Super Mario Bros. 3
  - Super Mario Bros. 3 HUD
  - Super Mario Bros. 3 HUD (Mixed Case)
  - Super Mario Land
  - Super Mario World
  - Super Mario World 2: Yoshi's Island
  - Super Mario 64
  - Super Mario 64 Multicolor
  - Mario is Missing
  - Super Mario RPG: Dark
  - Super Mario RPG: Light
  - Paper Mario: The Thousand Year Door
  - Mario Golf
  - Mario Golf: Advance Tour
  - Super Mario Kart
  - Mario Kart 8 Deluxe Outline
  - Marvel vs. Capcom: Clash of Super Heroes Battle Message
  - Marvel vs. Capcom: Clash of Super Heroes Health Name
  - Marvelous: Another Treasure Island
  - Mega Man
  - Mega Man 2
  - Mega Man 3
  - Mega Man 4
  - Mega Man 5
  - Mega Man 6
  - Mega Man 9
  - Mega Man X
  - Mega Man Battle Network
  - Metal Gear
  - Metal Storm
  - Metroid
  - Metroid II
  - Metroid II (Credits)
  - Super Metroid
  - Super Metroid (Mixed Case)
  - Metroid Fusion
  - Metroid Fusion Outline
  - Metroid Zero Mission
  - Metroid Zero Mission Outline
  - Mickey Mousecapade
  - Moon Crystal Dialog
  - Moon Crystal HUD
  - Mortal Kombat II SNES Story
  - Mortal Kombat II SNES Health Name
  - Mortal Kombat II SNES Flawless Victory
  - Mortal Kombat II SNES Header
  - Night in the Woods
  - Ninja Gaiden
  - Ninja Gaiden HUD
  - Ninja Gaiden II
  - Ninja Gaiden II HUD
  - Ninja Gaiden III
  - Ninja Gaiden III HUD
  - Ogre Battle: The March of the Black Queen
  - Pac-Man (NES)
  - Ms. Pac-Man (NES)
  - Panzer Dragoon
  - Panzer Dragoon Saga
  - Paperboy
  - PaRappa the Rapper
  - Phantasy Star
  - Phantasy Star 2
  - Pokemon Red/Blue/Yellow
  - Pokemon Gold/Silver
  - Pokemon Ruby/Sapphire (Dark)
  - Pokemon Ruby/Sapphire (Light)
  - Pokemon Fire Red/Leaf Green
  - Pokemon Diamond/Pearl
  - Princess Tomato in the Salad Kingdom
  - Punch-Out!!
  - Q\*bert (NES)
  - R.C. Pro-Am (NES) Race
  - R.C. Pro-Am (NES) Trophy
  - Rampage
  - Resident Evil 2
  - Ristar
  - River City Ransom
  - Robotrek
  - Robotrek (Battle)
  - Rygar (NES)
  - Secret of Evermore
  - Sega System
  - Shadowgate (NES)
  - Shadow Hearts: Covenant (Cutscene)
  - Shantae
  - Shining Force
  - Shovel Knight
  - The Simpsons: Bart vs. the Space Mutants
  - The Simpsons: Bart vs. the World (NES)
  - The Simpsons: Bart vs. the World (NES) Pause
  - The Simpsons: Bartman Meets Radioactive Man (NES)
  - The Simpsons: Bartman Meets Radioactive Man (NES) Pause
  - Solomon's Key (NES)
  - Solstice
  - Sonic Team
  - Splatterhouse
  - Spyhunter (NES)
  - Star Ocean (Dialog)
  - Star Ocean (HUD)
  - Star Ocean: The Second Story (HUD)
  - Star Trek: 25th Anniversary (NES) Dialog
  - Star Trek: 25th Anniversary (NES) Intro
  - Star Trek: 25th Anniversary (NES) Credits
  - Star Wars (NES)
  - Star Wars Japan
  - Star Wars: The Empire Strikes Back
  - Stardew Valley
  - StarTropics
  - StarTropics Title
  - Zoda's Revenge: StarTropics II
  - Suikoden
  - Tales of Phantasia (SFC)
  - Tales of Destiny Battle
  - Tales of Symphonia
  - TaleSpin (NES)
  - Tecmo Bowl
  - Tengai Makyou Zero
  - Tenchu 2: Birth of the Stealth Assassins (Small)
  - Tenchu 2: Birth of the Stealth Assassins (Large)
  - Terranigma
  - Tetris (GB)
  - Tetris (NES)
  - Teenage Mutant Ninja Turtles (NES)
  - Teenage Mutant Ninja Turtles II: The Arcade Game
  - Teenage Mutant Ninja Turtles II: The Arcade Game HUD
  - Teenage Mutant Ninja Turtles III: The Manhattan Project
  - Teenage Mutant Ninja Turtles: Tournament Fighters (NES)  
  - The Legend of Dragoon
  - Ultima Exodus (NES)
  - Ultima Exodus (NES) Credits
  - Umihara Kawase
  - Undertale
  - Wario's Woods
  - Wario Land 4 (Dark)
  - Wario Land 4 (Light)
  - Wild Arms
  - Willow (NES)
  - Wonder Boy In Monster World
  - X-Men Arcade
  - Ys (FC)
  - Ys III (FC)
  - Ys III (SNES)
  - The Legend of Zelda
  - The Legend of Zelda (Mixed Case)
  - Zelda II: The Adventures of Link
  - Zelda II (Mixed Case)
  - The Legend of Zelda: A Link to the Past
  - The Legend of Zelda: A Link to the Past Randomizer
  - The Legend of Zelda: Link's Awakening
  - The Legend of Zelda: Ocarina of Time
  - The Legend of Zelda: The Wind Waker
  - The Legend of Zelda: The Minish Cap
  - The Legend of Zelda: Four Swords Adventures
  - The Legend of Zelda: Skyward Sword
  - The Legend of Zelda: Breath of the Wild
  - Zero Wing
