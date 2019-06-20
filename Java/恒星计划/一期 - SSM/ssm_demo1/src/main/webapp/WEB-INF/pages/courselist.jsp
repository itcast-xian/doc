<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户登录</title>
    <script src="/js/vuejs-2.5.16.js"></script>
    <script src="/js/axios-0.18.0.js"></script>
    <script src="/js/md5.js"></script>
    <script src="/js/axios-request-interceptor.js"></script>
    <script src="/js/axios-response-interceptor.js"></script>

    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <script src="/js/jquery-3.2.1.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>

    <link href="/css/zpageNav.css" rel="stylesheet" />
    <script src="/js/zpageNav.js"></script>

    <style>
        body{
            background-color: honeydew;
        }
        .table_div{
            margin-top: 5px;
        }
        .con_div{
            border: 1px solid #dcdcdc;
            padding: 10px;
        }
        th:last-child{
            text-align: center;
        }
        .pag_nav{
            text-align: center;
        }

        h2{
            color: #4cae4c;
            font-family: 楷体;
        }

        textarea{
            resize: none;
        }
    </style>
</head>


<body>

<div id="app">

    <div  class="container ">
        <div class="row">
            <span style="font-size: 40px;color: #3e8f3e;font-family: 楷体;">课程管理</span>
        </div>

        <div class="row form-inline con_div">
            <div class="form-group col-sm-3">
                <button type="button" class="btn btn-success" v-on:click="entity={}"  data-toggle="modal" data-target="#editModal">
                    <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>&nbsp;&nbsp;&nbsp;添加
                </button>
            </div>

            <div class="form-group col-sm-3">
                <label for="name">课程名称:</label>
                <input type="text" class="form-control" v-model="searchEntity.name" id="name" placeholder="课程名称">
            </div>

            <div class="form-group col-sm-3">
                <label for="teacherName">讲师姓名:</label>
                <input type="text" class="form-control" v-model="searchEntity.teacherName" id="teacherName" placeholder="讲师姓名">
            </div>

            <div class="form-group col-sm-3" >
                <button type="button" class="btn btn-primary"  v-on:click="page=1;search();">
                    <span class="glyphicon glyphicon-search" aria-hidden="true"></span>&nbsp;&nbsp;&nbsp;搜索
                </button>
                <button type="button" class="btn btn-primary" v-on:click="reset();">
                    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;&nbsp;&nbsp;重置
                </button>
            </div>

        </div>



        <div class="row table_div">
            <table class="table table-bordered table-hover">
                <tr>
                    <th>ID</th>
                    <th>课程名称</th>
                    <th>讲师</th>
                    <th>价格</th>
                    <th>创建时间</th>
                    <th>更新时间</th>
                    <th>操作</th>
                </tr>
                <tr v-for="c in dataList">
                    <td>{{c.id}}</td>
                    <td>{{c.name}}</td>
                    <td>{{c.teacherName}}</td>
                    <td>{{c.price}}</td>
                    <td>{{formatTime(c.createtime)}}</td>
                    <td>{{formatTime(c.updatetime)}}</td>
                    <td align="center">
                        <button type="button" class="btn btn-success  btn-xs"  v-on:click="findOne(c.id)" data-toggle="modal" data-target="#editModal">
                            <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;&nbsp;&nbsp;修改
                        </button>

                        <button type="button" class="btn btn-danger btn-xs" v-on:click="del(c.id)">
                            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>&nbsp;&nbsp;删除
                        </button>

                    </td>
                </tr>
            </table>

        </div>

        <!-- Vue分页插件 -->
        <div class="wrap" id="wrap">
            <zpagenav v-bind:page="page" v-bind:page-size="pageSize" v-bind:total="total" v-bind:max-page="maxPage"  v-on:pagehandler="pageHandler"></zpagenav>
        </div>

    </div>


    <div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog" >
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h3 id="myModalLabel">课程管理</h3>
                </div>
                <div class="modal-body">

                    <table class="table table-bordered table-striped"  width="800px">
                        <tr>
                            <td>课程名称</td>
                            <td><input  type="text" class="form-control" placeholder="课程名称"  v-model="entity.name">  </td>
                        </tr>
                        <tr>
                            <td>讲师姓名</td>
                            <td><input type="text" class="form-control" placeholder="讲师姓名" v-model="entity.teacherName"></td>
                        </tr>
                        <tr>
                            <td>课程价格</td>
                            <td><input type="number" class="form-control" placeholder="课程价格" v-model="entity.price"></td>
                        </tr>
                        <tr>
                            <td>课程描述</td>
                            <td>
                                <textarea rows="10" class="form-control" placeholder="课程描述" v-model="entity.description"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-success" data-dismiss="modal" aria-hidden="true" v-on:click="save()">保存</button>
                    <button class="btn btn-default" data-dismiss="modal" aria-hidden="true">关闭</button>
                </div>
            </div>
        </div>
    </div>


</div>

</body>

<script>
   new Vue({
        el: "#app",
        data: {
            searchEntity:{},
            entity:{},
            dataList:[],
            page: 1,  //显示的是哪一页
            pageSize: 10, //每一页显示的数据条数
            total: 108, //记录总数
            maxPage:5  //最大页数
        },
        methods: {
            pageHandler: function (page) {
                this.page = page;
                this.search();
            },

            search:function(){
               var _this = this;
               axios.post('/course/search.do?page='+_this.page+"&pageSize="+_this.pageSize,_this.searchEntity).then(function(response){
                    _this.total = response.data.total;
                    _this.dataList = response.data.rows;
                })
            },

            save:function(){
                var _this = this;
                var url = _this.entity.id?'/course/update.do':'/course/add.do';

                axios.post(url,_this.entity).then(function(response){
                    if(response.data.success){
                        alert(response.data.message);
                        _this.search();
                    }else{
                        alert(response.data.message);
                    }
                })
            },

            del: function(id){
                var _this = this;
                if(confirm("确认删除吗?")){
                    axios.get('/course/delete.do?id='+id).then(function(response){
                        if(response.data.success){
                            alert(response.data.message);
                            _this.search();
                        }else{
                            alert(response.data.message);
                        }
                    })
                }
            },

            findOne:function(id){
                var _this = this;
                axios.get('/course/findOne.do?id='+id).then(function(response){
                    _this.entity = response.data;
                })
            },

            reset: function(){
                this.searchEntity = {};
                this.pageHandler(1);
            },

            formatTime: function(dateUnix){
                var time = new Date(dateUnix).toLocaleString();
                return time;
            },

        },
        mounted:function(){
            this.pageHandler(1);
        }
    });
</script>


</html>
