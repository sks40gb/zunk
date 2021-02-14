package com.sun.designpattern.structural.adapter;

public class AdaptorApp {

    public static void main(String[] args) {
        Player player = new Player();

        // Play mp3
        Music mp3 = new MP3();
        player.play(mp3);

        // Play video
        Music video = new MusicAdaptor(new Video());
        player.play(video);
    }
}

interface Music {

    void listen();
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

class MusicAdaptor implements Music {

    private Video video;

    MusicAdaptor(Video video) {
        this.video = video;
    }

    public void listen() {
        video.watch();
    }
}
