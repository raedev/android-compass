# android-compass

[![](https://jitpack.io/v/raedev/android-compass.svg)](https://jitpack.io/#raedev/android-compass)

集成指南针、方位角、侧倾角、俯仰角、经纬度

![截图](../../raw/master/static/img.png)

# 依赖

```groovy
// root build.gradle
buildscript {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
// project build.gradle
dependencies {
    implementation 'com.github.raedev:android-compass:1.0.2' // 这里请取JitPack最新版本
}
```

# 集成使用

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ... 省略其他代码
        // 实例指南针管理器
        CompassManager manager = new CompassManager(this);
        // 添加回调监听
        manager.addCompassChangedListener(this);
        // 指南针图片（可自行实现CompassChangedListener自定义View）
        manager.addCompassChangedListener(compassView);
        // 注册并绑定到当前生命周期
        manager.register(this);
    }
}
```

# 指南针参数

```java
public class CompassInfo implements Parcelable {

    /**
     * [0,360]方位角<br>
     * 0 = 北，90 = 东，180 = 南，270 = 西
     */
    private int azimuth;

    /**
     * 俯仰角[-180,180] <br>
     * 手机朝上时，取值变化为[0,90,180] <br>
     * 手机朝下时，取值变化为[0,-90,180]
     */
    private int pitch;

    /**
     * 侧倾角[-90,90] <br>
     * 手机朝上时，从左到右取值变化为[0,90,0]，从右到左取值变化为[0,-90,0] <br>
     * 手机朝下时，从左到右取值变化为[0,-90,0]，从右到左取值变化为[0,90,0]
     */
    private int roll;

}
```

# 指南针图片

`CompassImageView` 为指南针图片实现，设置方法跟`ImageView`一致，图片的设计规范为：`12点钟方向为正北`。

# 示意图

![示意图](../../raw/master/static/img2.png)

- `方位角` 为沿着`Z轴`滚动的方向
- `俯仰角` 为沿着`X轴`滚动的方向
- `侧倾角` 为沿着`Y轴`滚动的方向

当手机90度横屏时X轴和Y轴相互转换，即沿着`Y轴`滚动的方向为俯仰角，其他旋转方向以此类推。

