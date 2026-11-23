# Allay Item Reservation

A lightweight Minecraft mod that coordinates nearby Allays so they don't swarm the same dropped item.

- In vanilla Minecraft, multiple Allays frequently cluster and fly toward the exact same dropped item, competing for it while leaving other nearby items ignored.
- This mod coordinates them by **reserving items as Allays target them**, directing other Allays to search for different items in the area.
- Reservations are released automatically when an item is collected, moves out of range, or an Allay changes targets.

## Features

- Prevents multiple Allays from targeting and chasing the same dropped item entity.
- Significantly improves item collection efficiency when working with multiple Allays.
- Automatically handles stale reservations if an Allay dies, unloads, or changes targets.

## Requirements

- Fabric API (Fabric) / NeoForge

## Note

- This mod is server-side only. 
- For multiplayer, the mod must be installed on the server. 
- Clients do not need the mod unless it's a single-player world.

## LICENSE

This project is licensed under the [MIT License](LICENSE).
