# 电池

## BatteryManager
检测电池电量和充电状态

## 电池优化

- 避免无用的网络请求
- 按需对待BroadcastReceiver
  - 事件发生, 设备会被唤醒。当网络丢失, 则关闭所有receiver。
```kotlin
val receiver = ComponentName(context, Receiver::class.java)
val pm = getPackageManager()
pm.setComponentEnableSetting(receiver, PackageManager.COMPONENT_ENABLE_STATE_ENABLED, PackageManager.DONT_KILL_APP)
```
- 网络优化
  - 预获取数据
  - 减少连接数量
  - 批量处理和调度
  - 缓存机制
  - 压缩数据
  - GCM(国内不行)

# 总结

可能手表开发会用得到。其他的谁在乎呢？