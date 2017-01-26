package com.sun.designpattern.structural.adapter.b;


public class AdaptarApp {

    public static void main(String[] args) {
        Player player = new Player();
        
        // Play mp3
        Music mp3 = new MP3();
        player.play(mp3);
        
        // Play video
        Music video = new MusicAdaptar(new Video());
        player.play(video);
    }
}
interface Music {

    public void listen();
}

class MP3 implements Music {

    public void listen() {
        System.out.println("Listening MP3 music ....");
    }
}

class Player {

    public void play(Music music) {
        music.listen();
    }
}

class Video {

    public void watch() {
        System.out.println("Watching videos ....");
    }
}

class MusicAdaptar implements Music{
    private Video video;
    MusicAdaptar(Video video){
        this.video = video;
    }
    
    public void listen(){
        video.watch();
    }
}


