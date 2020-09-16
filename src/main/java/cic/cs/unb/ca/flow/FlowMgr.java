package cic.cs.unb.ca.flow;

import cic.cs.unb.ca.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;


public class FlowMgr {

    protected static final Logger logger = LoggerFactory.getLogger(FlowMgr.class);

    public static final String FLOW_SUFFIX = "_Flow.csv";   //保存的文件名后缀

    private static final FlowMgr Instance = new FlowMgr();

    private String mFlowSavePath;   //数据保存目录
    private String mDataPath;       //data目录

    private FlowMgr() {
        super();
    }

    public static FlowMgr getInstance() {
        return Instance;
    }

    //数据流信息初始化
    public FlowMgr init() {

        String rootPath = System.getProperty("user.dir");   //指向当前工程的根目录
        StringBuilder sb = new StringBuilder(rootPath);
        sb.append(Sys.FILE_SEP).append("data").append(Sys.FILE_SEP);    //  ./data

        mDataPath = sb.toString();  //data目录

        sb.append("daily").append(Sys.FILE_SEP);
        mFlowSavePath = sb.toString();  //文件保存目录

        return Instance;
    }

    public void destroy() {
    }

    public String getSavePath() {
        return mFlowSavePath;
    }

    public String getmDataPath() {
        return mDataPath;
    }

    public String getAutoSaveFile() {
        String filename = LocalDate.now().toString() + FLOW_SUFFIX; //当前时间保存文件名
        return mFlowSavePath + filename;
    }
}
