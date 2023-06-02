package oprpp2.hw03.servlets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Glasanje {


    public static Map<Integer, Integer> getResults(String fileName) throws IOException {
        List<String> votes = Files.readAllLines(Path.of(fileName));

        Map<Integer, Integer> votesMap = new TreeMap<>();
        for (String vote : votes) {
            String[] attr = vote.split("\t");
            int id = Integer.parseInt(attr[0]);
            int value = Integer.parseInt(attr[1]);
            votesMap.put(id, value);
        }

        return votesMap;
    }

    public static List<TriAtom> getDefinitions(String filename) throws IOException {
        List<String> defs = Files.readAllLines(Path.of(filename));

        List<TriAtom> atoms = new ArrayList<>();
        for (String def : defs) {
            String[] attr = def.split("\t");
            int id = Integer.parseInt(attr[0]);
            atoms.add(new TriAtom(id, attr[1], attr[2]));
        }

        return atoms;
    }

    public static class TriAtom {
        private Integer id;
        private String name;
        private String videoUrl;

        public TriAtom(Integer id, String name, String videoUrl) {
            this.id = id;
            this.name = name;
            this.videoUrl = videoUrl;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getVideoUrl() {
            return videoUrl;
        }
    }

}
