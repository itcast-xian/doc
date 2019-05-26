<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户登录</title>
    <script src="/js/vuejs-2.5.16.js"></script>
    <script src="/js/axios-0.18.0.js"></script>
    <script src="/js/md5.js"></script>
    <script src="/js/axios-interceptor.js"></script>
    <script src="/js/axios-response-interceptor.js"></script>
</head>


<body>
<div  id="app">
   User List ...
</div>
</body>

<script>
   new Vue({
        el: "#app",
        data: {

        },
        methods: {
            findUserList:function(){
               axios.post('/user/findUserList.do').then(function(response){
                    if(response.data.success){

                    }else{

                    }
                })

               //location.href = "/user/findUserList.do";

            }
        },
        mounted:function(){
            this.findUserList();
        }
    });
</script>


</html>
