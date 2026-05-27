# Overgeared x Epic Knights

*A compatibility mod bridging Epic Knights and Overgeared*

</div>

## What does it do?

Epic Knights adds a large variety of medieval weapons and armor to Minecraft but out of the box, everything is crafted
through vanilla crafting tables, which clashes with Overgeared's material-based forging system.

This mod fixes that. All vanilla Epic Knights crafting recipes are **removed** and replaced with proper Overgeared
recipes using **forging**, **casting**, and **assembly**.

<table align="center"><tr>
<td align="center">

![Shortsword-Blade forging recipe](https://cdn.modrinth.com/data/cached_images/d7156a85793c7bf78ceb969e9ce4ebc410bc687b.png)<div align="center">

</td>
<td align="center" width="60">
<b>→</b>
</td>
<td align="center">

![Shortsword assembly using the Blade and a Hilt](https://cdn.modrinth.com/data/cached_images/df051f49809ae69e814561f4dd67d45c306e7ee2.png)

</td>
</tr></table>

**Blueprints** are also included for Armor, Shields and Weapons.

### New Items

To achieve this, the mod adds new intermediate **blade and component items**.
These work exactly like Overgeared's own components: you cast or forge the blade first, then assemble the final weapon.
This keeps the crafting process consistent with how Overgeared handles its own gear.

<details>
<summary>Steel Forging Items</summary>
  
![All steel forging items added by this mod, from Shortsword-Blade to Morgenster-Head](https://cdn.modrinth.com/data/cached_images/ec62d1cfc830efcb2e3c7b2d839bdf51811def58.png)

</details>

## Compatibility

| DLC | Status |
|-----|--------|
| [Epic Knights: Shields, Armor and Weapons](https://modrinth.com/mod/epic-knights-shields-armor-and-weapons) | 🟢 Supported |
| [Epic Knights: Addon](https://modrinth.com/mod/epic-knights-addon) | 🟡 Planned |
| [Epic Knights: Slavic Armory](https://modrinth.com/mod/epic-knights-slavic-armory) | 🟡 Planned |
| [Epic Knights: Antique Legacy](https://modrinth.com/mod/epic-knights-antique-legacy) | 🟡 Planned|

| Minecraft Version | Status |
|-------------------|--------|
| 1.20.1 | 🟢 Supported |
| 1.21.1 | 🟢 Supported |

## Requirements

<table><tr>
<td align="center">
<a href="https://modrinth.com/mod/epic-knights-shields-armor-and-weapons">
<img src="https://cdn.modrinth.com/data/L6jvzao4/2eac93d65e0df5ce99db25a46209155281a37035.png" width="80" alt="Epic Knights"><br>
<img src="https://img.shields.io/badge/Epic%20Knights-required-e05252?style=flat-square&logo=modrinth&logoColor=white" alt="Epic Knights">
</a>
</td>
<td align="center">
<a href="https://modrinth.com/mod/overgeared">
<img src="https://cdn.modrinth.com/data/SQL3X2Ky/20c446ea6a90cbb8fcfd552bea7dbdc16388e31b_96.webp" width="80" alt="Overgeared"><br>
<img src="https://img.shields.io/badge/Overgeared-required-e05252?style=flat-square&logo=modrinth&logoColor=white" alt="Overgeared">
</a>
</td>
</tr></table>

## A Note on AI-Assisted Development
I was unable to get [_Epic Knights_](https://modrinth.com/mod/epic-knights-shields-armor-and-weapons) and [_Overgeared_](https://modrinth.com/mod/overgeared) running as compile-time dependencies, which meant I couldn't use their APIs directly. 
As a result, a large number of JSON files had to be written by hand.
A process that is highly repetitive, time-consuming, and error-prone.

For this reason, I used Claude Code to assist with generating the JSON. 
**My own work** on every item (and every material per item) still involved creating the blade or component texture, registering it, and designing the forging recipe.
The AI handled the mechanical transcription work in between.
