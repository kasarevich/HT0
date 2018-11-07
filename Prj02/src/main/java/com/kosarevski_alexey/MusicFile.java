package com.kosarevski_alexey;

public class MusicFile {
    private String nameOfTrack;
    private long length;
    private String path;
    private String checkSum;

    public String getNameOfTrack() {
        return nameOfTrack;
    }

    public void setNameOfTrack(String nameOfTrack) {
        this.nameOfTrack = nameOfTrack;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MusicFile)) return false;

        MusicFile musicFile = (MusicFile) o;

        if (getLength() != musicFile.getLength()) return false;
        return getNameOfTrack() != null ? getNameOfTrack().equals(musicFile.getNameOfTrack()) : musicFile.getNameOfTrack() == null;
    }

    @Override
    public int hashCode() {
        int result = getNameOfTrack() != null ? getNameOfTrack().hashCode() : 0;
        result = 31 * result + (int) (getLength() ^ (getLength() >>> 32));
        result = 31 * result + (getPath() != null ? getPath().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "\tНазвание: " + nameOfTrack + " Длительность: " + length + " c. " + " Расположение: " + path;
    }
}
