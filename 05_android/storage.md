# Android Storage

早期的Android手机会有一块内部储存空间(internal storage), 加上一块可移除的外部储存(external storage)空间(比如: micro SD卡)。现在的大部分手机都配备着大容量的空间, 尽管了没有了可移除的储存媒介, 但磁盘空间还是区分了内部储存和外部储存，而且API的使用方式和行为还是更早期一样。

## 内部储存

> /data/data/${applicationId}

- 一定是可用的(available)
- 你只能访问你自己app的数据(其他app也无法访问你的应用数据)。
- 卸载app时会一起删除/data/data/${applicationId}下的数据

如果您想暂时保留而非永久存储某些数据，则应使用特殊的缓存目录来保存这些数据。针对这些类型的文件，每个应用都有专门的私有缓存目录。当设备的内部存储空间不足时，Android 可能会删除这些缓存文件以回收空间。但是，您不应依赖系统为您清理这些文件，而应始终自行维护缓存文件，使其占用的空间保持在合理的限制范围内（例如 1 MB）。当用户卸载您的应用时，这些文件也会随之移除。

root权限才可访问

Context#getFilesDir

Context#getCacheDir

```java
// creates a file with that name in your app's internal cache directory: 
private File getTempFile(Context context, String url) {
    // For a more secure solution, use EncryptedFile from the Security library
    // instead of File.
    File file;
    try {
        String fileName = Uri.parse(url).getLastPathSegment();
        file = File.createTempFile(fileName, null, context.getCacheDir());
    } catch (IOException e) {
        // Error while creating file
    }
    return file;
}
```

```java
// Although you can define your own key generation parameter specification, it's
// recommended that you use the value specified here.
KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

String fileToRead = "my_sensitive_data.txt";
EncryptedFile encryptedFile = new EncryptedFile.Builder(
        File(directory, fileToRead),
        context,
        masterKeyAlias,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
).build();

StringBuffer stringBuffer = new StringBuffer();
try (BufferedReader reader =
             new BufferedReader(new FileReader(encryptedFile))) {

    String line = reader.readLine();
    while (line != null) {
        stringBuffer.append(line).append('\n');
        line = reader.readLine();
    }
} catch (IOException e) {
    // Error occurred opening raw file for reading.
} finally {
    String contents = stringBuffer.toString();
}
```



## 外部储存

> /Android/data/${applicationId}

- 无法确保一定可用, 因为用户可以装载和弹出外部储存设备。
- 全局可读。所以你的文件的访问无法在你的掌控内(out of your control).
- 卸载app时会一起删除/Android/data/${applicationId}下的数据(`getExternalFilesDir()`).

因此外部储存适合你保存你想要分享给其他app的文件或者希望用户可以浏览/访问的文件。

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="string"
          android:sharedUserId="string"
          android:sharedUserLabel="string resource" 
          android:versionCode="integer"
          android:versionName="string"
          android:installLocation=["auto" | "internalOnly" | "preferExternal"] >
  <!-- internalOnly是默认值 -->
    . . .
</manifest>
```

### data/data/[packageName]和Android/data/[packageName]区别

data/data/[packageName]: 内部储存的私有目录

Android/data/[packageName]: 外部储存的私有目录(Android Q(10)以前,其他应用也可以访问; Q及其之后只能自己的应用访问)

在App被卸载时，二者都会被移除。

### SharedPreference支持多进程吗？在使用上有是什么需要注意的？
不支持跨进程
commit是同步方法;apply是异步方法
Android基于xml实现的一种数据持久化方式
不要使用SP储存过大的数据

### mmkv了解吗

[mmkv](https://github.com/Tencent/MMKV/blob/master/README_CN.md)

## 兼容工作

glide: https://github.com/bumptech/glide/issues/3851