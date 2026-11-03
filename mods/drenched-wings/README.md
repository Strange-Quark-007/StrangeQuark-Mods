# Drenched Wings

Minecraft mod for Fabric and NeoForge that blocks elytra use in liquids, applies environmental penalties, and enforces cooldowns.
Fully configurable through server gamerules.

Inspired by **cubfan135’s “[Minecraft’s Elytra Problem](https://www.youtube.com/watch?v=fBbtP9GZITM)”** — implements his
proposed liquid-based Elytra limitations as a configurable, server-side mod.

## Core Mechanics

**Environmental Nerfs**

- **Configurable cooldown** (default: 20 seconds, max: 30s) for elytra in rain, water, or lava.
- **Firework boosts disabled** during rain.

**Splash-Water Cooldown**

- Hitting a player with a **splash water bottle** disables elytra for **up to the configured cooldown** (default: 20s), scaled by proximity.

**Server-Side Control**

- All effects configurable via server game rules.
- Fully **vanilla-compliant** using Minecraft’s `ItemCooldowns` system.

## Game Rules

- `do_elytra_nerf` – Global toggle for all elytra nerfs (default: true)
- `disable_elytra_in_rain` – Disables elytra in rain (default: true)
- `disable_elytra_in_water` – Disables elytra in water (default: true)
- `disable_elytra_in_lava` – Disables elytra in lava (default: true)
- `disable_rockets_in_rain` – Prevent firework rockets boost in rain (default: true)
- `apply_elytra_cooldown_from_splash_water` – Apply cooldown when hit by splash water bottle (default: true)
- `elytra_cooldown_seconds` – Elytra cooldown duration in seconds, range [0, 30] (default: 20)

## License

This project is licensed under the [MIT License](LICENSE).
