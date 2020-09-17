package cic.cs.unb.ca.jnetpcap.worker;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.FlowGenerator;
import cic.cs.unb.ca.jnetpcap.PacketReader;
import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory.Type;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

public class TrafficFlowWorker extends SwingWorker<String, String> implements FlowGenListener {

    public static final Logger logger = LoggerFactory.getLogger(TrafficFlowWorker.class);
    public static final String PROPERTY_FLOW = "flow";
    private final String device;

    public TrafficFlowWorker(String device) {
        super();
        this.device = device;   //传入网卡设备的名称
    }

    @Override
    protected String doInBackground() {

        FlowGenerator flowGen = new FlowGenerator(true, 120000000L, 5000000L);
        flowGen.addFlowListener(this);
        int snaplen = 64 * 1024;//2048; // Truncate packet at this size  //包的最大大小为64K
        int promiscous = Pcap.MODE_PROMISCUOUS; //设置网卡为混杂模式，接收所有流过该网卡的包，而不管目标地址是否是该地址
        int timeout = 60 * 1000; // In milliseconds  //超时时间为6s
        StringBuilder errbuf = new StringBuilder();
        Pcap pcap = Pcap.openLive(device, snaplen, promiscous, timeout, errbuf);    //开启网卡监听
        if (pcap == null) { //如果开启监听失败，打印失败信息
            logger.info("open {} fail -> {}", device, errbuf.toString());
            return String.format("open %s fail ->", device) + errbuf.toString();
        }

        //new一个handler接口，并加载其函数
        PcapPacketHandler<String> jpacketHandler = (packet, user) -> {

            /*
             * BufferUnderflowException while decoding header
             * that is because:
             * 1.PCAP library is not multi-threaded
             * 2.jNetPcap library is not multi-threaded
             * 3.Care must be taken how packets or the data they referenced is used in multi-threaded environment
             *
             * typical rule:
             * make new packet objects and perform deep copies of the data in PCAP buffers they point to
             *
             * but it seems not work
             */

            PcapPacket permanent = new PcapPacket(Type.POINTER);
            packet.transferStateAndDataTo(permanent);   //将缓冲区的数据包放入到permanent中

            flowGen.addPacket(PacketReader.getBasicPacketInfo(permanent, true, false)); //读取IPV4数据包，并将读取到的数据存入到数据分析流中
            if (isCancelled()) {
                pcap.breakloop();
                logger.debug("break Packet loop");
            }
        };

        //FlowMgr.getInstance().setListenFlag(true);
        logger.info("Pcap is listening...");
        firePropertyChange("progress", "open successfully", "listening: " + device);
        int ret = pcap.loop(Pcap.DISPATCH_BUFFER_FULL, jpacketHandler, device);

        String str;
        switch (ret) {
            case 0:
                str = "listening: " + device + " finished";
                break;
            case -1:
                str = "listening: " + device + " error";
                break;
            case -2:
                str = "stop listening: " + device;
                break;
            default:
                str = String.valueOf(ret);
        }

        return str;
    }

    @Override
    protected void process(List<String> chunks) {
        super.process(chunks);
    }

    @Override
    protected void done() {
        super.done();
    }

    @Override
    public void onFlowGenerated(BasicFlow flow) {
        firePropertyChange(PROPERTY_FLOW, null, flow);
    }
}
