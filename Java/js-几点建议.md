## 1. 建议在演示代码时，进行分屏或让代码比较紧凑，否则鼠标上下滚动，造成思维和视觉的不连贯

## 2. 有学员尝试将动态表格和表格全选两个案例结合，出现了如下问题

```html
<div>
    <input type="text" id="id" placeholder="请输入编号">
    <input type="text" id="name"  placeholder="请输入姓名">
    <input type="text" id="gender"  placeholder="请输入性别">
    <input type="button" value="添加" id="btn_add">

</div>
<table>
    <caption>学生信息表</caption>
    <tr>
        <th><input type="checkbox" id="firstCk"></th>
        <th>编号</th>
        <th>姓名</th>
        <th>性别</th>
        <th>操作</th>
    </tr>
    <tr>
        <td><input type="checkbox" name="cb" ></td>
        <td>1</td>
        <td>令狐冲</td>
        <td>男</td>
        <td><a href="javascript:void(0);" onclick="delTr(this);">删除</a></td>
    </tr>
    <tr>
        <td><input type="checkbox" name="cb" ></td>
        <td>2</td>
        <td>任我行</td>
        <td>男</td>
        <td><a href="javascript:void(0);" onclick="delTr(this);">删除</a></td>
    </tr>
    <tr>
        <td><input type="checkbox" name="cb" ></td>
        <td>3</td>
        <td>岳不群</td>
        <td>?</td>
        <td><a href="javascript:void(0);" onclick="delTr(this);" >删除</a></td>
    </tr>
</table>
```

```js
document.getElementById("btn_add").onclick = function() {
    var id = document.getElementById("id").value;
    var name = document.getElementById("name").value;
    var gender = document.getElementById("gender").value;
    var table = document.getElementsByTagName("table")[0];
        table.innerHTML +=
            "<tr>\n" +
            "   <td><input type=\"checkbox\" name=\"cb\" ></td>" +
            "   <td>"+id+"</td>\n" +
            "   <td>"+name+"</td>\n" +
            "   <td>"+gender+"</td>\n" +
            "   <td><a href='javascript:void(0);' onclick='delTr(this);' >删除</a></td>\n" +
            "</tr>";
};

document.getElementById("firstCk").onclick = function(){
    let cks = document.getElementsByName("cb");
    for (let i = 0; i < cks.length; i++) {
        cks[i].checked = this.checked;
    }
};

function delTr(obj){
    var table = obj.parentNode.parentNode.parentNode;
    var tr = obj.parentNode.parentNode;
    table.removeChild(tr);
}
```

新增行前全选功能正常，但新增行后绑定的事件消失，应该是 += 操作重绘了表格，导致全选事件丢失

解决思路

1. 将表格分成两部分，thead 和 tbody，新增的 += 只影响 tbody 中的行
2. 将事件绑定写在标签里而不是代码中

## 3. 建议使用 console.log 作为演示和调试手段，而不要用 alert 和 document.write

* 前者看不到对象具体信息
* 后者开发基本用不到，并且会破坏现有已加载页面

## 4. this 有时是一个坑，学员滥用会发生很多问题，例如：

```js
window.onload = function() {
    var username = document.getElementById("username");
    username.onblur = checkUsername;
    // ...
    // 给表单添加 onsubmit 事件
    var form = document.getElementById("form");
    form.onsubmit = function() {
        return checkUsername() && checkPassword();
    }
}
function checkUsername() {
    var s_username = document.getElementById("s_username");
    var username = document.getElementById("username");

    // 下面的 this 在 onblur 事件，指代的是 username 文本框
    //  但在 onsubmit 事件，指代的是 window 对象
    var flag = /^\w{6,12}$/.test(this.value);
    if(flag) {
        // 验证通过
        s_username.innerHTML = "<img src='img/gou.png'>";
    } else {
        // 验证不过，提示错误信息
        s_username.innerHTML = "用户名格式有误";
    }
    return flag;
}
```
