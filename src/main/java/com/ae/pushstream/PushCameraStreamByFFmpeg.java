package com.ae.pushstream;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * ffmpeg推流到SRS
 * @author xch
 *
 */
@Component
public class PushCameraStreamByFFmpeg {

    private String sourceAddress;

    private String targetAddress="rtmp://192.168.7.233:31980/live/livestream";

    private String ffmpegPath;


    /**
     * 推流外部摄像头
     * 完整地址 ffmpeg -re -i rtsp://admin:zd199611@192.168.0.109:554/h264/1/main/av_stream -vcodec copy -acodec copy -f flv -y rtmp://192.168.145.201:1935/live/livestream
     * 播放地址 http://192.168.145.201:8080/live/livestream.flv
     * @param sourceAddress 摄像头地址 rtsp://admin:zd199611@192.168.0.109:554/h264/1/main/av_stream
     * @param targetAddress SRS地址   rtmp://192.168.145.201:1935/live/livestream
     */
    public void startPushCameraStream(String sourceAddress, String targetAddress){
        try {
            String command = "ffmpeg -re -i " + sourceAddress + " -vcodec copy -acodec copy -f flv -y " + targetAddress;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while((line = br.readLine()) != null) {
                System.out.println("视频推流信息[" + line + "]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 推送本地摄像头
     * @param targetAddress
     */
    public static void startLocalCameraSteam(String targetAddress){
        try {
            String camraName=getCamera();
            String videoName=getAudio();
            if(camraName!=null){
                String command ="ffmpeg -f dshow -i video="+camraName+":audio="+videoName+" -vcodec libx264 -preset:v ultrafast -tune:v zerolatency -f flv "+targetAddress;
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = null;
                while((line = br.readLine()) != null) {
                    System.out.println("视频推流信息[" + line + "]");
                }
            }else {
                System.out.println("没有找到摄像头");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 推送桌面
     * @param targetAddress
     */
    public static void startWindows(String targetAddress){
        try {
                String command ="ffmpeg -f gdigrab -i desktop -vcodec libx264 -preset ultrafast -acodec libmp3lame -ar 44100 -ac 1 -r 25 -s 1920*1080 -f flv "+targetAddress;
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = null;
                while((line = br.readLine()) != null) {
                    System.out.println("桌面推流信息[" + line + "]");
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 推送桌面
     * @param targetAddress
     */
    public static void startWindowsAndVideo(String targetAddress){
        try {
            String camraName=getCamera();
            String videoName=getAudio();
            String command ="ffmpeg -f dshow -i video="+camraName+" -f dshow -i audio="+videoName+" -s 1920x1080 -r 20 -f gdigrab -i desktop -f dshow -i audio="+videoName+" -filter_complex \"[0:v]scale=iw/3:-1[sv];[2:v][sv]overlay=shortest=1:x=(main_w-overlay_w):y=(main_h-overlay_h)[vout];[1:a]volume=0.3[a0];[3:a]volume=0.6[a1];[a0][a1]amix=inputs=2:duration=shortest[aout]\" -map \"[vout]\" -map \"[aout]\" -vcodec libx264 -b:v 5000k -acodec aac -b:a 128k -ar 44100 -ac 2 -f flv "+targetAddress;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while((line = br.readLine()) != null) {
                System.out.println("桌面推流信息[" + line + "]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static String getCamera(){
        try {
            String cameraName="";
            String command ="ffmpeg -list_devices true -f dshow -i dummy";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while((line = br.readLine()) != null) {
                if(line.contains("Camera")){
                    cameraName=line.substring(line.indexOf('"'),line.lastIndexOf('"')+1);
                    return  cameraName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAudio(){
        try {
            String audioName="";
            String command ="ffmpeg -list_devices true -f dshow -i dummy";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while((line = br.readLine()) != null) {
                if(line.contains("麦克风")){
                    audioName=line.substring(line.indexOf('"'),line.lastIndexOf('"')+1);
                    return  audioName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
//        System.out.println(getAudio());
//        startLocalCameraSteam("rtmp://8.136.179.213/live/livestream");
//        startWindows("rtmp://8.136.179.213/live/livestream");
        startWindowsAndVideo("rtmp://192.168.7.233:31980/live/livestream");
    }

}
