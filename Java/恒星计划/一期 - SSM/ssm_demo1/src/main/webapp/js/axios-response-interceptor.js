
// 响应拦截
axios.interceptors.response.use(data => {
    if(data && data.data){
        if(data.data.success==false && data.data.message =='NO_LOGIN'){
            //需要登录
            window.location = "http://localhost:8080/index/login.do";
        }
    }
    return data;
})