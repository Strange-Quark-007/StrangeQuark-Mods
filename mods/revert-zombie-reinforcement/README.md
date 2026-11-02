# Revert Zombie Reinforcement

A lightweight Minecraft mod that restores the zombie reinforcement behavior from version pre-1.21.2.

- As of Minecraft 1.21.2+, zombies spawn reinforcements of the _same subtype_ (e.g., drowned, husk), which hurts
  zombie-reinforcement-based copper farms.
- This mod reverts the change i.e. **all reinforcements now spawn as regular `ZombieEntity`**
- Configurable by a gamerule `revertZombieReinforcement` (default `true`)

## Features

- Reinforcements always spawn as regular zombies, never as drowned or husks.
- Significantly improves the efficiency and reliability of copper farms that rely on zombie reinforcements

## Multi-Loader Support

- This mod now supports fabric, forge and neo-forge with multi-loader setup.

## Note

- This is a server-side only mod.
- For multiplayer, the mod must be installed on the server.
- Clients do not need the mod unless it's a single-player world.

## LICENSE

This project is licensed under the [MIT License](LICENSE).
