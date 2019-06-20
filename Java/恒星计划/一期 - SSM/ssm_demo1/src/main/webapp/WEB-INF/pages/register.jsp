<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户注册</title>
    <link href="/css/reg.css" rel="stylesheet">
    <script src="/js/vuejs-2.5.16.js"></script>
    <script src="/js/axios-0.18.0.js"></script>
    <script src="/js/md5.js"></script>
</head>

<body>
    <div class="rg_layout" id="app">
        <div id="div_fm">
                <h3><center>新用户注册</center></h3><br>
            <!--定义表单 form-->
                <table>
                    <tr>
                        <td class="td_left"><label for="username">用&nbsp;户&nbsp;名</label></td>
                        <td class="td_right"><input type="text" v-model="user.username" id="username" placeholder="请输入用户名"></td>
                    </tr>

                    <tr>
                        <td class="td_left"><label for="password">密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码</label></td>
                        <td class="td_right"><input type="password" v-model="user.password" id="password" placeholder="请输入密码"></td>
                    </tr>

                    <tr>
                        <td class="td_left"><label for="password">确认密码</label></td>
                        <td class="td_right"><input type="password" v-model="repassword" id="repassword" placeholder="请输入确认密码"></td>
                    </tr>

                    <tr>
                        <td colspan="2"  align="center">
                            <input type="button" v-on:click="register()" id="btn_sub" value="注册">
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <span>已有账号?<a href="/index/login.do">立即登录</a></span>
                        </td>
                    </tr>

                </table>

        </div>
    </div>
</body>

<script>
    var app = new Vue({
        el: "#app",
        data: {
            user:{}
        },
        methods: {
            register:function(){
                if(!app.checkForm()){
                    alert('参数非法');
                    return;
                }

                //进行MD5加密, 密码在进行传输时, 不能是明文 ;
                app.user.password = hex_md5(app.user.password);

                axios.post('/user/register.do',app.user).then(function(response){
                    if(response.data.success){
                        alert(response.data.message);
                        location.href="/index/login.do";
                    }else{
                        alert(response.data.message);
                    }
                })
            },
            checkForm:function(){
                if(app.user.username && app.user.password && app.repassword && app.user.password == app.repassword){
                    return true;
                }
                return false;
            }
        },
        created:function(){
           // alert(hex_md5('123456'));
        }
    });
</script>


</html>
