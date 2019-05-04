# 多线程能否提高程序运行效率

## 1. 理论部分
上次讨论的结论，不太认可。
> 上次的结论是：
> 1. 多个进程是为了提高 cpu 的利用率
> 2. 进程内的多个线程是为了提高应用程序的利用率

* 进程仅仅是线程的容器，操作系统调度的单位仍然是线程，所以，本质上仍然是多线程提高 cpu 的利用率。关于进程和线程不展开了，因为确实不是很精通。
* 进程内多个线程会提高应用程序的使用率吗？这跟每个线程的内部实现、以及操作系统的调度算法有关系。如果其中很多线程是诸如 sleep、wait、io 这样的操作，那么显然它们是不会占用 cpu 的

实际例子可以对比单进程模式服务器与多线程模式服务器

### 1) 单进程模式服务器
nodejs, nginx 等服务器都是工作在单进程模式下，缺点有这么两点
* 无法利用多核
* 不适合计算密集型任务，如蓝色任务执行时间长，会造成后续黄色任务感觉卡顿
|cpu|执行时间|
|-|-|
|1|<font color="red">--</font><font color="blue">------</font><font color="yellow">--</font>|
|2|无|
|3|无|
|4|无|


但优点也是明显的
* 没有线程安全问题
* 配合 IO 多路复用，让单线程也能被充分利用，占用内存少，没有线程切换，适合 IO 密集任务，支持更多的并发

### 2) 多线程模式服务器
典型的有 tomcat，它的工作线程有多个，优点是
* 可以充分利用多核
* 在多核情况下，降低简单任务被复杂任务压住的概率
|cpu|执行时间|
|-|-|
|1|<font color="red">--</font>|
|2|<font color="blue">----</font>|
|3|<font color="yellow">--</font>|
|4|<font color="blue">--</font>|


## 2. 多线程能否提高程序运行效率
### 1) 环境搭建
* cpu 核数限制，有两种思路
    1. 使用虚拟机，分配合适的核
    2. 使用 msconfig，分配合适的核，需要重启比较麻烦
* 选择基准测试工具，使用了据说比较靠谱的 JMH
* 并行计算方式的选择
	1. 最初想直接使用 parallel stream，后来发现它有自己的问题
	2. 改为了自己手动控制 thread，实现简单的并行计算

* 测试代码如下
```shell
mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jmh -DarchetypeArtifactId=jmh-java-benchmark-archetype -DgroupId=org.sample -DartifactId=test -Dversion=1.0
```

```java
package org.sample;

import java.util.Arrays;
import java.util.concurrent.FutureTask;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations=3)
@Measurement(iterations=5)
public class MyBenchmark {
	static int[] ARRAY = new int[1000_000_00];
	static {
		Arrays.fill(ARRAY, 1);
	}
    @Benchmark
    public int c() throws Exception {
        int[] array = ARRAY;
        FutureTask<Integer> t1 = new FutureTask<>(()->{
        	int sum = 0;
        	for(int i = 0; i < 250_000_00;i++) {
        		sum += array[0+i];
        	}
        	return sum;
        });
        FutureTask<Integer> t2 = new FutureTask<>(()->{
        	int sum = 0;
        	for(int i = 0; i < 250_000_00;i++) {
        		sum += array[250_000_00+i];
        	}
        	return sum;
        });
        FutureTask<Integer> t3 = new FutureTask<>(()->{
        	int sum = 0;
        	for(int i = 0; i < 250_000_00;i++) {
        		sum += array[500_000_00+i];
        	}
        	return sum;
        });
        FutureTask<Integer> t4 = new FutureTask<>(()->{
        	int sum = 0;
        	for(int i = 0; i < 250_000_00;i++) {
        		sum += array[750_000_00+i];
        	}
        	return sum;
        });
        new Thread(t1).start();
        new Thread(t2).start();
        new Thread(t3).start();
        new Thread(t4).start();
        return t1.get() + t2.get() + t3.get()+ t4.get();
    }
    @Benchmark
    public int d() throws Exception {
        int[] array = ARRAY;
        FutureTask<Integer> t1 = new FutureTask<>(()->{
        	int sum = 0;
        	for(int i = 0; i < 1000_000_00;i++) {
        		sum += array[0+i];
        	}
        	return sum;
        });
        new Thread(t1).start();
        return t1.get();
    }
}
```



### 2) 双核 CPU（4个逻辑CPU）

```shell
C:\Users\lenovo\eclipse-workspace\test>java -jar target/benchmarks.jar
# VM invoker: C:\Program Files\Java\jdk-11\bin\java.exe
# VM options: <none>
# Warmup: 3 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.sample.MyBenchmark.c

# Run progress: 0.00% complete, ETA 00:00:16
# Fork: 1 of 1
# Warmup Iteration   1: 0.022 s/op
# Warmup Iteration   2: 0.019 s/op
# Warmup Iteration   3: 0.020 s/op
Iteration   1: 0.020 s/op
Iteration   2: 0.020 s/op
Iteration   3: 0.020 s/op
Iteration   4: 0.020 s/op
Iteration   5: 0.020 s/op


Result: 0.020 ±(99.9%) 0.001 s/op [Average]
  Statistics: (min, avg, max) = (0.020, 0.020, 0.020), stdev = 0.000
  Confidence interval (99.9%): [0.019, 0.021]


# VM invoker: C:\Program Files\Java\jdk-11\bin\java.exe
# VM options: <none>
# Warmup: 3 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.sample.MyBenchmark.d

# Run progress: 50.00% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 0.042 s/op
# Warmup Iteration   2: 0.042 s/op
# Warmup Iteration   3: 0.041 s/op
Iteration   1: 0.043 s/op
Iteration   2: 0.042 s/op
Iteration   3: 0.042 s/op
Iteration   4: 0.044 s/op
Iteration   5: 0.042 s/op


Result: 0.043 ±(99.9%) 0.003 s/op [Average]
  Statistics: (min, avg, max) = (0.042, 0.043, 0.044), stdev = 0.001
  Confidence interval (99.9%): [0.040, 0.045]


# Run complete. Total time: 00:00:20

Benchmark            Mode  Samples  Score  Score error  Units
o.s.MyBenchmark.c    avgt        5  0.020        0.001   s/op
o.s.MyBenchmark.d    avgt        5  0.043        0.003   s/op
```
可以看到多核下，效率提升还是很明显的，快了一倍左右

