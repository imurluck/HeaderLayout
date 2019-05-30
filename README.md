## XML中配置网易云歌手详情滑动效果
### HeaderLayout

---------------------------------------------------------



​　　网易云音乐App给用户的体验效果一直都非常好，尤其是流畅的动画和滑动的联动效果，都给人一种如丝滑般的感受，这一点在其歌手详情页面体现得尤为突出。那么我们就来实现这样的效果，但是我们不能只局限在实现当中，否则当需求变化就需要改动大量的代码，同时也不能保证它的复用性，放到其他界面则需要写许多重复代码。因此我们需要跳出实现的限制，将其中的元素抽取出来，制作成一个通用的库，并且保证其可拓展性和充分的用户自定义性。经过研究，最终实现了此控件，并取名为HeaderLayout，那么我们先来看看实现效果以便直观的感受一下。

<img src="./screenshots/HeaderLayout.gif" width="200" alt="效果图" div align=center/>

### 如何使用

--------------------------------------------------------------------------



​　　效果图中所有的头部控件滑动联动效果都只需要在xml中配置几行代码即可完成，由于HeaderLayout是根据CoordinatorLayout的机制来实现的，所以HeaderLayout需要包裹在CoordinatorLayout中才会有效果。

#### 引入依赖[ ![Download](https://api.bintray.com/packages/neuzzx/HeaderLayout/headerlayout/images/download.svg) ](https://bintray.com/neuzzx/HeaderLayout/headerlayout/_latestVersion)

  ```xml
  implementation "com.imurluck:headerlayout:$lastVersion"
  ```

#### 编写布局

　　HeaderLayout继承自FrameLayout,且并没有改写FrameLayout的测量和布局逻辑，所以子控件的布局方式和FrameLayout相同即可，我们只需要关注HeaderLayout新增的几个属性。这里以效果图为例。

  ```xml
  <androidx.coordinatorlayout.widget.CoordinatorLayout
          ...>
  
      <com.zzx.headerlayout_kotlin.HeaderLayout
              android:layout_width="match_parent"
              android:layout_height="wrap_content"
              //新增属性
              app:extend_height="30%">
  
          <androidx.appcompat.widget.AppCompatImageView
                  android:layout_width="match_parent"
                  android:layout_height="300dp"
                  android:src="@drawable/singer"
                  android:scaleType="centerCrop"
                  //新增属性
                  app:transformation="scroll|extend_scale"
                  />
  
          ...
  
      </com.zzx.headerlayout_kotlin.HeaderLayout>
  
      <androidx.viewpager.widget.ViewPager
              android:id="@+id/viewPager"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              //配置依赖布局的layout_behavior
              app:layout_behavior="@string/header_layout_scrolling_view_behavior"/>
  
  </androidx.coordinatorlayout.widget.CoordinatorLayout>
  ```

