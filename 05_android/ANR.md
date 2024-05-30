# ANR治理


## BlockCanary

[BlockCanary](https://github.com/markzhai/AndroidPerformanceMonitor)

## matrix

[matrix](https://github.com/tencent/matrix?tab=readme-ov-file#matrix_android_cn)

Matrix-android 当前监控范围包括：应用安装包大小，帧率变化，启动耗时，卡顿，慢方法，SQLite 操作优化，文件读写，内存泄漏等等。
- APK Checker:  APK 安装包的分析检测工具
- Resource Canary: 检测Activity 泄漏和 Bitmap 重复创建检测工具
- Trance Canary: 监控ANR、界面流畅性、启动耗时、页面切换耗时、慢函数及卡顿
- SQLite Lint: 检测sql语句质量
- IO Canary: 检测IO问题。Closeable Leak 监控。
- Battery Canary: 监控 App 活跃线程（待机状态 & 前台 Loop 监控）、ASM 调用 (WakeLock/Alarm/Gps/Wifi/Bluetooth 等传感器)、 后台流量 (Wifi/移动网络)等 Battery Historian 统计 App 耗电的数据
- MemGuard: 检测堆内存访问越界、使用释放后的内存、重复释放等问题

## xCrash

[xCrash](https://github.com/iqiyi/xCrash)

捕获 java 崩溃，native 崩溃和 ANR。

原理: 监听 /data/anr 目录的变化。(发生ANR会在/data/anr/下产生一个traces.txt文件)



