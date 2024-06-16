# Village
Minecraft plugin for a small, simple job system. It keeps track of players jobs, levels and their balances.

## What are the commands?
### /<job\> [set|delete|create|promote|levelup]

`/job set` expects an argument: a valid job. All valid jobs should pop up in the auto completion. </br>
`/job delete` expects an argument: a valid job. The job provided will be deleted. Needed permission: `village.modify` (default for ops). </br>
`/job create` expects two arguments: a job name and an associated item with it. The item is what is gonna be spent to level up or add money to their balance.