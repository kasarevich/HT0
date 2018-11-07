package com.kosarevski_alexey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MusicCatalog {

    private HashMap<String, HashMap<String, HashSet<MusicFile>>> artists;//имя артиста, список альбомов

    public MusicCatalog() {
        artists = new HashMap<>();
    }

    public void addArtist(String nameOfArtist, HashMap<String, HashSet<MusicFile>> albums){
        artists.put(nameOfArtist, albums);
    }
    public void addAlbum(String nameOfArtist, String nameOfAlbum, HashSet<MusicFile> files){
        artists.get(nameOfArtist).put(nameOfAlbum, files);
    }
    public void addSong(String nameOfArtist, String nameOfAlbum, MusicFile musicFile){
        artists.get(nameOfArtist).get(nameOfAlbum).add(musicFile);
    }

    public HashMap<String, HashMap<String, HashSet<MusicFile>>> getArtists(){
        return artists;
    }


    public String print() {
        StringBuilder sb = new StringBuilder("");
        sb.append("Исполнители:\n");
        for(String artist: artists.keySet()){
            sb.append(artist + ":\n");
            for(String album : artists.get(artist).keySet()){
                sb.append("\tАльбом " + album + " Песни: \n");
                Iterator<MusicFile> iterator = artists.get(artist).get(album).iterator();
                while (iterator.hasNext()){
                    sb.append("\t" + iterator.next().toString() + "\n");
                }
            }
            sb.append("_________________________________________________________\n");
        }
        return sb.toString();
    }

    public String getHtml(){

        return null;
    }
}
