# AndroidCalculator

简易 Android 计算器应用，iOS 深色风格，支持基础运算与表达式显示。

## 功能

- 加减乘除四则运算（`+` `−` `×` `÷`）
- **表达式显示** — 上方显示当前算式（如 `12 + 34 =`），下方显示当前输入值
- 连续运算 — 按 `=` 算出结果后可直接按操作符继续计算
- 操作符替换 — 按了操作符后改按其他操作符，自动替换
- 百分比（`%`）— 将当前数值除以 100
- 正负切换（`+/-`）
- 退格删除（`⌫`）— 删除最后一位数字
- 小数点输入（`.`）
- 除零保护 — 显示"错误"
- 深色 iOS 计算器风格界面

## 按钮布局

| 行 | 按钮 |
|----|------|
| 第1行 | `AC` `+/-` `%` `÷` |
| 第2行 | `7` `8` `9` `×` |
| 第3行 | `4` `5` `6` `−` |
| 第4行 | `1` `2` `3` `+` |
| 第5行 | `0` `.` `⌫` `=` |

## 构建方法

代码 push 到 `main` 分支后，GitHub Actions 会自动构建 APK。

### 手动触发构建

1. 进入仓库 **Actions** 标签页
2. 选择 **Build APK** workflow
3. 点击 **Run workflow** → **Run workflow**

### 本地构建

```bash
./gradlew assembleDebug
```

## 下载 APK

1. 进入仓库的 **Actions** 标签页
2. 点击最新的成功 Workflow
3. 在 **Artifacts** 区域下载 `calculator-debug-apk.zip`
4. 解压后安装 `.apk` 文件到手机

## 技术栈

- Java 8
- Android SDK 34 (minSdk 21)
- ConstraintLayout
- Material Components
- Gradle 8.5 + AGP 8.2.2

## 版本

v1.1 — 修复表达式显示、退格键、按钮布局

## License

MIT
