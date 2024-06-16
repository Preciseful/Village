# Village
Minecraft plugin for a small, simple job system. It keeps track of players jobs, levels and their balances.

## What are the commands?
### /<job\> [set|delete|create|promote|levelup]

* `/job set` expects an argument: a valid job. All valid jobs should pop up in the auto completion. **NOTE!!!!** Setting your job as a new one will reset the level, you cannot go back. The balance will remain however.
* `/job delete` expects an argument: a valid job. The job provided will be deleted. Needed permission: `village.modify`.
* `/job create` expects two arguments: a job name and an associated item with it. The item is what is gonna be spent to level up or add money to their balance. Needed permission: `village.modify`.
* `/job promote` expects no arguments: it will take the associated item out of their hands and use it to rank up.
* `/job levelup` expects no arguments: it will take the associated item out of their hands and use it to add money to their balance. (just like promote)

## Permissions
`village.modify`: permission needed to create and delete jobs. Turned on by default for operators.

## Downsides
* All data is saved most of the time in RAM. For small servers, this barely matters. For big servers however, it can become quite a waste, as it is saving even non-online users jobs in RAM.
This does lead to faster job setting up, but usually it's not worth it. (there is a save file, but it's used to save data when the server restarts. however with a bit of tinkering, you could switch to using the saved data rather than saving all of it in RAM)
* Saved data is not encrypted. This is because I don't expect anyone to use this, it's just a silly little idea I made.
