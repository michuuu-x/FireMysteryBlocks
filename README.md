```
Placeholders:
  » %FireMysteryBlocks_<BLOCK>_REQUIRED% - Return number of required block mines
  » %FireMysteryBlocks_<BLOCK>_CURRENT_<ASC/DESC>% - Return number of current block mines
  » %FireMysteryBlocks_<BLOCK>_PLAYER% - Return number of player mines
  » %FireMysteryBlocks_<BLOCK>_POSITION_<1-X>_<NAME/MINES>% - Return data of position
  » %FireMysteryBlocks_<BLOCK>_PROGRESS_<BAR/PERCENTAGE>% - Return progress of block mined
  » %FireMysteryBlocks_<BLOCK>_DESTROYS% - Return number of how many times block was destroyed
  » %FireMysteryBlocks_<BLOCK>_COOLDOWN_ACTIVE% - Return boolean if cooldown is active
  » %FireMysteryBlocks_<BLOCK>_COOLDOWN_CURRENT_<FORMATTED/SHORT/PLAIN>% - Return string of current cooldown time
  » %FireMysteryBlocks_<BLOCK>_SCHEDULE_<PREV/NEXT>% - Return date, when will be block regenerated at.
  » %FireMysteryBlocks_<BLOCK>_SCHEDULE_<PREV/NEXT>_<FORMAT>% - Return formatted date, when will be block regenerated at.
  » %FireMysteryBlocks_<BLOCK>_SCHEDULE_REMAINING_<FORMATTED/SHORT/PLAIN>% - Return string of remaining time to regeneration
  » %FireMysteryBlocks_<BLOCK>_HISTORY_<1-X>_DATE% - Return string of date, when was block destroyed
  » %FireMysteryBlocks_<BLOCK>_HISTORY_<1-X>_SIZE% - Return number of history saved
  » %FireMysteryBlocks_<BLOCK>_HISTORY_<1-X>_POSITION_<1-X>_MINES% - Return history player mines
  » %FireMysteryBlocks_<BLOCK>_HISTORY_<1-X>_POSITION_<1-X>_NAME% - Return history player name
  » %FireMysteryBlocks_<BLOCK>_REGENERATION_ACTIVE% - Return boolean if regeneration is active
  » %FireMysteryBlocks_<BLOCK>_REGENERATION_AMOUNT% - Return number of regenerated health
  » %FireMysteryBlocks_<BLOCK>_REGENERATION_CURRENT_<FORMATTED/SHORT/PLAIN>% - Return string of current regeneration time

Legend:
  » ASC - In ascending order
  » DESC - In descending order
  » BAR - Return progress as ASCII bar
  » PERCENTAGE - Return progress as number
  » PREV - Return previous date
  » NEXT - Return next date
  » FORMAT - Return date in format (dd.MM.yyyy HH:mm:ss)
  » FORMATTED - 02:16:05
  » SHORT - 2h 16m 5s
  » PLAIN - 8156
  » 1-X - Position of player
  » NAME - Return name of player
  » MINES - Return number of mines of player
```