var pagesize = 10, currentpage = 0; //默认第一页
$('#zh-load-more').click(function () {
    $.ajax({
        url: '/fyQuery',
        type: 'POST',
        data: {
            currentpage:++currentpage,
            pagesize:10
        },
        async : false,
        complete: function (xhr) {
            if (200==xhr.status) {//成功返回
                currentpage++;
                $(xhr.responseText).insertBefore('#zh-load-more');//将返回内容插入容器中
            }
            else alert('动态页出错，返回内容：'+xhr.responseText);
        }
    });
    return false;
});