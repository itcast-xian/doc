# 函数提升

## 1. 问题引出
课堂上讲了`后定义的函数会覆盖先定义的函数`说法不是很确切，例如

```js
<script>
    foo(); // 3
    function foo() {
        console.log( 1 );
    }
    var foo = function() {
        console.log( 2 );
    };
    foo(); // 2
    function foo() {
        console.log( 3 );
    }
    var foo = function() {
        console.log( 4 );
    }
    foo(); // 4
</script>
```
如何解释，先理解以下规则：
* 变量声明会被提升，函数声明优先提升
* 匿名函数不会提升
* 具名函数后定义的会覆盖先定义的
* 作用域中同名函数以及变量仅会声明一次
* 声明与赋值分开运行，赋值语句留在原地

上述代码会被解析为:
```js
<script>
    function foo() { // 被覆盖
        console.log( 1 );
    }
    function foo() {
        console.log( 3 );
    }
    foo(); // 3    
    foo = function() { // var foo 被提升，但已有了同名的函数 foo，因此被忽略
        console.log( 2 );
    };
    foo(); // 2    
    foo = function() { // var foo 被提升，但已有了同名的函数 foo，因此被忽略
        console.log( 4 );
    }
    foo(); // 4
</script>
```