## 彗星 Bot (Comet)

一个基于 [Mirai-Core](https://github.com/mamoe/mirai) 开发的机器人

项目名来源于 [星街彗星 (Hoshimati Suisei)](https://zh.moegirl.org.cn/%E6%98%9F%E8%A1%97%E5%BD%97%E6%98%9F)

本项目处于开发阶段, 部分功能可能无法使用 (除了写在下面的功能) ~~反正也没人用~~

**一切开发旨在学习，请勿用于非法用途**

English Version: [README](https://github.com/StarWishsama/Comet-Bot/blob/mirai/README_en.md)

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/b26348aabf51452195dbc14846accd86)](https://www.codacy.com/manual/StarWishsama/Comet-Bot?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=StarWishsama/Comet-Bot&amp;utm_campaign=Badge_Grade)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=StarWishsama_Nameless-Bot&metric=alert_status)](https://sonarcloud.io/dashboard?id=StarWishsama_Nameless-Bot)
[![Kotlin Gradle](https://github.com/StarWishsama/Comet-Bot/workflows/Kotlin%20Gradle/badge.svg)](https://github.com/StarWishsama/Comet-Bot/actions/)
[![LICENSE](https://img.shields.io/github/license/StarWishsama/Comet-Bot.svg?style=popout)](https://github.com/StarWishsama/Comet-Bot/blob/master/LICENSE)
[![Issues](https://img.shields.io/github/issues/StarWishsama/Comet-Bot.svg?style=popout)](https://github.com/StarWishsama/Comet-Bot/issues)
![Stars](https://img.shields.io/github/stars/starwishsama/Comet-Bot)
[![Release](https://img.shields.io/github/v/release/StarWishSama/Comet-Bot?include_prereleases)](https://github.com/StarWishsama/Comet-Bot/releases)

## 🎉 它能干什么?
* 以图搜图 (支持 ascii2d/SauceNao)
* 彩虹六号战绩查询
* 打卡
* 签到
* bilibili UP主信息/动态查询
* 点歌 (QQ小程序卡片样式)
* 事件概率占卜
* 明日方舟 / 公主连结抽卡模拟器
* 去你大爷的小程序 (将小程序转换为文本)
* 单推小助手 
  - 支持订阅 bilibili 用户动态/开播提醒
  - 支持订阅蓝🐦用户最新消息
* rCon 功能, 支持连接到可以使用 rCon 的游戏服务器
* 查询蓝🐦用户信息/推文
* 自动推送最新推文到指定群聊
* 禁言/踢出群员
* 还在开发中...

## ☑ To-Do 列表
详见 [Issues](https://github.com/StarWishsama/Comet-Bot/issues)

## 💽 如何使用

### 自编译

- 注意: 请使用 JDK 8 或更高版本打包

1.1. Clone 或者下载这个项目.

```bash
git clone https://github.com/StarWishsama/Comet-Bot.git
```

1.2. 编译

本项目使用 Gradle, 因此你可以双击文件夹中的 `gradlew.bat` 以构建这个项目.
(你可以在 `/build/libs` 下找到构建好的 jar 文件)

### 直接下载
1. 到项目的 releases 页面下载最新版本的 jar
2. 使用 cmd 启动 Bot, 然后去 config.json 下面填写账号密码
3. 重新启动 Bot, 提示启动完成后就可以开始使用了!

## 📜 协议 
**一切开发旨在学习，请勿用于非法用途**

**本项目禁止进行被二次分发为进行违反中华人民共和国法律法规的行为**

使用前必须查看 [Mirai 使用协议](https://github.com/mamoe/mirai#%E8%AE%B8%E5%8F%AF%E8%AF%81)

[AGPL v3.0](https://github.com/StarWishsama/Comet-Bot/blob/master/LICENSE)

------

    Copyright (C) 2018-2020 StarWishsama
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.
    
    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.


