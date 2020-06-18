package classes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Recommender {
    private String title;
    private String headConcept;
    private String modifierConcept;
    private List<String> tipicalAttrs = new ArrayList<>();
    private List<Pair<String, Double>> attrs  = new ArrayList<>();

    private List<String> propList = new ArrayList<>();
    private List<String> notPropList = new ArrayList<>();

    private final int MAX = 30;

    private Map<String, Double> graduatoria= new HashMap<>();
    private Map<String, Double> sortedGraduatoria;

    private int somma = 0;

    public Recommender(String fileName){
        readAttributes(fileName);
    }

    private void readAttributes(String fileName){
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\domen\\Desktop\\AllMusic\\web\\prototipi\\"+fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line=line.strip();
                if(!line.equals("") && line.charAt(0)!='#'){
                    if (line.contains("Title")){
                        title = line.split(":")[1].strip();
                    } else if (line.contains("Head Concept Name") ){
                        headConcept = line.split(":")[1].strip();
                    } else if (line.contains("Modifier Concept Name")){
                        modifierConcept = line.split(":")[1].strip();
                    }else if(line.contains("T(head)") || line.contains("T(modifier)")){
                        String[] array=line.split(",");
                        attrs.add(new Pair<>(array[1].strip(), Double.parseDouble(array[2].strip())));
                    }else if(line.contains("head") || line.contains("modifier")){
                        tipicalAttrs.add(line.split(",")[1].strip());
                    }else if(line.contains("Result")){
                        String[] propB=line.split(":")[1].strip().split(",");
                        for(int i=0; i<propB.length-1; i++){
                            if(propB[i].strip().equals("'1'")){
                                propList.add(attrs.get(i).getAttr());
                            }
                        }

                        for(String s: tipicalAttrs){
                            if(!(s.charAt(0) == '-')){
                                propList.add(s);
                            }else {
                                notPropList.add(s);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> getGraduatoria() {
        elaboraGraduatoria();

        return sortedGraduatoria;
    }

    private void elaboraGraduatoria(){
        somma = 0;
        try {
            JSONObject jsonObject = new JSONObject(Files.readString(Paths.get("C:\\Users\\domen\\Desktop\\AllMusic\\src\\data.txt"), StandardCharsets.US_ASCII));
            JSONArray songs = jsonObject.getJSONArray("songs");
            for (int i=0; i<songs.length(); i++){
                somma += 1;
                String fileName = songs.getJSONObject(i).getString("title") + "!-!-!"
                        + songs.getJSONObject(i).getString("performer");

                fileName=fileName.replace("\"", "")
                        .replace("/", "_")
                        .replace(":", "")
                        .replace("?", "");

                if(!graduatoria.containsKey(fileName)){
                    graduatoria.put(fileName, 0.0);

                    for (String s: propList){
                        if(songs.getJSONObject(i).getJSONArray("attributes").toString().contains(s)){
                            graduatoria.replace(fileName, 0.1);
                        }
                    }

                    if(Files.exists(Paths.get("C:\\Users\\domen\\Desktop\\AllMusic\\src\\songs\\" +
                            fileName.replace("!-!-!", "-") + ".txt"))){
                        try (BufferedReader br = new BufferedReader(new FileReader(
                                "C:\\Users\\domen\\Desktop\\AllMusic\\src\\songs\\" +
                                fileName.replace("!-!-!", "-") + ".txt"))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                String[] word = line.split(":");
                                if(propList.contains(word[0].strip())){
                                    graduatoria.replace(fileName,
                                            graduatoria.get(fileName)+Double.parseDouble(word[1].strip()));
                                }
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }

                boolean ok = true;
                for(String prop: notPropList){
                    if(songs.getJSONObject(i).getJSONArray("attributes").toString().contains(prop)){
                        ok = false;
                    }
                }

                if(!ok){
                    graduatoria.replace(fileName, 0.0);
                }
            }

            sortedGraduatoria = sortHashMapByValues(graduatoria);
        } catch (IOException e) {
            e.printStackTrace();
        };

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadConcept() {
        return headConcept;
    }

    public void setHeadConcept(String headConcept) {
        this.headConcept = headConcept;
    }

    public String getModifierConcept() {
        return modifierConcept;
    }

    public void setModifierConcept(String modifierConcept) {
        this.modifierConcept = modifierConcept;
    }

    public int getMAX() {
        return MAX;
    }

    public int getSomma() {
        return somma;
    }

    public List<String> getTipicalAttrs() {
        return tipicalAttrs;
    }

    public void setTipicalAttrs(List<String> tipicalAttrs) {
        this.tipicalAttrs = tipicalAttrs;
    }

    public List<Pair<String, Double>> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<Pair<String, Double>> attrs) {
        this.attrs = attrs;
    }

    public List<String> getPropList() {
        return propList;
    }

    public void setPropList(List<String> propList) {
        this.propList = propList;
    }

    public List<String> getNotPropList() {
        return notPropList;
    }

    public void setNotPropList(List<String> notPropList) {
        this.notPropList = notPropList;
    }

    public LinkedHashMap<String, Double> sortHashMapByValues(Map<String, Double> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Double> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues, new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return Double.compare(o2, o1);
            }
        });
        Collections.reverse(mapKeys);

        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();

        Iterator<Double> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Double val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Double comp1 = passedMap.get(key);
                Double comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}
