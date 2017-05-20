# UltimateTS
UltimateTs is a simple plugin, allowed you to interact easly with your TeamSpeak server. With UltimateTS, you can link your accounts (and your ranks), send broadcast on TeamSpeak and more !
You can link your MC accounts and TeamSpeak simply. If you would like to link ranks, you need a permission plugins (like PermissionsEx, zPerms, LuckPerms, Vault,...). You can also choose to link MC accounts and TeamSpeak accounts without linking ranks.
When the TeamSpeak and Minecraft accounts are linked, the player have a custom description like "MC Name: name".

The plugin use this public TeamSpeak 3 Java API: https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API.


-- Commands --

Ingame commands:

    /ts link: Link your Minecraft and TeamSpeak accounts ;
    /ts unlink: Unlink your Minecraft and TeamSpeak accounts ;
    /ts status: Get if your accounts are linked ;
    /ts broadcast: Broadcast a message on TeamSpeak ;
    /ts list (minecraft|teamspeak): Get a list of online users on Minecraft or on TeamSpeak.

TeamSpeak commands:

    $ping: Reply with pong ;
    $info: Send plugin info directly on TeamSpeak ;



-- Permissions --

    ultimatets.*: Gives access to all UltimateTS commands ;
    ultimatets.broadcast: Allowed using /ts broadcast ;
    ultimatets.list.*: View all players online on TeamSpeak and Minecraft ;
    ultimatets.list.teamspeak: View all players online on TeamSpeak ;
    ultimatets.list.minecraft: View all players online on Minecraft ;

-- Configurations --

[SPOILER="config.yml"]
[CODE]login:
  ip: localhost
  username: serveradmin
  password: password
  port: 9987
bot:
  name: UltimateTs
database:
  enable: false
  host: localhost
  name: databaseName
  user: root
  password: password
config:
  kickNotRegister: false
  defaultGroupId: 8
#A value of 0 do not asign rank!
  assignWhenRegister: 0[/CODE]
[/SPOILER]

[SPOILER="messages.yml"]
[CODE]messages:
  poke: '[color=red]You need to register yourself before join TeamSpeak. Use ''/ts
    link'' IG to link your accounts.'
  kick: Please register yourself !
  bot:
    online: Bot Online!
    offline: Bot Offline!
  database:
    online: Connection successfully.
    offline: Successfully disconnected.
  linked:
    sucess: '&bYour TeamSpeak and Minecraft are now linked ! Your MC Name is %mcname%.'
    ready: '&bYour MC account is ready to be link ! Log in into TeamSpeak and get
      your accounts linked !'
    desc: 'MC Name: %mcname%'
    already: '&aYour MC and TeamSpeak are always linked, use /ts unlink to link it.'
    linked: '&aYour MC and TeamSpeak are linked. Use /ts unlink to unlink-it.'
    info: '&bYour TeamSpeak name is %tsname% (DbId: %dbid%).'
  unlinked:
    confirmation:
      confirmation: '&cWarning: Unlink you MC and TeamSpeak account removed all your
        servergroups on TeamSpeak. Do you really want to continue ? If yes, type ''YES''
        in chat (WITHOUT SLASH), else type ''NO''. '
      'yes': '&aReceived reponse YES. &rYour MC and TeamSpeak are now unlinked !'
      'no': '&cReceived reponse NO, canceled.'
    already: '&cYour MC and TeamSpeak aren''t linked yet!'
    unlinked: '&bYour MC and TeamSpeak account aren''t linked. Use /ts link to link-it.'
  broadcast:
    empty: '&cYour message can''t be empty.'
    to:
      teamspeak: '%message%.'
      minecraft: (TeamSpeak) %message%.
      sender: 'You broadcast this message on TeamSpeak: %message%.'
  list:
    minecraft:
      size: 'ยง2There are %n players online:'
      player: '&8- &f%player% &8- &7%uuid%'
      'false': '  &7Linked: &c%linked_true_false%'
      'true': '  &7Linked: &a%linked_true_false% ยง7with &b%linked_name% &8(&7%dbid%&8)'
    teamspeak:
      size: 'ยง2There are %n players online:'
      user: '&8- &f%name% &8(&7%dbid%&8)'[/CODE]
[/SPOILER]


-- Link your accounts --

