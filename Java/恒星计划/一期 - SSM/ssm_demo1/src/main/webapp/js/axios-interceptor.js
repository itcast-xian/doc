// 添加请求拦截器
axios.interceptors.request.use(function (config) {
    // 在发送请求向header添加jwt
    let jwt = sessionStorage.getItem("jwt_token");
    if(jwt){
        config.headers['Authorization'] = jwt;
    }
    return config;
}, function (error) {
    return Promise.reject(error);
});