jeui.use(["jquery","jeBox","jeDate","jeCheck","../jquery.cookie.min"],function () {
    let token = $.cookie("lhDream_token");
    if(null == token || "" == token || undefined == token){
        window.location.href = "login.html"
    }

    function resetUserList(){
        //获取用户列表
        $.post("userList", {
            token: $.cookie("lhDream_token"),
            username: $.cookie("lhDream_token_user")
        },function (data,status){
            if("success" == status){
                data = $.parseJSON(data);
                if("success" == data.res){
                    $("#userList").children().remove();
                    let userList = $.parseJSON(data.data);;
                    $.each(userList, function (n, value) {
                        $("#userList").append("<tr>" +
                            "<td align=\"center\"><input type=\"checkbox\" name=\"checkbox\" jename=\"chunk\"></td>" +
                            "<td>"+ n +"</td>" +
                            "<td>"+ value.name +"</td>" +
                            "<td>"+ value.uuid +"</td>" +
                            "<td>"+ (value.state == 1 ? "在线":"离线") +"</td>" +
                            "<td>---</td>" +
                            "<td>---</td>" +
                            "<td align=\"center\">" +
                            "<button class=\"je-btn je-btn-mini je-f12\">编辑</button>" +
                            "<button class=\"je-btn je-btn-mini je-bg-red je-f12\">删除</button>" +
                            "</td>")
                    });
                }else{
                    alert("请求数据失败，请重新登录。");
                }
            }
        });
    }
    $("#reset").click(resetUserList);
    resetUserList();

    var start = {
        format: 'YYYY-MM-DD hh:mm:ss',
        minDate: '2014-06-16 23:59:59', //设定最小日期为当前日期
        isinitVal:true,
        //festival:true,
        ishmsVal:false,
        maxDate: $.nowDate({DD:0}), //最大日期
        choosefun: function(elem, val, date){
            end.minDate = date; //开始日选好后，重置结束日的最小日期
            endDates();
        }
    };
    var end = {
        format: 'YYYY-MM-DD hh:mm:ss',
        minDate: $.nowDate({DD:0}), //设定最小日期为当前日期
        //festival:true,
        maxDate: '2099-06-16 23:59:59', //最大日期
        choosefun: function(elem, val, date){
            start.maxDate = date; //将结束日的初始值设定为开始日的最大日期
        }
    };
    //这里是日期联动的关键
    function endDates() {
        //将结束日期的事件改成 false 即可
        end.trigger = false;
        $("#inpend").jeDate(end);
    }
    $('#inpstart').jeDate(start);
    $('#inpend').jeDate(end);
    $("#newCheck").jeCheck({
        jename:"chunk",
        attrName:[false,"勾选"],
        itemfun: function(elem,bool) {
            console.log(bool)
            //console.log(elem.prop('checked'))
        },
        success:function(elem){
            jeui.chunkSelect(elem,'#gocheck','on')

        }
    })
});