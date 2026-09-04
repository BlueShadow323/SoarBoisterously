# SoarBoisterously

English | [简体中文](docs/README.zh-CN.md) | [繁體中文](docs/README.zh-TW.md)

![Minecraft](https://img.shields.io/badge/Minecraft-26.1%20%7C%2026.2-62BE47?logo=minecraft&logoColor=white)
![Fabric Loader](https://img.shields.io/badge/Fabric_Loader-0.19.x-dbd0b4?logo=fabricmc&logoColor=white)
![Fabric API](https://img.shields.io/badge/Fabric_API-0.145%20%7C%200.159-dbd0b4?logo=fabricmc&logoColor=white)
![Java](https://img.shields.io/badge/Java-25-007396?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.x-02303A?logo=gradle&logoColor=white)
![Stonecutter](https://img.shields.io/badge/Stonecutter-0.9.8-8A2BE2)
![License](https://img.shields.io/badge/License-GPL--3.0-blue)

<p align="center">
  <img src="docs/assets/icon.png" width="128" alt="SoarBoisterously logo">
</p>

A client-side Fabric mod for Minecraft 26.1 / 26.2: **speak up and take off**. The louder you talk into your microphone, the faster you rise — no elytra, no rockets, just your voice.

## Features

- **Voice flight** — mic volume above the threshold lifts you; louder voice means faster ascent
- **Toggle on the fly** — press `V` to enable/disable, with action-bar feedback and a sound cue
- **Config screen** — press `O` to open: toggle flight, toggle fall-damage suppression, and tune threshold / max lift / mic sensitivity with live volume readout
- **Adaptive noise gating** — background noise never triggers flight, even with fans or AC running

## How it works

1. **Capture** — a daemon thread reads 44.1 kHz 16-bit mono PCM from the default microphone via `javax.sound.sampled`.
2. **Denoise** — a one-pole high-pass filter (~100 Hz) strips rumble; an adaptive noise floor that rises slowly but drops instantly keeps quiet backgrounds from triggering flight.
3. **Envelope** — fast attack / slow release smoothing turns raw RMS into a stable 0–1 volume value, refreshed roughly every 23 ms.
4. **Flight** — every client tick, if the smoothed volume exceeds the configured threshold, vertical velocity is interpolated toward a target lift scaled by how loud you are. Fall damage is zeroed during flight unless explicitly enabled.
5. **Multi-version** — a single source tree is preprocessed by [Stonecutter](https://stonecutter.kikugie.dev/) into two jars targeting Minecraft 26.1 and 26.2 (the only real API divergence is `setScreen` between the two versions).

Configuration is stored as JSON at `config/soarboisterously.json`.

## Getting started

### Prerequisites

- JDK 25
- Gradle 9.x (no wrapper is bundled — use a local Gradle installation, e.g. 9.5)

### Clone and build

```bash
git clone https://github.com/BlueShadow323/SoarBoisterously.git
cd SoarBoisterously
gradle buildAll
```

Dependencies (Minecraft, Fabric Loader, Fabric API) are resolved automatically from the Fabric maven repository on the first build.

The build produces one jar per Minecraft version:

```
versions/26.1/build/libs/soarboisterously-<version>+mc26.1.jar
versions/26.2/build/libs/soarboisterously-<version>+mc26.2.jar
```

To test in a dev environment, run `gradle runClient` (builds and launches the active version, 26.2 by default).

### Install

1. Install [Fabric Loader](https://fabricmc.net/use/) for your Minecraft version
2. Drop [Fabric API](https://modrinth.com/mod/fabric-api) into your `mods` folder
3. Drop the matching `soarboisterously-*.jar` into `mods`
4. Join a world, press `V`, and start talking

## License

This project is licensed under the [GPL-3.0](LICENSE.txt).
