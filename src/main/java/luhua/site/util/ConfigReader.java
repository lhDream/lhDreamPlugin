package luhua.site.util;

import com.google.common.io.Files;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @description: 读取配置文件
 * @author: lhDream
 * @create: 2021-09-09 13:40
 **/
public class ConfigReader extends YamlConfiguration {
    private HashMap<String, String> comments;
    private HashMap<String, Object> contents;
    YamlConfiguration config;
    private String p;
    private File file;
    private boolean recordContents;
    private Plugin plugin;
    String[] waitingComment;

    public ConfigReader(Plugin plugin, String fileName) throws Exception {
        this(new File(plugin.getDataFolder(), fileName));
        this.plugin = plugin;
    }
    public ConfigReader(File file) throws Exception {
        this.p = null;
        this.file = null;
        this.recordContents = false;
        this.waitingComment = null;
        this.comments = new HashMap();
        this.contents = new HashMap();
        this.file = file;
        this.config = this.getyml(file);
    }

    public void load() {
        try {
            if (this.file.isFile()) {
                this.load(this.file);
            }
        } catch (InvalidConfigurationException | IOException var2) {
            var2.printStackTrace();
        }

    }
    @Override
    public void save(String file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        } else {
            this.save(new File(file));
        }
    }
    @Override
    public void save(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        } else {
            Files.createParentDirs(file);
            String data = this.insertComments(this.saveToString());
            PrintWriter writer = new PrintWriter(file, "UTF-8");

            try {
                writer.write(data);
            } finally {
                writer.close();
            }

        }
    }

    private String insertComments(String yaml) {
        if (this.comments.isEmpty()) {
            return yaml;
        } else {
            String[] yamlContents = yaml.split("[" + System.getProperty("line.separator") + "]");
            StringBuilder newContents = new StringBuilder();
            StringBuilder currentPath = new StringBuilder();
            boolean commentedPath = false;
            boolean node = false;
            int depth = 0;
            boolean firstLine = true;
            String[] var12 = yamlContents;
            int var11 = yamlContents.length;

            for(int var10 = 0; var10 < var11; ++var10) {
                String line = var12[var10];
                if (firstLine) {
                    firstLine = false;
                    if (line.startsWith("#")) {
                        continue;
                    }
                }

                boolean keyOk = true;
                int whiteSpace;
                int newDepth;
                int index;
                if (line.contains(": ")) {
                    index = line.indexOf(": ");
                    if (index < 0) {
                        index = line.length() - 1;
                    }

                    whiteSpace = 0;

                    for(newDepth = 0; newDepth < line.length() && line.charAt(newDepth) == ' '; ++newDepth) {
                        ++whiteSpace;
                    }

                    String key = line.substring(whiteSpace, index);
                    if (key.contains(" ")) {
                        keyOk = false;
                    } else if (key.contains("&")) {
                        keyOk = false;
                    } else if (key.contains(".")) {
                        keyOk = false;
                    } else if (key.contains("'")) {
                        keyOk = false;
                    } else if (key.contains("\"")) {
                        keyOk = false;
                    }
                }

                if (line.contains(": ") && keyOk || line.length() > 1 && line.charAt(line.length() - 1) == ':') {
                    commentedPath = false;
                    node = true;
                    index = line.indexOf(": ");
                    if (index < 0) {
                        index = line.length() - 1;
                    }

                    if (currentPath.toString().isEmpty()) {
                        currentPath = new StringBuilder(line.substring(0, index));
                    } else {
                        whiteSpace = 0;

                        for(newDepth = 0; newDepth < line.length() && line.charAt(newDepth) == ' '; ++newDepth) {
                            ++whiteSpace;
                        }

                        if (whiteSpace / 2 > depth) {
                            currentPath.append(".").append(line.substring(whiteSpace, index));
                            ++depth;
                        } else if (whiteSpace / 2 < depth) {
                            newDepth = whiteSpace / 2;

                            int lastIndex;
                            for(lastIndex = 0; lastIndex < depth - newDepth; ++lastIndex) {
                                currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "");
                            }

                            lastIndex = currentPath.lastIndexOf(".");
                            if (lastIndex < 0) {
                                currentPath = new StringBuilder();
                            } else {
                                currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "").append(".");
                            }

                            currentPath.append(line.substring(whiteSpace, index));
                            depth = newDepth;
                        } else {
                            newDepth = currentPath.lastIndexOf(".");
                            if (newDepth < 0) {
                                currentPath = new StringBuilder();
                            } else {
                                currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "").append(".");
                            }

                            currentPath.append(line.substring(whiteSpace, index));
                        }
                    }
                } else {
                    node = false;
                }

                StringBuilder newLine = new StringBuilder(line);
                if (node) {
                    String comment = null;
                    if (!commentedPath) {
                        comment = (String)this.comments.get(currentPath.toString());
                    }

                    if (comment != null && !comment.isEmpty()) {
                        newLine.insert(0, System.getProperty("line.separator")).insert(0, comment);
                        comment = null;
                        commentedPath = true;
                    }
                }

                newLine.append(System.getProperty("line.separator"));
                newContents.append(newLine.toString());
            }

            return newContents.toString();
        }
    }

    public void addHeaderComments(List<String> comments) {
        this.getC().options().header(formStringBuilder(comments).toString());
    }

    public void appendComment(String path, String... commentLines) {
        this.addComment(path, true, commentLines);
    }

    public void addComment(String path, String... commentLines) {
        this.addComment(path, false, commentLines);
    }

    public void addComment(String path, boolean append, String... commentLines) {
        if (this.p != null) {
            path = this.p + path;
        }

        StringBuilder commentstring = new StringBuilder();
        if (append && this.comments.containsKey(path)) {
            commentstring.append((String)this.comments.get(path));
        }

        String leadingSpaces = "";

        for(int n = 0; n < path.length(); ++n) {
            if (path.charAt(n) == '.') {
                leadingSpaces = leadingSpaces + "  ";
            }
        }

        String[] var9 = commentLines;
        int var8 = commentLines.length;

        for(int var7 = 0; var7 < var8; ++var7) {
            String line = var9[var7];
            if (!line.isEmpty()) {
                line = leadingSpaces + "# " + line;
            }

            if (commentstring.length() > 0) {
                commentstring.append(System.getProperty("line.separator"));
            }

            commentstring.append(line);
        }

        this.comments.put(path, commentstring.toString());
    }

    public YamlConfiguration getyml(File file) throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        FileInputStream fileinputstream = null;

        try {
            fileinputstream = new FileInputStream(file);
            InputStreamReader str = new InputStreamReader(fileinputstream, Charset.forName("UTF-8"));
            config.load(str);
            str.close();
        } catch (FileNotFoundException var14) {
        } catch (IOException | InvalidConfigurationException var15) {
            var15.printStackTrace();
            this.saveToBackup();
            throw var15;
        } finally {
            if (fileinputstream != null) {
                try {
                    fileinputstream.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

        return config;
    }

    public void saveToBackup() {
        this.saveToBackup(true);
    }

    public void saveToBackup(boolean inform) {
        File dataFolder = this.plugin == null ? CMILib.getInstance().getDataFolder() : this.plugin.getDataFolder();
        File cc = new File(dataFolder, "FileBackups");
        if (!cc.isDirectory()) {
            cc.mkdir();
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss ");
        String newFileName = dateFormat.format(date) + this.file.getName();
        if (inform) {
            CMIMessages.consoleMessage("&cFailed to load " + this.file.getName() + "! Backup have been saved into " + dataFolder.getPath() + File.separator + "FileBackups" + File.separator + newFileName);
        }

        File f = new File(dataFolder, "FileBackups" + File.separator + newFileName);

        try {
            Files.copy(this.file, f);
        } catch (IOException var9) {
            var9.printStackTrace();
        }

    }

    public void save() {
        try {
            this.save(this.file);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    private static void newLn(StringBuilder header) {
        header.append(System.lineSeparator());
    }

    private static StringBuilder formStringBuilder(List<String> list) {
        StringBuilder header = new StringBuilder();
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            String one = (String)var3.next();
            header.append(one);
            newLn(header);
        }

        return header;
    }

    public void header(List<String> list) {
        this.options().header(formStringBuilder(list).toString());
    }

    private void checkWaitingComment(String path) {
        if (this.waitingComment != null) {
            this.addComment(path, this.waitingComment);
            this.waitingComment = null;
        }
    }

    public YamlConfiguration getC() {
        return this.config;
    }

    public void copyDefaults(boolean value) {
        this.getC().options().copyDefaults(value);
    }

    public Boolean get(String path, Boolean boo) {
        path = this.process(path, boo);
        return this.config.getBoolean(path);
    }

    public String process(String path, Object value) {
        if (this.p != null) {
            path = this.p + path;
        }

        this.checkWaitingComment(path);
        this.config.addDefault(path, value);
        this.copySetting(path);
        return path;
    }

    public Object get(String path, Location boo) {
        path = this.process(path, boo);
        return this.config.get(path);
    }

    public int get(String path, int boo) {
        path = this.process(path, boo);
        return this.config.getInt(path);
    }

    public List<Integer> getIntList(String path, List<Integer> list) {
        path = this.process(path, list);
        return this.config.getIntegerList(path);
    }

    private static String convertUnicode(String st) {
        try {
            if (!st.contains("\\u")) {
                return st;
            } else {
                StringBuilder sb = new StringBuilder(st.length());

                for(int i = 0; i < st.length(); ++i) {
                    char ch = st.charAt(i);
                    if (ch == '\\') {
                        char nextChar = i == st.length() - 1 ? 92 : st.charAt(i + 1);
                        if (nextChar >= '0' && nextChar <= '7') {
                            String code = "" + nextChar;
                            ++i;
                            if (i < st.length() - 1 && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7') {
                                code = code + st.charAt(i + 1);
                                ++i;
                                if (i < st.length() - 1 && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7') {
                                    code = code + st.charAt(i + 1);
                                    ++i;
                                }
                            }

                            sb.append((char)Integer.parseInt(code, 8));
                            continue;
                        }

                        switch(nextChar) {
                            case 'u':
                                if (i < st.length() - 5) {
                                    try {
                                        int code = Integer.parseInt("" + st.charAt(i + 2) + st.charAt(i + 3) + st.charAt(i + 4) + st.charAt(i + 5), 16);
                                        sb.append(Character.toChars(code));
                                    } catch (NumberFormatException var6) {
                                        sb.append("\\");
                                        continue;
                                    }

                                    i += 5;
                                    continue;
                                }

                                ch = 'u';
                                ++i;
                                break;
                            default:
                                sb.append("\\");
                                continue;
                        }
                    }

                    sb.append(ch);
                }

                return sb.toString();
            }
        } catch (Exception var7) {
            var7.printStackTrace();
            return st;
        }
    }

    public List<String> get(String path, List<String> list) {
        path = this.process(path, list);
        if (this.recordContents) {
            this.contents.put(path, this.config.isList(path) ? this.config.getStringList(path) : list);
        }

        List<String> ls = this.config.getStringList(path);

        for(int p = 0; p < ls.size(); ++p) {
            String st = convertUnicode((String)ls.get(p));
            ls.set(p, st);
        }

        return ls;
    }

    public String get(String path, String boo) {
        path = this.process(path, boo);
        if (this.recordContents) {
            this.contents.put(path, this.config.isString(path) ? this.config.getString(path) : boo);
        }

        return convertUnicode(this.config.getString(path));
    }

    public Double get(String path, Double boo) {
        path = this.process(path, boo);
        return this.config.getDouble(path);
    }

    private synchronized void copySetting(String path) {
        this.set(path, this.config.get(path));
    }

    public void resetP() {
        this.p = null;
    }

    @Deprecated
    public void setP(String cmd) {
        this.p = "command." + cmd + ".info.";
    }

    public void setFullPath(String path) {
        this.p = path;
    }

    public String getPath() {
        return this.p;
    }

    public void setRecordContents(boolean recordContents) {
        this.recordContents = recordContents;
    }

    public HashMap<String, Object> getContents() {
        return this.contents;
    }

    public File getFile() {
        return this.file;
    }

    public void copyDefaults() {
        this.getC().options().copyDefaults(true);
    }

    public void forceSet(String path, Object obj) {
        this.config.set(path, obj);
        this.copySetting(path);
    }

    public String getObject(String path, Object boo) {
        if (path != null) {
            path = this.process(path, boo);
            if (this.recordContents) {
                this.contents.put(path, this.config.isString(path) ? this.config.getString(path) : boo);
            }

        }
        return convertUnicode(this.config.getString(path));
    }

}
