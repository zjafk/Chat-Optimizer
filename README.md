This mod is entirely written by AI.

Chat Optimizer — Player Guide
Mod Introduction
Chat Optimizer is a client-side-only chat enhancement mod designed to streamline in-game chatting in Minecraft. It features six core functions: unlimited input length, chat history, timestamps, right-click copy, local search, and auto-run commands. Every feature can be toggled freely via in-game commands.

Feature Overview
Unlimited Input
The chat box removes the original 256-character limit, supporting up to 65,536 characters by default (adjustable in the config file). Long messages and bulk pasted text will no longer get truncated.

Timestamps
Each incoming chat message is prefixed with a timestamp formatted as [HH:mm:ss] (visible only locally on your screen; no changes sent to the server or other players). This feature can be disabled at any time with a command.

Chat History
The mod automatically logs all sent and received chat messages:

Auto-save: History is saved as an individual JSON file per server to config/chat_optimizer_mod_1781247740/history/ upon exiting a server
Auto-load: Corresponding history records load automatically when rejoining a server
Memory cap: Only the most recent 1,000 messages are retained
Cleanup rule: History can be manually purged via command; the default rule discards the oldest messages once the 1,000-entry limit is reached
Right-Click Copy
Right-click any chat message in the chat screen to copy its full content to your clipboard. Formatted text and raw JSON data are both supported for copying.

Local Search
Run /chatoptimizer search <keyword> to open the search interface. You can filter chat history by player name or message content, scroll through results with the mouse wheel, and view live updated matches.

Auto Commands
Predefined command sequences run automatically after joining a server (/login and /l are included by default). This eliminates repetitive manual input for login-based servers.

Delayed execution: Commands fire after a 5-tick delay (~0.25 seconds) by default to ensure stable server connection
Add or remove auto commands anytime via dedicated commands
Hardware-encrypted storage is available (toggleable in config)
Full Command List
All commands start with /chatoptimizer followed by subcommands listed below:

Command	Function
/chatoptimizer help	Show help documentation
/chatoptimizer toggle	Toggle all features on or off with one click
/chatoptimizer status	Check the toggle state of every individual feature
/chatoptimizer timestamp on|off	Enable or disable message timestamps
/chatoptimizer history	View chat history status summary
/chatoptimizer history clear	Wipe all stored chat history
/chatoptimizer history count	Check the number of messages loaded in memory
/chatoptimizer search <keyword>	Open the history search interface
/chatoptimizer autocommand	Open the auto command management menu
/chatoptimizer autocommand list	List all saved auto-run commands
/chatoptimizer autocommand add <command>	Add a new auto command (must start with /)
/chatoptimizer autocommand remove <command>	Delete a specified auto command
Configuration
Most settings can also be modified directly in the client config file located at config/chat_optimizer_mod_1781247740-client.toml, including:

Master global toggle switch
Maximum allowed input character count
Timestamp toggle and display modes (in-chat only / log-to-file only / both enabled)
Chat history toggle and auto-save settings
Auto command list and execution delay (in ticks)
Notes & Precautions
This is a client-only mod; no server-side installation is required. All modifications are local and invisible to other players.
Chat history files are stored locally on your drive and will be lost after switching devices or reinstalling the game. Regularly back up the config/chat_optimizer_mod_1781247740/history/ folder as needed.
Enable the encryption feature if your auto commands contain sensitive data such as passwords.
The default auto command delay is 5 ticks. Increase this value in the config if the server experiences severe lag during the login sequence.
