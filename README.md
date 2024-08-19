# 萌芽星露谷物语钓鱼插件

## 介绍
这是一个致力于在MC中使用萌芽复刻的星露谷物语钓鱼系统的插件。
目前仍在开发中，欢迎大家提出建议和意见。

### 特点
1. 通过控制加速度达到**流畅的浮标和钓鱼动画**
2. 本地端口脚本，**0延迟0动态带宽占用**
3. 鱼有**超高的AI自定义性**
4. 可以**自由定义生物群系**的鱼
5. 兼容玩家**自定义钓鱼等级**
6. 兼容MythicItems和兼容萌芽的自定义Dos
7. 超高的二次开发性，请参考[API](https://github.com/zhaoch23/StardewValleyFishing/tree/main/src/main/java/com/zhaoch23/stardewvalleyfishing/api)

作者QQ：_1149470063_
联系作者： [邮箱](mailto:c233zhao@uwaterloo.ca)
仓库：https://github.com/zhaoch23/StardewValleyFishing

下面的GIF中展示了插件的部分功能：

![gif](./misc/example.gif)

## 使用方法

安装萌芽引擎后，将本插件放入`plugins`文件夹中即刻加载插件。可以将`misc/fishing`文件夹放到
`GermCache/textures`或`GermResourcePack/textures`中使用星露谷的材质或者
修改`fishing.yml`中的材质的Path来使用自定义材质。
**萌芽使用的本地端MOD版本为GermMod-Snapshot-4.4.1-2，
低于此版本可能会出现问题。**

## 更新计划
- [x] 钓鱼GUI交互鱼动画
- [x] 鱼的AI自定义，请参考`biome/plains.yml`中的`fish_ai`部分
- [x] 自定义生物群系的鱼
- [x] 兼容MythicItems
- [x] 钓鱼等级
- [x] 音效与Dos
- [ ] 宝箱系统
- [ ] 鱼饵与钓鱼工具