### 3) 单核 CPU

```shell
C:\Users\lenovo\eclipse-workspace\test>java -jar target/benchmarks.jar
# VM invoker: C:\Program Files\Java\jdk-11\bin\java.exe
# VM options: <none>
# Warmup: 3 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.sample.MyBenchmark.c

# Run progress: 0.00% complete, ETA 00:00:16
# Fork: 1 of 1
# Warmup Iteration   1: 0.064 s/op
# Warmup Iteration   2: 0.052 s/op
# Warmup Iteration   3: 1.127 s/op
Iteration   1: 0.053 s/op
Iteration   2: 0.052 s/op
Iteration   3: 0.053 s/op
Iteration   4: 0.057 s/op
Iteration   5: 0.088 s/op


Result: 0.061 ±(99.9%) 0.060 s/op [Average]
  Statistics: (min, avg, max) = (0.052, 0.061, 0.088), stdev = 0.016
  Confidence interval (99.9%): [0.001, 0.121]


# VM invoker: C:\Program Files\Java\jdk-11\bin\java.exe
# VM options: <none>
# Warmup: 3 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.sample.MyBenchmark.d

# Run progress: 50.00% complete, ETA 00:00:11
# Fork: 1 of 1
# Warmup Iteration   1: 0.054 s/op
# Warmup Iteration   2: 0.053 s/op
# Warmup Iteration   3: 0.051 s/op
Iteration   1: 0.096 s/op
Iteration   2: 0.054 s/op
Iteration   3: 0.065 s/op
Iteration   4: 0.050 s/op
Iteration   5: 0.055 s/op


Result: 0.064 ±(99.9%) 0.071 s/op [Average]
  Statistics: (min, avg, max) = (0.050, 0.064, 0.096), stdev = 0.018
  Confidence interval (99.9%): [-0.007, 0.135]


# Run complete. Total time: 00:00:22

Benchmark            Mode  Samples  Score  Score error  Units
o.s.MyBenchmark.c    avgt        5  0.061        0.060   s/op
o.s.MyBenchmark.d    avgt        5  0.064        0.071   s/op
```
性能几乎是一样的

## 3. while(true) 循环问题
顺便说一下 while(true) 循环问题，讨论时，有老师说 while(true) 不会造成 cpu 占用 100%，这是因为我们现在的 cpu 都是多核的，一个线程内的 while(true) 顶多造成其中一核的占用高，所以多开几个线程（核数）跑 while(true) 就能看到现象了。

实际编程中不建议跑空循环，都会在 while 中间加一个短暂的 sleep 避免 cpu 100%

## 4. 结论

1. 单核 cpu 下，多线程不能实际提高程序运行效率，只是为了能够在不同的任务之间切换，不同线程轮流使用 cpu ，不至于一个线程总占用 cpu，别的线程没法干活
2. 多核 cpu 可以并行跑多个线程，但能否提高程序运行效率还是要分情况的
	* 有些任务，经过精心设计，将任务拆分，并行执行，当然可以提高程序的运行效率。但不是所有计算任务都能拆分。
	* 也不是所有任务都需要拆分，任务的目的如果不同，谈拆分和效率没啥意义
3. 多线程更多的意义在于异步处理
	* 比如在项目中，视频文件需要转换格式等操作比较费时，这时开一个新线程处理视频转换，避免阻塞主线程
	* tomcat 的异步 servlet 也是类似的目的，让用户线程处理耗时较长的操作，避免阻塞 tomcat 的工作线程
	* ui 程序中，开线程进行其他耗时操作，避免阻塞 ui 线程
4. IO 操作不占用 cpu，只是我们一般拷贝文件使用的是【阻塞 IO】，这时相当于线程虽然不用 cpu，但需要一直等待 IO 结束，没能充分利用线程。所以才有后面的【非阻塞 IO】和【异步 IO】

## 5. 参考资料
* CPU 占用问题，参考了【编程之美】第一节
* 服务器模型
	* [资料1](https://www.jianshu.com/p/a253d21e4b16)
	* [资料2](https://www.cnblogs.com/linzhanfly/p/9082895.html)
* `C++` 中设置线程绑定的 CPU，`C++`还是强大啊
    * [资料1](https://docs.microsoft.com/zh-cn/windows/desktop/api/winbase/nf-winbase-setthreadaffinitymask)
    * [资料2](https://blog.csdn.net/qiaochenglei/article/details/4723959)
* [tomcat 异步 servlet](https://blog.csdn.net/wangyangzhizhou/article/details/53207966)