　　如上所示，HeaderLayout工作在CoordinatorLayout中并且是其直接子View。ViewPager由于需要根据HeaderLayout的滑动做出界面的调整，所以需要配置layout_behavior，并且其值为@string/header_layout_scrolling_view_behavior，这里和AppBarLayout的使用方式一致。我们的工作重点是头部控件的联动效果，因此咱们聚焦于HeaderLayout和其子View。我们看AppCompatImageView，它用来展示效果图中的歌手。仔细分析效果图中AppCompatImageView的变换方式，可以发现它是根据父控件HeaderLayout的滑动而做出的相应的变化效果，HeaderLayout向上滑动，其跟随向上，HeaderLayout向下滑动，则跟着向下。并且，在HeaderLayout滑动到底部继续向下拓展时，AppCompatImageView做了一个收缩的变换。这一切的一切都需要归功于app:transformation属性，可以在代码中看见其值为"scroll|extend_scale"，那么其含义是什么呢？对此，我们引出了一个概念----Transformation，它是一个接口，其意在为根据HeaderLayout的滑动及状态而做出相应的变化行为。在介绍Transformation之前，有必要介绍一下HeaderLayout滑动中的几种状态。
  ![HeadeerLayout状态图](https://img-blog.csdnimg.cn/20190522164331459.PNG?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3oxMjg5MDQyMzI0,size_16,color_FFFFFF,t_70)  
  
　　HeaderLayout的滑动实际上是HeaderLayout高度的动态变化，所以需要了解图中三种高度的含义。maxHeight是HeaderLayout第一次加载测量后的高度，minHeight是设置了app:sticky_until_exit="true"属性的子View的高度之和，此属性表示子View不随着HeaderLayout而滑出屏幕，形成一种粘连在屏幕顶部的效果，且子View是按照顺序排列的。extendHeight则是拓展的高度，展示在效果图中就是图片收缩scale时下滑的高度，extendHeight可以在xml中为HeaderLayout设置，其值可以为dimension，百分数，或者float比例，百分数和float比例是按照maxHeight而计算的。  
　　而图中五种状态用来表示HeaderLayout高度变化过程中的滑动状态，Transformation就是根据这五种状态而生，Transformation作用于HeaderLayout的直接子View或者间接子View(间接子View需要自己进行处理，可以参考[CommonToolbarTransformation](https://github.com/imurluck/HeaderLayout/blob/master/headerlayout_kotlin/src/main/java/com/zzx/headerlayout_kotlin/transformation/CommonToolbarTransformation.kt)),一个子View可以同时拥有多个Transformation，HeaderLayout在其状态变化时，则会遍历子View的所有Transformation，通知其做出改变。  
　　XML中作用于AppCompatImageView的app:transformation="scroll|extend_scale"属性,scroll 和 extend_scale则是内置的两种Transformation，如下表所示。
  
  <table width="600" align="center">
      <tr>
      	<td>属性</td>
          <td>值</td>
          <td>说明</td>
          <td>作用对象</td>
      </tr>
      <tr>
      	<th rowspan="6">transformation</th>
      </tr>
      <tr>
      	<td>scroll</td>
          <td>随着HeaderLayout滑动而滑动</td>
          <td>HeaderLayout直接子View</td>
      </tr>
      <tr>
      	<td>alpha</td>
          <td>STATE_MIN_HEIGHT到STATE_MAX_HEIGHT对应alpha为0->1</td>
          <td>HeaderLayout直接子View</td>
      </tr>
      <tr>
      	<td>alpha_contray</td>
          <td>与alpha相反，STATE_HEIGHT到STATE_MAX_HEIGHT对应alpha为1->0</td>
          <td>HeaderLayout直接子View</td>
      </tr>
      <tr>
      	<td>extendScale</td>
          <td>在STATE_MAX_HEIGHT到STATE_EXTEND_MAX_END之间做scale变换</td>
          <td>HeaderLayout直接子View</td>
      </tr>
      <tr>
      	<td>common_toolbar</td>
          <td>专为Toolbar设计，在STATE_MIN_HEIGHT时显示Title和Subtitle，否则隐藏，此属性必须设置给HeaderLayout的直接子View，但是Toolbar不需要为其直接子View</td>
          <td>HeaderLayout直接子View</td>
      </tr>
      <tr>
      	<td>sticky_until_exit</td>
          <td>true|false</td>
          <td>子View不随HeaderLayout而滑出屏幕，粘连在顶部</td>
          <td>HeaderLayout直接子View</td>
      </tr>
      <tr>
      	<td>custom_transformation</td>
          <td>@string</td>
          <td>自定义Transformation的全路径</td>
          <td>HeaderLayout直接子View</td>
      </tr>
      <tr>
      	<td>extend_height</td>
          <td>n(dp)|n%|0.n</td>
          <td>设置HeaderLayout的extendHeight，可以是dimension、百分比数或者小数比例</td>
          <td>HeaderLayout</td>
      </tr>
  </table>
  
  
　　transformation表示内置的几中Transformation，但是想要自定义Transformation应该如何做呢？

### 自定义Transformation

-----------------------------------

​　　custom_transformation属性则是专为自定义Transformation而服务，其值为自己实现的Transformation类的全路径。自定义Transformation有两种方式，其一是实现Transformation接口，另一种方式是继承TransformationAdapter类，TransformationAdapter是Transformation是Transformation接口的空实现，继承于此则不需要实现所有的方法。

```kotlin
interface Transformation<in V: View> {
    /**
     * @see [HeaderLayout.scrollState]为STATE_MIN_HEIGHT, 这个方法回调表示[HeaderLayout]的Bottom已经收缩到了最小高度
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param unConsumedDy 由其他状态到此状态未消耗完的dy
     */
    fun onStateMinHeight(child: V, parent: HeaderLayout, unConsumedDy: Int)

    /**
     * @see [HeaderLayout.scrollState]为STATE_NORMAL_PROCESS, 在STATE_MIN_HEIGHT和STATE_MAX_HEIGHT之间
     * 这个方法回调表示[HeaderLayout]的Bottom正在最小高度与最大高度之间
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param percent 0<percent<1, 值为([HeaderLayout.getBottom] - [HeaderLayout.minHeight]) / ([HeaderLayout.maxHeight] - [HeaderLayout.minHeight]])
     * 且值不会为0或者1, 为0相当于是回调了[onStateMinHeight], 为1相当于回调了[onStateMaxHeight], 由于值不会为0或1，
     * 所以在回调[onStateMinHeight]和[onStateMaxHeight]时会有一个未消耗的dy
     * @param dy 滑动的距离
     */
    fun onStateNormalProcess(child: V, parent: HeaderLayout, percent: Float, dy: Int)

    /**
     * @see [HeaderLayout.scrollState]为STATE_MAX_HEIGHT
     * 这个方法回调表示[HeaderLayout]的Bottom正处于[HeaderLayout.maxHeight]
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param unConsumedDy 由其他状态到此状态未消耗完的dy
     */
    fun onStateMaxHeight(child: V, parent: HeaderLayout, unConsumedDy: Int)

    /**
     * @see [HeaderLayout.scrollState]为STATE_EXTEND_PROCESS, 在STATE_MAX_HEIGHT和STATE_EXTEND_MAX_END之间
     * 这个方法回调表示[HeaderLayout]的Bottom正处于[HeaderLayout.maxHeight] 和 [HeaderLayout.maxHeight] + [HeaderLayout.extendHeight]之间
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param percent 0<percent<1, 值为([HeaderLayout.getBottom] - [HeaderLayout.maxHeight]) / [HeaderLayout.extendHeight]
     * 且值不会为0或者1, 为0相当于是回调了[onStateMaxHeight], 为1相当于回调了[onStateExtendMaxEnd], 由于值不会为0或1，
     * 所以在回调[onStateMaxHeight]和[onStateExtendMaxEnd]时会有一个未消耗的dy
     */
    fun onStateExtendProcess(child: V, parent: HeaderLayout, percent: Float, dy: Int)

    /**
     * @see [HeaderLayout.scrollState]为STATE_EXTEND_MAX_END,
     * 这个方法回调表示[HeaderLayout]的Bottom正处于[HeaderLayout.maxHeight] + [HeaderLayout.extendHeight]
     * @param child 当前需要做变换的view
     * @param parent [HeaderLayout]
     * @param unConsumedDy 由其他状态到此状态未消耗完的dy
     *
     */
    fun onStateExtendMaxEnd(child: V, parent: HeaderLayout, unConsumedDy: Int)
}
```

　　HeaderLayout在状态变化的时候会遍历子View的所有Transformation,也即是会回调Transformation接口中的这几个方法，使用者可以根据这几个方法的含义来变换子View。

### Tips

------------------------
　　开发者在使用app:transformation和app:sticky_until_exit等属性时，最好用AppCompatImageView代替ImageView，AppCompatTextView代替TextView，这样在XML文件中则不会因为系统控件无法使用自定义属性而报红线，即使报红线也不会影响程序正常的执行，只是看着别扭。


### 总结

---------------------------------------------------------

​　　HeaderLayout是根据参照网易云音乐的效果而实现的，但又跳出了“实现”的限制，提取出来了一个公共而又与业务无关的控件，其思想则是学习了CoordinatorLayout的behavior和ViewGroup事件的分发思想，将HeaderLayout的滑动状态分发给其子View，从而产生联动效果。
　　

### 协议
<a href="LICENSE">APACHE LICENSE-2.0</a> 


　