Before linking your accounts, your Bot need to be configurated (read "Installation" for more infos).

    Go to Minecraft and enter /ts link.
    You will receive a message "Your account is ready ...", perfect.
    Login on TeamSpeak.

Warning: When you login into TeamSpeak, you need to have the default group (set in configuration) and be in the default channel to be checked by the bot ! Else, your be "invisible" for the bot and you can't link your accounts ! If you or your staff have another groups, please remove them before try to link your accounts.

    If all is clear, the bot will put you your ranks, modify your description as well and send you a poke confirming that you have tied your accounts.

Perfect, you can now enjoy yourself !
For somes reason, you can choose to unlink your accounts, you just have to enter /ts unlink and write YES in chat and it's ok. Your accounts are now unlinked.


-- Installation --
[SPOILER="Read installation"]
Before start installing, you need:

    A minecraft server off ;
    A TeamSpeak server running ;
    You Admin Query password (gived when you start your TeamSpeak server for the first time!) ;

If you have any question about Admin Query on TeamSpeak, read this: http://media.teamspeak.com/ts3_literature/TeamSpeak 3 Server Query Manual.pdf.

Warning: To avoid losing access to your TeamSpeak server, it is highly recommended that you create a privilege key BEFORE you start the bot for the first time (key you can use in case of problems).

Now you can start:

    First, download the plugin ;
    Drag and drop the plugin into your /plugins folder ;

Start your server. It's possible that UltimateTs generated error, it's normal. Now, stop your server.

    Now, a new folder will be created (plugins/UltimateTs).
    You can now go to the configuration file (plugins/UltimateTs/config.yml). You need to add you TeamSpeak server IP & your password:

[CODE]login:
#Change by your TeamSpeak server IP (WITHOUT PORT)
  ip: localhost
  username: serveradmin
  password: password
#Port of your TeamSpeak server
  port: 9987
bot:
#A name for the bot !
  name: UltimateTs[/CODE]

    You can choose to use a database (to save your linked players in database) or to save in this file. If you want to use a database, enter your informations here:

[CODE]database:
  enable: false
  host: localhost
  name: databaseName
  user: root
  password: password[/CODE]

Else, leave this.

    You can choose if players were kicked when they joined the TeamSpeak and aren't registered by changing this:

[CODE]config:
#Kick players if they haven't linked her account
  kickNotRegister: false[/CODE]

    Now, you need to change the value of

[CODE]defaultGroupId[/CODE]
by the value of your default group on TeamSpeak (My default rank on TeamSpeak is guest with the id 8).
 

If you want all your linked players to get a special rank on TeamSpeak, you can by changing:
[CODE]asignWhenregister[/CODE] by the id of your TeamSpeak ranks.

Now, you need to define linked TeamSpeak ranks for your players. The ranks system is baised on permissions. Add this:
[CODE]  perms:
    exemplePerms: 10[/CODE]
exemplePerms is your permission and 10 is the ranks to link. In each permissions plugin, all ranks had the permission ranks.<name of rank>. Then, to link your ranks, add the permission ranks.<name of rank> and the id of your TeamSpeak ranks.

You can add an infinit numbers of permissions !

[SPOILER="Example for my server"]
[CODE]perms:
  rank.admin: 7
  rank.mod: 9
  rank.vip: 10
  mypermission: 12[/CODE]
Admin will get the TeamSpeak rank 7, Mod: 9, VIP: 10 and players with permission "mypermission": 12.
[/SPOILER]

    When you are finish configs all ranks and messages (you can modify all messages in UltimateTs/messages.yml), you can save and run you server !

Warning: Before editing configuration, ALWAYS STOP your server.

    To be sure that the Bot is online, there are several solutions:

           - You can check the console, if she said "Bot online!" when              starting the server, it's ok !

           -  Click on your TeamSpeak server. If "Current Query" equals one, it's ok.
           - Go to the default channel and type $ping. If the bot reply with "pong!", it's ok.

That's perfect, your players can now link there accounts. If you have any problem, question or sugest, open a new issues on github: https://github.com/DiscowZombie/UltimateTs/issues.
[/SPOILER]

-- Help & support --

If you need some help, open a new issues on github: https://github.com/DiscowZombie/UltimateTs/issues.
Source code available: https://github.com/DiscowZombie/UltimateTs.

Have fun and don't forgot to give 5 stars if you like the plugin :D
