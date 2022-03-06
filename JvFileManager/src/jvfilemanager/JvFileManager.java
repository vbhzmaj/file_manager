
package jvfilemanager;

import java.util.Scanner;
import java.io.*;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.regex.Pattern;
import javax.swing.filechooser.FileSystemView;

public class JvFileManager {

    private static String drive;
    private static boolean move;
    private static Path abspath;
    private static boolean isillegal;
        
    public  static void main(String[] args) throws IOException {
    boolean control = true;
    while(control){
        System.out.println("1.LIST   2.INFO   3.CREATE_DIR   4.RENAME   5.COPY   6.MOVE   7.DELETE   8.HELP/POMOĆ");
        Scanner s = new Scanner(System.in);
        
        System.out.println("IZABERITE JEDNU OD PONUĐENIH OPCIJA (1-8) ILI ZAUSTAVITE APLIKACIJU (9).");
            if(s.hasNextInt())
            {int number = s.nextInt();
                    switch(number){
                        case 1: listDirFile(); break;
                        case 2: infoDirFile(); break;
                        case 3: createDir();  break;
                        case 4: renameDirFile(); break;
                        case 5: move = false; copyChoice();break;
                        case 6: move = true; copyChoice(); break;
                        case 7: deleteDirFile(); break;
                        case 8: instructions(); break;
                        case 9: control = false; break;
                        default: System.out.println("Broj treba biti u rasponu 1-9!"); break;
            }}else System.out.println("Trebate uneti celi broj!");}
    }
    private static void listDirFile(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Navedite putanju foldera koji želite izlistati:");
        String entry = sc.nextLine();
        checkPathEntry(entry); 
        File dir = abspath.toFile();
        try{if(dir.exists() && dir.isDirectory()){
                String[] strings = dir.list();
                if (strings.length == 0){System.out.println("Folder " + dir.getAbsolutePath() + " je prazan.");
                    } else {System.out.println("U folderu se nalaze:");
                    for(int i = 0; i < strings.length; i++)
                    {System.out.println(strings[i]);}}}
            else {System.out.println("Folder nije pronađen.");}}
    catch(Exception e) {System.out.println(e);}   
    }
    private static void infoDirFile() throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.print("Navedite putanju foldera za prikaz informacija o njegovim fajlovima:");
        String entry = sc.nextLine();
        checkPathEntry(entry);
        File dir = abspath.toFile();
        if(dir.exists() && dir.isDirectory()){
           try{ File[] files = dir.listFiles();
            if (files.length == 0){System.out.println("Folder " + dir.getAbsolutePath() + " je prazan.");
            } else {
                for(int i = 0; i < files.length; i++)
                {System.out.println("");
                System.out.println("Ime: " +files[i].getName());
                System.out.print("Apsolutna putanja: " + files[i].getAbsolutePath());
                if (files[i].isDirectory()){System.out.println(", ovo je folder.");}
                else {System.out.println(", ovo je fajl.");}
                System.out.println("Size = " + files[i].length());
                Path path1 = files[i].toPath();
                BasicFileAttributes fatr = Files.readAttributes (path1, BasicFileAttributes.class);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy. HH:mm:ss");
                Instant instant = Instant.ofEpochMilli(files[i].lastModified());
                LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                System.out.println("Kreacija: " + new SimpleDateFormat("dd. MMMM yyyy. HH:mm:ss")
                               .format(fatr.creationTime().toMillis()));
                System.out.println("Zadnja promena: " + dateTime.format(dateTimeFormatter));            
                }
            }} catch(IOException e){System.out.println(e);} 
            catch(Exception e){System.out.println(e);}
        }else {System.out.println("Folder nije pronađen.");}
        System.out.println("");
    }
    private static void createDir()throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Navedite putanju foldera koji želite kreirati:");
        checkPathEntry(sc.nextLine()); 
        File newDirectory = abspath.toFile();
        {if(!newDirectory.exists())
                {if (newDirectory.mkdir()){System.out.println("Kreiran folder " + newDirectory.getAbsolutePath());}
                else {System.out.println("Nije moguće kreirati folder sa željenom putanjom.");}
                }
            else if(newDirectory.exists() && newDirectory.isDirectory()) {
            System.out.println("Folder " + newDirectory.getAbsolutePath() + " već postoji.");} 
        }
    }
    private static void renameDirFile() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Navedite putanju fajla/foldera koji želite preimenovati:");
        checkPathEntry(sc.nextLine()); 
        File oldfile = abspath.toFile();
        
        if(!oldfile.exists()){
                System.out.println("Fajl/folder koji želite preimenovati ne postoji!");}
        
        else if (oldfile.exists() && oldfile.isDirectory()){
            
          Scanner sca = new Scanner(System.in);
            System.out.print("Navedite novo ime za folder koji želite preimenovati (bez putanje):");
            String newDirName = sca.nextLine();
            System.out.println("Vaš unos je: " + newDirName);
            if(newDirName.length()>100){System.out.println("Unos je predug.");
                }else { String illegal = "<>:\"/|?*\\";
                char[] entrych = newDirName.toCharArray();
                char[] illegalch = illegal.toCharArray();
                for (char c : entrych) { 
                        for (char i : illegalch)
                            {if (c == i) {System.out.println(c + " je nedozvoljen karakter u imenu fajla/foldera.");
                            System.out.println("Lista nedozvoljenih karaktera: < > : \" / | ? * \\ ");
                            isillegal=true;break;} }if(isillegal)break;
                }
                if(!isillegal)
                {File newdir = new File(oldfile.getParentFile() + "\\" + newDirName);
                    if(newdir.exists()){System.out.println("Folder (ili fajl) sa željenim imenom već postoji!");}
                    else {if(oldfile.renameTo(newdir)) {
                            System.out.println("Preimenovanje foldera uspešno izvršeno.");}
                              
        }}}}
                            
            
                else if (oldfile.exists() && oldfile.isFile()){
                    Scanner scan = new Scanner(System.in);
                    System.out.println("Navedite novo ime za fajl koji želite preimenovati (bez putanje ali sa ekstenzijom):");
                    String newFileName = scan.nextLine();
                    
                    System.out.println("Vaš unos je: " + newFileName);
            if(newFileName.length()>100){System.out.println("Unos je predug.");
                }else { String illegalf = "<>:\"/|?*\\";
                char[] entrycha = newFileName.toCharArray();
                char[] illegalcha = illegalf.toCharArray();
                for (char c : entrycha) { 
                        for (char i : illegalcha)
                            {if (c == i) {System.out.println(c + " je nedozvoljen karakter u imenu fajla/foldera.");
                            System.out.println("Lista nedozvoljenih karaktera: < > : \" / | ? * \\ ");
                            isillegal=true;break;} }if(isillegal)break;
                }
                if(!isillegal)
                    {boolean hasdot = false;
                     char i = '.';
                     for (char c : entrycha) {
                            if(c==i){hasdot=true; break;}
                     } 
                    if(hasdot) {File newfile = new File(oldfile.getParent() + "\\" + newFileName);
                    if(newfile.exists()){System.out.println("Željeno ime nije slobodno!");return;}
                    if(oldfile.renameTo(newfile)) {
                        System.out.println("Preimenovanje fajla uspešno izvršeno.");
                        }}           
                                else {System.out.println("Niste naveli ekstenziju za fajl.");}
                            }
                    
                        }
            }} 
    


