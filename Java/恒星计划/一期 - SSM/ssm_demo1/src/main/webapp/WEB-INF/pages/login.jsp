<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户登录</title>
    <link href="/css/reg.css" rel="stylesheet">
    <script src="/js/vuejs-2.5.16.js"></script>
    <script src="/js/axios-0.18.0.js"></script>
    <script src="/js/md5.js"></script>
    <script src="/js/axios-request-interceptor.js"></script>
</head>

<body>
    <div class="rg_layout" id="app">
        <div id="div_fm" style="margin-top: 50px">
                <h3><center>用户登录</center></h3><br>
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
                        <td colspan="2" align="center">
                            <input type="button" v-on:click="login()" style="border-radius: 15px" id="btn_sub" value="登录">
                            <span>&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0)">忘记密码?</a></span>
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
            login:function(){
                app.user.password = hex_md5(app.user.password);
                axios.post('/user/login.do',app.user).then(function(response){
                    if(response.data.success){
                        console.log(response.data.message);
                        sessionStorage.setItem("jwt_token",response.data.message);
                        location.href="http://localhost:8080/index/courselist.do";
                    }else{
                        alert(response.data.message);
                    }
                })
            }
        }
    });
</script>


</html>
