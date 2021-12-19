package ratel.com.shizhuang.duapp;

import android.util.Log;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.inspect.ClassLoadMonitor;
import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.sekiro.api.ActionHandler;
import com.virjar.sekiro.api.SekiroRequest;
import com.virjar.sekiro.api.SekiroResponse;
import com.virjar.sekiro.api.databind.AutoBind;

public class SearchQueryHandler implements ActionHandler {
    @Override
    public String action() {
        return "search-hot-word";
    }


//    static {
//        // 这里是为了在onSuccess的时候，把数据打印出来
//        RposedHelpers.findAndHookMethod("com.shizhuang.duapp.common.helper.net.facade.AbsViewHandler",
//                RatelToolKit.hostClassLoader, "onSuccess",
//                Object.class,
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.i(CommonConst.AppTag, "com.ratel.MyDataModel onSuccess,param.args[0]:"
//                                + param.args[0].toString());
//                        Object dataResult = param.args[0];
//                        Object searchWord = RposedHelpers.callMethod(dataResult,"getSearchWord");
//                        Log.i(CommonConst.AppTag,"searchWord:" + searchWord.toString());
//                        Object listData = RposedHelpers.callMethod(dataResult,"getList");
//                    }
//                });
//    }

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, final SekiroResponse sekiroResponse) {
        String searchJavaName = "com.shizhuang.duapp.modules.search.facade.SearchFacade";
        Class<?> searchJava = ClassLoadMonitor.tryLoadClass(searchJavaName);
        if (searchJava != null) {
            // 加载 l函数需要的参数
            Class<?> myDataModel = ClassLoadMonitor.tryLoadClass(
                    "com.shizhuang.duapp.common.helper.net.facade.ViewHandler");
            if(myDataModel!=null) {
                Log.i(CommonConst.AppTag, myDataModel.toString());
                try{
                    // 创建一个ViewHandler实例
                    Object myDataModelProxy = RposedHelpers.newInstance(myDataModel);
                    // 调用l方法
                    RposedHelpers.callStaticMethod(searchJava, "l", myDataModelProxy);
                    Log.i(CommonConst.AppTag,"call l StaticMethod finish");

                }catch (Exception ex){
                    Log.e(CommonConst.AppTag, "call l StaticMethod fail", ex);
                }
                sekiroResponse.success(myDataModel.getMethods());
            }else {
                Log.i(CommonConst.AppTag, "com.ratel.MyDataModel not found.");
                sekiroResponse.failed("myDataModel not foun");
            }
        }
    }
}