private static void copyChoice() throws IOException{Scanner sc = new Scanner(System.in);
       if(move){System.out.println("Odaberite željenu opciju za premeštanje (1-2):");
       System.out.println("1. Premeštanje fajla.");
       System.out.println("2. Premeštanje foldera.");
       }else {System.out.println("Odaberite željenu opciju za kopiranje (1-2):");
       System.out.println("1. Kopiranje fajla.");
       System.out.println("2. Kopiranje foldera.");}
       if(sc.hasNextInt()){
            try{int number = sc.nextInt();
                switch(number){
                case 1: choiceFileCopy();
                break;
                case 2: copyDir();
                break;
                default: System.out.println("Celi broj je trebao biti u rasponu 1-2!");
                break;
                }} catch(IOException e){System.out.println(e);}  
                   catch(Exception e){System.out.println(e);} 
        }else System.out.println("Trebalo je ukucati celi broj!");
    }
    private static void choiceFileCopy() throws IOException{Scanner sc = new Scanner(System.in);
       if(!move){System.out.println("Odaberite željenu opciju za kopiranje fajla (1-2):");
       System.out.println("1. Kopiranje fajla u postojeći folder.");
       System.out.println("2. Kopiranje fajla u novi folder koji će aplikacija automatski kreirati.");
       }else{System.out.println("Odaberite željenu opciju za premeštanje fajla (1-2):");
       System.out.println("1. Premeštanje fajla u postojeći folder.");
       System.out.println("2. Premeštanje fajla u novi folder koji će aplikacija automatski kreirati.");}
       if(sc.hasNextInt()){
           try{int number = sc.nextInt();
                    switch(number){
                        case 1: existDestFileCopy();
                        break;
                        case 2: newDestFileCopy();
                        break;
                        default: System.out.println("Celi broj je trebao biti u rasponu 1-2!");
                        break;}
            }catch(IOException e){System.out.println(e);}
             catch(Exception e){System.out.println(e);}
        }else System.out.println("Trebalo je ukucati celi broj!");
  } 
    private static void existDestFileCopy() throws IOException {
        if(!move){System.out.println("Navedite putanju fajla koji želite kopirati:");
        }else{System.out.println("Navedite putanju fajla koji želite premestiti:");}
        Scanner sc = new Scanner(System.in);
        String entry = sc.nextLine();
        checkPathEntry(entry); 
        File source = abspath.toFile();    
        if(source.exists() && source.isFile()){
            Scanner scd = new Scanner(System.in);
            System.out.println("Navedite putanju odredišnog foldera:");
            String entryDest = scd.nextLine();
            checkPathEntry(entryDest); 
            File destination = abspath.toFile();
            try{
                if(destination.exists() && destination.isDirectory()){
                    File target = destDirConvert(source, destination);
                    if (source.toString().equals(target.toString())){System.out.println("Nema smisla kopirati fajl u samog sebe.");
                    }else {replaceTargetFile(source, target);}
                }else{System.out.println("Odredišni folder ne postoji.");}
            } catch(IOException e){System.out.println(e);}
              catch(Exception e){System.out.println(e);}
        }else {System.out.println("Fajl ne postoji na zadatoj lokaciji.");}
    }
    private static void newDestFileCopy() throws IOException{Scanner sc = new Scanner(System.in);
        if(move){System.out.println("Navedite putanju fajla koji želite premestiti:");
        }else{System.out.println("Navedite putanju fajla koji želite kopirati:");}
        checkPathEntry(sc.nextLine()); 
        File source = abspath.toFile();
        if(source.exists() && source.isFile()){
            Scanner scd = new Scanner(System.in);
            System.out.print("Navedite putanju odredišnog foldera koji će aplikacija kreirati:");
            checkPathEntry(scd.nextLine()); 
            File destination = abspath.toFile();
                if(destination.exists() && destination.isDirectory()){System.out.println("Odredišni folder već postoji.");
                    }else if (destination.exists() && destination.isFile()){if(move){System.out.println("Nije moguće premeštanje fajla u fajl.");
                                                                            }else{System.out.println("Nije moguće kopiranje fajla u fajl.");} }
                    else if(!destination.exists()){
                        try{if (destination.mkdir()){System.out.println("Kreiran folder " + destination.getAbsolutePath() + ".");
                        File target = destDirConvert(source, destination);
                        replaceTargetFile(source, target);}} 
                        catch(IOException e){System.out.println(e);}catch(Exception e){System.out.println(e);}}
        }else{ System.out.print("Fajl sa putanjom " + source + " ne postoji");}}
    private static void replaceTargetFile(File source, File target) throws IOException {
		try{Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    if(move){System.out.println("Fajl uspešno premešten.");
                            source.delete();
                    } else {System.out.println("Fajl uspešno kopiran.");}}
                catch (IOException e){System.out.println(e);}
                catch (Exception e){System.out.println(e);}
    }
    private static void copyDir() throws IOException{
        Scanner sc = new Scanner(System.in);
        if(move){System.out.print("Navedite putanju foldera koji želite premestiti:");
        } else {System.out.print("Navedite putanju foldera koji želite kopirati:");}
        checkPathEntry(sc.nextLine()); 
        File source = abspath.toFile();
        if(source.exists() && source.isDirectory()){
        Path srcpath = source.toPath();
        Scanner sca = new Scanner(System.in);
        System.out.print("Navedite putanju odredišnog foldera (roditelj):");
        checkPathEntry(sca.nextLine()); 
        File destination = abspath.toFile();
        if(!destination.isDirectory() || !destination.exists()){
        System.out.println("Folder sa imenom " + destination + " ne postoji");}
        else {
        File target = destDirConvert(source, destination);
        Path trgpath = target.toPath();
        if(target.exists() && target.isDirectory()){
            Scanner scan = new Scanner(System.in);
            System.out.println("Folder sa putanjom " + target.getAbsolutePath() + " već postoji");
            System.out.println("Ako ga želite prebrisati ukucajte da");
            System.out.println("Ako ga ne želite prebrisati ukucajte nešto drugo.");
            String s = scan.nextLine();
            switch(s){case "da": try{deleteDirWalk(target);
                        if(!target.exists()) {if (target.mkdir()){
                        System.out.println("Kreiran novi folder pod nazivom " + target.getName());
                        }else System.out.println("Nije moguće kreirati folder pod nazivom " + target.getName());}           
                        } catch (Exception e) {System.out.println(e);}
                            try {
//                                copyDirWalk(srcpath, trgpath);
                            
                                Files.walkFileTree(srcpath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                                new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                                throws IOException { Path targetdir = trgpath.resolve(srcpath.relativize(dir));
                                try {Files.copy(dir, targetdir);
                                } catch (FileAlreadyExistsException e) {if (!Files.isDirectory(targetdir))
                                throw e;}
                                return CONTINUE;}
                                @Override
                                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException {Files.copy(file, trgpath.resolve(srcpath.relativize(file)));
                                return CONTINUE;}
                                });
                            if(move){System.out.println("Premeštanje foldera uspešno.");
                                deleteDirWalk(source);
                                } else {System.out.println("Kopiranje foldera uspešno.");}
                            } catch (IOException e) {Logger.getLogger(JvFileManager.class.getName()).log(Level.SEVERE, null, e);}
                            catch (Exception e) {System.out.println(e);}
                       break;
                       default: System.out.println("");
                       break;}
        } else {
            try {
//                copyDirWalk(srcpath, trgpath);
                 Files.walkFileTree(srcpath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                 @Override
                 public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                 throws IOException { Path targetdir = trgpath.resolve(srcpath.relativize(dir));
                 try {Files.copy(dir, targetdir);
                 } catch (FileAlreadyExistsException e) {if (!Files.isDirectory(targetdir))
                 throw e;}
                 return CONTINUE;}
                 @Override
                 public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                 throws IOException {Files.copy(file, trgpath.resolve(srcpath.relativize(file)));
                     return CONTINUE;}
                    });  
            if(move){deleteDirWalk (srcpath.toFile());
                    System.out.println("Premeštanje foldera uspešno.");} 
                else {System.out.println("Kopiranje foldera uspešno.");}
                }catch (IOException e) {
                    Logger.getLogger(JvFileManager.class.getName()).log(Level.SEVERE, null, e);
                }catch (Exception e) {System.out.println(e);}
    }}}else {System.out.println("Folder ne postoji.");}
    } 
    private static void deleteDirFile()throws AccessDeniedException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Navedite putanju fajla/foldera koji želite izbrisati:");
        checkPathEntry(sc.nextLine()); 
        File file = abspath.toFile();
        if(!file.exists()) {
            System.out.println("Nije obrisan " + file.getName() + " zato što ne postoji.");} 
        else if (file.exists() && file.isFile()){
            file.delete();
            System.out.println("Fajl uspešno obrisan!");}
        else if(file.exists() && file.isDirectory()) {deleteDirWalk(file);}
}
    private static void deleteDirWalk (File fileD){
     Path rootTree = fileD.toPath();
     try {
    Files.walkFileTree(rootTree, new SimpleFileVisitor<Path>() {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      System.out.println("Obrisan fajl: " + file.toString());
      Files.delete(file);
      return FileVisitResult.CONTINUE;}
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
      Files.delete(dir);
      System.out.println("Obrisan folder: " + dir.toString());
      return FileVisitResult.CONTINUE;
    }
  });
} catch(IOException e){e.printStackTrace();}
}
    private static File destDirConvert(File source, File destination){
        String Basepath = destination.getAbsolutePath();
        String Name = source.getName(); 
        Path trgpath = Paths.get(Basepath, Name);
        File target = trgpath.toFile();
	return target;
        }
    private static void checkPathEntry (String entry){
    try{
        System.out.println("Vaš unos je: " + entry);
        //The maximum path in Windows system on drive C is "C:\some 256-character path string<NUL>
    if(entry.length()>256){System.out.println("Unos je predug."); 
        String fakePath="C:\\non\\existing\\directory";
               abspath=Paths.get(fakePath);
               instructions();      
        }else {
            String illegal = "<>:\"/|?*";
            char[] entrych = entry.toCharArray();
            char[] illegalch = illegal.toCharArray();
        
        // Checking for illegal characters 
        for (char c : entrych) { 
            for (char i : illegalch)
                {if (c == i) {System.out.println(c + " je nedozvoljen karakter za putanju."); isillegal=true;
                              break; }}
        if(isillegal) break;
        }
        if(isillegal) {String fakePath="C:\\non\\existing\\directory";
                        abspath=Paths.get(fakePath);
                        instructions();}
        else {
            
            boolean drivecontrol = true;
        while (drivecontrol){
            System.out.println("Odaberite disk na kojem se nalazi(1-6):  1. C     2. D     3. E     4. F     5. G     6. H");
            Scanner sc = new Scanner(System.in);
            if(sc.hasNextInt())
            {int number = sc.nextInt();
                    switch(number){
                        case 1:  drive = "C";  drivecontrol = false; break;
                        case 2:  drive = "D";  drivecontrol = false; break;
                        case 3:  drive = "E";  drivecontrol = false; break;
                        case 4:  drive = "F";  drivecontrol = false; break;
                        case 5:  drive = "G";  drivecontrol = false; break;
                        case 6:  drive = "H";  drivecontrol = false; break;
                        default: System.out.println("Broj treba biti u rasponu 1-6! Aplikacija ne podržava više od šest nosača podataka."); break;
            }}else System.out.println("Trebali ste ukucati celi broj!");
        }
        String root = drive + ":";  
        Path rootPath = Paths.get(root);  
        File rootFile = rootPath.toFile();  
        
       try{if(!rootFile.exists()){System.out.println("Drive" + root + "nije dostupan.");
        System.out.println("Dostupni su:");
        drivesList();}
        }catch(NullPointerException e){System.out.println(e);}
        
    //Creating an array from string using \ as separator.   
    //String[] filedirArrays = entry.split("[\\]"); will produce syntax error
        String[] filedirArrays = entry.split(Pattern.quote(System.getProperty("file.separator")));
       
    //removing null strings created by multiple backslashes \\
        filedirArrays = Arrays.stream(filedirArrays)
        .filter(s -> (s != null && s.length() > 0))
        .toArray(String[]::new); 
        
       for(String filedirArray : filedirArrays) {
       root = root + "\\" +filedirArray;}
       if(rootFile.exists()){abspath = Paths.get(root);
            System.out.println("Kompletna putanja je: " + abspath);
        }else {String fakePath="C:\\non\\existing\\directory";
               abspath=Paths.get(fakePath);
               System.out.println("Zamišljena nepostojeća putanja je: " + root);}
    }}}catch(Exception e){System.out.println(e);}
    }
    private static void drivesList(){
        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        paths = File.listRoots();
        for(File path:paths)
            {
             System.out.println("Drive: "+path);
             System.out.println("  Opis: "+fsv.getSystemTypeDescription(path));
            }
    }
    private static void instructions(){
        System.out.println("Tražene putanje foldera se unose u formatu dir\\dir\\..\\dir\\dir");
        System.out.println("Tražene putanje fajlova se unose u formatu dir\\dir\\..\\dir\\dir\\fajl.ext");
        System.out.println("Putanje ne mogu sadržavati karaktere: < > : \" / | ? *");
        System.out.println("Imena fajlova i foldera ne mogu sadržavati karaktere: < > : \" / | ? * \\");
        System.out.println("Maksimalna dužina unesene putanje je 256, a imena fajla ili foldera 100 karaktera.");
        
    }

    
}



