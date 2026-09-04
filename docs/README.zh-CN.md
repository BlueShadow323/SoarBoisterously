# SoarBoisterously

[English](../README.md) | 简体中文 | [繁體中文](README.zh-TW.md)

![Minecraft](https://img.shields.io/badge/Minecraft-26.1%20%7C%2026.2-62BE47?logo=minecraft&logoColor=white)
![Fabric Loader](https://img.shields.io/badge/Fabric_Loader-0.19.x-dbd0b4?logo=fabricmc&logoColor=white)
![Fabric API](https://img.shields.io/badge/Fabric_API-0.145%20%7C%200.159-dbd0b4?logo=fabricmc&logoColor=white)
![Java](https://img.shields.io/badge/Java-25-007396?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.x-02303A?logo=gradle&logoColor=white)
![Stonecutter](https://img.shields.io/badge/Stonecutter-0.9.8-8A2BE2)
![License](https://img.shields.io/badge/License-GPL--3.0-blue)

一个面向 Minecraft 26.1 / 26.2 的 Fabric 客户端模组：**大声喊出来，起飞！** 对着麦克风说话的声音越大，上升速度越快——不需要鞘翅，不需要烟花，只需要你的声音。

## 功能

- **语音飞行** —— 麦克风音量超过阈值就会上升，声音越大飞得越快
- **随时开关** —— 按 `V` 启用/禁用，动作栏提示 + 专属音效反馈
- **配置界面** —— 按 `O` 打开：飞行开关、摔落伤害开关，以及阈值 / 最大升力 / 麦克风灵敏度滑块，附带实时音量读数
- **自适应降噪** —— 风扇、空调等背景噪音永远不会误触发飞行

## 实现原理

1. **采集** —— 守护线程通过 `javax.sound.sampled` 以 44.1 kHz 16-bit 单声道 PCM 读取默认麦克风。
2. **降噪** —— 一阶高通滤波器（约 100 Hz）去除低频隆隆声；自适应噪音底"上升缓慢、下降瞬时"，保证安静背景永远不触发飞行。
3. **包络** —— 快攻击 / 慢释放的平滑处理把原始 RMS 转换成稳定的 0–1 音量值，约每 23 ms 刷新一次。
4. **飞行** —— 每个客户端 tick，若平滑后的音量超过配置阈值，垂直速度就向目标升力插值（升力与音量成正比）。除非显式开启，飞行期间摔落伤害会被清零。
5. **多版本** —— 单一源码树由 [Stonecutter](https://stonecutter.kikugie.dev/) 预处理，产出 Minecraft 26.1 和 26.2 两个 jar（两个版本间唯一的 API 分歧是 `setScreen`）。

配置以 JSON 形式保存在 `config/soarboisterously.json`。

## 快速开始

### 环境要求

- JDK 25
- Gradle 9.x（仓库未附带 wrapper，请使用本地安装的 Gradle，如 9.5）

### 克隆与构建

```bash
git clone https://github.com/BlueShadow323/SoarBoisterously.git
cd SoarBoisterously
gradle buildAll
```

依赖（Minecraft、Fabric Loader、Fabric API）会在首次构建时自动从 Fabric maven 仓库解析。

构建产物为每个 Minecraft 版本各一个 jar：

```
versions/26.1/build/libs/soarboisterously-<version>+mc26.1.jar
versions/26.2/build/libs/soarboisterously-<version>+mc26.2.jar
```

开发环境测试可运行 `gradle runClient`（构建并启动激活版本，默认 26.2）。

### 安装

1. 为对应 Minecraft 版本安装 [Fabric Loader](https://fabricmc.net/use/)
2. 将 [Fabric API](https://modrinth.com/mod/fabric-api) 放入 `mods` 文件夹
3. 将对应版本的 `soarboisterously-*.jar` 放入 `mods`
4. 进入世界，按 `V`，开始说话

## 许可证

本项目基于 [GPL-3.0](../LICENSE.txt) 许可证开源。
