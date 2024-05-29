# 安全

## 捕获APK文件

```shell
# 罗列所有安装的包名
adb shell pm list package
# 这个命令会返回一个data/app文件目录下的地址
adb shell pm path com.netease.cloudmusic
# 拉取apk
adb pull /data/app/com.netease.cloudmusic-2.apk
```

## 剖析
resource.arsc文件由aapt(Android Asset Packaging Tool)创建。

工具:
- dex2jar
- JD-GUI

```shell
java -jar dex2jar.jar target.apk
```

JD-GUI直接打开clas文件。

## 代码注入

apktool

```shell
# 解开apk
apktool d -r HelloWorld.apk HelloWorld
# ...省略注入代码的步骤 
# 重新构建应用程序
apktool b ./HelloWorld
# 生成签名
keytool -genkey -v -keystore example.keystore -alias example_alias-keyalg RSA -vaildity 100000
# 签名apk
jarsinger -verbose -keystore example.keystore ./HelloWorld/dist/HelloWorld.apk alias_name
```

## 保护

混淆
```groovy
debuggable false
minifyEnable true
```

## 非安全储存

SharedPreferences
内部储存
外部储存



