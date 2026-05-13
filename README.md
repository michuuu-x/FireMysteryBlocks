<div align="center">

<img src="images/logo.jpg" alt="FireMysteryBlocks" width="180"/>

# FireMysteryBlocks

**A highly configurable Spigot/Paper plugin that turns ordinary blocks into competitive, reward-driven "mystery" blocks.**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.16%E2%80%931.20.1-brightgreen)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://adoptium.net/)
[![Build](https://img.shields.io/badge/Build-Gradle-blue)](https://gradle.org/)
[![Version](https://img.shields.io/badge/Version-2.3.16-yellow)](#)

</div>

---

## Table of Contents

- [About](#about)
- [Features](#features)
- [Showcase](#showcase)
- [Requirements](#requirements)
- [Installation](#installation)
- [Building from Source](#building-from-source)
- [Configuration](#configuration)
- [Commands](#commands)
- [Permissions](#permissions)
- [PlaceholderAPI Placeholders](#placeholderapi-placeholders)
- [Action System](#action-system)
- [Hologram Providers](#hologram-providers)
- [Contributing](#contributing)
- [License](#license)
- [Credits](#credits)

---

## About

**FireMysteryBlocks** lets server administrators place special "mystery" blocks
in the world that act as shared, server-wide objectives. Players compete to mine
them, earn rewards based on their contribution, and trigger custom actions on
mine, destroy, regeneration, and more.

Every block is defined by its own YAML file, so you can mix slow community
events, fast PvP mining races, scheduled daily blocks, GUI-based info panels,
and anti-cheat-protected grinding blocks within the same server.

## Features

- **Per-block configuration** — every mystery block lives in its own file under
  `blocks/` and can be tuned independently.
- **Top-player tracking** — keeps a leaderboard of who mined the most, with
  configurable reward tiers per position and per amount mined.
- **Rewards on mine, destroy, reset, and regenerate** — commands, messages,
  broadcasts, sounds, titles, action bars, effects, and more.
- **Offline reward cache** — actions prefixed with `$` are stored and replayed
  when the player logs back in.
- **Delayed and probabilistic actions** — `@<ms>@` delays and `%<chance>%`
  percentage gates can be combined with any action.
- **Regeneration** — instant or progressive healing of blocks after a period of
  inactivity.
- **Cooldown** — temporarily swap the block to an unbreakable material once
  destroyed and swap it back on schedule.
- **Schedule** — restrict respawn to specific times of day with timezone
  support, and optionally auto-destroy if not mined in time.
- **Hologram support** — hook into CMI, HolographicDisplays, DecentHolograms or
  FancyHolograms to display live stats above each block.
- **GUI** — fully customizable inventory GUI per block, openable with a command
  or by clicking the block.
- **PlaceholderAPI** — exposes a rich set of placeholders for use in scoreboards,
  chat plugins, tab lists, holograms, and more.
- **Item damage rules** — control pickaxe durability loss, ignore unbreaking,
  and define damage modifiers from enchantments and effects.
- **Enchantment limit** — restrict which tools may mine a block.
- **Mining effects** — apply constant or random potion effects while mining
  (including the `FREEZE` effect on 1.19+).
- **Force field** — knock back or block players standing too close to a block.
- **Visibility** — hide nearby players while someone is actively mining.
- **Anti-cheat** — measure mine speed with enchantment/effect-aware modifiers
  and trigger configurable actions on violations.
- **Anti-AFK** — `KNOCKBACK` or `CAPTCHA` interruptions to discourage AFK
  mining.
- **History** — keep the last N destruction records per block with timestamps
  and leaderboards.
- **Click actions** — left-click a block to trigger commands (e.g. open its
  GUI).
- **Storage** — SQLite by default, with optional MySQL and HikariCP pools.
- **Auto-save** — periodic database flush to minimize rollback on crash.

## Showcase

<div align="center">

<img src="images/Showcase.png" alt="Showcase" width="640"/>

</div>

## Requirements

| Component        | Version                                |
| ---------------- | -------------------------------------- |
| Server software  | Spigot / Paper                         |
| Minecraft        | 1.16 – 1.20.1 (built against 1.20.1)   |
| Java             | 17 or newer                            |

Optional integrations (auto-detected as soft dependencies):

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [CMI](https://www.spigotmc.org/resources/cmi-167-commands-economy-portals-essentials-anti-grief-anti-cheat.3742/) + CMILib
- [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays)
- [DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-20-2-papi-support-no-dependencies.96927/)
- [FancyHolograms](https://modrinth.com/plugin/fancyholograms)

## Installation

1. Download the latest `FireMysteryBlocks-<version>.jar` from the
   [Releases](../../releases) page (or build it yourself — see below).
2. Drop the jar into your server's `plugins/` directory.
3. Start the server once so the default configuration is generated.
4. Edit `plugins/FireMysteryBlocks/config.yml` and the example block in
   `plugins/FireMysteryBlocks/blocks/` to suit your server.
5. Run `/mb reload` (or restart) to apply changes.

## Building from Source

```bash
git clone https://github.com/Firestone82/FireMysteryBlocks.git
cd FireMysteryBlocks
./gradlew build
```

The shaded jar is written to `target/<version>/FireMysteryBlocks-<version>.jar`.

## Configuration

The plugin generates the following files on first run:

```
plugins/FireMysteryBlocks/
├── config.yml           # Global settings (database, holograms, placeholders…)
├── lang.yml             # All plugin messages
├── cache.yml            # Offline reward cache
├── history.yml          # Destruction history
└── blocks/
    └── first.yml        # Example block definition
```

To create a new mystery block run `/mb create <name>` while looking at the
block you want to register. A new `<name>.yml` file appears in the `blocks/`
folder and can be tuned freely. See [`src/main/resources/blocks/first.yml`](src/main/resources/blocks/first.yml)
for a fully annotated reference of every available option.

## Commands

The base command is `/mysteryblocks`, with aliases `/firemysteryblocks`,
`/mb` and `/fmb`.

| Command                              | Description                                      |
| ------------------------------------ | ------------------------------------------------ |
| `/mb help`                           | Shows the in-game help screen                    |
| `/mb info`                           | Plugin info                                      |
| `/mb list`                           | Lists all configured mystery blocks              |
| `/mb create <name>`                  | Creates a new mystery block at the targeted block|
| `/mb delete <name>`                  | Deletes an existing mystery block                |
| `/mb set`                            | Repositions an existing mystery block            |
| `/mb tp <name>`                      | Teleports you to a mystery block                 |
| `/mb open <name> [player]`           | Opens the block's GUI                            |
| `/mb reset <name>`                   | Resets an individual block's progress            |
| `/mb resetall`                       | Resets every block's progress                    |
| `/mb finish <name>`                  | Force-finishes a block (triggers destroy actions)|
| `/mb history <block> <entry>`        | Shows a stored history entry                     |
| `/mb breaks-add <block> <n> [player]`| Adds breaks to a block (admin / debugging tool)  |
| `/mb visibility`                     | Toggles player visibility near blocks for you    |
| `/mb messages`                       | Toggles receiving plugin messages                |
| `/mb bypass`                         | Prints item info usable in bypass lists          |
| `/mb reload`                         | Reloads configuration files                      |

## Permissions

Command permissions follow the pattern `firemysteryblocks.command.<command>`:

```
firemysteryblocks.command.help
firemysteryblocks.command.list
firemysteryblocks.command.create
firemysteryblocks.command.delete
firemysteryblocks.command.set
firemysteryblocks.command.teleport
firemysteryblocks.command.open
firemysteryblocks.command.reset
firemysteryblocks.command.resetall
firemysteryblocks.command.finish
firemysteryblocks.command.history
firemysteryblocks.command.breaks-add
firemysteryblocks.command.visibility
firemysteryblocks.command.messages
firemysteryblocks.command.bypass
firemysteryblocks.command.reload
```

Per-block permissions (replace `<block>` with the file name without `.yml`):

```
firemysteryblocks.<block>.mine                        # required to mine, if Permission: true
firemysteryblocks.<block>.bypass.item-damage          # do not lose pickaxe durability
firemysteryblocks.<block>.bypass.enchant-limit        # ignore enchantment limit
firemysteryblocks.<block>.bypass.mining-effects       # ignore mining effects
firemysteryblocks.<block>.bypass.anti-cheat           # ignore anti-cheat checks
firemysteryblocks.<block>.bypass.anti-afk             # ignore anti-AFK checks
firemysteryblocks.warn                                # receive [WARN] messages
```

## PlaceholderAPI Placeholders

Available when [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
is installed:

```
%FireMysteryBlocks_<BLOCK>_REQUIRED%
%FireMysteryBlocks_<BLOCK>_CURRENT_<ASC/DESC>%
%FireMysteryBlocks_<BLOCK>_PLAYER%
%FireMysteryBlocks_<BLOCK>_POSITION_<1-X>_<NAME/MINES>%
%FireMysteryBlocks_<BLOCK>_PROGRESS_<BAR/PERCENTAGE>%
%FireMysteryBlocks_<BLOCK>_DESTROYS%
%FireMysteryBlocks_<BLOCK>_COOLDOWN_ACTIVE%
%FireMysteryBlocks_<BLOCK>_COOLDOWN_CURRENT_<FORMATTED/SHORT/PLAIN>%
%FireMysteryBlocks_<BLOCK>_SCHEDULE_<PREV/NEXT>%
%FireMysteryBlocks_<BLOCK>_SCHEDULE_<PREV/NEXT>_<FORMAT>%
%FireMysteryBlocks_<BLOCK>_SCHEDULE_REMAINING_<FORMATTED/SHORT/PLAIN>%
%FireMysteryBlocks_<BLOCK>_HISTORY_<1-X>_DATE%
%FireMysteryBlocks_<BLOCK>_HISTORY_<1-X>_SIZE%
%FireMysteryBlocks_<BLOCK>_HISTORY_<1-X>_POSITION_<1-X>_MINES%
%FireMysteryBlocks_<BLOCK>_HISTORY_<1-X>_POSITION_<1-X>_NAME%
%FireMysteryBlocks_<BLOCK>_REGENERATION_ACTIVE%
%FireMysteryBlocks_<BLOCK>_REGENERATION_AMOUNT%
%FireMysteryBlocks_<BLOCK>_REGENERATION_CURRENT_<FORMATTED/SHORT/PLAIN>%
```

**Legend**

| Token        | Meaning                                          |
| ------------ | ------------------------------------------------ |
| `ASC`/`DESC` | Sort order                                       |
| `BAR`        | Progress as an ASCII bar                         |
| `PERCENTAGE` | Progress as a number                             |
| `PREV`/`NEXT`| Previous / next scheduled date                   |
| `FORMAT`     | Formatted date (`dd.MM.yyyy HH:mm:ss`)           |
| `FORMATTED`  | `02:16:05`                                       |
| `SHORT`      | `2h 16m 5s`                                      |
| `PLAIN`      | Seconds (e.g. `8156`)                            |
| `1-X`        | Index of the player on the leaderboard           |
| `NAME`       | Player name                                      |
| `MINES`      | Number of mines                                  |

## Action System

Any list of actions in `blocks/*.yml` follows the same format:

```
[TYPE] value
```

| Type               | Description                                          |
| ------------------ | ---------------------------------------------------- |
| `[COMMAND]`        | Runs a command as the player                         |
| `[OP-COMMAND]`     | Runs a command as a temporarily op'd player          |
| `[CONSOLE-COMMAND]`| Runs a command from the console                      |
| `[MESSAGE]`        | Sends a chat message to the player                   |
| `[BROADCAST]`      | Broadcasts a message to everyone                     |
| `[SOUND]`          | Plays a sound — `SOUND-VOLUME-PITCH`                 |
| `[ACTIONBAR]`      | Shows an action bar message                          |
| `[WARN]`           | Sends a warning to staff with `firemysteryblocks.warn` |
| `[TITLE]`          | Shows a title (use `\n` to split lines)              |
| `[EFFECT]`         | Applies a potion effect — `EFFECT-AMPLIFIER-DURATION`|

Prefixes that can be combined (in **cache → delay → percentage** order):

- `$` — **cache**: if the player is offline, store the action and replay it on
  next login.
- `@<ms>@` — **delay**: postpone execution by `<ms>` milliseconds.
- `%<chance>%` — **probability**: execute only with the given chance (0–100).

```yaml
- "[CONSOLE-COMMAND] $@500@%50%give {2} diamond 5"
```

A full list of placeholders available inside action values (`{0}` … `{19}`,
`{pos-N-name}`, `{history-N-pos-M-value}`, …) is documented inline at the top
of [`src/main/resources/blocks/first.yml`](src/main/resources/blocks/first.yml).

## Hologram Providers

Enable holograms in `config.yml`:

```yaml
Settings:
  Holograms:
    Enabled: true
    Provider: DecentHolograms   # CMI | HolographicDisplays | DecentHolograms | FancyHolograms
```

Each provider has its own block-level options under `Hologram.Providers.*`.
See the example block for the full list of tunables.

## Contributing

Contributions are welcome. The project uses standard Gradle conventions:

1. Fork the repository and create your branch from `main`.
2. Run `./gradlew build` to verify everything compiles.
3. Open a Pull Request describing the change and, if relevant, how to test it.

If you would like to add support for a new hologram provider, look at
[`Hologram/Providers/`](src/main/java/cz/devfire/mysteryblocks/Hologram/Providers)
— each provider implements the same small interface.

Bug reports and feature requests are tracked in [GitHub Issues](../../issues).

## License

This project is open source. See the [`LICENSE`](LICENSE) file for details.
If no license file is present yet, please open an issue so one can be added
before redistributing modified builds.

## Credits

- Created and maintained by **Firestone82**.
- Built on top of the excellent [Spigot](https://www.spigotmc.org/) API.
- Bundles [IridiumColorAPI](https://github.com/PeachesMLG/IridiumColorAPI) for
  HEX, gradient and rainbow color support.
- Uses [HikariCP](https://github.com/brettwooldridge/HikariCP) for optional
  MySQL connection pooling.
- Thanks to everyone who reported bugs and suggested features over the years.
