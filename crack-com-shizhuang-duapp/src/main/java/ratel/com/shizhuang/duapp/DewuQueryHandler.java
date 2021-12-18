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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import external.com.alibaba.fastjson.JSON;

public class DewuQueryHandler implements ActionHandler {
    @Override
    public String action() {
        return "search ";
    }

    @AutoBind
    private String url;


    @AutoBind
    private String body;

    static {
        // 这里是为了在MyACallback onSuccess的时候，把数据通过SekiroResponse回传给服务端
        // 理论上在MyACallback上面做更舒服，但是MyACallback的代码是smali，太难写了，还是直接使用hook比较舒服
        RposedHelpers.findAndHookMethod("com.ratel.MyDataModel",
                RatelToolKit.hostClassLoader, "onSuccess",
                Object.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        SekiroResponse networkCallback = RposedHelpers.getObjectField(
                                param.thisObject,"myACallbackResult");
                        Log.i(CommonConst.AppTag, "com.ratel.MyDataModel onSuccess,param.args[0]:" + param.args[0].toString());
                        Object dataResult = param.args[0];
                        Object searchWord = RposedHelpers.callMethod(dataResult,"getSearchWord");
                        Log.i(CommonConst.AppTag,"searchWord:" + searchWord.toString());
                        Object listData = RposedHelpers.callMethod(dataResult,"getList");
                        if(networkCallback !=null){
                            networkCallback.success(listData);
                        }
                    }
                });
    }

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, final SekiroResponse sekiroResponse) {
        String searchJavaName = "com.shizhuang.duapp.modules.search.facade.SearchFacade";
        Class<?> searchJava = ClassLoadMonitor.tryLoadClass(searchJavaName);
        if (searchJava != null) {
            Class<?> myDataModel = ClassLoadMonitor.tryLoadClass(
                    "com.ratel.MyDataModel");
            if(myDataModel!=null) {
                Log.i(CommonConst.AppTag, myDataModel.toString());
                try{
                    Object myDataModelProxy = RposedHelpers.newInstance(myDataModel, sekiroResponse);
                    RposedHelpers.callStaticMethod(searchJava, "l", myDataModelProxy);
                    Log.i(CommonConst.AppTag,"callStaticMethod finish");

                }catch (Exception ex){
                    Log.e(CommonConst.AppTag, "callStaticMethod fail", ex);
                }
            }else {
                Log.i(CommonConst.AppTag, "com.ratel.MyDataModel not found.");

            }
            // sekiroResponse.success(searchJava.getMethods());

        }
    }
}