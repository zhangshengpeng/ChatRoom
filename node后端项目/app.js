var createError = require('http-errors');
var express = require('express')
var app = require('express')();
var http = require('http').Server(app);
var multer = require('multer')
var _ = require('lodash');
var findWhere = require('lodash.findwhere');

var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var url = require('url');
var fs = require('fs');
var io = require('socket.io')(http);



var jwt = require('jsonwebtoken');
var cert = fs.readFileSync('./private.key');
var token
var id
var arrAllSocket = [];

var Router = require('./router');



// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');


app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));


//图片存储
var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, './public/img');
  },
  filename: function (req, file, cb) {
    cb(null, file.originalname );
  }
})
var upload = multer({ storage: storage });
app.use(upload.any());



//登录拦截
// app.use(function (req, res,next) {
//   // console.log('url: ',req.url);
//   // console.log('headers: ',req.headers.cookie);
//     var url = req.url;//获取请求地址
//      token = req.cookies.token;
//       // console.log(req.cookies);
//     if (url=="/home") {
//         // console.log("执行token判断");
//             jwt.verify(token,cert,function(err,decoded){
//                 if (err||!decoded) {
//                   res.redirect('/turnToLogin.html');
//                 }
//                 if (decoded.id) {
//                   id = decoded.id
//                   next();
//                 }
//             })
        
//            }
//     else{
//       next();
//     }
   
// });


io.on('connection',function(socket){
  
  socket.on('online', function(id){
  	console.log(id,"加入聊天室")
    arrAllSocket[id] = socket.id
    console.log("online:",arrAllSocket)
  })

  socket.on('message',function(str){
  	var obj = JSON.parse(str)
    console.log("聊天信息:",obj)
    

     var msg = {} 
    msg = {
      name : obj.name,
      message : obj.message,
      url : obj.url
    };
    
    io.emit('message',msg)
    
  })
    socket.on('sayTo', function (data) {
      var obj = JSON.parse(data)
      var data = {}
      data.fromId = obj.fromId
      data.toId = obj.toId
      data.message = obj.message
      data.name = obj.name
      data.url = obj.url

        if (arrAllSocket[obj.toId]!=null&& arrAllSocket[obj.toId]) {
           obj.states = 1
            io.to(arrAllSocket[obj.toId]).emit('sayTo',data);
            io.to(arrAllSocket[obj.fromId]).emit('sayTo',data);
            console.log("已发送")

        }else{
          obj.states = 0
         console.log("该用户不在线")
        
          io.to(arrAllSocket[obj.fromId]).emit('sayTo',data);
        }
          })

  socket.on('disconnect',function(){
  	
    arrAllSocket.forEach(function(item,index){
      if (item == socket.id) {
       delete arrAllSocket[index] 
        console.log("连接断开：",socket.id)
        console.log("online:",arrAllSocket)
      }
    })
  })
})
  

    
  
app.post('/Insert',Router.Insert);
app.post('/Login',Router.Login);
app.post('/User_info',Router.User_info);
app.post('/uploadImg',Router.uploadImg,upload.single('img'));
app.post('/friendList',Router.friendList)
app.post('/Group',Router.Group)
app.post('/Friend',Router.Friend)



// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;

http.listen(6666)

