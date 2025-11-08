## Unofficial TaCZ 1.21.1 NeoForge Port
[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/tacz-1.21.1)
[![CurseForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/tacz-1-21-1)
[![GitHub](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/MUKSC/TACZ-1.21.1)

> [!IMPORTANT]
> This is UNOFFICIAL; Support is not guaranteed and things may not work properly  
> Do not report issues with this to the original devs  

> [!WARNING]
> Don't try to open any worlds from older versions with this mod  
> All the item data are incompatible and will likely to break things

If you have any questions, you can reach me on the [TaCZ Official Discord](https://discord.gg/uX6TdWUVpA) in the [#community-showcase > Unofficial TaCZ 1.21.1 NeoForge Port](https://discord.com/channels/1243278348399022252/1329058172148645909) channel

Recipes in other gun packs are broken by default  
You can upgrade packs to be 1.21.1 NeoForge compatible using the pack upgrader mod, which you can find the download link [here](https://discord.com/channels/1243278348399022252/1329058172148645909/1349704400540668006) (join the TaCZ Offical Discord)

### Known Issues
The `FIXME` annotations in the source code indicate issues I couldn't fix

- Built-in KubeJS recipe integration is disabled
  - I don't really know about KubeJS, and it got massive changes, so I'll just remove it for now

## ===== Original README =====

<p align="center">
    <img width="300" src="https://s2.loli.net/2024/04/30/NJrstR1QzpoLyIT.png" alt="title">
</p>
<hr>
<p align="center">Timeless and Classics Guns Zero</p>
<p align="center">
    <a href="https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero">
        <img src="http://cf.way2muchnoise.eu/full_timeless-and-classics-zero.svg" alt="CurseForge Download">
    </a>
    <img src="https://img.shields.io/badge/license-GNU GPL 3.0 | CC%20BY--NC--ND%204.0-green" alt="License">
    <br>
    <a href="https://jitpack.io/#MCModderAnchor/TACZ">
        <img src="https://jitpack.io/v/MCModderAnchor/TACZ.svg" alt="jitpack build">
    </a>
    <a href="https://crowdin.com/project/tacz">
        <img src="https://badges.crowdin.net/tacz/localized.svg" alt="crowdin">
    </a>
</p>
<p align="center">
    <a href="https://github.com/MCModderAnchor/TACZ/issues">Report Bug</a>    ·
    <a href="https://github.com/MCModderAnchor/TACZ/releases">View Release</a>    ·
    <a href="https://tacwiki.mcma.club/zh/">Wiki</a>
</p>

Timeless and Classics Guns Zero is a gun mod for Minecraft Forge 1.20.1.

## Notice

- If you have any bugs, you can visit [Issues](https://github.com/MCModderAnchor/TACZ/issues) to
  submit issues.

## Authors

- Programmer: `286799714`, `TartaricAcid`, `F1zeiL`, `xjqsh`, `ClumsyAlien`
- Artist: `NekoCrane`, `Receke`, `Pos_2333`

## Credits

- Other players who have helped me in any ways, and you

## License

- Code: [GNU GPL 3.0](https://www.gnu.org/licenses/gpl-3.0.txt)
- Assets: [CC BY-NC-ND 4.0](https://creativecommons.org/licenses/by-nc-nd/4.0/)

## Maven

```groovy
repositories {
    maven {
        // Add curse maven to repositories
        name = "Curse Maven"
        url = "https://www.cursemaven.com"
        content {
            includeGroup "curse.maven"
        }
    }
}

dependencies {
    // You can see the https://www.cursemaven.com/
    // Choose one of the following three

    // If you want to use version tacz-1.20.1-1.1.6-release
    implementation fg.deobf("curse.maven:timeless-and-classics-zero-1028108:6632240-sources-6633203")
}
```
