# android-captcha

### 介绍
图形认证和图片认证封装，依赖了[luozhanming-Captcha](https://github.com/luozhanming/Captcha)
+ 图片认证自定了样式，支持高度和宽度变化（以往不支持）；
+ 图形认证支持自定义宽高，自适应控件宽高（不设置宽高），自定义背景色、圆角、干扰线数量、干扰点数量等；
+ minSdk 15。

![](/captcha.jpg)

### 引入

#### 第一步
在根build.gradle文件中添加如下代码
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
#### 第二步
添加依赖
```
implementation 'com.github.cqray:android-captcha:1.1.2'
```

### 如何使用

#### 图片认证
[luozhanming-Captcha](https://github.com/luozhanming/Captcha)
重新定义以下内容，可以修改图片认证基础的样式
```
    <!-- 内部间距 -->
    <dimen name="captchaPadding">10dp</dimen>
    <!-- 提示文字大小 -->
    <dimen name="captchaTextSize">14sp</dimen>
    <!-- 提示文字颜色 -->
    <color name="captchaTextColor">#FFFFFF</color>
    <!-- 刷新按钮颜色 -->
    <color name="captchaRefreshColor">#555555</color>
    <!-- 刷新按钮间隔 -->
    <dimen name="captchaRefreshMargin">8dp</dimen>
    <!-- 刷新按钮大小 -->
    <dimen name="captchaRefreshSize">24dp</dimen>
```
#### 图形验证
```       
// 更多使用自行查看Api
CodeCaptcha cc = CodeCaptcha.builder()
        .backgroundColor(Color.WHITE)
        .backgroundRadius(5)
        .interfereSize(2)
        .codeBold(true)
        .capitalEnable(false)
        .codeLength(4)
        .build();
cc.setCodeInto(iv);
```
