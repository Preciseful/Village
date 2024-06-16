# Village
Minecraft plugin for a small, simple job system. It keeps track of players jobs, levels and their balances.

## What are the commands?
### /<job\> [set|delete|create|promote|levelup]

`/job set` expects an argument: a valid job. All valid jobs should pop up in the auto completion. **NOTE!!!!** Setting your job as a new one will reset the level, you cannot go back. The balance will remain however. </br>
`/job delete` expects an argument: a valid job. The job provided will be deleted. Needed permission: `village.modify`. </br>
`/job create` expects two arguments: a job name and an associated item with it. The item is what is gonna be spent to level up or add money to their balance. Needed permission: `village.modify`. </br>
`/job promote` expects no arguments: it will take the associated item out of their hands and use it to rank up. </br>
`/job levelup` expects no arguments: it will take the associated item out of their hands and use it to add money to their balance. (just like promote)

## Permissions
`village.modify`: permission needed to create and delete jobs. Turned on by default for operators.