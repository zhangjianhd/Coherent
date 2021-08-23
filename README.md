>很多场景下（下载、上传，转码等）我们需要展示进度给用户缓解焦虑，优化体验，但是很多时候我们拿到的进度值并不是连续渐进的，这就导致在展示进度时存在跳跃性，会有闪动的感觉，体验不是很友好。为了体验舒适，我们需要补齐中间过程，使动画显得流畅。

这里提供的解决方案是一个数据源的处理工具类ProgressCoherent，不涉及自定义View，在控件外部二次处理数据，交给Coherent控制显示隐藏，避免在View中耦合这部分无关逻辑，View本身只用关心对应进度的绘制展示过程，这样也不需要对现有控件做任何修改。

#### 效果对比

![效果展示](/Coherent.gif)

[演示apk下载](/Coherent.apk)

- 普通进度：就是不做处理直接展示的进度
- 平滑进度是处理之后的效果

#### ProgressCoherent
- 可以直接创建：传入需要被控制的View,做透明度和显示隐藏控制；以及progressUpdate：处理控件进度变化的方法
````
val coherent = ProgressCoherent(view) {
            dataBinding.tcCoherentProgressCover.setProgress(it)
        }
````
- 对于Progress,提供了一个单独的方法：createCoherentForProgressBar，直接操作它的setProgress方法
````
val coherent = createCoherentForProgressBar(progress)
````
然后进度的数据源变化时调用更新进度的方法setProgress(progress : Float)
