package com.kosarevski_alexey;
import com.mpatric.mp3agic.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Manager {

    private final static String LOG_CONFIGURATION = "log4j.configurationFile";
    private final static String LOG_CONFIGURATION_FILENAME = "log4j2.xml";
    private final static String SUM_DUPLICATES_LOG = "duplicatesBySum";
    private final static String NAME_DUPLICATES_LOG = "duplicatesByName";
    private final static String EXTENSION_PATTERN = "\\.mp3$";
    private final static String DEFAULT_ARTIST = "Unknown artist";
    private final static String DEFAULT_ALBUM = "Unknown album";
    private final static String DEFAULT_TITLE = "Unknown track";
    private final static String CHECKSUM_ALGORITHM = "SHA-1";

    private ArrayList<String> mp3Files = new ArrayList<>();
    private MusicCatalog musicCatalog = new MusicCatalog();
    private HashMap<String, ArrayList<String>> duplicatesByCheckSum = new HashMap<>();
    private HashMap<String, ArrayList<String>> duplicatesByName = new HashMap<>();

    public Manager() {
        System.setProperty(LOG_CONFIGURATION, LOG_CONFIGURATION_FILENAME);
    }

    /**
     * Метод рекурсивно ищет mp3 файлы в заданной директории
     * Если файл имеет расширение ".mp3", и к нему возможно получить доступ, он добавляется в ArrayList mp3 файлов
     * @param directory
     * @return
     */
    public void searchMp3(File directory) {
            if (directory.listFiles() != null) {
                for (File item : directory.listFiles()) {
                    if (item.exists() && item.canRead() && !item.isHidden()) {
                        if (item.isDirectory()) {
                            searchMp3(item);
                        } else if (item.isFile()) {
                            Pattern pattern = Pattern.compile(EXTENSION_PATTERN);
                            Matcher matcher = pattern.matcher(item.getName());
                            if (matcher.find()) {
                                mp3Files.add(item.getAbsolutePath());
                            }
                        }
                    } else {
                        System.out.println("Access error " + item.getAbsolutePath());
                    }
                }
            }
    }

    /**
     * Метод получает необходимые метаданные с помощью библиотеки mp3agic,
     * На основе полученных данных создается entity MusicFile, вызывается метод поиска повторов и добавляется в MusicCatalog
     * Во время добавления файла, производится проверка на дубликаты по чексумме или полному названию
     * По окончании парсинга, производится запись о дубликатах в соответствующие логи
     * @throws InvalidDataException
     * @throws IOException
     * @throws UnsupportedTagException
     * @throws NoSuchAlgorithmException
     */
    public void parseMp3() throws InvalidDataException, IOException, UnsupportedTagException {
            for (String path : mp3Files) {
                    Mp3File mp3File = new Mp3File(path);
                    String nameOfArtist;
                    String nameOfAlbum;
                    String title;
                    long length;

                    if (mp3File.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                    nameOfArtist = id3v1Tag.getArtist();
                    nameOfAlbum = id3v1Tag.getAlbum();
                    title = id3v1Tag.getTitle();
                    } else {
                    nameOfArtist = DEFAULT_ARTIST; // если библиотека не может получить метаданные, заполняется заглушками
                    nameOfAlbum = DEFAULT_ALBUM;
                    title = DEFAULT_TITLE;
                    }
                    MusicFile musicFile = new MusicFile();
                    try {
                        musicFile.setCheckSum(getCheckSum(path));
                    }catch (NoSuchAlgorithmException e) {
                        System.out.println(e.getMessage());
                    } catch (IOException e) {
                        System.out.println("Problem of file access for getting checksum " + path);
                    }
                    length = mp3File.getLengthInSeconds();
                    musicFile.setNameOfTrack(title);
                    musicFile.setLength(length);
                    musicFile.setPath(path);

                    checkForDuplicates(musicFile, nameOfArtist, nameOfAlbum); // проверяем на наличие дубликатов по имени и по хэшкоду
                    addToMusicCatalog(nameOfArtist, nameOfAlbum, musicFile);
            }
        writeDuplicatesLog(duplicatesByCheckSum, LogManager.getLogger(SUM_DUPLICATES_LOG));
        writeDuplicatesLog(duplicatesByName, LogManager.getLogger(NAME_DUPLICATES_LOG));
    }

    /**
     * Метод осуществляет проверку наличие переданного файла в коллекции MusicCatalog
     * В случае
     *      а. Существования в коллекции файлов с идентичными именем исполнителя, названием альбома и названием трека,
     *          новый entity MusicFile(со своим путем к mp3-файлу и чексуммой) добавляется в каталог
     *      b. Отсутствия в коллекции файлов с идентичными именем исполнителя,
     *          Создается новая иерархия Artist, заполняется переданными данными и добавляется в каталог
     * @param nameOfArtist
     * @param nameOfAlbum
     * @param musicFile
     */
    private void addToMusicCatalog(String nameOfArtist, String nameOfAlbum, MusicFile musicFile) {
        if (!musicCatalog.getArtists().containsKey(nameOfArtist)) {                                 //проверяем, есть ли такой артист
            createArtist(nameOfArtist, nameOfAlbum, musicFile);                                     //если нет, создаем
        }
        if (!musicCatalog.getArtists().get(nameOfArtist).containsKey(nameOfAlbum)) {                //если артист есть, но у него нет такого
            HashSet<MusicFile> files = new HashSet<>();                                             //альбома, создаем альбом
            files.add(musicFile);
            musicCatalog.addAlbum(nameOfArtist, nameOfAlbum, files);
        }
        if (musicCatalog.getArtists().containsKey(nameOfArtist)) {                                  // если есть артист, проверяем
            if (musicCatalog.getArtists().get(nameOfArtist).containsKey(nameOfAlbum)) {             //если у него есть такой альбом
                musicCatalog.addSong(nameOfArtist, nameOfAlbum, musicFile);                         // добавляем песню
            } else {                                                                                //если у него нет альбома, создаем новый и добавляем песню
                HashSet<MusicFile> file = new HashSet<>();
                file.add(musicFile);
                musicCatalog.addAlbum(nameOfArtist, nameOfAlbum, file);
            }
        }
    }

    /**
     * Метод создает иерархию "Artist", заполняет данными из полученного файла и добавляет нового артиста в коллекцию MusicCatalog
     * @param nameOfArtist
     * @param nameOfAlbum
     * @param musicFile
     */
    private void createArtist(String nameOfArtist, String nameOfAlbum, MusicFile musicFile) {
        HashMap<String, HashSet<MusicFile>> newAlbum = new HashMap<>();
        HashSet<MusicFile> newSongs = new HashSet<>();
        newSongs.add(musicFile);
        newAlbum.put(nameOfAlbum, newSongs);
        musicCatalog.addArtist(nameOfArtist, newAlbum);
    }

    /**
     * Метод вычисляет чексумму переданного файла
     * @param fileName
     * @return строковое значение чексуммы
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private String getCheckSum(String fileName) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(CHECKSUM_ALGORITHM);
        FileInputStream fis = new FileInputStream(fileName);
        byte[] dataBytes = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(dataBytes)) > 0) {
            md.update(dataBytes, 0, bytesRead);
        }
        byte[] mdBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mdBytes.length; i++) {
            sb.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return sb.toString();
    }


    /**
     * Метод осуществляет поиск по всей коллекции добавленных музыкальных файлов, сравнивая
     *      a. Название композиции, имя исполнителя и название альбома
     *      b. Чексумму
     *  нового файла с имеющимися.
     *  Если выявлен дубликат, он отправляется на запись в соответствующую коллекцию дубликактов
     * @param musicFile
     * @param nameOfArtist
     * @param nameOfAlbum
     */
    private void checkForDuplicates(MusicFile musicFile, String nameOfArtist, String nameOfAlbum){
        HashMap<String, HashMap<String, HashSet<MusicFile>>> bufferCollection = musicCatalog.getArtists();
        for(String artist: bufferCollection.keySet()){
            for(String album : bufferCollection.get(artist).keySet()){
                for(MusicFile mf : bufferCollection.get(artist).get(album)){
                    if(mf.getCheckSum().equals(musicFile.getCheckSum())){
                        addDuplicate(musicFile, nameOfArtist, nameOfAlbum, mf.getPath(), duplicatesByCheckSum);
                    }
                    /* Сравнение по совпадению музыкального файла.
                    Метод equals переопределен и сравнивает только по имени трека,
                    по альбому и артисту был доступ к треку, поэтому они совпадают */
                    if(mf.equals(musicFile)){
                        addDuplicate(musicFile, nameOfArtist, nameOfAlbum, mf.getPath(), duplicatesByName);
                    }
                }
            }
        }
    }

    /**
     * Если программа выявила, что музыккальный файл является дублем
     *          а. По полному совпадению названия композиции, артиста и альбома
     *          b. По совпадению чексуммы
     * Производится запись файла в соответствующую коллекцию повторов
     * @param musicFile     - entity музыкального файла (содержит название песни и путь к файлу)
     * @param nameOfArtist  - имя исполнителя
     * @param nameOfAlbum   - название альбома
     * @param originPath    - путь к файлу, сравнивая с которым, был выявлен дубликат
     *                        (если в коллекции повторов нет информации о данной песне, запишется путь к оригиналу и выявленному дубликату)
     * @param repeats       - коллекция дубликатов(повторы по чексумме / повторы по имени)
     */
    private void addDuplicate(MusicFile musicFile, String nameOfArtist, String nameOfAlbum, String originPath, HashMap<String, ArrayList<String>> repeats){
        String keyName = nameOfArtist + " " + nameOfAlbum + " " + musicFile.getNameOfTrack();
        if (repeats.containsKey(keyName)) {                         // если уже был файл с таким именем, добавляется путь
            if(!repeats.get(keyName).contains(musicFile.getPath())) {
                repeats.get(keyName).add(musicFile.getPath());
            }
        } else {                                                    // если не было, создается лист файлов и добавляем новый файл, а так же путь к оригиналу
            ArrayList<String> files = new ArrayList<>();
            files.add(musicFile.getPath());
            files.add(originPath);
            repeats.put(keyName, files);
        }
    }


    /**
     * @return возвращает текстовое представление каталога музыкальных файлов
     */
    public String getStringInfo(){
       return musicCatalog.getString();
    }

    /**
     * Метод создает по переданному пути HTML документ, содержащий информацию обо всей музыкальной коллекции
     * @param fileName - путь к файлу, в который нужно записать HTML документ
     * @throws IOException
     */
    public void writeHTML(String fileName) throws IOException {
        FileWriter out = new FileWriter(fileName);
        BufferedWriter br = new BufferedWriter(out);
        PrintWriter pw = new PrintWriter(br);
        pw.println(musicCatalog.getHtml());
        pw.close();
    }

    /**
     *  Метод записывает в лог информацию о дубликатах
     *      а. По полному совпадению названия композиции, артиста и альбома
     *      b. По совпадению чексуммы
     *  В зависимости от того, какая коллекция и логгер ему переданы
     * @param repeats - коллекция с дубликатами либо по имени, либо по чексумме
     * @param logger - для каждой коллекции отдельный логгер
     */
    private void writeDuplicatesLog(HashMap<String, ArrayList<String>> repeats, Logger logger){
        if(!repeats.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String s : repeats.keySet()) {
                sb.append("Дубликаты " + s + ":\n");
                for (String path : repeats.get(s)) {
                    sb.append("Путь: " + path + "\n");
                }
            }
            logger.info(sb.toString());
        }
    }
}