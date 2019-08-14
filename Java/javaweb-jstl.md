问题：

```jsp
<c:forEach begin="1" end="10" step="2" varStatus="v">
    ${v.index}
</c:forEach>
```

输出结果为 1 3 5 7 9

javax.servlet.jsp.jstl.core.LoopTagSupport

```java
public int doAfterBody() throws JspException {
    this.index += this.step - 1;
    ++this.count;
    if (this.hasNext() && !this.atEnd()) {
        ++this.index;
        this.item = this.next();
        this.discard(this.step - 1);
        this.exposeVariables(false);
        this.calibrateLast();
        return 2;
    } else {
        return 0;
    }
}
```

```java
class Status implements LoopTagStatus {
    Status() {
    }
    public Object getCurrent() {
        return LoopTagSupport.this.getCurrent();
    }
    public int getIndex() {
        return LoopTagSupport.this.index + LoopTagSupport.this.begin;
    }
```

索引最后的 index 每一次的值为 (index + step - 1)++ + begin === i 的值
