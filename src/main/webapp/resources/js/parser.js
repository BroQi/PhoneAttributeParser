/**
 * 初始web uploader
 */
var uploader = WebUploader.create({
    swf: 'reources/plugin/webUploader/Uploader.swf',
    accept: {
        title: "Excels",
        extensions: "xlsx,xls",
        mimeTypes: 'excel/*'
    },
    auto: true,
    pick: "#picker",
    server: "parse"
});
// 加载所需模块
layui.use(['form', 'upload', 'element', 'util'], function(){
    var form = layui.form
        ,upload = layui.upload;
    var util = layui.util;

    /**
     * 配置右下角工具栏
     */
    util.fixbar({
        bar1: "&#xe6fc;",
        css: {bottom:180},
        click: function(type){
            if(type === 'bar1'){
                // 显示音乐播放器
                if (!$("#cloudMusic").is(":hidden")) {
                    $("#cloudMusic").hide();
                } else {
                    $("#cloudMusic").show();
                }
            }
        }
    });
});

var layerLoad = null;
uploader.on("startUpload", function() {
    layerLoad = layer.load();
});
uploader.on( 'uploadSuccess', function( file, response) {
    if (response.code == 200) {
        window.location.href = "parse/download/" + response.data;
    } else {
        layer.alert(response.message);
    }
});

uploader.on( 'uploadError', function( file ) {
    layer.alert("上传出现错误");
});

uploader.on( 'uploadComplete', function( file ) {
    uploader.removeFile(file);
    layer.close(layerLoad);
});

/**
 * 修改背景图片
 */
function changeBGI() {
    var  name = Math.floor(Math.random() * 22 + 1) + ".gif";
    $("body").css("background-image", "url('resources/images/" + name + "')");
}