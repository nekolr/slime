<p align="center">
  <a href="https://github.com/nekolr/slime"><img src="slime.svg" width="128" height="128" alt="logo"></a>
</p>
<div align="center">

_✨ 一个可视化的爬虫平台 ✨_

</div>

<p align="center">
  <a href="https://github.com/nekolr/slime/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/nekolr/slime?style=flat-square" alt="license">
  </a>
  <a href="https://github.com/nekolr/slime/releases">
    <img src="https://img.shields.io/github/v/release/nekolr/slime?style=flat-square&color=blueviolet&include_prereleases" alt="release">
  </a>
  <a href="https://github.com/nekolr/slime/tags">
    <img src="https://img.shields.io/github/v/tag/nekolr/slime?style=flat-square" alt="tag">
  </a>
  <a href="https://github.com/nekolr/slime">
    <img src="https://img.shields.io/github/stars/nekolr/slime?style=flat-square" alt="stars">
  </a>
</p>

## 介绍
一个可视化的爬虫平台。以流程图的方式配置爬虫，基本上无需编写代码即可完成工作。

本项目源自开源项目 [spider-flow](https://github.com/ssssssss-team/spider-flow) ，本着学习的目的，在它的基础上进行了重构，修复了一些问题，并增加了一些新的功能。

## 特性
- 支持 xpath 和 css 选择器
- 支持选择器提取、正则提取、json 提取等
- 支持 Cookie 自动管理
- 支持抓取由 js 动态渲染的页面
- 支持代理
- 支持多数据源
- 内置常用的字符串、日期、文件、加解密等函数
- 支持结果保存至多目的地（数据库、csv 文件等）
- 支持插件扩展（自定义执行器，自定义函数等）
- 支持任务日志
- 支持爬虫可视化调试

新增的特性：

- 采用内置数据库 [H2](https://github.com/h2database/h2database) ，做到开箱即用
- 支持同步执行，对于执行结果有顺序要求的可以使用该功能

## 感谢
[spider-flow](https://github.com/ssssssss-team/spider-flow) - 新一代爬虫平台，以图形化方式定义爬虫流程，不写代码即可完成爬虫。

## 免责声明
请勿使用本项目进行任何可能会违反法律规定和道德约束的工作。如您选择使用本项目，即代表您遵守此声明，作者不承担由于您违反此声明所带来的任何法律风险和损失。