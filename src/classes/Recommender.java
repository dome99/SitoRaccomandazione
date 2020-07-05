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
    private final String PROJECT_PATH; //Path del progetto

    private String title; //Nome della combinazione
    private String headConcept; //Nome del genere HEAD
    private String modifierConcept; //Nome del genere MODIFIER
    private List<String> rigidProperties = new ArrayList<>(); //Lista delle proprietà rigide
    private List<Pair<String, Double>> typicalProperties  = new ArrayList<>(); //Lista delle proprietà tipiche

    private List<String> propList = new ArrayList<>(); //Lista delle proprietà da tenere in considerazione al fine della raccomandazione
    private List<String> notPropList = new ArrayList<>(); //Lista delle proprietà rigide negate

    private final int MAX = 30; //Numero massimo delle canzoni da mostrare dopo la riclassificazione

    private final Map<String, Double> graduatoria= new HashMap<>(); //Graduatoria delle canzoni riclassificate
    private Map<String, Double> sortedGraduatoria; //Graduatoria riordinata

    private int somma = 0;


    public Recommender(String fileName, String path){
        PROJECT_PATH = path;
        readAttributes(fileName);
    }


    //Lettura delle proprietà del file specificato come prototipo
    private void readAttributes(String fileName){
        try (BufferedReader br = new BufferedReader(new FileReader(PROJECT_PATH + "/prototipi/"+fileName))) {
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
                        typicalProperties.add(new Pair<>(array[1].strip(), Double.parseDouble(array[2].strip())));
                    }else if(line.contains("head") || line.contains("modifier")){
                        rigidProperties.add(line.split(",")[1].strip());
                    }else if(line.contains("Result")){
                        String[] propB=line.split(":")[1].strip().split(",");
                        for(int i=0; i<propB.length-1; i++){
                            if(propB[i].strip().equals("'1'")){
                                propList.add(typicalProperties.get(i).getAttr());
                            }
                        }

                        for(String s: rigidProperties){
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

    //Elaborazione della graduatoria
    private void elaboraGraduatoria(){
        somma = 0;
        try {
            JSONObject jsonObject = new JSONObject(Files.readString(Paths.get(PROJECT_PATH + "/WEB-INF/classes/data.txt"), StandardCharsets.US_ASCII));
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

                    if(Files.exists(Paths.get(PROJECT_PATH + "/WEB-INF/classes/songs/" + fileName.replace("!-!-!", "-") + ".txt"))){
                        try (BufferedReader br = new BufferedReader(new FileReader(PROJECT_PATH + "/WEB-INF/classes/songs/" +
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
        }

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

    public List<String> getrigidProperties() {
        return rigidProperties;
    }

    public void setrigidProperties(List<String> rigidProperties) {
        this.rigidProperties = rigidProperties;
    }

    public List<Pair<String, Double>> gettypicalProperties() {
        return typicalProperties;
    }

    public void settypicalProperties(List<Pair<String, Double>> typicalProperties) {
        this.typicalProperties = typicalProperties;
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

    //Metodo utilizzato per riordinare l'HashMap in ordine decrescente sull'attributo value
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
