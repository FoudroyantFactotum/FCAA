# Fou's Archive
A mini minecraft mod dedicated to showing the stuff of yesteryear.

Currently the mod contains:

- A Tuning Fork
- A Multi-Block Player Piano
- Many Piano Rolls (Pre 1924) (Not in this repo though)

There is hopes to expand the mod to include other ideas that are 
currently tucked behind some nasty looking spider webs.
## In game
###### Tuning Fork
```
-I-
-II
S--
I = Iron
S = Stick
```

###### Multi-block Player Piano
if looking face on:
```
JJ
WW
J = Jukebox
W = Oak Wooden Planks
```
Then it must be hit by the _Tuning Fork_ in the bottom left corner.

- Right click with a _piano roll_ to load in Piano.
- Right click with empty hand to start and stop (if a _piano roll_ is 
loaded)
- Shift-Right click with empty hand to unload the Piano.
- Using a comparator on the bottom left block allows you to find the 
current position of the song.

###### Spawning Piano Rolls
If you're looking for a particular song and don't want to have to find
it through looting chests, if you have *op* power call _listPianoRolls_
in game with search parameters such as...
```
\listPianoRolls t"name"d"1905"m
```
The command takes in matched pairs, case insensitive. eg. _t"as"_ The 
't' before the string. Currently supports prefixes...
```
t = Title
c = Composer
d = Date
m = Manufacturer
```
The command will spawn the roll if there is only one match.

## Config File
Check out the config file if you have any issues with the mod in game.

To loud?
Adjust the max volume value between 127 - 0.
```
piano_player {
    I:b7_max_vol=111
            .
            .
            .
}
```

Want to hear the Piano from a klick away, fiddle with this number.
```
piano_player {
    I:i_start_position_audio_fall_off=35
            .
            .
            .
}
```

## IMC Support
The mod currently allows other mods to add songs to the Player Piano 
using IMC events.

To add songs the other mod must send a msg to this mod with the id of 
_register.playerPiano.roll.list.json_ (currently the only one 
implemented) with a string key of the resource location to a gzip json 
file which contains midi details of the songs.

IMC support still needs much improvement, but this comes hand in hand 
with display msg fixes.

## Building the mod
In order to build this mod the user must understand the basics of 
setting up modding workspace and dealing with this mod. 

1. clone repo.
2. setup workspace, making sure that in the _build.properties_ that
_structure_local_enabled_ is set to _false_, unless you know what you're
doing.
```
gradle setupDecompWorkspace
```
3. code and build to one's delight!
4. be confused as to why there isn't any songs.
#### Adding Songs
5. create a _midi_ folder at the root of the project.
6. add songs of your choosing. (They must contain the correct metadata
within the file as the building set won't work. Also note that this 
works with piano instruments. Not using a piano may cause the it to
bork because the midi msg are not completely sanitised yet)
7. Run the main function in _utility.midi.FileSupporter.main_
(_Please_ note that the entire midi details system and details display
 needs to be revamped, so if it doesn't work you can add the song 
 manually with _utility.Utility.registerAdditionalPianoRoll_)
8. After that deal with errors and run the mod to your enjoyment!
