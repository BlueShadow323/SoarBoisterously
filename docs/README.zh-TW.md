# SoarBoisterously

[English](../README.md) | [简体中文](README.zh-CN.md) | 繁體中文

![Minecraft](https://img.shields.io/badge/Minecraft-26.1%20%7C%2026.2-62BE47?logo=minecraft&logoColor=white)
![Fabric Loader](https://img.shields.io/badge/Fabric_Loader-0.19.x-dbd0b4?logo=fabricmc&logoColor=white)
![Fabric API](https://img.shields.io/badge/Fabric_API-0.145%20%7C%200.159-dbd0b4?logo=fabricmc&logoColor=white)
![Java](https://img.shields.io/badge/Java-25-007396?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.x-02303A?logo=gradle&logoColor=white)
![Stonecutter](https://img.shields.io/badge/Stonecutter-0.9.8-8A2BE2)
![License](https://img.shields.io/badge/License-GPL--3.0-blue)

<p align="center">
  <img src="assets/icon.png" width="128" alt="SoarBoisterously logo">
</p>

一個面向 Minecraft 26.1 / 26.2 的 Fabric 用戶端模組：**大聲喊出來，起飛！** 對著麥克風說話的聲音越大，上升速度越快——不需要鞘翅，不需要煙火，只需要你的聲音。

## 功能

- **語音飛行** —— 麥克風音量超過閾值就會上升，聲音越大飛得越快
- **隨時開關** —— 按 `V` 啟用/停用，動作列提示 + 專屬音效回饋
- **設定介面** —— 按 `O` 開啟：飛行開關、摔落傷害開關，以及閾值 / 最大升力 / 麥克風靈敏度滑桿，附帶即時音量讀數
- **自適應降噪** —— 風扇、空調等背景噪音永遠不會誤觸發飛行

## 實作原理

1. **採集** —— 常駐執行緒透過 `javax.sound.sampled` 以 44.1 kHz 16-bit 單聲道 PCM 讀取預設麥克風。
2. **降噪** —— 一階高通濾波器（約 100 Hz）去除低頻隆隆聲；自適應噪音底「上升緩慢、下降即時」，保證安靜背景永遠不觸發飛行。
3. **包絡** —— 快攻擊 / 慢釋放的平滑處理把原始 RMS 轉換成穩定的 0–1 音量值，約每 23 ms 更新一次。
4. **飛行** —— 每個用戶端 tick，若平滑後的音量超過設定閾值，垂直速度就向目標升力插值（升力與音量成正比）。除非明確開啟，飛行期間摔落傷害會被歸零。
5. **多版本** —— 單一原始碼樹由 [Stonecutter](https://stonecutter.kikugie.dev/) 前置處理，產出 Minecraft 26.1 和 26.2 兩個 jar（兩個版本間唯一的 API 分歧是 `setScreen`）。

設定以 JSON 形式儲存在 `config/soarboisterously.json`。

## 快速開始

### 環境需求

- JDK 25
- Gradle 9.x（儲存庫未附帶 wrapper，請使用本機安裝的 Gradle，如 9.5）

### 複製與建置

```bash
git clone https://github.com/BlueShadow323/SoarBoisterously.git
cd SoarBoisterously
gradle buildAll
```

依賴（Minecraft、Fabric Loader、Fabric API）會在首次建置時自動從 Fabric maven 儲存庫解析。

建置產物為每個 Minecraft 版本各一個 jar：

```
versions/26.1/build/libs/soarboisterously-<version>+mc26.1.jar
versions/26.2/build/libs/soarboisterously-<version>+mc26.2.jar
```

開發環境測試可執行 `gradle runClient`（建置並啟動啟用版本，預設 26.2）。

### 安裝

1. 為對應 Minecraft 版本安裝 [Fabric Loader](https://fabricmc.net/use/)
2. 將 [Fabric API](https://modrinth.com/mod/fabric-api) 放入 `mods` 資料夾
3. 將對應版本的 `soarboisterously-*.jar` 放入 `mods`
4. 進入世界，按 `V`，開始說話

## 授權條款

本專案基於 [GPL-3.0](../LICENSE.txt) 授權條款開源。
