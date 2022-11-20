package cz.devfire.mysteryblocks.api.Commands;

import java.util.List;

public interface CommandsHandler {
    List<ICommand> getSubCommands();
}
